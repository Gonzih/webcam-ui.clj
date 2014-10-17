(ns webcam-ui.core; {{{
  (:require
    [webcam-ui.image :refer :all]
    [webcam-ui.screenshot :refer :all]
    [compojure.core :refer [defroutes GET POST]]
    [compojure.handler :refer [site]]
    [clojure.core.async :as async :refer [go go-loop <! chan put!]]
    [org.httpkit.server :refer [run-server]]
    [ring.middleware.file :refer [wrap-file]]
    [ring.middleware.reload :as reload]
    [ring.middleware.session :refer [wrap-session]]
    [ring.util.response :refer [response]]
    [taoensso.sente :as sente])); }}}

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn; {{{
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids)) ; Watchable, read-only atom; }}}

(defn read-src-data! []; {{{
  (-> "/tmp/00000001.png"
      read-file
      base64->string
      string->src)); }}}

(def data-chan (chan 1))

(defn start-image-generator! []
  (future
    (loop []
      (when (< 0 (count (:any @connected-uids)))
        (println "Taking screenshot")
        (take-screenshot!)
        (put! data-chan (read-src-data!)))
      (Thread/sleep 1000)
      (recur))))

(defn start-broadcaster! []
  (println "Starting broadcaster")
  (go-loop
    []
    (let [src (<! data-chan)]
      (println (format "Broadcasting server>user: %s" @connected-uids))
      (doseq [uid (:any @connected-uids)]
        (chsk-send! uid
                    [::data
                     {:src-data src}])))
    (recur)))

(defn layout [yield]; {{{
  (str "<html>
        <head>
          <script src='/javascripts/webcam_ui.js'></script>
        </head>
        <body>"
       yield
       "</doby>
        </html>")); }}}

(defn index-handler [req]
  ; (take-screenshot!)
  (let [src (read-src-data!)]
    (layout (str "<img id='target' src='" src "'/>"))))

(defroutes main-handler; {{{
  (GET "/" [] index-handler)
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req))); }}}

(def app (-> main-handler
             (wrap-file "resources/public")
             wrap-session))

(defn in-dev? [_] true)

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (start-image-generator!)
  (start-broadcaster!)
  (let [handler (if (in-dev? args)
                  (reload/wrap-reload (site #'app)) ;; only reload when dev
                  (site app))]
    (println "Running web server")
    (run-server handler {:port 8080})))
