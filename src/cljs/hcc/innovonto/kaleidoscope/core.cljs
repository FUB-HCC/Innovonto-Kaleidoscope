(ns ^:figwheel-hooks hcc.innovonto.kaleidoscope.core
  (:require
    [goog.dom :as gdom]
    [hcc.innovonto.kaleidoscope.views :as views]
    [re-frame.core :as rf]
    [hcc.innovonto.kaleidoscope.db :as db]
    [reagent.dom :as rdom]
    [sparql-client.client :as sparql-client]))

;;TODO remove after other tests have been implemented
(defn multiply [a b] (* a b))


(defn get-app-element []
  (gdom/getElement "app"))

(rf/reg-event-db
  ::initialize-db
  (fn [_ _]
    db/default-db))

(defn mount [el]
  (rf/clear-subscription-cache!)
  (rf/dispatch-sync [::initialize-db])
  (rdom/render [views/kaleidoscope-app] el))

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

