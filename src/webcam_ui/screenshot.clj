(ns webcam-ui.screenshot
  (:require [clojure.java.shell :as shell]))

(defn take-screenshot! []
  (shell/sh
    "mplayer" "-vo" "png" "-frames" "1" "tv://"
    :dir "/tmp"))
