(ns hcc.innovonto.kaleidoscope.rdf-backend
  (:require [yesparql.core :as sparql-client]
            [yesparql.sparql :as sparql-utils]))

;;TODO make configurable
;;TODO add a way to upload your own file.
;;(def api-endpoint "http://localhost:3030/ac2/sparql")
(def api-endpoint "https://innovonto-core.imp.fu-berlin.de/management/core/query")

(sparql-client/defquery all-ideas-query "queries/all-ideas.sparql" {:connection api-endpoint})
(sparql-client/defquery ideas-by-wikidata-marker "queries/ideas-by-wikidata-marker.sparql" {:connection api-endpoint})
(sparql-client/defquery ideas-by-dbpedia-marker "queries/ideas-by-dbpedia-marker.sparql" {:connection api-endpoint})
(sparql-client/defquery available-marker "queries/available-marker.sparql" {:connection api-endpoint})


;;TODO include the application-case into the query.
(defn get-all-ideas [{:keys [limit]}]
  (with-open [response (all-ideas-query {:limit limit})]
    (let [result (sparql-utils/copy-result-set (sparql-utils/->result response))]
      (sparql-utils/result->json result))))

(defn ideas-containing-marker [marker-id]
  (with-open [response (ideas-by-dbpedia-marker {
                                                 :limit    10
                                                 :bindings {:marker (java.net.URI. marker-id)}})]
    (let [result (sparql-utils/copy-result-set (sparql-utils/->result response))]
      (sparql-utils/result->json result))))

(defn get-available-marker []
  (with-open [response (available-marker {})]
    (let [result (sparql-utils/copy-result-set (sparql-utils/->result response))]
      (sparql-utils/result->json result))))
