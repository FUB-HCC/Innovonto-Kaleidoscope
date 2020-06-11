(ns ^:figwheel-hooks hcc.innovonto.kaleidoscope.core
  (:require
    [goog.dom :as gdom]
    [reagent.core :as reagent]
    [hcc.innovonto.kaleidoscope.views :as views]
    [re-frame.core :as re-frame]
    [hcc.innovonto.kaleidoscope.db :as db]))

;;TODO remove
(defn multiply [a b] (* a b))


(defn get-app-element []
  (gdom/getElement "app"))

(re-frame/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(defn mount [el]
  (re-frame/clear-subscription-cache!)
  (re-frame/dispatch-sync [::initialize-db])
  (reagent/render-component [views/kaleidoscope-app] el))

(defn mount-app-element []
  (when-let [el (get-app-element)]
    (mount el)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)

;; specify reload hook with ^;after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

