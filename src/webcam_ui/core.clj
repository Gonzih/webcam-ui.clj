(ns webcam-ui.core
  (:require
    [compojure.core :refer [defroutes GET POST]]
    [webcam-ui.image :refer :all]
    [webcam-ui.screenshot :refer :all]
    [ring.middleware.file :refer :all]
    [ring.middleware.reload :as reload]
    [compojure.handler :refer [site]]
    [taoensso.sente :as sente]
    [org.httpkit.server :refer [run-server]]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids)) ; Watchable, read-only atom

(defn layout [yield]
  (str "<html>
        <head>
          <script src='/javascripts/webcam_ui.js'></script>
        </head>
        <body>"
       (str @connected-uids)
       yield
       "</doby>
        </html>"))

(defn index-handler [req]
  ; (take-screenshot!)
  (let [src (-> "/tmp/00000001.png"
                read-file
                base64->string
                string->src)]
    (layout (str "<img src='" src "'/>"))))

(defroutes main-handler
  (GET "/" [] index-handler)
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req)))

(def app (wrap-file main-handler "resources/public"))

(future
  (map #(chsk-send! % {:test :test}) @connected-uids)
  (Thread/sleep 3000))

(defn in-dev? [_] true)

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (let [handler (if (in-dev? args)
                  (reload/wrap-reload (site #'app)) ;; only reload when dev
                  (site app))]
    (run-server handler {:port 8080})))
