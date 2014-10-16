(ns webcam-ui.image)

(defn read-file [file-path]
  (with-open [reader (clojure.java.io/input-stream file-path)]
    (let [length (.length (clojure.java.io/file file-path))
          buffer (byte-array length)]
      (.read reader buffer 0 length)
      buffer)))

(defn base64->string [array]
  (.encodeToString (java.util.Base64/getEncoder) array))

(defn string->src [string]
  (str "data:image/png;base64," string))
