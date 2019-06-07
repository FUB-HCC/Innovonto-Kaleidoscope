(ns hcc.innovonto.kaleidoscope.server
  (:require [bidi.ring :refer (make-handler)]
            [ring.util.response :as res]
            [yesparql.core :as sparql-client]
            [yesparql.sparql :as sparql-utils]))

(def api-endpoint "http://localhost:3030/ac2/sparql")
(sparql-client/defquery all-ideas-query "queries/all-ideas.sparql" {:connection api-endpoint})
(sparql-client/defquery ideas-containing-marker "queries/ideas-containing-marker.sparql" {:connection api-endpoint})
(sparql-client/defquery ideas-by-dbpedia-marker "queries/ideas-by-dbpedia-marker.sparql" {:connection api-endpoint})
(sparql-client/defquery available-marker "queries/available-marker.sparql" {:connection api-endpoint})

(defn article-handler
  [{:keys [route-params]}]
  (res/response (str "You are viewing article: " (:id route-params))))

(defn not-found-handler [req]
  (do
    ;;(println (str "Got not-found handler for request " req))
    {:status  404
     :headers {"Content-Type" "text/html"}
     :body    "404 - not found."}))

;;TODO include the application-case into the query.
;;TODO limit does not work
(defn all-ideas-response []
  (with-open [response (all-ideas-query {:limit 10})]
    (let [result (sparql-utils/copy-result-set (sparql-utils/->result response))]
      (sparql-utils/result->json result))))

(defn get-all-ideas-handler [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (all-ideas-response)})

(defn find-all-ideas-containing [marker-id]
  (with-open [response (ideas-by-dbpedia-marker {
                                                 :limit    10
                                                 :bindings {:marker (java.net.URI. marker-id)}})]
    (let [result (sparql-utils/copy-result-set (sparql-utils/->result response))]
      (sparql-utils/result->json result))))

(defn get-available-marker []
  (with-open [response (available-marker {})]
    (let [result (sparql-utils/copy-result-set (sparql-utils/->result response))]
      (sparql-utils/result->json result))))

(defn get-available-marker-handler [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (get-available-marker)})

(defn find-by-marker-id-handler [req]
  (if (contains? (:params req) :marker-id)
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    (find-all-ideas-containing (:marker-id (:params req)))}
    {
     :status  400
     :headers {"Content-Type" "application/json"}
     :body    (str "{\"error\":\"Missing required parameter 'marker-id'\"}")
     }))

(def handler
  (make-handler ["/" {["articles/" :id "/article.html"] article-handler
                      ["api/all-ideas/"]                get-all-ideas-handler
                      ["api/ideas-by-marker/"]          find-by-marker-id-handler
                      ["api/available-marker/"]         get-available-marker-handler
                      true                              not-found-handler}]))

(defn run-standalone-api-server []
  ;;TODO: implement the following steps:
  ;;

  ;; lein repl
  ;; (use 'ring.adapter.jetty)
  ;; (use 'hello-world.core)
  ;; (run-jetty handler {:port 3000})
  "foo")