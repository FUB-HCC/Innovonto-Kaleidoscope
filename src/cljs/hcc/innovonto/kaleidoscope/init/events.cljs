(ns hcc.innovonto.kaleidoscope.init.events
  (:require [re-frame.core :as rf]))


(rf/reg-event-db
  ::change-datasource
  (fn [db [_ datasource-type]]
    (assoc-in db [:init :datasource-type] datasource-type)))

(rf/reg-event-db
  ::change-sparql-endpoint
  (fn [db [_ sparql-endpoint]]
    (assoc-in db [:init :sparql-endpoint] sparql-endpoint)))

;;TODO start a new kaleidoscope session at the server, with the given config.
;; - Create a session-identifier
;; - Upload and parse the file if needed
;; - Check the sparql-endpoint if needed
;; return idea map config:
;; - :marker
;; -
;; -
(rf/reg-event-fx
  ::init-kaleidoscope
  (fn [{:keys [db]} [_ _]]
    (println "init kaleidoscope! Config is" (:init db))
    {
     :db db
     }))