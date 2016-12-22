(ns tracks.models
  (:require [clojure.java.jdbc :as jdbc]
            [buddy.hashers :as hashers]
            )
  )

(def db {
         :classname   "org.sqlite.JDBC"
         :subprotocol "sqlite"
         :subname     "test.db"})

(defn create-tables []
  (jdbc/db-do-commands db
                       (jdbc/create-table-ddl
                         :cats
                         [[:id :integer :primary :key :AUTOINCREMENT]
                          [:name "varchar(128)"]
                          [:slug "varchar(128)"]]))
  (jdbc/db-do-commands db
                       (jdbc/create-table-ddl
                         :tracks
                         [[:id :integer :primary :key :AUTOINCREMENT]
                          [:category_id :integer]
                          [:user_id :integer]
                          [:title "varchar(128)"]
                          [:lyrics "varchar(128)"]
                          [:author "varchar(128)"]]))
  (jdbc/db-do-commands db
                       (jdbc/create-table-ddl
                         :users
                         [[:id :integer :primary :key :AUTOINCREMENT]
                          [:login "varchar(128)"]
                          [:password "varchar(128)"]
                          [:email "varchar(256)"]
                          [:name "varchar(128)"]
                          [:phone "varchar(128)"]
                          [:activated :integer]
                          ]))
  (jdbc/execute! db "CREATE UNIQUE INDEX IF NOT EXISTS unique_login ON users (login)")
  )




(defn insert-sample-data []
  (jdbc/insert-multi! db
                      :cats [{
                              :name "Rock n roll"
                              :slug "rock"},
                             {
                               :name "Sad"
                               :slug "sad"},
                             {:name "Chill out"
                              :slug "chill"}])
  (jdbc/insert-multi! db
                      :users [{
                               :login     "admin"
                               :password  (hashers/encrypt "admin")
                               :email     "none@none.com"
                               :activated 1
                               }])
  (jdbc/insert-multi! db
                      :tracks [{
                                :category_id  1
                                :user_id  1
                                :title  "Відчуваю"
                                :lyrics  "
A#m
Твій голос, знайде мене завжди,
                          Fm
Твій голос - невидимі сліди,
       F#                 A#m
Я знаю, так хочеш тільки ти!

Я бачу такий чудовий світ,
Я бачу, як тане вишні цвіт,
Я знаю, так можеш тільки ти!

   A#m            D#m
   Я відчуваю тебе
         G#     A#m
   Такою, такою, такою,
   G#           D#m
   Я називаю тебе
          G#      A#m
   Весною, весною, весною
    G#               D#m
   Кожну хвилину життя
           G#       A#m
   З тобою, з тобою, з тобою
   G#           D#m
   Я називаю тебе
          G#            F#
   Весною, бо ти і є саме життя!"
                                :author "Океан Ельзи"
                                },
                               {
                                 :category_id  2
                                 :user_id  1
                                 :title  "Street Spirit"
                                 :lyrics  "
Am   Asus4  Am  Asus2
rows of     houses
Am  Asus4   Am      Asus2
all bearing down on me
Am   Asus4   Am    Asus2
i    can     feel  their
Am   Asus4 Am       Asus2
blue hands touching me
Em        Em7    Em   Em*
all these things into position
Em        Em7          Em      Em*
all these things we'll one day swallow hole"
                                 :author "Radiohead"
                                 },
                               {
                                 :category_id  2
                                 :user_id  1
                                 :title  "Yesterday"
                                 :lyrics  "
F       Em7          A7              Dm     Dm/C
Yesterday   all my troubles seemed so far away
Bb     C7              F         C  Dm  G7       Bb   F
now I need a place to hide away oh I believe in yesterday"
                                 :author "The Beatles"
                                 },
                               {
                                 :category_id  3
                                 :user_id  1
                                 :title  "Shape of my Heart"
                                 :lyrics  "
F#m          C#m         Bm       C#7
He deals the cards as a meditation
F#m          C#m         Bm     C#7
And those he plays never suspect
D          D            A        C#7
He doesn't play for the money he wins
D          C#7       F#m
He doesn't play for respect"
                                 :author "Sting"
                                 }])
  )

(defn init-db []
  (try
    (do
      (create-tables)
      (insert-sample-data))))
;(catch java.sql.BatchUpdateException e)))
; tables already exists


(defn get-cats []
  (jdbc/query db "SELECT * FROM cats"))

(defn get-track [track-id]
  (first
    (jdbc/query db
      ["SELECT * FROM tracks WHERE tracks.id = ?" track-id]
      )))

(defn get-tracks []
  (jdbc/query db "SELECT tracks.*, users.name FROM tracks JOIN users ON tracks.user_id=users.id"))

(defn get-tracks-by-cat [slug]
  (jdbc/query db ["SELECT cats.*, tracks.*, users.name FROM cats INNER JOIN tracks ON tracks.category_id = cats.id JOIN users ON tracks.user_id=users.id WHERE cats.slug = ?" slug]))

(defn insert-track [category_id user_id title lyrics author]
  (jdbc/insert! db :tracks {:category_id category_id :user_id user_id :title title :lyrics lyrics :author author} ))

(defn delete-track [id]
  (jdbc/delete! db :tracks ["id=?" id] ))

(defn update-track [id]
  (jdbc/update! db :tracks ["id=?" id] ))

(defn insert-user [map]
  (jdbc/insert! db :users (assoc (assoc map :password (hashers/encrypt (:password map))) :activated 1)))

(defn get-cat [category-slug]
  (first
    (jdbc/query
      db
      ["SELECT * FROM cats WHERE slug = ? LIMIT 1" category-slug])))


(defn get-username [username]
  (first
    (jdbc/query db
                ["SELECT * FROM users WHERE login = ?" username]
                )))