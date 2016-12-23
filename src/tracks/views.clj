(ns tracks.views
  (:require [clojure.java.io]
            [ring.util.response :refer [response redirect]]
            [tracks.models :as m]
            [compojure.route :as route]
            [tracks.helpers :as h]
            [tracks.validations :as valid]
            ))


(defn index [req]
  (h/file-renderer req "index.html"
                   {
                    :cats (m/get-cats)
                    })
  )

(defn login [req]
  (if (h/is-authenticated? req)
    (redirect "/")
    (h/file-renderer req "login.html" {}))
  )

(defn track [track-id req]
  (let [track (m/get-track track-id)]
    (if track
      (h/file-renderer req "track.html"
        {
          :track track
          })
      (route/not-found "Not found"))))



(defn tracks [req]
  (h/file-renderer req "tracks.html"
                   {
                    :tracks (m/get-tracks)
                    }))

(defn add-track [req]
  (h/file-renderer req "add_track.html"
                   {
                    :cats (m/get-cats)
                    }))

(defn post-add-track [{{category_id :category_id title :title lyrics :lyrics author :author} :params :as req}]
  (m/insert-track category_id (get-in req [:session :identity :id] nil) title lyrics author)
  (h/file-renderer req "add_track.html" {}))

(defn post-login [{{login :login password :password} :params session :session :as req}]
  (let [user-from-db (h/login-user login password)
        counter (h/counter-update req)
        ]
    (if user-from-db
      (do
        (assoc (redirect "/") :session (assoc session :identity (select-keys user-from-db [:login :id]))))
      (do
        (h/file-renderer req "login.html"
                         {
                          :error_message "Something wrong with your credentials!"
                          :q             user-from-db
                          }))
      )
    )
  )

(defn logout [req]
  (h/counter-update req)
  (assoc (redirect "/") :session (assoc (:session req) :identity {})))

(defn signup [req]
  (if (h/is-authenticated? req)
    (redirect "/")
    (h/file-renderer req "signup.html"
                     {
                      :login_ok           true
                      :passwd_ok          true
                      :password_repeat_ok true
                      :email_ok           true
                      :email_repeat       true
                      :sex_ok             true
                      :name_ok            true
                      :surname_ok         true
                      :lastname_ok        true
                      :country_ok         true
                      :city_ok            true
                      :phone_ok           true
                      :add_phone_ok       true
                      :signed_ok          true
                      }))

  )

(defn post-signup [{{
                     login           :login
                     password        :password
                     password_repeat :password_repeat
                     email           :email
                     email_repeat    :email_repeat
                     name            :name
                     phone           :phone

                     } :params session :session :as req}]
  (let [login_ok (and (valid/check-range login 2 10) (valid/check-login-free? login))
        passwd_ok (valid/check-range password 2 10)
        password_repeat_ok (= password password_repeat)
        email_ok (and (valid/check-range email 2 32) (valid/check-email? email))
        email_repeat (= email email_repeat)
        name_ok (valid/check-range name 2 10)
        phone_ok (and (valid/check-len phone 13) (valid/check-phone? phone))

        ]
    (if (and login_ok passwd_ok password_repeat_ok email_ok email_repeat name_ok phone_ok )
      (do
        (m/insert-user
          {
           :login     login
           :password  password
           :email     email
           :name      name
           :phone     phone
           })
        (assoc (redirect "/") :session (assoc session :identity (select-keys (h/login-user login password) [:login :id])))
        )
      (h/file-renderer req "signup.html"
                       {
                        :login              login
                        :login_ok           login_ok
                        :passwd_ok          passwd_ok
                        :password_repeat_ok password_repeat_ok
                        :email              email
                        :email_ok           email_ok
                        :email_repeat       email_repeat
                        :name               name
                        :name_ok            name_ok
                        :phone              phone
                        :phone_ok           phone_ok
                        :add_count          (count phone)
                        :add_count2         (valid/check-phone? phone)
                        })))

  )

(defn log-flush [req]
  (h/logger-flush)
  (redirect "/"))


(defn category [category-slug req]
  (let [cat (m/get-cat category-slug)]
    (if cat
      (h/file-renderer req "category.html"
                       {
                        :cat cat
                        :tracks (m/get-tracks-by-cat category-slug)
                        })
      (route/not-found "Not found"))))

(defn about [req]
  (h/markup
    (html
      (head)
      (body
        (h1 "Students of group #251005")
        (ul {class "my-list"}
            (li "Mamajka Nascia")
            (li "Kulik Arciom")
            (li "Jurkou Kiryl"))
        (p {class content title "Happy New Year!"}
           Example of clojure-to-html DSL )))))