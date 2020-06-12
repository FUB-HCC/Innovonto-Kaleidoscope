(ns hcc.innovonto.kaleidoscope.events
  (:require [hcc.innovonto.kaleidoscope.api :as api]
            [re-frame.core :as re-frame]
            [day8.re-frame.http-fx]
            [ajax.core :as ajax]))

;; DEBUG EVENTS
(re-frame/reg-event-db
  ::debug-print-db
  (fn [db _]
    (do
      (println db)
      db)))

(re-frame/reg-event-db
  ::generic-ajax-error
  (fn [db event]
    (println (str "Ajax Error: " event))
    db))

(re-frame/reg-event-db
  ::update-color
  (fn [db [_ marker-id color]]
    (assoc-in db [:marker marker-id :color] color)))

(re-frame/reg-event-db
  ::update-icon
  (fn [db [_ marker-id icon]]
    (assoc-in db [:marker marker-id :icon] icon)))

(re-frame/reg-event-fx
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

(re-frame/reg-event-db
  ::add-marker-to-ideas
  (fn [db [_ marker-id response]]
    (apply-marker-to-db conj marker-id (:bindings (:results response)) db)))

(re-frame/reg-event-db
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
(re-frame/reg-event-fx
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


(re-frame/reg-event-fx
  ::request-app-init
  (fn [{:keys [db]} _]
    {
     :db         db
     :http-xhrio {:method          :get
                  ;;TODO optional params
                  :uri             (api/backend-url-for ::api/all-ideas nil)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::initialize-cell-map]
                  :on-failure      [::generic-ajax-error]}
     }))

;;TODO initialize: Available Marker
(re-frame/reg-event-db
  ::add-available-marker
  (fn [db [_ response]]
    (-> db
        (assoc-in [:marker-list] (api/get-marker-order-from response))
        (assoc-in [:marker] (api/get-marker-from response)))))

(re-frame/reg-event-fx
  ::initialize-available-marker
  (fn [{:keys [db]} _]
    {
     :db         db
     :http-xhrio {:method          :get
                  ;;TODO optional params
                  :uri             (api/backend-url-for ::api/available-marker nil)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::add-available-marker]
                  :on-failure      [::generic-ajax-error]}
     }))

;;TODO reg-event-db
(re-frame/reg-event-fx
  ::initialize-cell-map
  (fn [{:keys [db]} [_ response]]
    {
     :db (assoc db :all-ideas (api/convert-to-db-structure response))}))

(re-frame/reg-event-db
  ::close-idea-toolbox
  (fn [db _]
    (assoc db :active-toolbox {:title "marker-toolbox"})))

(re-frame/reg-event-db
  ::show-idea-details
  (fn [db [_ idea-id]]
    (assoc db :active-toolbox {
                               :title "idea-details"
                               :idea  idea-id
                               })))