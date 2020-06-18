(ns hcc.innovonto.kaleidoscope.init.events
  (:require [re-frame.core :as rf]
            [sparql-client.client :as sparql-client]
            [hcc.innovonto.kaleidoscope.api :as api]))


(rf/reg-event-db
  ::change-datasource
  (fn [db [_ datasource-type]]
    (assoc-in db [:init :datasource-type] datasource-type)))

(rf/reg-event-db
  ::change-sparql-endpoint
  (fn [db [_ sparql-endpoint]]
    (assoc-in db [:init :sparql-endpoint] sparql-endpoint)))

(rf/reg-event-fx
  ::init-app
  (fn [{:keys [db]} [_ _]]
    (println "init kaleidoscope! Config is" (:init db))
    {
     :db db

     ;;Dispatch n
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

(rf/reg-event-fx
  ::update-ideas
  (fn [{:keys [db]} _]
    {
     :db         (assoc-in db [:sync-state] :loading)
     :http-xhrio (sparql-client/query-request {
                                       :query      (api/all-ideas-query 81)
                                       :on-success [::initialize-cell-map]
                                       :on-failure [::generic-ajax-error]})}))

(rf/reg-event-fx
  ::update-available-markers
  (fn [{:keys [db]} _]
    {
     :db         (assoc-in db [:sync-state] :loading)
     :http-xhrio (sparql-client/query-request {
                                       :query      (api/available-markers-query)
                                       :on-success [::add-available-marker]
                                       :on-failure [::generic-ajax-error]})}))