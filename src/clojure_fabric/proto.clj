;; Copyright 2017 Jong-won Choi <oz.jongwon.choi@gmail.com>
;;
;; Licensed under the Apache License, Version 2.0 (the "License")
;; you may not use this file except in compliance with the License.
;; You may obtain a copy of the License at
;;
;;     http://www.apache.org/licenses/LICENSE-2.0
;;
;; Unless required by applicable law or agreed to in writing, software
;; distributed under the License is distributed on an "AS IS" BASIS,
;; WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
;; See the License for the specific language governing permissions and
;; limitations under the License.

(ns clojure-fabric.proto
  (:require [clojure-fabric.core :as core]
            [clojure-fabric.crypto-suite :as crypto-suite]
            [clojure-fabric.utils :as utils]
            [clojure.core.async :as async])
  (:import [clojure_fabric.core Channel Orderer Peer]
           [com.google.protobuf ByteString ByteString$LiteralByteString Timestamp]
           io.grpc.ManagedChannel
           [io.grpc.netty GrpcSslContexts NegotiationType NettyChannelBuilder]
           io.grpc.stub.StreamObserver
           io.netty.handler.ssl.SslProvider
           io.netty.handler.ssl.util.InsecureTrustManagerFactory
           org.bouncycastle.util.encoders.Hex
           [org.hyperledger.fabric.protos.common Common$Block Common$BlockData Common$BlockHeader Common$BlockMetadata Common$ChannelHeader
            Common$Envelope Common$Header Common$HeaderType Common$Payload Common$SignatureHeader Configtx$ConfigGroup Configtx$ConfigPolicy
            Configtx$ConfigSignature Configtx$ConfigUpdate Configtx$ConfigUpdateEnvelope Configtx$ConfigValue Ledger$BlockchainInfo
            Policies$Policy Policies$SignaturePolicyEnvelope Policies$SignaturePolicy Policies$SignaturePolicy$NOutOf
            MspPrincipal$MSPPrincipal MspPrincipal$MSPPrincipal$Classification MspPrincipal$MSPRole MspPrincipal$MSPRole$MSPRoleType]
           org.hyperledger.fabric.protos.msp.Identities$SerializedIdentity
           [org.hyperledger.fabric.protos.orderer Ab$SeekInfo Ab$SeekInfo$SeekBehavior Ab$SeekNewest Ab$SeekOldest Ab$SeekPosition Ab$SeekSpecified AtomicBroadcastGrpc AtomicBroadcastGrpc$AtomicBroadcastStub]
           [org.hyperledger.fabric.protos.peer Chaincode$ChaincodeDeploymentSpec Chaincode$ChaincodeDeploymentSpec$ExecutionEnvironment Chaincode$ChaincodeID Chaincode$ChaincodeInput Chaincode$ChaincodeInvocationSpec Chaincode$ChaincodeSpec Chaincode$ChaincodeSpec$Type ChaincodeEventPackage$ChaincodeEvent EndorserGrpc ProposalPackage$ChaincodeAction ProposalPackage$ChaincodeHeaderExtension ProposalPackage$ChaincodeProposalPayload ProposalPackage$Proposal ProposalPackage$SignedProposal ProposalResponsePackage$Endorsement ProposalResponsePackage$ProposalResponse ProposalResponsePackage$ProposalResponsePayload ProposalResponsePackage$Response Query$ChaincodeInfo Query$ChaincodeQueryResponse Query$ChannelInfo Query$ChannelQueryResponse TransactionPackage$ChaincodeActionPayload TransactionPackage$ChaincodeEndorsedAction TransactionPackage$ProcessedTransaction TransactionPackage$Transaction TransactionPackage$TransactionAction]))

;;;
;;;
(defprotocol ICljToProto
  (clj->proto [this]))

(defprotocol IProtoToClj
  (proto->clj [this opts]))

;;;
;;; Records are one to one relationship with proto message definitions
;;; Think about how to generate them
;;;
(defrecord ChaincodeID [^String name ^String version ^String path]
  ICljToProto
  (clj->proto [this]
    (cond-> (Chaincode$ChaincodeID/newBuilder)
      name (.setName name)
      version (.setVersion version)
      path (.setPath path)
      true (.build))))

(defn make-chaincode-id
  [& {:keys [name version path]}]
  (map->ChaincodeID {:name name :version version :path path}))

(declare convert-args)
(defrecord ChaincodeInput [^Iterable args ^java.util.Map decorations]
  ICljToProto
  (clj->proto [this]
    ;; Iterable - repeated
    ;; map has putAll* method
    (cond-> (Chaincode$ChaincodeInput/newBuilder)
      args (.addAllArgs (convert-args args))
      decorations (.putAllDecorations decorations)
      true (.build))))

(defn make-chaincode-input
  ;; args = fn + fn-args
  [& {:keys [args decorations]}]
  (map->ChaincodeInput {:args args :decorations decorations}))

(defonce lang-types {:undefined Chaincode$ChaincodeSpec$Type/UNDEFINED
                     :golang Chaincode$ChaincodeSpec$Type/GOLANG
                     :node Chaincode$ChaincodeSpec$Type/NODE
                     :car Chaincode$ChaincodeSpec$Type/CAR
                     :java Chaincode$ChaincodeSpec$Type/JAVA})

(defrecord ChaincodeSpec [^clojure.lang.Keyword type
                          ^ChaincodeID chaincode-id
                          ^ChaincodeInput chaincode-input
                          ^Long timeout]
  ICljToProto
  (clj->proto [this]
    (cond-> (Chaincode$ChaincodeSpec/newBuilder)
      type (.setType (lang-types type))
      chaincode-id (.setChaincodeId ^Chaincode$ChaincodeID (clj->proto chaincode-id))
      chaincode-input (.setInput ^Chaincode$ChaincodeInput (clj->proto chaincode-input))
      timeout (.setTimeout ^Timestamp timeout)
      true (.build))))

(defn make-chaincode-spec
  [& {:keys [type chaincode-id chaincode-input timeout] :or {type :golang timeout 2000}}]
  (map->ChaincodeSpec {:type type :chaincode-id chaincode-id :timeout timeout
                       :chaincode-input chaincode-input}))

(defrecord ChaincodeInvocationSpec [^ChaincodeSpec chaincode-spec
                                    ^String id-generation-alg]
  ICljToProto
  (clj->proto [this]
    (cond-> (Chaincode$ChaincodeInvocationSpec/newBuilder)
       chaincode-spec (.setChaincodeSpec ^Chaincode$ChaincodeSpec (clj->proto chaincode-spec))
       id-generation-alg (.setIdGenerationAlg id-generation-alg)
       true (.build))))

(defn make-chaincode-invocation-spec
  [& {:keys [chaincode-spec id-generation-alg]}]
  (map->ChaincodeInvocationSpec {:chaincode-spec chaincode-spec
                                           :id-generation-alg id-generation-alg}))

(defrecord ChaincodeHeaderExtension [payload-visibility ;; FIXME: type not sure
                                     ^ChaincodeID chaincode-id]
  ICljToProto
  (clj->proto [this]
    (cond-> (ProposalPackage$ChaincodeHeaderExtension/newBuilder)
      chaincode-id (.setChaincodeId ^Chaincode$ChaincodeID (clj->proto chaincode-id))
      payload-visibility (.setPayloadVisibility payload-visibility)
      true (.build))))

(defn make-chaincode-header-extension
  [& {:keys [payload-visibility chaincode-id]}]
  (map->ChaincodeHeaderExtension {:payload-visibility payload-visibility
                                  :chaincode-id chaincode-id}))

(defrecord ProtoTimestamp [^Long seconds ^Long nanos]
  ICljToProto
  (clj->proto [this]
    (cond-> (Timestamp/newBuilder)
      seconds (.setSeconds seconds)
      nanos (.setNanos nanos)
      true (.build))))

(defn make-timestamp
  ([]
   (make-timestamp (System/currentTimeMillis)))
  ([ts]
   (map->ProtoTimestamp {:seconds (quot ts 1000) :nanos (-> (rem ts 1000) (* 1000000))})))

(defonce header-types {:message                 Common$HeaderType/MESSAGE_VALUE
                       :config                  Common$HeaderType/CONFIG_VALUE
                       :config-update           Common$HeaderType/CONFIG_UPDATE_VALUE
                       :endorser-transaction    Common$HeaderType/ENDORSER_TRANSACTION_VALUE
                       :orderer-transaction     Common$HeaderType/ORDERER_TRANSACTION_VALUE
                       :deliver-seek-info       Common$HeaderType/DELIVER_SEEK_INFO_VALUE
                       :chaincode-package       Common$HeaderType/CHAINCODE_PACKAGE_VALUE})

(defrecord ChannelHeader [^Long type ^Long version ^Long timestamp ^String channel-id
                          ^String tx-id ^Long epoch extension]
  ICljToProto
  (clj->proto [this]
    (cond-> (Common$ChannelHeader/newBuilder)
      type (.setType (header-types type))
      version (.setVersion version)
      timestamp (.setTimestamp ^Timestamp (clj->proto (make-timestamp timestamp)))
      channel-id (.setChannelId channel-id)
      tx-id (.setTxId tx-id)
      epoch (.setEpoch epoch)
      extension (.setExtension (.toByteString ^ProposalPackage$ChaincodeHeaderExtension
                                              (clj->proto extension)))
      true (.build))))

(defn make-channel-header
  [& {:keys [type version timestamp channel-id tx-id epoch extension]
      :or {type :endorser-transaction version 1 timestamp (System/currentTimeMillis) epoch 0}}]
  (map->ChannelHeader {:type type :version version :timestamp timestamp :epoch epoch
                       :channel-id channel-id :tx-id tx-id :extension extension}))

;; FIXME: id-bytes type
(defrecord SerializedIdentity [^String mspid ^String id-bytes] ;; mspid and certificate
  ICljToProto
  (clj->proto [this]
    (cond-> (Identities$SerializedIdentity/newBuilder)
      mspid (.setMspid mspid)
      id-bytes (.setIdBytes (ByteString/copyFromUtf8 id-bytes))
      true (.build))))

(defn make-serialized-identity
  [& {:keys [mspid id-bytes]}]
  (map->SerializedIdentity {:mspid mspid :id-bytes id-bytes}))

(defn user->serialized-identity
  [user]
  (make-serialized-identity :mspid (:msp-id user) :id-bytes (:certificate user)))

(defrecord SignatureHeader [^SerializedIdentity creator ^bytes nonce]
  ICljToProto
  (clj->proto [this]
    (cond-> (Common$SignatureHeader/newBuilder)
      creator (.setCreator (.toByteString ^Identities$SerializedIdentity (clj->proto creator)))
      nonce (.setNonce (ByteString/copyFrom nonce))
      true (.build))))

(defn make-signature-header
  [& {:keys [creator nonce]}]
  (map->SignatureHeader {:creator creator :nonce nonce}))

(defrecord Header [channel-header signature-header] ;; both bytes
  ICljToProto
  (clj->proto [this]
    (cond-> (Common$Header/newBuilder)
      channel-header (.setChannelHeader (.toByteString ^Common$ChannelHeader (clj->proto channel-header)))
      signature-header (.setSignatureHeader (.toByteString ^Common$SignatureHeader (clj->proto signature-header)))
      true (.build))))

(defn make-header
  [& {:keys [channel-header signature-header]}]
  (map->Header {:channel-header channel-header :signature-header signature-header}))

(defrecord Proposal [header payload extension] ; all bytes
  ICljToProto
  (clj->proto [this]
    (cond-> (ProposalPackage$Proposal/newBuilder)
      header (.setHeader (.toByteString ^Common$Header (clj->proto header)))
      payload (.setPayload (.toByteString ^ProposalPackage$ChaincodeProposalPayload (clj->proto payload)))
      extension (.setExtension (.toByteString
                                ;; FIXME: not sure 'type' yet and can't give type
                                (clj->proto extension)))
      true (.build))))

(defn make-proposal
  [& {:keys [header payload extension]}]
  (map->Proposal {:header header :payload payload :extension extension}))

;; FIXME: input bytes
(defrecord ChaincodeProposalPayload [input ^java.util.Map transient-map]
  ICljToProto
  (clj->proto [this]
    (cond-> (ProposalPackage$ChaincodeProposalPayload/newBuilder)
      input (.setInput (.toByteString ^Chaincode$ChaincodeInvocationSpec (clj->proto input)))
      transient-map (.putAllTransientMap transient-map)
      true (.build))))

(defn make-chaincode-proposal-payload
  [& {:keys [input transient-map]}]
  (map->ChaincodeProposalPayload {:input input :transient-map transient-map}))

(defrecord SignedProposal [^Proposal proposal-bytes ^bytes signature]
  ICljToProto
  (clj->proto [this]
    (cond-> (ProposalPackage$SignedProposal/newBuilder)
      proposal-bytes (.setProposalBytes (.toByteString ^ProposalPackage$Proposal (clj->proto proposal-bytes)))
      signature (.setSignature (ByteString/copyFrom signature))
      true (.build))))

(defn make-signed-proposal
  [& {:keys [proposal-bytes signature]}]
  (map->SignedProposal {:proposal-bytes proposal-bytes :signature signature}))

(defonce exec-env-map {:docker Chaincode$ChaincodeDeploymentSpec$ExecutionEnvironment/DOCKER
                       :system Chaincode$ChaincodeDeploymentSpec$ExecutionEnvironment/SYSTEM})

(defrecord ChaincodeDeploymentSpec [^ChaincodeSpec chaincode-spec ^Long effective-date
                                    ^bytes code-package exec-env]
  ICljToProto
  (clj->proto [this]
    (cond-> (Chaincode$ChaincodeDeploymentSpec/newBuilder)
      chaincode-spec (.setChaincodeSpec ^Chaincode$ChaincodeSpec (clj->proto chaincode-spec))
      effective-date (.setEffectiveDate ^Timestamp (clj->proto (make-timestamp effective-date)))
      ;; code-package must exist at this point
      code-package (.setCodePackage (ByteString/copyFrom code-package))
      exec-env (.setExecEnv (exec-env-map exec-env))
      true (.build))))

(defn make-chaincode-deployment-spec
  [& {:keys [chaincode-spec effective-date code-package exec-env]
      :or {effective-date (System/currentTimeMillis) exec-env :docker}}]
  (map->ChaincodeDeploymentSpec {:chaincode-spec chaincode-spec
                                 :effective-date effective-date
                                 :code-package code-package
                                 :exec-env exec-env}))

(defrecord ConfigSignature [^SignatureHeader signature-header ^bytes signature]
  ICljToProto
  (clj->proto [this]
    (cond-> (Configtx$ConfigSignature/newBuilder)
      signature-header (.setSignatureHeader (.toByteString ^Common$SignatureHeader (clj->proto signature-header)))
      signature (.setSignature (ByteString/copyFrom signature))
      true (.build))))

(defn make-config-signature
  [& {:keys [signature-header signature]}]
  (map->ConfigSignature {:signature-header signature-header :signature signature}))

(defrecord ConfigValue
    [^Long version ^bytes value ^String mod-policy]
  ICljToProto
  (clj->proto [this]
    (cond-> (Configtx$ConfigValue/newBuilder)
      version (.setVersion version)
      value (.setValue value)
      mod-policy (.setModPolicy mod-policy)
      true (.build))))

(defn make-config-value
  [& {:keys [version value mod-policy]}]
  (map->ConfigValue {:version version :value value :mod-policy mod-policy}))

(defrecord Policy [^Long type ^bytes value]
  ICljToProto
  (clj->proto [this]
    (cond-> (Policies$Policy/newBuilder)
      type (.setType type)
      value (.setValue value)
      true (.build))))

(defn make-policy
  [& {:keys [type value]}]
  (map->Policy {:type type :value value}))

(defrecord NOutOf [^Long n rules]
  ICljToProto
  (clj->proto [this]
    (cond-> (Policies$SignaturePolicy$NOutOf/newBuilder)
      n (.setN n)
      rules  (.addAllRules (mapv #(clj->proto %) rules))
      true (.build))))

(defn make-n-out-of
  [& {:keys [n rules]}]
  (map->NOutOf {:n n :rules rules}))

(defrecord SignaturePolicy [^Long signed-by ^NOutOf n-out-of]
  ICljToProto
  (clj->proto [this]
    (cond-> (Policies$SignaturePolicy/newBuilder)
      signed-by (.setSignedBy signed-by)
      n-out-of  (.setNOutOf ^Policies$SignaturePolicy$NOutOf (clj->proto n-out-of))
      true (.build))))

(defn make-signature-policy
  [& {:keys [signed-by n-out-of]}]
  (map->SignaturePolicy {:signed-by signed-by :n-out-of n-out-of}))

(defonce msp-role-type-map {:member MspPrincipal$MSPRole$MSPRoleType/MEMBER
                            :admin MspPrincipal$MSPRole$MSPRoleType/ADMIN})

(defrecord MSPRole [^String msp-identifier role]
  ICljToProto
  (clj->proto [this]
    (cond-> (MspPrincipal$MSPRole/newBuilder)
      msp-identifier (.setMspIdentifier msp-identifier)
      role (.setRole (msp-role-type-map role))
      true (.build))))

(defn make-msp-role
  [& {:keys [msp-identifier role]}]
  (map->MSPRole {:msp-identifier msp-identifier :role role}))

(defonce classification-map {:role MspPrincipal$MSPPrincipal$Classification/ROLE
                             :organization-unit MspPrincipal$MSPPrincipal$Classification/ORGANIZATION_UNIT
                             :identity MspPrincipal$MSPPrincipal$Classification/IDENTITY})

(defrecord MSPPrincipal [principal-classification ^MSPRole principal]
  ICljToProto
  (clj->proto [this]
    (cond-> (MspPrincipal$MSPPrincipal/newBuilder)
      principal-classification (.setPrincipalClassification (classification-map principal-classification))
      principal (.setPrincipal (.toByteString ^MspPrincipal$MSPRole (clj->proto principal)))
      true (.build))))

(defn make-msp-principal
  [& {:keys [principal-classification principal]}]
  (map->MSPPrincipal {:principal-classification principal-classification :principal principal}))

(defrecord SignaturePolicyEnvelope [^Long version ^SignaturePolicy rule identities]
  ICljToProto
  (clj->proto [this]
    (cond-> (Policies$SignaturePolicyEnvelope/newBuilder)
      version   (.setVersion version)
      rule      (.setRule ^Policies$SignaturePolicy (clj->proto rule))
      identities (.addAllIdentities (mapv #(clj->proto %) identities))
      true (.build))))

(defn make-signature-policy-envelope
  [& {:keys [version rule identities]}]
  (map->SignaturePolicyEnvelope {:version version :rule rule :identities identities}))

(defrecord ConfigPolicy [^Long version ^Policies$Policy policy ^String mod-policy]
  ICljToProto
  (clj->proto [this]
    (cond-> (Configtx$ConfigPolicy/newBuilder)
        version (.setVersion version)
        policy (.setPolicy ^Policies$Policy (clj->proto policy))
        mod-policy (.setModPolicy mod-policy)
        true (.build))))

(defn make-config-policy
  [& {:keys [version policy mod-policy]}]
  (map->ConfigPolicy {:version version :policy policy :mod-policy mod-policy}))

(defrecord ConfigGroup [version groups values policies mod-policy]
  ICljToProto
  (clj->proto [this]
    (cond-> (Configtx$ConfigGroup/newBuilder)
      version (.setVersion version)
      groups (.putAllGroups (utils/map-vals #(clj->proto %) groups))
      values (.putAllValues (utils/map-vals #(clj->proto %) values))
      policies (.putAllPolicies (utils/map-vals #(clj->proto %) policies))
      mod-policy (.setModPolicy mod-policy)
      true (.build))))

(defn make-config-group
  [& {:keys [version groups values policies mod-policy]}]
  (map->ConfigGroup {:version version :groups groups :values values
                     :policies policies :mod-policy mod-policy}))

(defrecord ConfigUpdate [channel-id read-set write-set type isolated-data]
  ICljToProto
  (clj->proto [this]
    (cond-> (Configtx$ConfigUpdate/newBuilder)
      channel-id (.setChannelId channel-id)
      read-set (.setReadSet ^Configtx$ConfigGroup (clj->proto read-set))
      write-set (.setWriteSet ^Configtx$ConfigGroup (clj->proto write-set))
      type (.setType type)
      isolated-data (.putAllIsolatedData isolated-data)
      true (.build))))

(defn make-config-update
  [& {:keys [channel-id read-set write-set type isolated-data]}]
  (map->ConfigUpdate {:channel-id channel-id :read-set read-set
                      :write-set write-set :type type
                      :isolated-data isolated-data}))

(defrecord ConfigUpdateEnvelope [^bytes config-update signatures]
  ICljToProto
  (clj->proto [this]
    (cond-> (Configtx$ConfigUpdateEnvelope/newBuilder)
      config-update (.setConfigUpdate (ByteString/copyFrom config-update))
      signatures (.addAllSignatures (mapv #(clj->proto %) signatures))
      true (.build))))

(defn make-config-update-envelope
  [& {:keys [config-update signatures]}]
  (map->ConfigUpdateEnvelope {:config-update config-update :signatures signatures}))

;;;
;;; Responses
;;;

(defrecord ChannelInfo [^String channel-id])
(defn make-channel-info
  [& {:keys [channel-id]}]
  (map->ChannelInfo {:channel-id channel-id}))

(defrecord ChaincodeInfo [^String name ^String version ^String path ^String input ^String escc ^String vscc])
(defn make-chaincode-info
  [& {:keys [name version path input escc vscc]}]
  (map->ChaincodeInfo {:name name :version version :path path :input input :escc escc :vscc vscc}))

(defrecord BlockchainInfo [^Long height current-block-hash previous-block-hash] ;; bytes
  )
(defn make-blockchain-info
  [& {:keys [height current-block-hash previous-block-hash]}]
  (map->BlockchainInfo {:height height :current-block-hash current-block-hash :previous-block-hash previous-block-hash}))

(defrecord BlockHeader [^Long number previous-hash data-hash]) ;; bytes
(defn make-block-header
  [& {:keys [number previous-hash data-hash]}]
  (map->BlockHeader {:number number :previous-hash previous-hash :data-hash data-hash}))

(defrecord BlockData [^clojure.lang.PersistentVector data])
(defn make-block-data
  [& {:keys [data]}]
  (map->BlockData {:data data}))

(defrecord BlockMetadata [^clojure.lang.PersistentVector metadata])
(defn make-block-metadata
  [& {:keys [metadata]}]
  (map->BlockMetadata {:metadata metadata}))

(defrecord Block [^BlockHeader header ^BlockData data ^BlockMetadata metadata])

(defn make-block
  [& {:keys [header data metadata]}]
  (map->Block {:header header :data data :metadata metadata}))

(defrecord Payload [^Header header ^bytes data]
  ICljToProto
  (clj->proto [this]
    (cond-> (Common$Payload/newBuilder)
      header (.setHeader ^Common$Header (clj->proto header))
      ;; Assume the clj->proto result is Configtx$ConfigUpdateEnvelope type
      data (.setData (.toByteString ^Configtx$ConfigUpdateEnvelope (clj->proto data)))
      true (.build))))

(defn make-payload
  [& {:keys [header data]}]
  (map->Payload {:header header :data data}))

(defrecord Envelope [payload signature] ;; bytes
  ICljToProto
  (clj->proto [this]
    (cond-> (Common$Envelope/newBuilder)
      payload (.setPayload (.toByteString ^Common$Payload (clj->proto payload)))
      signature (.setSignature (ByteString/copyFrom #^bytes signature))
      true (.build))))

(defn make-envelope
  [& {:keys [payload signature]}]
  (map->Envelope {:payload payload :signature signature}))

(defrecord Response [^Long status ^String message ^bytes payload])
(defn make-response
  [& {:keys [status message payload]}]
  (map->Response {:status status :message message :payload payload}))

(defrecord ChaincodeAction [^bytes results ^bytes events ^Response response
                            ^ChaincodeID chaincode-id])
(defn make-chaincode-action
  [& {:keys [results events response chaincode-id]}]
  (map->ChaincodeAction {:results results :events events :response response
                         :chaincode-id chaincode-id}))


(defrecord ChaincodeEvent [^String chaincode-id ^String tx-id ^String event-name ^bytes payload])
(defn make-chaincode-event
  [& {:keys [chaincode-id tx-id event-name payload]}]
  (map->ChaincodeEvent {:chaincode-id chaincode-id :tx-id tx-id :event-name event-name
                        :payload payload}))

(defrecord ProposalResponsePayload [^bytes proposal-hash ^bytes extension])
(defn make-proposal-response-payload
  [& {:keys [proposal-hash extension]}]
  (map->ProposalResponsePayload {:proposal-hash proposal-hash :extension extension}))


(defrecord Endorsement [^bytes endorser ^bytes signature])
(defn make-endorsement
  [& {:keys [endorser signature]}]
  (map->Endorsement {:endorser endorser :signature signature}))

(defrecord ProposalResponse [^Long version ^Long timestamp ^Response response
                             ^bytes payload ^Endorsement endorsement])
(defn make-proposal-response
  [& {:keys [version timestamp response payload endorsement]}]
  (map->ProposalResponse {:version version :timestamp timestamp :response response
                          :payload payload :endorsement endorsement}))

(defrecord ChaincodeEndorsedAction [^bytes proposal-response-payload endorsements])
(defn make-chaincode-endorsed-action
  [& {:keys [proposal-response-payload endorsements]}]
  (map->ChaincodeEndorsedAction {:proposal-response-payload proposal-response-payload
                                 :endorsements endorsements}))

(defrecord ChaincodeActionPayload [^bytes chaincode-proposal-payload ^ChaincodeEndorsedAction action])
(defn make-chaincode-action-payload
  [& {:keys [chaincode-proposal-payload action]}]
  (map->ChaincodeActionPayload {:chaincode-proposal-payload chaincode-proposal-payload
                                :action action}))

(defrecord TransactionAction [^bytes header ^bytes payload])
(defn make-transaction-action
  [& {:keys [header payload]}]
  (map->TransactionAction {:header header :payload payload}))

(defrecord Transaction [actions])
(defn make-transaction
  [& {:keys [actions]}]
  (map->Transaction {:actions actions}))

(defrecord ProcessedTransaction [^Envelope transaction-envelope ^Long validation-code] )
(defn make-processed-transaction
  [& {:keys [transaction-envelope validation-code]}]
  (map->ProcessedTransaction {:transaction-envelope transaction-envelope :validation-code validation-code}))

(defrecord SeekSpecified [^Long number]
  ICljToProto
  (clj->proto [this]
    (cond-> (Ab$SeekSpecified/newBuilder)
      number (.setNumber number)
      true (.build))))

(defn make-seek-specified
  [& {:keys [number]}]
  (map->SeekSpecified {:number number}))

(defrecord SeekOldest []
  ICljToProto
  (clj->proto [this]
    (cond-> (Ab$SeekOldest/newBuilder)
      true (.build))))

(defn make-seek-oldest
  []
  (->SeekOldest))

(defrecord SeekNewest []
  ICljToProto
  (clj->proto [this]
    (cond-> (Ab$SeekNewest/newBuilder)
      true (.build))))

(defn make-seek-newest
  []
  (->SeekNewest))

(defrecord SeekPosition [newest oldest specified]
  ICljToProto
  (clj->proto [this]
    (cond-> (Ab$SeekPosition/newBuilder)
      newest (.setNewest ^Ab$SeekNewest (clj->proto newest))
      oldest (.setOldest ^Ab$SeekOldest (clj->proto oldest))
      specified (.setSpecified ^Ab$SeekSpecified (clj->proto specified))
      true (.build))))

(defn make-seek-position
  [& {:keys [newest oldest specified]}]
  (map->SeekPosition {:newest newest :oldest oldest :specified specified}))

(defonce seek-behavior-map {:block-until-ready Ab$SeekInfo$SeekBehavior/BLOCK_UNTIL_READY
                            :fail-if-not-ready Ab$SeekInfo$SeekBehavior/FAIL_IF_NOT_READY})

(defrecord SeekInfo [^SeekPosition start ^SeekPosition stop behavior];; behaviour keyword
  ICljToProto
  (clj->proto [this]
    (cond-> (Ab$SeekInfo/newBuilder)
      start (.setStart ^Ab$SeekPosition (clj->proto start))
      stop (.setStop ^Ab$SeekPosition (clj->proto stop))
      behavior (.setBehavior ^Ab$SeekBehavior (seek-behavior-map behavior))
      true (.build))))

(defn make-seek-info
  [& {:keys [start stop behavior]}]
  (map->SeekInfo {:start start :stop stop :behavior behavior}))

(defn- convert-args
  [args]
  (mapv #(cond (utils/bytes? %) (ByteString/copyFrom #^bytes %)
               (instance? ChaincodeDeploymentSpec %) (.toByteString ^Chaincode$ChaincodeDeploymentSpec (clj->proto %))
               (instance? Common$Block %) (.toByteString ^Common$Block %)
               :else (ByteString/copyFromUtf8 (str %)))
        args))

(defonce parse-trees
  {:block-configuration
   {:data
    {:data
     {:fn #(Common$Envelope/parseFrom ^ByteString %)
      :use-parse-tree :block-config-update-envelope}}}

   :block-transaction
   {:data
    {:data
     {:fn #(Common$Envelope/parseFrom ^ByteString %)
      :use-parse-tree :block-transaction-envelope}}
    :header
    {:previous-hash {:fn #(.toByteArray ^ByteString %)}
     :data-hash {:fn #(.toByteArray ^ByteString %)}}
    :metadata
    {:metadata {:fn #(.toByteArray ^ByteString %)}}}

   :block-transaction-envelope
   {:payload
    { ;;:fn #(Common$Payload/parseFrom ^ByteString %)
     :header {:signature-header
              {:creator {:id-bytes {:fn #(.toByteArray ^ByteString %)}}
               :nonce {:fn #(.toByteArray ^ByteString %)}}}
     :data {:fn #(TransactionPackage$Transaction/parseFrom ^ByteString %)
            :actions
            {:header {:fn #(.toByteArray ^ByteString %)}
             :payload {:fn #(TransactionPackage$ChaincodeActionPayload/parseFrom ^ByteString %)
                       :chaincode-proposal-payload
                       {:fn #(ProposalPackage$ChaincodeProposalPayload/parseFrom ^ByteString %)
                        :input {:fn #(Chaincode$ChaincodeInvocationSpec/parseFrom ^ByteString %)}}

                       :action
                       {:proposal-response-payload {:fn #(.toByteArray ^ByteString %)}
                        :endorsements {:endorser {:fn #(.toByteArray ^ByteString %)}
                                       :signature {:fn #(.toByteArray ^ByteString %)}}}}}}}}
   :block-config-update-envelope
   {:payload
    {;;:fn #(Common$Payload/parseFrom ^ByteString %)
     :data {:fn #(Configtx$ConfigUpdateEnvelope/parseFrom ^ByteString %)
            :config-update {:fn #(Configtx$ConfigUpdate/parseFrom ^ByteString %)}}}}})

(defmacro maybe-applying-proto->clj-transform [[tx-map data-call]]
  `(let [data# ~data-call]
     (if-let [tx-fn# (:fn ~tx-map)]
       (let [transformed-data# (tx-fn# data#)]
         (if (utils/bytes? transformed-data#)
           transformed-data#
           (proto->clj transformed-data#
                       (if-let [use-parse-tree# (:use-parse-tree ~tx-map)]
                         (parse-trees use-parse-tree#)
                         (dissoc ~tx-map :fn)))))
       data#)))

(extend-protocol IProtoToClj

  Query$ChaincodeQueryResponse
  (proto->clj [this opts]
    (mapv (fn [^Query$ChaincodeInfo ci]
            (make-chaincode-info :name (.getName ci) :version (.getVersion ci) :path (.getPath ci)
                                 :input (.getInput ci) :escc (.getEscc ci) :vscc (.getVscc ci)))
          (.getChaincodesList this)))

  Query$ChannelQueryResponse
  (proto->clj [this opts]
    (mapv (fn [^Query$ChannelInfo ci] (make-channel-info :channel-id (.getChannelId ci)))
          (.getChannelsList this)))
  
  Ledger$BlockchainInfo
  (proto->clj [this opts]
    (make-blockchain-info :height (.getHeight this)
                          :current-block-hash (.getCurrentBlockHash this) :previous-block-hash (.getPreviousBlockHash this)))

  ByteString$LiteralByteString
  (proto->clj [this {:keys [fn]}]
    (fn this))
  
  Common$BlockHeader
  (proto->clj [this {:keys [previous-hash data-hash]}]
    (make-block-header :number          (.getNumber this)
                       :previous-hash   (proto->clj (.getPreviousHash this) previous-hash)
                       :data-hash       (proto->clj (.getDataHash this) data-hash)))
  
  Common$BlockData
  (proto->clj [this {:keys [data]}]
    (make-block-data :data (mapv #(maybe-applying-proto->clj-transform [data %]) (.getDataList this))))
  
  Common$BlockMetadata
  (proto->clj [this {:keys [metadata]}]
    (make-block-metadata :metadata (mapv #(maybe-applying-proto->clj-transform [metadata %]) (.getMetadataList this))))

  Common$Block
  (proto->clj [this {:keys [header data metadata]}]
    (make-block :header         (proto->clj (.getHeader this) header)
                :data           (proto->clj (.getData this) data)
                :metadata       (proto->clj (.getMetadata this) metadata)))

  ProposalResponsePackage$Response
  (proto->clj [this opts]
    (make-response :status (.getStatus this)
                   :message (.getMessage this)
                   :payload (.getPayload this)))

  Chaincode$ChaincodeID
  (proto->clj [this opts]
    (make-chaincode-id :path (.getPath this)
                       :name (.getName this)
                       :version (.getVersion this)))
  
  ProposalResponsePackage$ProposalResponsePayload
  (proto->clj [this {:keys [extension]}]
    (make-proposal-response-payload :proposal-hash (.getProposalHash this)
                                    :extension (maybe-applying-proto->clj-transform [extension (.getExtension this)])))

  ChaincodeEventPackage$ChaincodeEvent
  (proto->clj [this ignore]
    (make-chaincode-event :chaincode-id (.getChaincodeId this)
                          :tx-id (.getTxId this)
                          :event-name (.getEventName this)
                          :payload (.getPayload this)))
  
  ProposalPackage$ChaincodeAction
  (proto->clj [this {:keys [events response chaincode-id]}]
    (make-chaincode-action :results (.getResults this)
                           :events (maybe-applying-proto->clj-transform [events (.getEvents this)])
                           :response (proto->clj (.getResponse this) response)
                           :chaincode-id (proto->clj (.getChaincodeId this) chaincode-id)))
  
  TransactionPackage$ChaincodeEndorsedAction
  (proto->clj [this {:keys [proposal-response-payload endorsements]}]
    (make-chaincode-endorsed-action :proposal-response-payload
                                    (maybe-applying-proto->clj-transform [proposal-response-payload
                                                                          (.getProposalResponsePayload this)])
                                    :endorsements (mapv #(proto->clj % endorsements)
                                                        (.getEndorsementsList this))))

  Chaincode$ChaincodeInput
  (proto->clj [this ignore]
    (make-chaincode-input :args (.getArgsList this)
                          :decorations (utils/map-vals #(proto->clj % nil)
                                                       (.getDecorations this))))
  
  Chaincode$ChaincodeSpec
  (proto->clj [this ignore]
    (make-chaincode-spec :type (.getTypeValue this)
                         :chaincode-id (proto->clj (.getChaincodeId this) ignore)
                         :chaincode-input (proto->clj (.getInput this) ignore)
                         :timeout (.getTimeout this)))
  
  Chaincode$ChaincodeInvocationSpec
  (proto->clj [this ignore]
    (make-chaincode-invocation-spec :chaincode-spec (proto->clj (.getChaincodeSpec this) ignore)
                                    :id-generation-alg (.getIdGenerationAlg this)))
  
  ProposalPackage$ChaincodeProposalPayload
  (proto->clj [this {:keys [input]}]
    (make-chaincode-proposal-payload :input
                                     (maybe-applying-proto->clj-transform [input (.getInput this)])
                                   
                                     :transiten-map (utils/map-vals #(proto->clj % nil) (.getTransientMap this))))
  
  TransactionPackage$ChaincodeActionPayload
  (proto->clj [this {:keys [action chaincode-proposal-payload]}]
    (make-chaincode-action-payload :chaincode-proposal-payload
                                   (maybe-applying-proto->clj-transform [chaincode-proposal-payload
                                                                         (.getChaincodeProposalPayload this)])
                                   
                                   :action (proto->clj (.getAction this) action)))


  TransactionPackage$TransactionAction
  (proto->clj [this {:keys [payload header]}]
    (make-transaction-action :header (proto->clj (.getHeader this) header)
                             :payload (maybe-applying-proto->clj-transform [payload (.getPayload this)])))

  TransactionPackage$Transaction
  (proto->clj [this {:keys [actions]}]
    (make-transaction :actions (mapv #(proto->clj % actions) (.getActionsList this))))
  
  TransactionPackage$ProcessedTransaction
  (proto->clj [this opts]
    (make-processed-transaction :transaction-envelope (proto->clj (.getTransactionEnvelope this)
                                                                  {})
                                :validation-code (.getValidationCode this)))
  ;; FIXME: fix all above proto->clj like this!

  Timestamp
  (proto->clj [this ignore]
    (+ (* (.getSeconds this) 1000)
       (quot (.getNanos this) 1000000)))
  
  Common$ChannelHeader
  (proto->clj [this ignore]
    (let [extension (.getExtension this)]
     (make-channel-header :type (.getType this) :version (.getVersion this)
                          :timestamp (proto->clj (.getTimestamp this) nil)
                          :channel-id (.getChannelId this)
                          :tx-id (.getTxId this) :epoch (.getEpoch this)
                          :extesion (when-not (.isEmpty extension)
                                      ;; FIXME: more specific type?
                                      extension))))
  Identities$SerializedIdentity
  (proto->clj [this {:keys [id-bytes]}]
    (make-serialized-identity :mspid (.getMspid this)
                              :id-bytes (proto->clj (.getIdBytes this) id-bytes)))
  Common$SignatureHeader
  (proto->clj [this {:keys [creator nonce]}]
    (make-signature-header :creator (-> (.getCreator this)
                                        (Identities$SerializedIdentity/parseFrom)
                                        (proto->clj creator))
                           :nonce (proto->clj (.getNonce this) nonce)))
  
  Common$Header
  (proto->clj [this {:keys [channel-header signature-header]}]
    (let [raw-signature-header (.getSignatureHeader this)]
      (make-header :channel-header (proto->clj (Common$ChannelHeader/parseFrom (.getChannelHeader this))
                                               channel-header)
                   :signature-header (when-not (.isEmpty raw-signature-header)
                                       (proto->clj (Common$SignatureHeader/parseFrom raw-signature-header)
                                                   signature-header)))))
  Configtx$ConfigValue
  (proto->clj [this ignore]
    (make-config-value :version (.getVersion this)
                       :value (.getValue this)
                       :mod-policy (.getModPolicy this)))
  Policies$Policy
  (proto->clj [this ignore]
    (make-policy :type (.getType this) :value (.getValue this)))
  
  Configtx$ConfigPolicy
  (proto->clj [this ignore]
    (make-config-policy :version (.getVersion this)
                        :policy ^Policies$Policy (proto->clj (.getPolicy this) nil)
                        :mod-policy (.getModPolicy this)))
  
  Configtx$ConfigGroup
  (proto->clj [this ignore]
    (make-config-group :version (.getVersion this)
                       :groups (utils/map-vals #(proto->clj % nil) (.getGroups this))
                       :values (utils/map-vals #(proto->clj % nil) (.getValues this))
                       :policies (utils/map-vals #(proto->clj % nil) (.getPolicies this))
                       :mod-policy (.getModPolicy this)))
  
  Configtx$ConfigUpdate
  (proto->clj [this ignore]
    (make-config-update :channel-id (.getChannelId this)
                        :read-set (proto->clj (.getReadSet this) nil)
                        :write-set (proto->clj (.getWriteSet this) nil)
                        :type (.getType this)
                        :isolated-data (.getIsolatedData this)))
  
  Configtx$ConfigUpdateEnvelope
  (proto->clj [this {:keys [config-update]}]
    (make-config-update-envelope :config-update (maybe-applying-proto->clj-transform [config-update (.getConfigUpdate this)])
                                 :signatures (.getSignaturesList this)))

  Common$Payload
  (proto->clj [this {:keys [data header]}]
    (make-payload :header (proto->clj (.getHeader this) header)
                  :data (maybe-applying-proto->clj-transform [data (.getData this)])))
  
  Common$Envelope
  (proto->clj [this {:keys [payload]}] ;; {:data {:payload {:data ...}}}
    (let [signature (.getSignature this)]
      (make-envelope :payload (proto->clj (Common$Payload/parseFrom (.getPayload this))
                                          payload)
                     :signature (when-not (.isEmpty signature)
                                  (.toByteArray signature)))))

  ProposalResponsePackage$Endorsement
  (proto->clj [this {:keys [endorser signature]}]
    (make-endorsement :endorser (proto->clj (.getEndorser this) endorser)
                      :signature (proto->clj (.getSignature this) signature)))
  
  ProposalResponsePackage$ProposalResponse
  (proto->clj [this ignore]
    (make-proposal-response :version (.getVersion this)
                            :timestamp (proto->clj (.getTimestamp this) nil)
                            :response (proto->clj (.getResponse this) ignore)
                            :payload (.getPayload this)
                            :endorsement (.getEndorsement this))))

;;;
(defn send-update-channel-using-envelope
  [channel-name ^bytes envelope]
  (let [cc-envelope (Common$Envelope/parseFrom envelope)
        cc-payload (Common$Payload/parseFrom (.getPayload cc-envelope))
        cc-channel-header (Common$ChannelHeader/parseFrom (.getChannelHeader (.getHeader cc-payload)))]
    (assert (= (.getNumber Common$HeaderType/CONFIG_UPDATE) (.getType cc-channel-header)))
    (assert (= channel-name (.getChannelId cc-channel-header)))
    (let [config-update-env (Configtx$ConfigUpdateEnvelope/parseFrom (.getData cc-payload))
          config-update (.getConfigUpdate config-update-env)]
      
      #_
      (send-dupate-channel (.toByteArray config-update singers orderer))
      )))

(defn make-chaincode-proposal-payload-message
  ([chaincode-id fcn args]
   (make-chaincode-proposal-payload-message chaincode-id fcn args {}))
  ([chaincode-id fcn args transient-map]
   (assert (vector? args))
   (let [^Chaincode$ChaincodeInvocationSpec
         chaincode-invocation-spec
         (->> (make-chaincode-input :args `[~fcn ~@args])
              (make-chaincode-spec :chaincode-id chaincode-id
                                   :chaincode-input)
              (make-chaincode-invocation-spec :chaincode-spec))]
     (make-chaincode-proposal-payload :input chaincode-invocation-spec :transient-map transient-map))))

;;;
;;; Message building functions
;;;
(defn make-nonce
  ([]
   (make-nonce 24))
  ([nonce-size]
   (crypto-suite/random-bytes nonce-size)))

(defn get-epoch
  [& _]
  ;; FIXME: currently always default-epoch, 0
  0)

(defn identity-nonce->tx-id
  [identity ^bytes nonce {algorithm :hash-algorithm}]
  (-> (ByteString/copyFrom nonce)
      (.concat (.toByteString ^Identities$SerializedIdentity (clj->proto identity)))
      (.toByteArray)
      (crypto-suite/hash :algorithm algorithm)
      (Hex/toHexString)))

(defn make-header-message
  [channel-name user {:keys [channel-header-type header-version
                             extension]
                      :or {header-version 1 channel-header-type :endorser-transaction}}]
  (let [identity (user->serialized-identity user)
        nonce (make-nonce)]
    (make-header :channel-header
                 (make-channel-header :type channel-header-type
                                      :version header-version
                                      :channel-id channel-name
                                      :tx-id (identity-nonce->tx-id identity nonce (:crypto-suite user))
                                      :epoch (get-epoch channel-name)
                                      :extension extension)
                 :signature-header (make-signature-header :creator identity :nonce nonce))))

(defn make-envelope-message
  [channel-name user channel-header-type payload-data]
  (let [payload (make-payload :header (make-header-message channel-name user {:channel-header-type channel-header-type})
                              :data payload-data)]
    (make-envelope :payload payload 
                   :signature (crypto-suite/sign (.toByteArray ^Common$Payload (clj->proto payload))
                                                 (:private-key user)
                                                 {:algorithm (:key-algorithm (:crypto-suite user))}))))

(defn make-chaincode-signed-proposal-message
  [channel-name user header-extension proposal-payload {:keys [args] :as opts}]
  (let [proposal (make-proposal :header
                                (make-header-message channel-name user
                                                     (assoc opts :extension header-extension))
                                :payload proposal-payload)
        signature (crypto-suite/sign (.toByteArray ^ProposalPackage$SignedProposal (clj->proto proposal))
                                     (:private-key user)
                                     {:algorithm (:key-algorithm (:crypto-suite user))})]
    (make-signed-proposal :proposal-bytes  proposal
                          :signature signature)))

(defn get-random-node
  ([nodes-access-key]
   (get-random-node core/*channel* nodes-access-key))
  ([channel nodes-access-key]
   (rand-nth (core/get-nodes channel nodes-access-key))))

(defn target->nodes [target]
  (if (sequential? target)
    target
    (condp instance? target
      Peer [target]
      Orderer [target]
      Channel [(get-random-node target :peers)])))

(defn verify-response
  [^ProposalResponsePackage$ProposalResponse raw-response user]
  (let [endorsement (.getEndorsement raw-response)
        signature (.getSignature endorsement)
        endorser (.getEndorser endorsement)
        cert (Identities$SerializedIdentity/parseFrom endorser)
        text (.concat (.getPayload raw-response) endorser)]
    ;;(:crypto-suite user)
    (crypto-suite/verify (-> cert .getIdBytes .toByteArray)
                         (.toByteArray signature)
                         (.toByteArray text)
                         (:crypto-suite user))))

(defonce type->parse-tree-key
  {Common$Block :block-transaction})

(defn- proto->clj-using-parse-tree
  [response]
  (->> (type response)
       (type->parse-tree-key)
       (parse-trees)
       (proto->clj response)))

;;;
;;; Async processing
;;;
(defn node->channel
  [{:keys [url pem hostname-override?]}]
  ;; pem = byte of ASN.1 encoding 
  (let [{:keys [protocol host port]} (utils/parse-grpc-url url)
        channel-builder (NettyChannelBuilder/forAddress ^String host (Integer/parseInt port))]
    (case protocol
      "grpc"  (.usePlaintext channel-builder true)
      ;; From Fabric Java SDK
      "grpcs" (if (nil? pem)
                (throw (Exception. "FIXME: Implement - use root certificate!"))

                ;; FIXME: somehow this does not work!!
                ;; (maybe 'overrideAuthority' is still experimental??
                ;; 
                ;; (let [ssl-context (-> (GrpcSslContexts/forClient)
                ;;                       (.trustManager (ByteArrayInputStream. pem))
                ;;                       (.sslProvider SslProvider/OPENSSL)
                ;;                       (.build))]
                ;;   (when hostname-override?
                ;;     (let [{:keys [host port]} (utils/parse-grpc-url url)]
                ;;       (.overrideAuthority channel-builder ^String (str host ":" port))))
                ;;   (.negotiationType (.sslContext channel-builder (.build ssl-context)) NegotiationType/TLS)
                ;;   ;; set builder properties
                ;;   ;; keepAliveTime
                ;;   ;; keepAliveTimeout
                ;;   ;; withOption
                ;;   )
                (let [ssl-context-builder (-> (GrpcSslContexts/forClient)
                                              (.sslProvider SslProvider/OPENSSL))]
                  (when hostname-override?
                    ;; This is for 'development' purpose
                    (.trustManager ssl-context-builder (InsecureTrustManagerFactory/INSTANCE)))
                  (.negotiationType (.sslContext channel-builder (.build ssl-context-builder)) NegotiationType/TLS)
                  ;; set builder properties
                  ;; keepAliveTime
                  ;; keepAliveTimeout
                  ;; withOption
                  )))
    (.build channel-builder)))

(defn response-waiting-observer
  [k ch]
  (reify StreamObserver
    (onNext [this proposal-response]
      (async/put! ch [k proposal-response]))
    (onError [this err]
      (async/put! ch [k err]))
    (onCompleted [this])))

(defmacro binding-grpc-waiting-auto-callback [[k callback-fn] & body]
  `(let [ch# (async/chan)
         ~callback-fn (response-waiting-observer ~k ch#)]
     ~@body
     ch#))

(defn send-proposal-to-peer
  [peer proposal]
  (binding-grpc-waiting-auto-callback [peer callback]
    (.processProposal (EndorserGrpc/newStub ^ManagedChannel (node->channel peer))
                      (clj->proto proposal)
                      callback)))

(defn send-proposal
  [signed-proposal target user response-fn
   {:keys [verify?] :or {verify? false} :as opts}]
  ;; target==channel (get-random-node channel :peers)
  (->> (target->nodes target)
       (mapv #(send-proposal-to-peer % signed-proposal))
       (mapv #(let [[peer raw-response] (async/<!! %)]
                (if (instance? Exception raw-response)
                  raw-response
                  (if (and verify? (not (verify-response raw-response user)))
                    (Exception. "Verification failed!")
                    (if response-fn
                      (let [response (.getResponse ^ProposalResponsePackage$ProposalResponse raw-response)]
                        (-> (.getPayload ^ProposalResponsePackage$Response response)
                            (response-fn)
                            (proto->clj-using-parse-tree)))
                      raw-response)))))))

(defn envelope-broadcast-observer
  [ch]
  (reify StreamObserver
    (onNext [this boradcast-response]
      (async/put! ch boradcast-response))
    (onError [this err]
      (async/put! ch err))
    (onCompleted [this]
      (async/put! ch :done))))

;; typeout values (from Java SDK)
;; defaultProperty(PROPOSAL_WAIT_TIME, "20000");
;; defaultProperty(CHANNEL_CONFIG_WAIT_TIME, "15000");
;; defaultProperty(ORDERER_RETRY_WAIT_TIME, "200");
;; defaultProperty(ORDERER_WAIT_TIME, "3000");
;; defaultProperty(EVENTHUB_CONNECTION_WAIT_TIME, "1000");
;; defaultProperty(GENESISBLOCK_WAIT_TIME, "5000");

(defonce timeouts {:orderer-wait-time 3000})

(defn broadcast-or-deliver-via-orderer
  [call-type orderer envelope]
  (letfn [(send-envelope-via-orderer-using-async [call-fn]
            (let [ch (async/chan 32)
                  callback (envelope-broadcast-observer ch)
                  ^StreamObserver req-observer (call-fn (AtomicBroadcastGrpc/newStub ^ManagedChannel (node->channel orderer))
                                                        callback)]
              (.onNext req-observer ^Common$Envelope (clj->proto envelope))
              (try
                (async/go-loop [result []]
                  (let [[v ch] (async/alts! [ch (async/timeout (:orderer-wait-time timeouts))])]
                    (cond (= v :done) result
                          (nil? v) (throw (Exception. "Timeout to send Evelope using orderer"))
                          (instance? Exception v) (throw v)
                          :else (recur (conj result v)))))
                (finally (.onCompleted req-observer)))))]
    (async/<!!
     (send-envelope-via-orderer-using-async
      (case call-type
        :deliver #(.deliver ^AtomicBroadcastGrpc$AtomicBroadcastStub %1 %2)
        :broadcast #(.broadcast ^AtomicBroadcastGrpc$AtomicBroadcastStub %1 %2))))))
;;;;
(comment
  (def chaincode-id1 (map->ChaincodeID {:name "name" :version "version" :path "path"}))
  (def chaincode-input1 (map->ChaincodeInput {:args [] :decorations {}}))
  (def chaincode-spec1 (map->ChaincodeSpec {:type :golang
                                                      :chaincode-id chaincode-id1
                                                      :chaincode-input chaincode-input1
                                                      :timeout 100}))

  (def chaincode-invocation-spec1 (map->ChaincodeInvocationSpec {:chaincode-spec chaincode-spec1
                                                                           :id-generation-alg ""}))

  (def chaincode-header-extension1 (map->ChaincodeHeaderExtension
                                                  {:payload-visibility "FIXME"
                                                   :chaincode-id chaincode-id1}))

  (def timestamp1 (make-timestamp))
  )
