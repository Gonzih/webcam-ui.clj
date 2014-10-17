(ns webcam-ui.core ; .cljs
  (:require-macros
   [cljs.core.async.macros :as asyncm :refer (go go-loop)])
  (:require
   ;; <other stuff>
   [cljs.core.async :as async :refer (<! >! put! chan)]
   [taoensso.sente  :as sente :refer (cb-success?)]))

(enable-console-print!)

(defonce user-id (atom 1))

;;; Add this: --->
(let [{:keys [chsk ch-recv send-fn state]}
     ; Note the same path as before
      (sente/make-channel-socket! "/chsk"
                                  :user-id-fn (fn [_] (deref user-id))
       {:type :auto})]
  (def chsk       chsk)
  (def ch-chsk    ch-recv) ; ChannelSocket's receive channel
  (def chsk-send! send-fn) ; ChannelSocket's send API fn
  (def chsk-state state))  ; Watchable, read-only atom

(go-loop []
  (let [event (<! ch-chsk)]
    (println "==================")
    (event-msg-handler event))
  (recur))

(defmulti event-msg-handler :id)

(defmethod event-msg-handler :default ; Fallback
  [{:as ev-msg :keys [event]}]
  (prn "Unhandled event: %s" event))

(defmethod event-msg-handler :chsk/state
  [{:as ev-msg :keys [?data]}]
  (if (= ?data {:first-open? true})
    (prn "Channel socket successfully established!")
    (prn "Channel socket state change: %s" ?data)))

(defmethod event-msg-handler :chsk/recv
  [{:as ev-msg :keys [?data]}]
  (prn "Push event from server: %s" ?data)
  (data-handler ?data))

(defmulti data-handler first)

(defmethod data-handler :default
  [data]
  (prn "Unhandled data: " data))

(defmethod data-handler :webcam-ui.core/data
  [[_ data]]
  (set! (.-src (js/document.getElementById "target"))
        (:src-data data)))
