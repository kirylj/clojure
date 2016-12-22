(ns tracks.validations
  (:require [clojure.string :refer [blank? starts-with?]]
            [tracks.models :as m]))

(defn check-range [str min max]
  (not (or (blank? str) (> (count str) max) (< (count str) min))))

(defn check-len [str len]
  (= (count str) len))

(defn check-in [vl coll]
  (.contains coll vl))

(defn check-login-free? [login]
  (let [user-from-db (m/get-username login)]
    (if user-from-db
      false
      true)
    )
  )

(defn check-email? [email]
  (if (and (string? email) (re-matches #"^.+\@.+\..+$" email))
    true
    false)
  )

(defn check-phone? [phone]
  (if (starts-with? phone "+375")
    true
    false))