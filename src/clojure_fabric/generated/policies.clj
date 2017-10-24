(ns clojure-fabric.policies
  (:import org.hyperledger.fabric.protos.common.Policies))

;;;---------------------------------------------------
;;;org.hyperledger.fabric.protos.common.Policies$Policy
;;;

;;;  getType
(defn
 get-type
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy]
 (. policies$-policy getType))

;;;  getValue
(defn
 get-value
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy]
 (. policies$-policy getValue))

;;;  parseFrom
(defn
 parse-from
 ([generic-arg1]
  (cond
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^java.io.InputStream
    generic-arg1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    
    generic-arg1)))
 ([generic-arg1
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (cond
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^java.io.InputStream
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1
    extension-registry-lite1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1
    extension-registry-lite1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$Policy/parseFrom
    
    generic-arg1
    extension-registry-lite1))))

;;;  newBuilderForType
(defn
 new-builder-for-type
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy]
 (. policies$-policy newBuilderForType))

;;;  isInitialized
(defn
 is-initialized
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy]
 (. policies$-policy isInitialized))

;;;  getParserForType
(defn
 get-parser-for-type
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy]
 (. policies$-policy getParserForType))

;;;  equals
(defn
 equals
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy
  ^java.lang.Object
  object0]
 (. policies$-policy equals object0))

;;;  hashCode
(defn
 hash-code
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy]
 (. policies$-policy hashCode))

;;;  parser
(defn
 parser
 []
 (org.hyperledger.fabric.protos.common.Policies$Policy/parser))

;;;  parseDelimitedFrom
(defn
 parse-delimited-from
 ([^java.io.InputStream input-stream0]
  (org.hyperledger.fabric.protos.common.Policies$Policy/parseDelimitedFrom
   input-stream0))
 ([^java.io.InputStream
   input-stream0
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (org.hyperledger.fabric.protos.common.Policies$Policy/parseDelimitedFrom
   input-stream0
   extension-registry-lite1)))

;;;  newBuilder
(defn
 new-builder
 ([] (org.hyperledger.fabric.protos.common.Policies$Policy/newBuilder))
 ([^org.hyperledger.fabric.protos.common.Policies$Policy
   policies$-policy0]
  (org.hyperledger.fabric.protos.common.Policies$Policy/newBuilder
   policies$-policy0)))

;;;  getDefaultInstance
(defn
 get-default-instance
 []
 (org.hyperledger.fabric.protos.common.Policies$Policy/getDefaultInstance))

;;;  getDefaultInstanceForType
(defn
 get-default-instance-for-type
 [^org.hyperledger.fabric.protos.common.Policies$Policy
  policies$-policy]
 (. policies$-policy getDefaultInstanceForType))


;;;---------------------------------------------------
;;;org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
;;;

;;;  hasRule
(defn
 has-rule
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope hasRule))

;;;  getIdentitiesOrBuilderList
(defn
 get-identities-or-builder-list
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getIdentitiesOrBuilderList))

;;;  getIdentitiesOrBuilder
(defn
 get-identities-or-builder
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope
  
  int0]
 (. policies$-signature-policy-envelope getIdentitiesOrBuilder int0))

;;;  getVersion
(defn
 get-version
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getVersion))

;;;  getIdentities
(defn
 get-identities
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope
  
  int0]
 (. policies$-signature-policy-envelope getIdentities int0))

;;;  parseFrom
(defn
 parse-from
 ([generic-arg1
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (cond
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^java.io.InputStream
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1
    extension-registry-lite1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1
    extension-registry-lite1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    
    generic-arg1
    extension-registry-lite1)))
 ([generic-arg1]
  (cond
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^java.io.InputStream
    generic-arg1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseFrom
    
    generic-arg1))))

;;;  newBuilderForType
(defn
 new-builder-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope newBuilderForType))

;;;  isInitialized
(defn
 is-initialized
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope isInitialized))

;;;  getParserForType
(defn
 get-parser-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getParserForType))

;;;  equals
(defn
 equals
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope
  ^java.lang.Object
  object0]
 (. policies$-signature-policy-envelope equals object0))

;;;  getRuleOrBuilder
(defn
 get-rule-or-builder
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getRuleOrBuilder))

;;;  getIdentitiesCount
(defn
 get-identities-count
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getIdentitiesCount))

;;;  hashCode
(defn
 hash-code
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope hashCode))

;;;  parser
(defn
 parser
 []
 (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parser))

;;;  getIdentitiesList
(defn
 get-identities-list
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getIdentitiesList))

;;;  parseDelimitedFrom
(defn
 parse-delimited-from
 ([^java.io.InputStream input-stream0]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseDelimitedFrom
   input-stream0))
 ([^java.io.InputStream
   input-stream0
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/parseDelimitedFrom
   input-stream0
   extension-registry-lite1)))

;;;  getRule
(defn
 get-rule
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getRule))

;;;  newBuilder
(defn
 new-builder
 ([^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
   policies$-signature-policy-envelope0]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/newBuilder
   policies$-signature-policy-envelope0))
 ([]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/newBuilder)))

;;;  getDefaultInstance
(defn
 get-default-instance
 []
 (org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope/getDefaultInstance))

;;;  getDefaultInstanceForType
(defn
 get-default-instance-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicyEnvelope
  policies$-signature-policy-envelope]
 (. policies$-signature-policy-envelope getDefaultInstanceForType))


;;;---------------------------------------------------
;;;org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
;;;

;;;  getSignedBy
(defn
 get-signed-by
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy getSignedBy))

;;;  hasNOutOf
(defn
 has-n-out-of
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy hasNOutOf))

;;;  getTypeCase
(defn
 get-type-case
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy getTypeCase))

;;;  getNOutOf
(defn
 get-n-out-of
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy getNOutOf))

;;;  parseFrom
(defn
 parse-from
 ([generic-arg1
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (cond
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^java.io.InputStream
    generic-arg1
    extension-registry-lite1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1
    extension-registry-lite1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    
    generic-arg1
    extension-registry-lite1)))
 ([generic-arg1]
  (cond
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^java.io.InputStream
    generic-arg1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseFrom
    
    generic-arg1))))

;;;  newBuilderForType
(defn
 new-builder-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy newBuilderForType))

;;;  isInitialized
(defn
 is-initialized
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy isInitialized))

;;;  getParserForType
(defn
 get-parser-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy getParserForType))

;;;  equals
(defn
 equals
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy
  ^java.lang.Object
  object0]
 (. policies$-signature-policy equals object0))

;;;  hashCode
(defn
 hash-code
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy hashCode))

;;;  parser
(defn
 parser
 []
 (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parser))

;;;  parseDelimitedFrom
(defn
 parse-delimited-from
 ([^java.io.InputStream input-stream0]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseDelimitedFrom
   input-stream0))
 ([^java.io.InputStream
   input-stream0
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/parseDelimitedFrom
   input-stream0
   extension-registry-lite1)))

;;;  newBuilder
(defn
 new-builder
 ([]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/newBuilder))
 ([^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
   policies$-signature-policy0]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/newBuilder
   policies$-signature-policy0)))

;;;  getDefaultInstance
(defn
 get-default-instance
 []
 (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy/getDefaultInstance))

;;;  getDefaultInstanceForType
(defn
 get-default-instance-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy getDefaultInstanceForType))

;;;  getNOutOfOrBuilder
(defn
 get-n-out-of-or-builder
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy
  policies$-signature-policy]
 (. policies$-signature-policy getNOutOfOrBuilder))


;;;---------------------------------------------------
;;;org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
;;;

;;;  getRulesCount
(defn
 get-rules-count
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of getRulesCount))

;;;  getRulesOrBuilder
(defn
 get-rules-or-builder
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of
  
  int0]
 (. policies$-signature-policy$-n-out-of getRulesOrBuilder int0))

;;;  getRulesList
(defn
 get-rules-list
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of getRulesList))

;;;  getRulesOrBuilderList
(defn
 get-rules-or-builder-list
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of getRulesOrBuilderList))

;;;  parseFrom
(defn
 parse-from
 ([generic-arg1]
  (cond
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^java.io.InputStream
    generic-arg1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    
    generic-arg1)))
 ([generic-arg1
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (cond
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1
    extension-registry-lite1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^java.io.InputStream
    generic-arg1
    extension-registry-lite1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1
    extension-registry-lite1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseFrom
    
    generic-arg1
    extension-registry-lite1))))

;;;  newBuilderForType
(defn
 new-builder-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of newBuilderForType))

;;;  isInitialized
(defn
 is-initialized
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of isInitialized))

;;;  getParserForType
(defn
 get-parser-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of getParserForType))

;;;  equals
(defn
 equals
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of
  ^java.lang.Object
  object0]
 (. policies$-signature-policy$-n-out-of equals object0))

;;;  hashCode
(defn
 hash-code
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of hashCode))

;;;  parser
(defn
 parser
 []
 (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parser))

;;;  parseDelimitedFrom
(defn
 parse-delimited-from
 ([^java.io.InputStream
   input-stream0
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseDelimitedFrom
   input-stream0
   extension-registry-lite1))
 ([^java.io.InputStream input-stream0]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/parseDelimitedFrom
   input-stream0)))

;;;  getRules
(defn
 get-rules
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of
  
  int0]
 (. policies$-signature-policy$-n-out-of getRules int0))

;;;  newBuilder
(defn
 new-builder
 ([^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
   policies$-signature-policy$-n-out-of0]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/newBuilder
   policies$-signature-policy$-n-out-of0))
 ([]
  (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/newBuilder)))

;;;  getDefaultInstance
(defn
 get-default-instance
 []
 (org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf/getDefaultInstance))

;;;  getDefaultInstanceForType
(defn
 get-default-instance-for-type
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of getDefaultInstanceForType))

;;;  getN
(defn
 get-n
 [^org.hyperledger.fabric.protos.common.Policies$SignaturePolicy$NOutOf
  policies$-signature-policy$-n-out-of]
 (. policies$-signature-policy$-n-out-of getN))


;;;---------------------------------------------------
;;;org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
;;;

;;;  getSubPolicyBytes
(defn
 get-sub-policy-bytes
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy getSubPolicyBytes))

;;;  getSubPolicy
(defn
 get-sub-policy
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy getSubPolicy))

;;;  parseFrom
(defn
 parse-from
 ([generic-arg1
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (cond
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^java.io.InputStream
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1
    extension-registry-lite1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1
    extension-registry-lite1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    
    generic-arg1
    extension-registry-lite1)))
 ([generic-arg1]
  (cond
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    ^java.io.InputStream
    generic-arg1)
   :else
   (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseFrom
    
    generic-arg1))))

;;;  newBuilderForType
(defn
 new-builder-for-type
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy newBuilderForType))

;;;  isInitialized
(defn
 is-initialized
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy isInitialized))

;;;  getParserForType
(defn
 get-parser-for-type
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy getParserForType))

;;;  equals
(defn
 equals
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy
  ^java.lang.Object
  object0]
 (. policies$-implicit-meta-policy equals object0))

;;;  hashCode
(defn
 hash-code
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy hashCode))

;;;  parser
(defn
 parser
 []
 (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parser))

;;;  parseDelimitedFrom
(defn
 parse-delimited-from
 ([^java.io.InputStream
   input-stream0
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseDelimitedFrom
   input-stream0
   extension-registry-lite1))
 ([^java.io.InputStream input-stream0]
  (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/parseDelimitedFrom
   input-stream0)))

;;;  getRule
(defn
 get-rule
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy getRule))

;;;  newBuilder
(defn
 new-builder
 ([^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
   policies$-implicit-meta-policy0]
  (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/newBuilder
   policies$-implicit-meta-policy0))
 ([]
  (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/newBuilder)))

;;;  getDefaultInstance
(defn
 get-default-instance
 []
 (org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy/getDefaultInstance))

;;;  getDefaultInstanceForType
(defn
 get-default-instance-for-type
 [^org.hyperledger.fabric.protos.common.Policies$ImplicitMetaPolicy
  policies$-implicit-meta-policy]
 (. policies$-implicit-meta-policy getDefaultInstanceForType))
