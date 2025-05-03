(ns examples.refactored.library-locker-refactored
  (:require [clj-http.client :as client]))

(defn fetch-data []
  (client/get "https://httpbin.org/get"))

(defn send-data [info]
  (client/post "https://httpbin.org/post" {:body info}))

(println (fetch-data))
(println (send-data "test"))
