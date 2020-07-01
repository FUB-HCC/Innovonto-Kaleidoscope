(ns hcc.innovonto.kaleidoscope.init.events
  (:require [re-frame.core :as rf]
            [hcc.innovonto.kaleidoscope.api :as api]
            [clojure.string :as string]
            [hcc.innovonto.kaleidoscope.db :as db]
            [ajax.core :as ajax]
            [cljs.pprint :as pprint]))

(rf/reg-event-db
  ::reset
  (fn [db _]
    db/default-db))

(rf/reg-event-db
  ::toggle-modal
  (fn [db _]
    (update-in db [:init :show-init-modal] not)))

(rf/reg-event-db
  ::change-datasource
  (fn [db [_ datasource-type]]
    (assoc-in db [:init :datasource-type] datasource-type)))

(rf/reg-event-db
  ::change-sparql-endpoint
  (fn [db [_ sparql-endpoint]]
    (assoc-in db [:init :sparql-endpoint] sparql-endpoint)))

;;TODO spec: session-id
(rf/reg-event-fx
  ::session-init-successful
  (fn [{:keys [db]} [_ response]]
    (println "init successful. Session-id is: " (:session-id response))
    {
     :db         (-> db
                     (assoc-in [:init :show-init-modal] false))
     :dispatch-n [
                  [::update-ideas]
                  [::update-available-markers]
                  [::update-snapshots]]
     }))

;;TODO implement: show red frames, error messages in the init-modal
(rf/reg-event-db
  ::error-in-init-config
  (fn [db [_ errors]]
    db))

(rf/reg-event-fx
  ::initialize-session
  (fn [{:keys [db]} [_ _]]
    (println "init kaleidoscope! Config is" (:init db))
    {
     ;;TODO set loading state of init button :initialization-done :in-progress?
     :db         (assoc-in db [:init :initialization-done] true)
     :http-xhrio {
                  :method          :put
                  :uri             (api/backend-url-for ::api/session nil)
                  :params          (:init db)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::session-init-successful]
                  :on-failure      [::error-in-init-config]}}))

;;API: GET ALL
(defn get-id [binding-response]
  (last (string/split (:value (:idea binding-response)) #"/")))

(defn get-local-id [iri]
  (last (string/split iri #"/")))

(defn convert-one [input]
  (let [local-id (get-local-id (:value (:idea input)))]
    [local-id
     {
      :id      local-id
      :creator (get-local-id (:value (:creator input)))
      :content (:value (:content input))
      :marker  #{}}]))

(defn convert-to-db-structure [server-response]
  (let [response-ideas (:bindings (:results server-response))
        mappings (into [] (map convert-one response-ideas))]
    (into (sorted-map) mappings)))

(rf/reg-event-db
  ::initialize-cell-map
  (fn [db [_ response]]
    (-> db
        (assoc :sync-state :up-to-date)
        (assoc :all-ideas (convert-to-db-structure response)))))

;;TODO: own loading state for: cellmap, marker-component, snapshot-component
(rf/reg-event-fx
  ::update-ideas
  (fn [{:keys [db]} _]
    {
     :db         (assoc-in db [:sync-state] :loading)
     ;;TODO limit?
     :http-xhrio {
                  :method          :get
                  :uri             (api/backend-url-for ::api/all-ideas nil)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::initialize-cell-map]
                  :on-failure      [::generic-ajax-error]}}))


(rf/reg-event-db
  ::initialize-available-marker-component
  (fn [db [_ response]]
    (-> db
        (assoc-in [:marker-list] (api/get-marker-order-from response))
        (assoc-in [:marker] (api/get-marker-from response)))))


(rf/reg-event-fx
  ::update-available-markers
  (fn [{:keys [db]} _]
    {
     :db         (assoc-in db [:sync-state] :loading)
     :http-xhrio {
                  :method          :get
                  :uri             (api/backend-url-for ::api/available-marker nil)
                  :format          (ajax/json-request-format)
                  :response-format (ajax/json-response-format {:keywords? true})
                  :on-success      [::initialize-available-marker-component]
                  :on-failure      [::generic-ajax-error]}}))

(rf/reg-event-fx
  ::update-snapshots
  (fn [{:keys [db]} _]
    {
     :db db
     }))