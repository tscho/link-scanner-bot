(ns link-scanner-bot.core
  (:gen-class)
  (:require 
    [clojure.tools.logging :refer [log] :as logging]
    [overtone.at-at :as atat]
    [link-scanner-bot.reddit :as reddit]
    [link-scanner-bot.wrs :as wrs]
    [link-scanner-bot.db :as db]))

(defonce lastPost (ref nil))
(defonce fetch-delay (ref 3500))

(defonce sched-pool (atat/mk-pool))

(defn getLinkObjects
  [comments]
  (log :debug (str "Getting link objects for " (count comments) " comments"))
  (let
    [linkObjs (filter (complement nil?) (map reddit/extractLinks comments))]
    (log :debug (str "Got " (count linkObjs) " link objects"))
    linkObjs))

(defn rate
  [comment]
  (log :debug (str "Rating " (count (comment :links)) " links"))
  (dorun 
    (map
      (fn [url]
        (db/add-link
          (let [{rating :r id :id} (first @(wrs/getRating url))]
            (log :debug (str "Rating " rating " id " id))
            {:url url
             :rating rating
             :partition id
             :name (comment :name)})))
      (comment :links))))

(defn fetchComments []
  (let [items @(reddit/fetchNew @lastPost)
        ni (count items)]
    (log :info (str "Got " ni " items"))
    (dosync
      (if (> ni 0)
        (ref-set lastPost (:name (:data (first items))))
        (ref-set lastPost nil))
      (if (< ni 100)
        (ref-set fetch-delay (+ 500 @fetch-delay))
        (ref-set fetch-delay 3500)))
    (dorun (map rate (getLinkObjects items)))
    (atat/after @fetch-delay fetchComments sched-pool)))

(defn -main
  "The entry point, yo"
  [& args]
  ;; work around dangerous default behaviour in Clojure
  ;; (alter-var-root #'*read-eval* (constantly false))
  (db/initDb)
  (log :info "Starting setup")
  (fetchComments)
  (log :info  "Started processing"))
