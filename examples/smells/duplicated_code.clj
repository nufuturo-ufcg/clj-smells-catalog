(ns examples.smells.duplicated-code
  (:require [clojure.string :as str]))

(defn info-log [message]
  (str "[INFO] " (clojure.string/upper-case message) " - " (java.time.Instant/now)))

(defn error-log [message]
  (str "[ERROR] " (clojure.string/upper-case message) " - " (java.time.Instant/now)))

(println (info-log "Process started"))
(println (error-log "File not found"))