(ns webcam-ui.screenshot
  (:require [clojure.java.shell :as shell]))

(defn take-screenshot! []
  (Thread/sleep 1000)
  (shell/sh
    "mplayer" "-vo" "png" "-frames" "1" "tv://"
    :dir "/tmp"))
