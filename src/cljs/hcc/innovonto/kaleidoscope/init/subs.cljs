(ns hcc.innovonto.kaleidoscope.init.subs
  (:require [re-frame.core :as rf]))

(rf/reg-sub
  ::modal-open?
    (fn [db]
      (:show-init-modal (:init db))))

(rf/reg-sub
  ::initialized?
  (fn [db]
    (:initialization-done (:init db))))

(rf/reg-sub
  ::datasource-type
  (fn [db]
    (:datasource-type (:init db))))

(rf/reg-sub
  ::sparql-endpoint
  (fn [db]
    (:sparql-endpoint (:init db))))

;; see: https://stackoverflow.com/questions/28269117/clojure-regex-if-string-is-a-url-return-string
;; I additionally included 127.0.0.1 and localhost, to enable testing against a local SPARQL endpoint
(def url-pattern #"(?i)^(?:(?:https?|ftp)://)(?:\S+(?::\S*)?@)?(?:localhost|(?!(?:169\.254|192\.168)(?:\.\d{1,3}){2})(?!172\.(?:1[6-9]|2\d|3[0-1])(?:\.\d{1,3}){2})(?:[1-9]\d?|1\d\d|2[01]\d|22[0-3])(?:\.(?:1?\d{1,2}|2[0-4]\d|25[0-5])){2}(?:\.(?:[1-9]\d?|1\d\d|2[0-4]\d|25[0-4]))|(?:(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)(?:\.(?:[a-z\u00a1-\uffff0-9]-*)*[a-z\u00a1-\uffff0-9]+)*(?:\.(?:[a-z\u00a1-\uffff]{2,}))\.?)(?::\d{2,5})?(?:[/?#]\S*)?$")

(rf/reg-sub
  ::sparql-endpoint-invalid?
  (fn [db]
    (if-let [sparql-endpoint (:sparql-endpoint (:init db))]
      (do
        (println "Endpoint is now: " sparql-endpoint)
        (println (str "Endpoint valid? " (some? (re-matches url-pattern sparql-endpoint))))
        (re-matches url-pattern sparql-endpoint))
      false)))

(rf/reg-sub
  ::configured?
  (fn [db]
    (let [datasource-type (:datasource-type (:init db))]
      (case datasource-type
        :file-upload (some? (get-in db [:init :filename]))
        :sparql-endpoint (some? (get-in db [:init :sparql-endpoint]))
        :innovonto-core true
        false))))