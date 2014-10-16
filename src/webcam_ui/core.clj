(ns webcam-ui.core
  (:require
    [compojure.core :refer :all]
    [compojure.route :as route]
    [taoensso.sente :as sente]
    [webcam-ui.image :refer :all]
    [webcam-ui.screenshot :refer :all]))

(let [{:keys [ch-recv send-fn ajax-post-fn ajax-get-or-ws-handshake-fn
              connected-uids]}
      (sente/make-channel-socket! {})]
  (def ring-ajax-post                ajax-post-fn)
  (def ring-ajax-get-or-ws-handshake ajax-get-or-ws-handshake-fn)
  (def ch-chsk                       ch-recv) ; ChannelSocket's receive channel
  (def chsk-send!                    send-fn) ; ChannelSocket's send API fn
  (def connected-uids                connected-uids)) ; Watchable, read-only atom

(defn index-handler [req]
  (take-screenshot!)
  (let [src (-> "/tmp/00000001.png"
                read-file
                base64->string
                string->src)]
    (str "<img src='" src "'/>")))

(defroutes app
  (GET "/" [] index-handler)
  (GET  "/chsk" req (ring-ajax-get-or-ws-handshake req))
  (POST "/chsk" req (ring-ajax-post                req)))
