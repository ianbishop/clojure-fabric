(ns clojure-fabric.admin-package
  (:import org.hyperledger.fabric.protos.peer.AdminPackage))

;;;---------------------------------------------------
;;;org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
;;;

;;;  STATUS_FIELD_NUMBER
(defn
 status_-field_-number
 []
 (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/STATUS_FIELD_NUMBER))

;;;  getUnknownFields
(defn
 get-unknown-fields
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status getUnknownFields))

;;;  getSerializedSize
(defn
 get-serialized-size
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status getSerializedSize))

;;;  getStatus
(defn
 get-status
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status getStatus))

;;;  toBuilder
(defn
 to-builder
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status toBuilder))

;;;  getStatusValue
(defn
 get-status-value
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status getStatusValue))

;;;  writeTo
(defn
 write-to
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status
  ^com.google.protobuf.CodedOutputStream
  coded-output-stream0]
 (. admin-package$-server-status writeTo coded-output-stream0))

;;;  parseFrom
(defn
 parse-from
 ([generic-arg1]
  (cond
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^java.io.InputStream
    generic-arg1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1)
   :else
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    
    generic-arg1)))
 ([generic-arg1
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (cond
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1
    extension-registry-lite1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1
    extension-registry-lite1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^java.io.InputStream
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1
    extension-registry-lite1)
   :else
   (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseFrom
    
    generic-arg1
    extension-registry-lite1))))

;;;  newBuilderForType
(defn
 new-builder-for-type
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status newBuilderForType))

;;;  isInitialized
(defn
 is-initialized
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status isInitialized))

;;;  getParserForType
(defn
 get-parser-for-type
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status getParserForType))

;;;  equals
(defn
 equals
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status
  ^java.lang.Object
  object0]
 (. admin-package$-server-status equals object0))

;;;  hashCode
(defn
 hash-code
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status hashCode))

;;;  parser
(defn
 parser
 []
 (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parser))

;;;  parseDelimitedFrom
(defn
 parse-delimited-from
 ([^java.io.InputStream input-stream0]
  (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseDelimitedFrom
   input-stream0))
 ([^java.io.InputStream
   input-stream0
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/parseDelimitedFrom
   input-stream0
   extension-registry-lite1)))

;;;  newBuilder
(defn
 new-builder
 ([]
  (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/newBuilder))
 ([^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
   admin-package$-server-status0]
  (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/newBuilder
   admin-package$-server-status0)))

;;;  getDefaultInstance
(defn
 get-default-instance
 []
 (org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus/getDefaultInstance))

;;;  getDefaultInstanceForType
(defn
 get-default-instance-for-type
 [^org.hyperledger.fabric.protos.peer.AdminPackage$ServerStatus
  admin-package$-server-status]
 (. admin-package$-server-status getDefaultInstanceForType))


;;;---------------------------------------------------
;;;org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
;;;

;;;  LOG_LEVEL_FIELD_NUMBER
(defn
 log_-level_-field_-number
 []
 (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/LOG_LEVEL_FIELD_NUMBER))

;;;  getUnknownFields
(defn
 get-unknown-fields
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getUnknownFields))

;;;  getSerializedSize
(defn
 get-serialized-size
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getSerializedSize))

;;;  getLogLevel
(defn
 get-log-level
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getLogLevel))

;;;  toBuilder
(defn
 to-builder
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request toBuilder))

;;;  LOG_MODULE_FIELD_NUMBER
(defn
 log_-module_-field_-number
 []
 (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/LOG_MODULE_FIELD_NUMBER))

;;;  getLogModule
(defn
 get-log-module
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getLogModule))

;;;  writeTo
(defn
 write-to
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request
  ^com.google.protobuf.CodedOutputStream
  coded-output-stream0]
 (. admin-package$-log-level-request writeTo coded-output-stream0))

;;;  getLogModuleBytes
(defn
 get-log-module-bytes
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getLogModuleBytes))

;;;  parseFrom
(defn
 parse-from
 ([generic-arg1]
  (cond
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^java.io.InputStream
    generic-arg1)
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1)
   :else
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    
    generic-arg1)))
 ([generic-arg1
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (cond
   (instance? com.google.protobuf.ByteString generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^com.google.protobuf.ByteString
    generic-arg1
    extension-registry-lite1)
   (instance? java.nio.ByteBuffer generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^java.nio.ByteBuffer
    generic-arg1
    extension-registry-lite1)
   (instance? com.google.protobuf.CodedInputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^com.google.protobuf.CodedInputStream
    generic-arg1
    extension-registry-lite1)
   (instance? java.io.InputStream generic-arg1)
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    ^java.io.InputStream
    generic-arg1
    extension-registry-lite1)
   :else
   (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseFrom
    
    generic-arg1
    extension-registry-lite1))))

;;;  newBuilderForType
(defn
 new-builder-for-type
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request newBuilderForType))

;;;  isInitialized
(defn
 is-initialized
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request isInitialized))

;;;  getParserForType
(defn
 get-parser-for-type
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getParserForType))

;;;  equals
(defn
 equals
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request
  ^java.lang.Object
  object0]
 (. admin-package$-log-level-request equals object0))

;;;  getLogLevelBytes
(defn
 get-log-level-bytes
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getLogLevelBytes))

;;;  hashCode
(defn
 hash-code
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request hashCode))

;;;  parser
(defn
 parser
 []
 (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parser))

;;;  parseDelimitedFrom
(defn
 parse-delimited-from
 ([^java.io.InputStream input-stream0]
  (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseDelimitedFrom
   input-stream0))
 ([^java.io.InputStream
   input-stream0
   ^com.google.protobuf.ExtensionRegistryLite
   extension-registry-lite1]
  (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/parseDelimitedFrom
   input-stream0
   extension-registry-lite1)))

;;;  newBuilder
(defn
 new-builder
 ([]
  (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/newBuilder))
 ([^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
   admin-package$-log-level-request0]
  (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/newBuilder
   admin-package$-log-level-request0)))

;;;  getDefaultInstance
(defn
 get-default-instance
 []
 (org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest/getDefaultInstance))

;;;  getDefaultInstanceForType
(defn
 get-default-instance-for-type
 [^org.hyperledger.fabric.protos.peer.AdminPackage$LogLevelRequest
  admin-package$-log-level-request]
 (. admin-package$-log-level-request getDefaultInstanceForType))