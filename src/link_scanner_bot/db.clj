(ns link-scanner-bot.db
  (:gen-class)
  (:use link-scanner-bot.sqlite)
  (:require [clojure.tools.logging :refer [log] :as logging]
    [clojure.java.jdbc :as j]))

(defn create-tables
  []
  (j/create-table-ddl
    :linkRatings
    [:id "INT" "PRIMARY KEY"]
    [:url "VARCHAR(256)" "NOT NULL"]
    [:partition "VARCHAR(256)" "NOT NULL"]
    [:name "VARCHAR(32)" "NOT NULL"]
    [:rating "CHAR(1)"]))

(defn add-link
  [linkInfo]
  (j/insert! db-conn
    :linkRatings
      linkInfo))

(defn createDb
  []
  (j/db-do-commands new-db-conn
    (create-tables)))

(defn initDb
  []
  (log :info "Initializing db")
  (if-not (dbExists?)
    (createDb)))
