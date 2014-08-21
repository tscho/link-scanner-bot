(ns link-scanner-bot.reddit
  (:gen-class)
  (:require [org.httpkit.client :as http]
  [clojure.tools.logging :refer [log] :as logging]
  [clojure.data.json :as json]))

(defn getItems [blob]
  (:children (:data (json/read-str blob :key-fn keyword))))

(defn fetchNew 
  ([] (fetchNew nil))
  ([before]
    (log :debug (str "PARAM [before] " before))
    (let [options { :timeout 1000
                    :query-params (merge { :limit 100 } (if before {:before before} {}))
                    :user-agent "Link-Scanner-Bot" }]
      (http/get "http://www.reddit.com/comments.json" options
        (fn [{:keys [status headers body error]}]
          (if error
            (logging/spyf :warn (str "Error fetching new comments: " error) [])
            (if (= status 200)
              (getItems body)
              (log :warn (str "Got status code [" status "] when fetching new comments")))))))))

(defn extractLinks [comment]
  (let [cTxt (:body_html (:data comment))]
    (if (and cTxt (> (.length cTxt) 0))
      (let [links (map last (re-seq #"&lt;a href=\"(http\S+)\"/?&gt;" cTxt))]
        (log :debug (str "Extracted " (count links) " links from comment " (:name (:data comment))))
        (if (> (count links) 0)
          {:name (:name (:data comment))
           :links links}
          nil))
      nil)))
