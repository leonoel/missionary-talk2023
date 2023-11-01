(ns slides.slack-v2
  (:require [missionary.core :as m]
            [slides.nav :as n]))

(def black "#000000")
(def red "#ff0000")

(def steps
  [[update :hidden assoc :graph false]
   [update :text assoc :chan-list-value ":random"]
   [update :stroke assoc :chan-view-output red]
   [update :stroke assoc :chan-view-box red]
   [update :stroke assoc :chan-posts-ws-output red]
   [update :stroke assoc :chan-posts-ws-box red]
   [update :hidden assoc
    :chan-posts-ws-process true
    :chan-posts-ws-output true
    :chan-posts-ws-value true]
   [update :stroke assoc :profile-view-output red]
   [update :stroke assoc :profile-view-box red]
   [update :stroke assoc :user-status-ws-output red]
   [update :hidden assoc
    :user-status-ws-output true
    :user-status-ws-value true]
   [update :hidden assoc
    :profile-view-process true
    :profile-view-output true]
   [update :hidden assoc
    :chan-view-process true
    :chan-view-output true]
   [n/apply-steps
    [update :hidden assoc
     :chan-view-process false
     :chan-view-output false]
    [update :text assoc
     :chan-view-arg ":random)"
     :chan-posts-ws-arg ":random)"
     :chan-posts-ws-value "\"Hello random\""]
    [update :stroke assoc
     :chan-view-output black
     :chan-view-box black
     :chan-posts-ws-output black
     :chan-posts-ws-box black
     :profile-view-output black
     :profile-view-box black
     :user-status-ws-output black]]
   [update :hidden assoc
    :chan-posts-ws-process false
    :chan-posts-ws-output false
    :chan-posts-ws-value false]])

(def states
  (->> steps
    (reductions n/apply-steps
      {:stroke {:chan-view-output black
                :chan-view-box black
                :chan-posts-ws-output black
                :chan-posts-ws-box black
                :profile-view-output black
                :profile-view-box black
                :user-status-ws-output black}
       :hidden {:graph true
                :chan-posts-ws-process false
                :chan-posts-ws-output false
                :chan-posts-ws-value false
                :chan-view-process false
                :chan-view-output false
                :profile-view-process false
                :profile-view-output false
                :user-status-ws-output false
                :user-status-ws-value false}
       :text {:chan-list-value ":clojure"
              :chan-view-arg ":clojure)"
              :chan-posts-ws-arg ":clojure)"
              :chan-posts-ws-value "\"Hello clojure\""}})
    (vec)))

#?(:cljs
   (def app
     (->> (n/step (count steps))
       (m/reduce (fn [_ i]
                   (let [{:keys [stroke hidden text]} (states i)]
                     (reduce-kv (fn [_ k v] (n/set-text! (name k) v)) nil text)
                     (reduce-kv (fn [_ k v] (n/set-hidden! (name k) v)) nil hidden)
                     (reduce-kv (fn [_ k v] (n/set-stroke! (name k) v)) nil stroke))) nil)
       (m/join {} n/jump))))

#?(:cljs
   (app (.-log js/console) (.-error js/console)))