(defproject webcam-ui "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-ring "0.8.11"]]
  :ring {:handler webcam-ui.core/app}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [com.taoensso/sente "1.2.0"]
                 [compojure "1.2.0"]])
