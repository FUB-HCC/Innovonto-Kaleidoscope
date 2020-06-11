(ns hcc.innovonto.kaleidoscope.subs
  (:require [re-frame.core :as re-frame]))


(defn fmap [f m]
  (zipmap (keys m) (map f (vals m))))

(re-frame/reg-sub
  ::all-marker
  (fn [db _]
    (:marker db)))

(re-frame/reg-sub
  ::marker-list
  (fn [db _]
    (:marker-list db)))

(re-frame/reg-sub
  ::ordered-available-marker
  :<- [::available-marker]
  :<- [::marker-list]
  (fn [[all-marker marker-list] _]
    (filterv some? (map (fn [id] (get all-marker id)) marker-list))))

(re-frame/reg-sub
  ::available-marker
  :<- [::all-marker]
  (fn [all-marker _]
    (into {} (filter #(not= (:state (second %)) :selected) all-marker))))

(re-frame/reg-sub
  ::selected-marker
  :<- [::all-marker]
  (fn [all-marker _]
    (into {} (filter #(= (:state (second %)) :selected) all-marker))))

(defn add-all-marker [all-marker idea]
  (assoc idea :marker (mapv #(get all-marker %) (:marker idea))))

(re-frame/reg-sub
  ::all-ideas
  (fn [db _]
    (fmap (partial add-all-marker (:marker db)) (:all-ideas db))))

(re-frame/reg-sub
  ::active-toolbox
  (fn [db _]
    (:active-toolbox db)))

(re-frame/reg-sub
  ::idea-details
  :<- [::all-ideas]
  (fn [all-ideas [_ idea-id]]
    (get all-ideas idea-id)))