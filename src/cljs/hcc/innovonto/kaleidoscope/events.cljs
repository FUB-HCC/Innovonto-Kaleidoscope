(ns hcc.innovonto.kaleidoscope.events
  (:require [hcc.innovonto.kaleidoscope.api :as api]
            [re-frame.core :as rf]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]
            [sparql-client.format :as sparql]))

;; DEBUG EVENTS
(rf/reg-event-db
  ::debug-print-db
  (fn [db _]
    (do
      (println db)
      db)))

;;TODO add toast to ui
(rf/reg-event-db
  ::generic-ajax-error
  (fn [db event]
    (println (str "Ajax Error: " event))
    db))

(rf/reg-event-db
  ::switch-tab
  (fn [db [_ tab]]
    (assoc db :active-tab tab)))

(rf/reg-event-db
  ::update-color
  (fn [db [_ marker-id color]]
    (assoc-in db [:marker marker-id :color] color)))

(rf/reg-event-db
  ::update-icon
  (fn [db [_ marker-id icon]]
    (assoc-in db [:marker marker-id :icon] icon)))

;;TODO wtf?
(rf/reg-event-fx
  ::remove-marker
  (fn [{:keys [db]} [_ marker-id]]
    {
     :db         (update-in db [:marker marker-id] dissoc :state)
     :http-xhrio {
                  :method          :get
                  ;;TODO optional params
                  :uri             (api/backend-url-for ::api/all-ideas nil)
                  :params          {:marker-id marker-id}
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::remove-marker-from-ideas marker-id]
                  :on-failure      [::generic-ajax-error]}
     }))

(defn apply-marker-to-one-idea [apply-fn db idea-id marker-id]
  (update-in db [:all-ideas idea-id :marker] apply-fn marker-id))

(defn apply-marker-to-db [apply-fn marker-id idea-ids db]
  (if (empty? idea-ids)
    db
    (let [all-ideas (:all-ideas db)
          head (api/get-id (first idea-ids))
          tail (rest idea-ids)]
      (if (contains? all-ideas head)
        (apply-marker-to-db apply-fn marker-id tail (apply-marker-to-one-idea apply-fn db head marker-id))
        (apply-marker-to-db apply-fn marker-id tail db)))))

(rf/reg-event-db
  ::add-marker-to-ideas
  (fn [db [_ marker-id response]]
    (apply-marker-to-db conj marker-id (:bindings (:results response)) db)))

(rf/reg-event-db
  ::remove-marker-from-ideas
  (fn [db [_ marker-id response]]
    (apply-marker-to-db disj marker-id (:bindings (:results response)) db)))


(defn to-selected-marker [marker]
  {
   :id    (:id marker)
   :label (:label marker)
   :state :selected
   :icon  (get marker :icon "fiber_manual_record")
   :color (get marker :color "#9C9C9C")
   })


;;TODO check if i can do to-selected marker without explicit argument
;;TODO handle nil in update-in (fnil inc 0)
(rf/reg-event-fx
  ::add-marker
  (fn [{:keys [db]} [_ marker-id]]
    {:db         (update-in db [:marker marker-id] to-selected-marker)
     :http-xhrio {
                  :method          :get
                  :uri             (api/backend-url-for ::api/ideas-by-marker {:id (:marker-id marker-id)})
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::add-marker-to-ideas marker-id]
                  :on-failure      [::generic-ajax-error]}
     }))

(rf/reg-event-db
  ::close-idea-toolbox
  (fn [db _]
    (assoc db :active-toolbox {:title "marker-toolbox"})))

;;TODO sparql-query
(rf/reg-event-db
  ::show-idea-details
  (fn [db [_ idea-id]]
    (assoc db :active-toolbox {
                               :title "idea-details"
                               :idea  idea-id
                               })))