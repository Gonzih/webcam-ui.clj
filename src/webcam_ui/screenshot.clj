(ns webcam-ui.screenshot)

(defn take-screenshot! []
  (clojure.java.shell/sh
    "mplayer" "-vo" "png" "-frames" "1" "tv://"
    :dir "/tmp"))
