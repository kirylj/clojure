(ns tracks.core)
(use 'clojure.pprint)


(defn test-handler
  "docstring"
  [request]
  {
   :status  200
   :headers {"Content-Type" "text/plain"}
   :body    (with-out-str (clojure.pprint/pprint request))})

