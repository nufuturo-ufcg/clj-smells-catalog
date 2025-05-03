(ns examples.smells.library-locker
  (:require [clj-http.client :as client]))

(defn do-get [url]
  (client/get url))

(defn do-post [url data]
  (client/post url {:body data}))

(defn fetch-data []
  (do-get "https://httpbin.org/get"))

(defn send-data [info]
  (do-post "https://httpbin.org/post" info))

(println (fetch-data))
(println (send-data "test"))