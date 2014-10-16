(defproject webcam-ui "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/clojurescript "0.0-2371"]
                 [com.taoensso/sente "1.2.0"]
                 [compojure "1.2.0"]
                 [http-kit "2.1.19"]
                 [ring/ring-devel "1.3.1"]
                 [ring/ring-core "1.3.1"]
                 [javax.servlet/servlet-api "2.5"]]
  :plugins [[lein-cljsbuild "1.0.3"]
            [lein-figwheel "0.1.4-SNAPSHOT"]]
  ;:hooks [leiningen.cljsbuild]
  :ring {:handler webcam-ui.core/app}
  :main webcam-ui.core
  :cljsbuild {:builds [{:id "dev"
                        :source-paths ["src-cljs/webcam_ui"]
                        :compiler {:output-to "resources/public/javascripts/webcam_ui.js"
                                   ; :source-map true
                                   :output-dir "resources/public/javascripts/out"
                                   :optimizations :whitespace}}]})
