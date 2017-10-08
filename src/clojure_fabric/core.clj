;;;
;;; The basic idea and initial code are from Amazonica
;;; - https://github.com/mcohen01/amazonica
;;;
(ns clojure-fabric.core
  "Clojure wrapper for Hyperledger Java SDK functions"
  (:use [clojure.algo.generic.functor :only (fmap)])
  (:require [clojure.string :as str]
            [clojure.core.cache :as cache]
            [buddy.core.keys :as keys])
  (:import clojure.lang.Reflector
           [org.hyperledger.fabric.sdk HFClient User Enrollment]
           [org.hyperledger.fabric.sdk.security CryptoSuite$Factory]
           [org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey]
           [java.text SimpleDateFormat ParsePosition]
           java.util.Date
           org.joda.time.DateTime
           org.joda.time.base.AbstractInstant
           java.io.File           
           java.nio.ByteBuffer
           [java.lang.reflect InvocationTargetException ParameterizedType
            Method Modifier]

           ;; java.io.PrintWriter
           ;; java.io.StringWriter
           ;; java.math.BigDecimal
           ;; java.math.BigInteger



           )
  ;; (:require [clojure.algo.generic.functor :as algo]
  ;;           [clojure.string :as str]
  ;;           [buddy.core.certificates :as certs]
  ;;           [buddy.core.keys :as keys]
  ;;           [promissum.core :as promis])
  
  ;; (:import [org.hyperledger.fabric.sdk User Enrollment Peer Orderer]
  ;;          [org.bouncycastle.jcajce.provider.asymmetric.ec BCECPrivateKey])
  )

;;;
;;; Dynamic variables
;;; 
(defonce ^:dynamic ^:private *hf-client* nil)
(defonce ^:dynamic ^:private *channel* nil)
(defonce ^:dynamic ^:private *peers* nil)
(defonce ^:dynamic ^:private *orderers* nil)
(defonce ^:dynamic ^:private *proposal-request* nil)
(defonce ^:dynamic ^:private *proposal-response* nil)
(defonce ^:dynamic ^:private *order-request* nil)
(defonce ^:dynamic ^:private *order-response* nil)

;;;
;;;
;;;
(def ^:private root-unwrapping (atom false))

(defn set-root-unwrapping!
  "Enables JSON-like root unwrapping of singly keyed
  top level maps.
    {:root {:key 'foo' :name 'bar'}}
  would become
    {:key 'foo' :name 'bar'}"
  [b]
  (reset! root-unwrapping b))



(def ^:private date-format (atom "yyyy-MM-dd"))

(defn set-date-format!
  "Sets the java.text.SimpleDateFormat pattern to use
  for transparent coercion of Strings passed as
  arguments where java.util.Dates are required by the API."
  [df]
  (reset! date-format df))


;;;
;;; Client - manually construct
;;;
(defn- general-lru-cache-read [k lru-cache miss-hit-fn & fn-args]
    (let [cache-hit (swap! lru-cache cache/hit k)]
      (if-let [found (cache/lookup cache-hit k)]
        found
        (let [new-entry (apply miss-hit-fn fn-args)]
        (swap! lru-cache cache/miss k new-entry)
        new-entry))))

(defonce client-lru-cache
  ;; FIXME: magic number
  (atom (cache/lru-cache-factory {} :threshold 64)))

(defn make-hf-client [msp-id name priv-key cert {:keys [roles account affiliation]}]
  (let [client (HFClient/createNewInstance)]
    (.setCryptoSuite client (CryptoSuite$Factory/getCryptoSuite))
    (.setUserContext client (reify User
                               (getName [this] name)
                               ;; FIXME: Set<String>
                               (getRoles [this] roles)
                               (getAccount [this] account)
                               (getAffiliation [this] affiliation)
                               (getEnrollment [this]
                                 (reify Enrollment
                                   (getKey [this] priv-key)
                                   (getCert [this] cert)))
                               (getMspId [this] msp-id)))
    client))

(defn- priv-key+cert->hash [priv-key cert]
  (hash (str (.toString ^BCECPrivateKey priv-key) cert)))

(defn evict-client-from-cache [client]
  (let [enrollment (.getEnrollment ^User (.getUserContext ^HFClient client))
        cache-key (priv-key+cert->hash (.getKey ^Enrollment enrollment)
                                       (.getCert ^Enrollment enrollment))]
    (swap! client-lru-cache cache/evict cache-key)))

(defn get-or-make-client [msp-id name priv-key cert opts]
  (general-lru-cache-read (priv-key+cert->hash priv-key cert)
                          client-lru-cache
                          make-hf-client msp-id name priv-key cert opts))


;;;
;;;
;;;
(defonce ^:private client-config (atom {}))

(defonce ^:dynamic ^:private *credentials* nil)

(defonce ^:dynamic ^:private *client-config* nil)

(defonce ^:dynamic ^:private *client-class* nil)





(defn- keyword-converter
  "Given something that tokenizes a string into parts, turn it into
  a :kebab-case-keyword."
  [separator-regex]
  (fn [s]
    (->> (str/split s separator-regex)
         (map str/lower-case)
         (interpose \-)
         str/join
         keyword)))

(def ^:private camel->keyword
  "from Emerick, Grande, Carper 2012 p.70"
  (keyword-converter #"(?<=[a-z])(?=[A-Z])"))

(defn- keyword->camel
  [kw]
  (let [n (name kw)
        m (.replace n "?" "")]
    (->> (str/split m #"\-")
         (fmap str/capitalize)
         str/join)))

(defn- fabric-package?
  [^Class clazz]
  (->> (.getName clazz)
       (re-find #"org\.hyperledger\.fabric\.sdk")
       some?))

(defn to-date
  [date]
  (cond
    (instance? Date date) date
    (instance? AbstractInstant date) (.toDate ^AbstractInstant date)
    (integer? date) (Date. ^int date)
    true (.. (SimpleDateFormat. @date-format)
           (parse (str date) (ParsePosition. 0)))))

(defn to-file
  [file]
  (if (instance? File file)
    file
    (if (string? file)
      (File. ^String file))))

(defn to-enum
  "Case-insensitive resolution of Enum types by String."
  [type value]
  (some
   #(if-let [enum-str (.toString %)]
      (= (str/upper-case value) (str/upper-case enum-str))
      %)
    (-> type
        (.getDeclaredMethod "values" (make-array Class 0))
        (.invoke type (make-array Object 0)))))

; assoc java Class to Clojure cast functions
(defonce ^:private coercions
  (->> [:String :Integer :Long :Double :Float]
       (reduce
         (fn [m e]
           (let [arr (str "[Ljava.lang." (name e) ";")
                 clazz (Class/forName arr)]
             (assoc m clazz into-array))) {})
       (merge {
         String     str
         Integer    int
         Long       long
         Boolean    boolean
         Double     double
         Float      float
         BigDecimal bigdec
         BigInteger bigint
         Date       to-date
         File       to-file
         ByteBuffer #(-> % str .getBytes ByteBuffer/wrap)
         "int"      int
         "long"     long
         "double"   double
         "float"    float
         "boolean"  boolean})
       atom))

(defn register-coercions
  "Accepts key/value pairs of class/function, which defines
  how data will be converted to the appropriate type
  required by the Java method."
  [& {:as coercion}]
  (swap! coercions merge coercion))

(defn coerce-value
  "Coerces the supplied stringvalue to the required type as
  defined by the Java method signature. String or keyword
  conversion to Enum types (e.g. via valueOf()) is supported."
  [value type]
  (let [value (if (keyword? value) (name value) value)]
    (if-not (instance? type value)
      (if (= java.lang.Enum (.getSuperclass ^Class type))
        (to-enum type value)
        (if-let [coercion (@coercions (if (.isPrimitive ^Class type)
                                        (str type)
                                        type))]
          (coercion value)
          (throw (IllegalArgumentException.
                  (format "No coercion is available to turn %s into an object of type %s"
                          value type)))))
      value)))

(defn- default-value
  [class-name]
  (get
    {"boolean" false
     "double" (double 0.0)
     "float" (float 0.0)
     "long" 0
     "int" (int 0)}
    class-name))

(defn- unwind-types
  [depth param]
  (if (instance? ParameterizedType param)
      (let [f (partial unwind-types (inc depth))
            types (-> param .getActualTypeArguments)
            t (-> types last f)]
        (if (and (instance? ParameterizedType (last types))
                 (.contains (-> types last str) "java.util")
                 (.contains (-> types last str) "java.lang"))
          {:type [(-> types last .getRawType)]
           :depth depth}
          t))
      {:type [param]
       :depth depth}))

(defn- paramter-types
  [method]
  (let [types (seq (.getGenericParameterTypes method))
        param (last types)
        rval  {:generic types}]
    (if (instance? ParameterizedType param)
        (let [t (unwind-types 1 param)]
          (merge rval
                 {:actual (:type t)
                  :depth  (:depth t)}))
        rval)))

(defn- normalized-name
  [method-name]
  (-> (name method-name)
      (.replaceFirst
        (case (.substring method-name 0 3)
          "get" "get"
          "set" "set"
          "default")
      "")
      (.toLowerCase)))

(defn- matches?
  "We exclude any mutators of the bean which
   expect a java.util.Map$Entry as the first
   argument, as we won't be dealing in these
   from Clojure. Specifically, this is meant
   to address the various setKey() methods
   in the DynamoDBV2Client.
   http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/model/DeleteItemRequest.html#setKey(java.util.Map.Entry, java.util.Map.Entry)
   http://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/dynamodbv2/model/GetItemRequest.html#setKey(java.util.Map.Entry, java.util.Map.Entry)"
  [method name value]
  (let [args (.getParameterTypes method)]
    (and (= name (normalized-name (.getName method)))
         (if (empty? value)
             (= 0 (count args))
             (and (< 0 (count args))
                  (or (= (count args) (count (flatten value)))
                      (and (coll? value)
                           (= 1 (count args))
                           (or (contains? @coercions (first args))
                               (.isArray (first args))
                               (and (.getPackage (first args))
                                    (.startsWith
                                      (.getName (.getPackage (first args)))
                                      "java.util")))))
                  (not (and (= 2 (count args))
                            (every? (partial = java.util.Map$Entry)
                                    args))))))))

(defn- accessor-methods
  [class-methods name value]
  (reduce
    #(if (matches? %2 name value)
      (conj %1 %2)
      %1)
    []
    class-methods))

(defn- find-methods
  [pojo k & v]
  (-> (.getClass pojo)
      (.getMethods)
      (accessor-methods
        (.toLowerCase (keyword->camel k))
        v)))

(defn to-java-coll
  "Need this only because S3 methods actually try to
   mutate (e.g. sort) collections passed to them."
  [col]
  (cond
    (map? col)
      (doto
        (java.util.HashMap.)
        (.putAll col))
    (set? col)
      (java.util.HashSet. col)
    (or (list? col) (vector? col))
      (java.util.ArrayList. col)))

(defn- invoke
  [pojo method v]
  (.invoke method pojo
    (into-array
      ;; misbehaving S3Client mutates the coll
      (if (and (coll? v)
               (not (nil? *client-class*))
               (= "AmazonS3Client" (.getSimpleName *client-class*)))
        (if (and (> (count (.getParameterTypes method)) 1)
                 (sequential? v))
          (to-java-coll v)
          [(to-java-coll v)])
          [v])))
  true)

(defn kw->str [k]
  (if (keyword? k) (name k) k))

(defn- populate
  [types key props]
  (let [type (-> types key last)]
    (assert type (str "Bad data type - there is no " key " in " types))
    (if (contains? @coercions type)
      (coerce-value props type)
      (set-fields (new-instance type) props))))

(defn- unmarshall
  "Transform Clojure data to the required Java objects."
  [types col]
  (let [type (last (or (:actual types)
                       (:generic types)))
        pp   (partial populate types :actual)]
    (try
      (if (fabric-package? type)
       (if (map? col)
         (if (contains? types :actual)
           (if (< (:depth types) 3)
             (apply assoc {}
                    (interleave (fmap kw->str (apply vector (keys col)))
                                (fmap pp (apply vector (vals col)))))
             (apply assoc {}
                    (interleave (fmap kw->str (apply vector (keys col)))
                                [(fmap #(populate {:generic [type]}
                                                  :generic
                                                  %)
                                       (first (apply vector (vals col))))])))
           (populate types :generic col))
         (if (and (contains? types :actual)
                  (= (:depth types) 3))
           (fmap #(fmap pp %) col)
           (fmap pp col)))
       (if (and (contains? types :actual)
                (fabric-package? type))
         (fmap pp col)
         (fmap #(coerce-value % type) col)))
      (catch Throwable e
        (throw (RuntimeException. (str
                                    "Failed to create an instance of "
                                    (.getName type)
                                    " from " col
                                    " due to " e
                                    ". Make sure the data matches an existing constructor and setters.")))))))

(defn- invoke-method
  [pojo v method]
  (let [f       (partial invoke pojo method)
        types   (paramter-types method)
        generic (last (:generic types))]
    (if (and
          (coll? v)
          (not (contains? @coercions generic)))
      (f (unmarshall types v))
      (if (instance? generic v)
        (f v)
        (f (coerce-value v generic))))))

(defn set-fields
  "Returns the populated AWS *Request bean with 'args' as
   the values. args is a map with keywords as keys and any
   type values. Complex values will be recursively resolved
   to the corresponding method calls on the object graph."
  [pojo args]
  (doseq [[k v] args]
    (try
      (->> (find-methods pojo k v)
           (some (partial invoke-method pojo v)))
      (catch Throwable e
        (throw (ex-info
                 (str "Error setting " k ": " (.getMessage e) ". Perhaps the value isn't compatible with the setter?")
                 {:property k, :value v}
                 e)))))
  pojo)

(defn- create-bean
  [clazz args]
  (-> clazz new-instance (set-fields args)))

(defn- create-request-bean
  "Create a new instance of an AWS *Request style Java
   bean passed as the argument to a method call on the
   Amazon*Client class. (Note that we assume all AWS
   service calls take at most a single argument.)"
  [method args]
  (let [clazz (first (.getParameterTypes method))]
    (if (contains? @coercions clazz)
        (coerce-value (into {} args) clazz)
        (create-bean clazz args))))


(defprotocol IMarshall
  "Defines the contract for converting Java types to Clojure
  data. All return values from AWS service calls are
  marshalled. As such, the AWS service-specific namespaces
  will frequently need to implement this protocol in order
  to provide convenient data representations. See also the
  register-coercions function for coercing Clojure data to
  Java types."
  (marshall [obj]))

(defn- getter?
  [method]
  (let [name (.getName method)
        type (.getName (.getReturnType method))]
    (or (and
          (.startsWith name "get")
          (= 0 (count (.getParameterTypes method))))
        (and
          (.startsWith name "is")
          (= "boolean" type)))))

(defn accessors
  "Returns a vector of getters or setters for the class."
  [clazz getters?]
  (reduce
    #(if (or
           (and getters? (getter? %2))
           (and (not getters?)
                (.startsWith (.getName %2) "set")))
      (conj % %2)
      %)
    []
    (.getDeclaredMethods clazz)))

(defn- prop->name
  [method]
  (let [name (.getName method)]
    (if (.startsWith name "is")
      (str (.substring name 2) "?")
      (.substring name 3))))

(defn get-fields
  "Returns a map of all non-null values returned by
  invoking all public getters on the specified object."
  [obj]
  (let [no-arg (make-array Object 0)]
    (into {}
      (for [m (accessors (class obj) true)]
        (let [r (marshall (.invoke m obj no-arg))]
          (if-not (nil? r)
            (hash-map
              (camel->keyword (prop->name m))
              r)))))))

(extend-protocol IMarshall
  nil
  (marshall [obj] nil)

  java.util.Map
  (marshall [obj]
    (if-not (empty? obj)
      (apply assoc {}
        (interleave
          (fmap #(if (string? %) (keyword %) %)
                (apply vector (keys obj)))
          (fmap marshall
                (apply vector (vals obj)))))))

  java.util.Collection
  (marshall [obj]
    (if (instance? clojure.lang.IPersistentSet obj)
      obj
      (fmap marshall (apply vector obj))))

  java.util.Date
  (marshall [obj] (DateTime. (.getTime obj)))

  ; `false` boolean objects (i.e. (Boolean. false)) come out of e.g.
  ; .doesBucketExist, which wreak havoc on Clojure truthiness
  Boolean
  (marshall [obj] (when-not (nil? obj) (.booleanValue obj)))

  Object
  (marshall [obj]
    (if (fabric-package? (class obj))
        (get-fields obj)
        obj)))

(defn- use-aws-request-bean?
  [method args]
  (let [types (.getParameterTypes method)]
    (and (or (map? args) (< 1 (count args)))
         (< 0 (count types))
         (or (map? args)
             (and
                (or (and
                      (even? (count args))
                      (not= java.io.File (last types)))
                    (and
                      (odd? (count args))
                      (= java.io.File (last types)))) ; s3 getObject() support
                (some keyword? args)))
         (or (fabric-package? (first types))
             (and (fabric-package? (last types))
                  (not (< (count types) (count args))))))))

(defn- prepare-args
  [method args]
  (let [types (.getParameterTypes method)
        num   (count types)]
    (if (and (empty? args) (= 0 num))
      (into-array Object args)
      (if (= num (count args))
        (into-array Object
          (map #(if (and (fabric-package? %2) (seq (.getConstructors %2)))
                  ; must be a concrete, instantiatable class
                  (if (contains? @coercions %2)
                      (coerce-value % %2)
                      (create-bean %2 %))
                  (coerce-value % %2))
               (vec args)
               types))
        (if (use-aws-request-bean? method args)
          (cond
            (= 1 num)
            (into-array Object
                        [(create-request-bean
                            method
                            (seq (apply hash-map args)))])
            (and (fabric-package? (first types))
                 (= 2 num)
                 (= File (last types)))
            (into-array Object
                        [(create-request-bean
                            method
                            (seq (apply hash-map (butlast args))))
                         (last args)])))))))


(defn- args-from
  "Function arguments take an optional first parameter map
  of AWS credentials. Addtional parameters are either a map,
  or seq of keys and values."
  [arg-seq]
  (let [args (first arg-seq)]
    (cond
      (or (and (or (map? args)
                   (map? (first args)))
               (or (contains? (first args) :access-key)
                   (contains? (first args) :endpoint)
                   (contains? (first args) :profile)
                   (contains? (first args) :client-config)))
          (instance? AWSCredentialsProvider (first args))
          (instance? AWSCredentials (first args)))
      {:args (if (-> args rest first map?)
                 (if (-> args rest first empty?)
                     {}
                     (mapcat identity (-> args rest args-from :args)))
                 (rest args))
       :credential (if (map? (first args))
                       (dissoc (first args) :client-config)
                       (first args))
       :client-config (:client-config (first args))}
      (map? (first args))
      {:args (let [m (mapcat identity (first args))]
               (if (seq m) m {}))}
      :default {:args args})))

(swap! client-config assoc :transfer-manager-fn (memoize transfer-manager*))

(defn candidate-client
  [clazz args]
  (let [cred-bound (or *credentials* (:credential args))
        credential (if (map? cred-bound)
                             (merge @credential cred-bound)
                             (or cred-bound @credential))
        config-bound (or *client-config* (:client-config args))
        client-config (merge @client-config config-bound)
        encryption-client (:encryption-client-fn client-config)
        amazon-client (:amazon-client-fn client-config)
        transfer-manager (:transfer-manager-fn client-config)
        crypto (if (even? (count (:args args)))
                   (:encryption (apply hash-map (:args args))))
        client  (if (and crypto (or (= (.getSimpleName clazz) "AmazonS3Client")
                                    (= (.getSimpleName clazz) "TransferManager")))
                    (delay (encryption-client crypto credential client-config))
                    (delay (amazon-client clazz credential client-config)))]
        (if (= (.getSimpleName clazz) "TransferManager")
            (transfer-manager credential client-config crypto)
            @client)))


(defn- fn-call
  "Returns a function that reflectively invokes method on
   clazz with supplied args (if any). The 'method' here is
   the Java method on the Amazon*Client class."
  [clazz method & arg]
  (binding [*client-class* clazz]
    (let [args    (args-from arg)
          arg-arr (prepare-args method (:args args))
          client  (delay (candidate-client clazz args))]
      (fn []
        (try
          (let [java (.invoke method @client arg-arr)
                cloj (marshall java)]
            (if (and
                  @root-unwrapping
                  (map? cloj)
                  (= 1 (count (keys cloj))))
              (-> cloj first second)
              cloj))
          (catch InvocationTargetException ite
            (throw (.getTargetException ite))))))))

(defn- types-match-args
  [args method]
  (let [types (.getParameterTypes method)]
    (if (and (= (count types) (count args))
             (every? identity (map instance? types args)))
        method)))

(defn- coercible? [type]
  (and (contains? @coercions type)
       (not (re-find #"java\.lang" (str type)))))

(defn- choose-from [possible]
  (if (= 1 (count possible))
      (first possible)
      (first
        (sort-by
          (fn [method]
            (let [types (.getParameterTypes method)]
              (cond
                (some coercible? types) 1
                (some #(= java.lang.Enum (.getSuperclass %)) types) 2
                :else 3)))
          possible))))

(defn possible-methods
  [methods args]
  (filter
    (fn [method]
      (let [types (.getParameterTypes method)
            num   (count types)]
        (if (or
              (and (empty? args) (= 0 num))
              (use-aws-request-bean? method args)
              (and
                (= num (count args))
                (not (keyword? (first args)))
                (not (fabric-package? (first types)))))
          method
          false)))
    methods))

(defn- best-method
  "Finds the appropriate method to invoke in cases where
  the Amazon*Client has overloaded methods by arity or type."
  [methods & arg]
  (let [args (:args (args-from arg))]
    (or (some (partial types-match-args args) methods)
        (choose-from (possible-methods methods args)))))

(defn- clojure-case
  "Similar to \"kabob case\" but the returned string is suitable for
  reading a single symbol with `read-string`."
  [string]
  (-> string
      ;; Replace the space between a non-upper-case letter and an
      ;; upper-case letter with a dash.
      (str/replace #"(?<=[^A-Z])(?=[A-Z])" "-")
      ;; Remove anything that a Clojure reader would not accept in a
      ;; symbol.
      (str/replace #"[\\'\"\[\]\(\){}\s]" "")
      (str/lower-case)))

(defn- type-clojure-name
  "Given a `java.lang.Class` return it's name in kabob case"
  [type]
  (let [type-name (last (.. type getName (split "\\.")))
        type-name (if-let [;; Check for a type name like "[C" etc. 
                           [_ name] (re-matches #"\[([A-Z]+)$" type-name)]
                    name
                    type-name)]
    (clojure-case type-name)))

(defn- parameter-clojure-name
  "Given a `java.lang.reflect.Parameter` return it's name in kabob
  case."
  [parameter]
  (if (. parameter isNamePresent)
    (clojure-case (. parameter getName))
    ;; The name will be synthesized so instead we'll derive
    ;; it from it's type.
    (type-clojure-name (. parameter getType))))

(def ^{:arglists '([method])
       :private true}
  parameter-names
  "Given a `java.lang.reflect.Method` return a list of it's parameter
  names."
  ;; The regular expression here will only match against version
  ;; numbers that are 1.7.X and below.
  (if (re-matches #"[^2-9]\.[1-7]\..+" (System/getProperty "java.version")) 
    ;; Java 1.7 and below.
    (fn [method]
      (map type-clojure-name (. method getParameterTypes)))
    ;; Java 1.8 and above.
    (fn [method]
      (map parameter-clojure-name (. method getParameters)))))

(defn- method-arglist
  "Derives a Clojure `:arglist` vector from a
  `java.lang.reflect.Method`."
  [method]
  (let [names (parameter-names method)
        ;; This will help determine when parameter names should be
        ;; suffixed with an index i.e. `parameter-1`. Suffixing is
        ;; necessary when parameter names are synthesized from their
        ;; type names and the likelihood duplicates is high.
        name-frequency (frequencies names)]
    (loop [names names
           ;; This map keeps track of the index of names when they
           ;; appear more than once.
           name-index {}
           arglist []]
      (if (empty? names)
        arglist
        (let [[name & names*] names]
          (if (= (name-frequency name) 1)
            (let [arg-symbol (symbol name)
                  arglist* (conj arglist arg-symbol)]
              (recur names*
                     name-index
                     arglist*))
            ;; The parameter name appears more than once so we need to
            ;; attach an index to it and update our name-index for the
            ;; next parameter with the same name.
            (let [index (get name-index name 1)
                  name-index* (assoc name-index name (inc index))
                  arg-symbol (symbol (str name "-" index))
                  arglist* (conj arglist arg-symbol)]
              (recur names*
                     name-index*
                     arglist*))))))))

(defn intern-function
  "Interns into ns, the symbol mapped to a Clojure function
   derived from the java.lang.reflect.Method(s). Overloaded
   methods will yield a variadic Clojure function."
  [client ns fname methods]
  (intern ns (with-meta (symbol (name fname))
               {:amazonica/client client
                :amazonica/methods methods
                :arglists (sort (map method-arglist methods))})
    (fn [& args]
      (if-let [method (best-method methods args)]
        (if-not args
          ((fn-call client method))
          ((fn-call client method args)))
        (throw (IllegalArgumentException.
                 (format "Could not determine best method to invoke for %s using arguments %s"
                         (name fname) args)))))))

(defn- client-methods
  "Returns a map with keys of idiomatic Clojure hyphenated keywords
  corresponding to the public Java method names of the class argument, vals are
  vectors of java.lang.reflect.Methods."
  [client]
  (->> (.getDeclaredMethods client)
       (remove (fn [method]
                 (let [mods (.getModifiers method)]
                   (or (.isSynthetic method)
                       (Modifier/isPrivate mods)
                       (Modifier/isProtected mods)
                       (Modifier/isStatic mods)))))
       (group-by #(camel->keyword (.getName %)))))

(defn- show-functions [ns]
  (intern ns (symbol "show-functions")
    (fn []
      (->> (ns-publics ns)
           sort
           (map (comp println first))))))

(defn set-client
  "Intern into the specified namespace all public methods
   from the Amazon*Client class as Clojure functions."
  [client ns]
  (show-functions ns)
  (intern ns 'client-class client)
  (doseq [[fname methods] (client-methods client)
          :let [the-var (intern-function client ns fname methods)
                fname2 (-> methods first .getName camel->keyword)]]
    (when (not= fname fname2)
      (let [the-var2 (intern-function client ns fname2 methods)]
        (alter-meta! the-var assoc :amazonica/deprecated-in-favor-of the-var2)))))
#_
(comment
  ;; Copy from fabcar example

  ;; 1. Make a client
  (defonce cli
    (get-or-make-client "Org1MSP"
                        "PeerAdmin"
                        (-> (slurp "resources/creds/cd96d5260ad4757551ed4a5a991e62130f8008a0bf996e4e4b84cd097a747fec-priv")
                            (keys/str->private-key))
                        "-----BEGIN CERTIFICATE-----\nMIICGDCCAb+gAwIBAgIQFSxnLAGsu04zrFkAEwzn6zAKBggqhkjOPQQDAjBzMQsw\nCQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZy\nYW5jaXNjbzEZMBcGA1UEChMQb3JnMS5leGFtcGxlLmNvbTEcMBoGA1UEAxMTY2Eu\nb3JnMS5leGFtcGxlLmNvbTAeFw0xNzA4MzEwOTE0MzJaFw0yNzA4MjkwOTE0MzJa\nMFsxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1T\nYW4gRnJhbmNpc2NvMR8wHQYDVQQDDBZBZG1pbkBvcmcxLmV4YW1wbGUuY29tMFkw\nEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEV1dfmKxsFKWo7o6DNBIaIVebCCPAM9C/\nsLBt4pJRre9pWE987DjXZoZ3glc4+DoPMtTmBRqbPVwYcUvpbYY8p6NNMEswDgYD\nVR0PAQH/BAQDAgeAMAwGA1UdEwEB/wQCMAAwKwYDVR0jBCQwIoAgQjmqDc122u64\nugzacBhR0UUE0xqtGy3d26xqVzZeSXwwCgYIKoZIzj0EAwIDRwAwRAIgXMy26AEU\n/GUMPfCMs/nQjQME1ZxBHAYZtKEuRR361JsCIEg9BOZdIoioRivJC+ZUzvJUnkXu\no2HkWiuxLsibGxtE\n-----END CERTIFICATE-----\n"
                        {}))
  ;; 2. Add (channel), orderer, and peer
  (add-channel-end cli "mychannel" (map->OrdererOpts {:name "orderer" :grpc-url "grpc://localhost:7050"}))
  (add-channel-end cli "mychannel" (map->PeerOpts {:name "peer" :grpc-url "grpc://localhost:7051"}))

  ;; FIXME: need a way to get a peer ...
  (hf-client/query-installed-chaincodes cli (first (get-channel-ends (map->PeerOpts {:name "peer" :grpc-url "grpc://localhost:7051"})
                                                                     (get-or-make-channel cli "mychannel"))))

  ;; FIXME: init for query-by-chaincode
  (channel/initialize (get-or-make-channel cli "mychannel"))

  (let [proposal-req (hf-client/new-query-proposal-request cli)
        channel (get-or-make-channel cli "mychannel")]
    (->> (map->TransactionOpts {:fcn "queryAllCars"
                                :args nil
                                :proposal-wait-time 10000})
         ;; 3-1. Proposal
         (prepare-chaincode-proposal-tx-req channel
                                            proposal-req
                                            (make-ChaincodeOpts {:name "fabcar" :version "1.0" :path "github.com/fabcar"}))
         ;; Send Tx proposal to peers
         (channel/query-by-chaincode channel)
         ;; 3-2 Order
         (order-transaction cli "mychannel")))
  
  (channel/query-by-chaincode (get-or-make-channel cli "mychannel")
                              (prepare-chaincode-proposal-tx-req cli "mychannel" )
                              (query-by-chaincode-request/new-instance (hf-client/get-user-context cli)))
  
  
  ;; 3. Tx
  (let [tx-future (->> (map->TransactionOpts {:fcn "createCar"
                                              ;; FIXME: marshall/unmarshall
                                              :args (java.util.ArrayList. ["CAR10" "Chevy" "Volt" "Red" "Nick"])
                                              :proposal-wait-time 10000})
                       ;; 3-1. Proposal
                       (prepare-chaincode-proposal-tx-req cli
                                                "mychannel"
                                                (make-ChaincodeOpts {:name "fabcar" :version "1.0" :path "github.com/fabcar"}))
                       
                       ;; Send Tx proposal to peers
                       (apply channel/send-transaction-proposal channel req)
                       ;; 3-2 Order
                       (order-transaction cli "mychannel"))]
    ;; 4. Get Tx result 
    (get-order-transaction-result tx-future #(println "OK" %) #(println "ERROR" %)))
  
  )
