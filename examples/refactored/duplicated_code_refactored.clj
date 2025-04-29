(ns examples.refactored.duplicated-code-refactored)

(defn format-log [level message]
  (str "[" level "] " (.toUpperCase message) " - " (java.time.Instant/now)))

(println (format-log "INFO" "Process started"))
(println (format-log "ERROR" "File not found"))