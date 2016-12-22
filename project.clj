(defproject . "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/java.jdbc "0.7.0-alpha1"]
                 [org.xerial/sqlite-jdbc "3.15.1"]

                 [ring/ring-devel "1.5.0"]
                 [ring/ring-core "1.5.0"]
                 [ring/ring-jetty-adapter "1.5.0"]

                 [compojure "1.5.1"]
                 [selmer "1.10.1"]
                 [ring/ring-defaults "0.2.1"]
                 [buddy/buddy-auth "1.3.0"]
                 [buddy/buddy-hashers "1.1.0"]
                 ]


  :plugins [
            [lein-ring "0.10.0"]
            ]
  :main handler/main

  :ring {
         :init          handler/app_init
         :handler       handler/app

         :auto-reload?  true
         :auto-refresh? true
         }

  :profiles
  {
   :dev {
         :dependencies [
                        [javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})



