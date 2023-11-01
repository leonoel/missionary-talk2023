(ns slides.propagation
  (:require [missionary.core :as m]
            [slides.nav :as n]))

(def idle "#000000")
(def ready "#116cd3")

(def steps
  [[update :stack conj "call swap!"]
   [update :stack conj "step watch"]
   [update :edges assoc :watch ready]
   [update :queue conj "<input"]
   [update :state assoc :input "?"]
   [update :stack conj "step <input (left)"]
   [update :edges assoc :input-left ready]
   [update :state assoc :latest-left "?"]
   [update :stack conj "step latest"]
   [update :edges assoc :latest ready]
   [update :queue conj "<twice"]
   [update :stack pop]
   [update :stack pop]
   [update :stack conj "step <input (right)"]
   [update :edges assoc :input-right ready]
   [update :state assoc :latest-right "?"]
   [update :stack pop]
   [update :queue subvec 1]
   [update :state assoc :twice "?"]
   [update :stack conj "step <twice"]
   [update :edges assoc :twice ready]
   [update :stack conj "transfer <twice"]
   [update :edges assoc :twice idle]
   [update :stack conj "transfer latest"]
   [update :edges assoc :latest idle]
   [update :stack conj "transfer <input (left)"]
   [update :edges assoc :input-left idle]
   [update :stack conj "transfer watch"]
   [update :edges assoc :watch idle]
   [update :stack conj "call @!input"]
   [update :stack pop]
   [update :stack pop]
   [update :state assoc :input "1"]
   [update :stack pop]
   [update :state assoc :latest-left "1"]
   [update :stack conj "transfer <input (right)"]
   [update :edges assoc :input-right idle]
   [update :stack pop]
   [update :state assoc :latest-right "1"]
   [update :stack conj "call +"]
   [update :stack pop]
   [update :stack pop]
   [update :state assoc :twice "2"]
   [update :stack pop]
   [update :stack conj "call (prn 2)"]
   [update :stack pop]
   [update :stack pop]
   [update :queue pop]
   [update :stack pop]
   [update :stack pop]])

(def states
  (->> steps
    (reductions n/apply-steps
      {:stack []
       :queue []
       :edges {:watch idle
               :input-left idle
               :input-right idle
               :latest idle
               :twice idle}
       :state {:input        "0"
               :latest-left  "0"
               :latest-right "0"
               :twice        "0"}})
    (vec)))

#?(:cljs
   (def stack-items
     (into [] (map (fn [i] (.getElementById n/svg (str "stack-" i)))) (range 10))))

#?(:cljs
   (def queue-items
     (into [] (map (fn [i] (.getElementById n/svg (str "queue-" i)))) (range 4))))

#?(:cljs
   (do
     (.removeAttribute (.getElementById n/svg "stack") "display")
     (.removeAttribute (.getElementById n/svg "queue") "display")
     ((->> (n/step (count steps))
        (m/reduce
          (fn [_ i]
            (let [{:keys [edges state stack queue]} (states i)]
              (reduce-kv (fn [_ k v] (n/set-stroke! (str "ready-" (name k)) v)) nil edges)
              (reduce-kv (fn [_ k v]
                           (let [state-id (str "state-" (name k))
                                 box-id (str "box-" (name k))
                                 color (if (= v "?") ready idle)]
                             (n/set-text! state-id v)
                             (n/set-fill! state-id color)
                             (n/set-stroke! box-id color)))
                nil state)
              (reduce-kv
                (fn [_ i item]
                  (if (< i (count stack))
                    (do (.removeAttribute item "display")
                        (set! (.-textContent (.item (.getElementsByTagName item "tspan") 0)) (stack i)))
                    (.setAttribute item "display" "none")))
                nil stack-items)
              (reduce-kv
                (fn [_ i item]
                  (if (< i (count queue))
                    (do (.removeAttribute item "display")
                        (set! (.-textContent (.item (.getElementsByTagName item "tspan") 0)) (queue i)))
                    (.setAttribute item "display" "none")))
                nil queue-items))) nil)
        (m/join {} n/jump))
      (.-log js/console) (.-error js/console))))