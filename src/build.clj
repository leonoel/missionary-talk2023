(ns build
  (:require [cljs.build.api :as cljs]
            [clojure.java.shell :as shell])
  (:import (java.io File)))

(defn build-one! [main]
  (cljs/build
    (merge
      {:main          (str "slides." (name main))
       :output-to     (str "release/" (name main) ".js")
       :optimizations :simple})))

(defn build-all! []
  (run! build-one! [:simple :slack-v1 :slack-v2 :propagation]))

(defn scour! [^File file]
  (shell/sh "scour"
    "-i" (.getPath file)
    "-o" (.getPath (File. "release" (.getName file)))))

(defn scour-all! []
  (run! scour! (.listFiles (File. "svg"))))

(comment
  (do
    (scour-all!)
    (build-all!)
    )
  )
