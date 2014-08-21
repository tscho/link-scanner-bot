(ns link-scanner-bot.wrs
  (:gen-class)
  (:require [org.httpkit.client :as http]
  [clojure.tools.logging :refer [log] :as logging]
  [clojure.xml :as xml]
  [clojure.zip :as zip]))

(defn getRating [site]
  (let [options { :query-params { :url site } 
                  :as :stream 
                  :user-agent "Mozilla/5.0 (Windows NT 6.1; rv:27.3) Gecko/20130101 Firefox/27.3"}]
    (http/get "http://ratings-wrs.symantec.com/rating" options
      (fn [{:keys [status headers body error]}]
        (if error
          (log :warn (str "Error looking up " site " : " error))
          (if (= status 200)
            (for [x (xml-seq
              (xml/parse body))
                :when (= :site (:tag x))]
                (:attrs x))
            (log :warn (str "Got status code [" status "] while looking up site [" site "]"))))))))
