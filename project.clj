(defproject link-scanner-bot "0.1.0-SNAPSHOT"
  :description "Some kinda bot"
  :url "http://sbot.schwap.org"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [http-kit "2.1.18"]
                 [org.clojure/tools.logging "0.3.0"]
                 [log4j/log4j "1.2.16"]
                 [org.clojure/data.json "0.2.2"]
                 [org.clojure/java.jdbc "0.3.3"]
                 [org.xerial/sqlite-jdbc "3.7.2"]
                 [overtone/at-at "1.2.0"]]
  :main link-scanner-bot.core)
