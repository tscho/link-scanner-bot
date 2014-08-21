(ns link-scanner-bot.sqlite
  (:import (java.io.File)))

(def dbconfig {
  :classname "org.sqlite.JDBC"
  :subprotocol "sqlite"
  :subname "db/link-scanner-bot.sqlite"
})

(def new-db-conn (merge dbconfig {:create true}))
(def db-conn dbconfig)

(defn dbExists?
  []
  (let
    [f (new java.io.File "db/link-scanner-bot.sqlite")]
    (.exists f)))

