(ns hcc.innovonto.kaleidoscope.api
  (:require [clojure.string :as str]
            [reitit.core :as r]))

;;TODO how to configure this?
(def backend-endpoint "http://localhost:6001")

;;;;TODO include session
(def api-router
  (r/router
    ["/api"
     ["/session" ::session]
     ["/ideas" ::all-ideas]
     ["/ideas/by-marker" ::ideas-by-marker]
     ["/marker" ::available-marker]]))

;;TODO make params optional
(defn backend-url-for [name params]
  (str backend-endpoint (:path (r/match-by-name api-router name params))))

(defn get-id [binding-response]
  (last (str/split (:value (:idea binding-response)) #"/")))

;;API available marker
;;TODO replace _ with " "
;;TODO build ordering-function (see indexed-entity)
(defn to-label [marker-id]
  (last (str/split marker-id #"/")))

(defn convert-to-marker [available-concept]
  (let [marker-id (:value (:linkedResource available-concept))
        label (to-label marker-id)]
    {marker-id {
                :id    marker-id
                :label label
                }}))

(defn get-marker-order-from [server-response]
  (mapv #(:value (:linkedResource %1)) (:bindings (:results server-response))))

(defn get-marker-from [server-response]
  (into {} (map convert-to-marker (:bindings (:results server-response)))))