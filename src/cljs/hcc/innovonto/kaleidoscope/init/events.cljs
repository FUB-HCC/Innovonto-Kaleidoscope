(ns hcc.innovonto.kaleidoscope.init.events
  (:require [re-frame.core :as rf]
            [sparql-client.client :as sparql-client]
            [hcc.innovonto.kaleidoscope.api :as api]
            [clojure.string :as string]
            [hcc.innovonto.kaleidoscope.db :as db]))

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

;[[::tracking/track {:type :validate-annotation-candidate
;                                       :id   id
;                                       :text (:text (:current-annotation-candidate (:icv db)))}]
;                    [::move-to-next-annotation-candidate]]
(rf/reg-event-fx
  ::init-app
  (fn [{:keys [db]} [_ _]]
    (println "init kaleidoscope! Config is" (:init db))
    {
     ;;TODO update the sparql-endpoint that is used by the sparql-client
     :db         (-> db
                     (assoc-in [:init :initialization-done] true)
                     (assoc-in [:init :show-init-modal] false))
     :dispatch-n [
                  [::update-ideas]
                  [::update-available-markers]
                  [::update-snapshots]]
     }))

;;TODO init-ideas, init-marker, init snapshots
;;TODO start a new kaleidoscope session at the server, with the given config.
;; - Create a session-identifier
;; - Upload and parse the file if needed
;; - Check the sparql-endpoint if needed
;; return idea map config:
;; - :marker
;; -
;; -

;;API: GET ALL
(defn get-id [binding-response]
  (last (string/split (:value (:idea binding-response)) #"/")))

(defn convert [element]
  {:id      (get-id element)
   :content (:value (:content element))
   :marker  #{}})

(defn convert-one [result [head & tail]]
  (if (nil? head)
    result
    (convert-one (assoc result (get-id head) (convert head)) tail)))

(defn convert-to-db-structure [server-response]
  (let [response-ideas (:bindings (:results server-response))
        result-map {}]
    (convert-one result-map response-ideas)))

(rf/reg-event-db
  ::initialize-cell-map
  (fn [db [_ response]]
    (-> db
        (assoc :sync-state :up-to-date)
        (assoc :all-ideas (convert-to-db-structure response)))))

(rf/reg-event-fx
  ::update-ideas
  (fn [{:keys [db]} _]
    {
     :db         (assoc-in db [:sync-state] :loading)
     :http-xhrio (sparql-client/query-request {
                                               :query      (api/all-ideas-query 81)
                                               :on-success [::initialize-cell-map]
                                               :on-failure [::generic-ajax-error]})}))

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
     :http-xhrio (sparql-client/query-request {
                                               :query      (api/available-markers-query)
                                               :on-success [::initialize-available-marker-component]
                                               :on-failure [::generic-ajax-error]})}))

(rf/reg-event-fx
  ::update-snapshots
  (fn [{:keys [db]} _]
    {
     :db db
     }))