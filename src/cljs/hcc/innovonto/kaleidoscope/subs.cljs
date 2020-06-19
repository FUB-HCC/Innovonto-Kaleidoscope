(ns hcc.innovonto.kaleidoscope.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub
  ::active-tab
  (fn [db _]
    (:active-tab db)))

(defn fmap [f m]
  (zipmap (keys m) (map f (vals m))))

(rf/reg-sub
  ::all-marker
  (fn [db _]
    (:marker db)))

(rf/reg-sub
  ::marker-list
  (fn [db _]
    (:marker-list db)))

(rf/reg-sub
  ::ordered-available-marker
  :<- [::available-marker]
  :<- [::marker-list]
  (fn [[all-marker marker-list] _]
    (filterv some? (map (fn [id] (get all-marker id)) marker-list))))

(rf/reg-sub
  ::available-marker
  :<- [::all-marker]
  (fn [all-marker _]
    (into {} (filter #(not= (:state (second %)) :selected) all-marker))))

(rf/reg-sub
  ::selected-marker
  :<- [::all-marker]
  (fn [all-marker _]
    (into {} (filter #(= (:state (second %)) :selected) all-marker))))

(defn add-all-marker [all-marker idea]
  (assoc idea :marker (mapv #(get all-marker %) (:marker idea))))

(rf/reg-sub
  ::all-ideas
  (fn [db _]
    (fmap (partial add-all-marker (:marker db)) (:all-ideas db))))

(rf/reg-sub
  ::active-toolbox
  (fn [db _]
    (:active-toolbox db)))

(rf/reg-sub
  ::idea-details
  :<- [::all-ideas]
  (fn [all-ideas [_ idea-id]]
    (get all-ideas idea-id)))

(rf/reg-sub
  ::sync-state
  (fn [db _]
    (:sync-state db)))