(ns examples.traditional.refactored.long-function-refactored
  (:require [clojure.string :as str]))

(defn validate-name [name errors]
  (if (string? name)
    (do
      (when (< (count name) 2)
        (swap! errors conj "Name is too short."))
      (when (> (count name) 100)
        (swap! errors conj "Name is too long."))
      (when (str/blank? name)
        (swap! errors conj "Name cannot be blank."))
      (when (re-find #"\d" name)
        (swap! errors conj "Name must not contain numbers.")))
    (swap! errors conj "Invalid name.")))

(defn validate-email [email errors]
  (if (string? email)
    (do
      (when (not (re-matches #".+@.+\..+" email))
        (swap! errors conj "Invalid email format."))
      (when (or (str/includes? email "spam")
                (str/includes? email "fake"))
        (swap! errors conj "Email contains suspicious terms."))
      (when (str/ends-with? email ".xyz")
        (swap! errors conj "Emails ending in .xyz are not allowed.")))
    (swap! errors conj "Email must be a string.")))

(defn validate-password [pwd errors]
  (if (string? pwd)
    (do
      (when (not-any? #(Character/isUpperCase %) pwd)
        (swap! errors conj "Password must contain an uppercase letter."))
      (when (not-any? #(Character/isDigit %) pwd)
        (swap! errors conj "Password must contain a number."))
      (when (not-any? #(contains? #{\! \@ \# \$ \% \&} %) pwd)
        (swap! errors conj "Password must contain a special character."))
      (when (str/includes? pwd " ")
        (swap! errors conj "Password must not contain spaces.")))
    (swap! errors conj "Invalid password.")))

(defn validate-age [age errors]
  (cond
    (nil? age) (swap! errors conj "Age is required.")
    (not (number? age)) (swap! errors conj "Age must be a number.")
    (> age 120) (swap! errors conj "Invalid age.")))

(defn validate-preferences [prefs errors]
  (if (sequential? prefs)
    (do
      (when (empty? prefs)
        (swap! errors conj "Preferences list is empty."))
      (when (> (count prefs) 20)
        (swap! errors conj "Too many preferences.")))
    (swap! errors conj "Preferences must be a list.")))

(defn validate-user [user]
  (let [errors (atom [])]
    (validate-name (:name user) errors)
    (validate-email (:email user) errors)
    (validate-password (:password user) errors)
    (validate-age (:age user) errors)
    (validate-preferences (:preferences user) errors)

    (if (empty? @errors)
      {:valid true}
      {:valid false :errors @errors})))

(def user-example
  {:name "John1"
   :email "john@fake.xyz"
   :password "weak"
   :age 200
   :preferences []})

(println (validate-user user-example))
