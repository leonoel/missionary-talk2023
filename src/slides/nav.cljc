(ns slides.nav
  (:require [clojure.string :as str]
            [missionary.core :as m]))

(def slides
  [:boot
   :intro
   :resources
   :management
   :slack-v1
   :slack-v2
   :sharing
   :hypothesis
   :foundations
   :flow
   :propagation
   :conclusion])

(defn apply-steps
  ([r] r)
  ([r [f & args]] (apply f r args))
  ([r step & steps] (reduce apply-steps (apply-steps r step) steps)))

#?(:cljs
   (def svg (.-documentElement js/document)))

#?(:cljs
   (defn events [elt type]
     (fn [listener]
       (.addEventListener elt type listener)
       #(.removeEventListener elt type listener))))

(defn clamp [i n]
  (-> i (max 0) (min n)))

#?(:cljs
   (defn step [n]
     (->> (m/observe (events svg "keydown"))
       (m/eduction
         (map (fn [e]
                (case (.-key e)
                  "ArrowLeft" -1
                  "ArrowRight" 1
                  nil)))
         (remove nil?))
       (m/reductions (fn [r d] (clamp (+ r d) n)) 0)
       (m/relieve))))

#?(:cljs
   (def jump
     (let [current (.-href (.-location js/window))
           prefix-index (inc (str/last-index-of current "/"))
           prefix (subs current 0 prefix-index)
           file-name (subs current prefix-index)
           current-index (reduce-kv
                           (fn [_ i slide]
                             (when (= file-name (str (name slide) ".svg"))
                               (reduced i)))
                           nil slides)
           page-at (fn [i] (str prefix (str (name (slides i)) ".svg")))
           prev-page (let [i (dec current-index)]
                       (when-not (neg? i) (page-at i)))
           next-page (let [i (inc current-index)]
                       (when (< i (count slides)) (page-at i)))]
       (->> (m/observe (events svg "keydown"))
         (m/eduction
           (map (fn [e]
                  (case (.-key e)
                    "ArrowUp" prev-page
                    "ArrowDown" next-page
                    nil)))
           (remove nil?))
         (m/reduce (fn [_ href] (set! (.-href (.-location js/window)) href)) nil)))))

#?(:cljs
   (defn set-text! [id v]
     (set! (.-textContent (.-firstChild (.getElementById svg id))) v)))

#?(:cljs
   (defn set-hidden! [id v]
     (let [item (.getElementById svg id)]
       (if v
         (.setAttribute item "display" "none")
         (.removeAttribute item "display")))))

#?(:cljs
   (defn set-stroke! [id v]
     (let [item (.getElementById svg id)]
       (.setAttribute item "stroke" v))))

#?(:cljs
   (defn set-fill! [id v]
     (let [item (.getElementById svg id)]
       (.setAttribute item "fill" v))))