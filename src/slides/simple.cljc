(ns slides.simple
  (:require [missionary.core :as m]
            [slides.nav :as n]))

#?(:cljs
   (def hidden
     (->> (.querySelectorAll n/svg "*[display=none]")
       (sort-by #(.getAttribute % "id"))
       (vec))))

#?(:cljs
   (def main
     (->> (n/step (count hidden))
       (m/reduce
         (fn [_ s]
           (reduce-kv
             (fn [_ i item]
               (if (< i s)
                 (.removeAttribute item "display")
                 (.setAttribute item "display" "none")))
             nil hidden)) nil)
       (m/join {} n/jump))))

#?(:cljs
   (main (.-log js/console) (.-error js/console)))