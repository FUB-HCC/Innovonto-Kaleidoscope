(ns hcc.innovonto.kaleidoscope.server.handler
  (:require [mount.core :refer [defstate]]
            [org.httpkit.server :as http]
            [reitit.ring :as reitit-ring]
            [ring.util.response :as res]
            [ring.middleware.cors :refer [wrap-cors]]
            [reitit.ring.middleware.parameters :as parameters]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.middleware.exception :as exception]
            [ring.middleware.json :refer [wrap-json-response wrap-json-body]]
            [hcc.innovonto.kaleidoscope.server.rdf-backend :as rdf]
            [clojure.pprint :as pprint]
            [mount.core :as mount])
  (:import (java.util UUID)))

(defn get-all-ideas-handler [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (rdf/get-all-ideas {:limit 80})})

(defn get-available-marker-handler [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (rdf/get-available-marker)})

(defn find-by-marker-id-handler [req]
  (if (contains? (:params req) "marker-id")
    (let [marker (get (:params req) "marker-id")]
      {:status  200
       :headers {"Content-Type" "application/json"}
       :body    (rdf/ideas-containing-marker marker)})
    {
     :status  400
     :headers {"Content-Type" "application/json"}
     :body    (str "{\"error\":\"Missing required parameter 'id'\"}")
     }))

(defn create-session-handler [req]
  (pprint/pprint (:body req))
  (res/response {
                 :session-id (.toString (UUID/randomUUID))
                 }))

;;TODO add json middleware to all api routes
(def app-routes
  (reitit-ring/ring-handler
    (reitit-ring/router
      ["/api"
       ["/session" {:put (wrap-json-response (wrap-json-body create-session-handler {:keywords? true}))}]
       ["/ideas" {:get get-all-ideas-handler}]
       ["/ideas/by-marker" {:get find-by-marker-id-handler}]
       ["/marker" {:get get-available-marker-handler}]])
    (reitit-ring/routes
      (reitit-ring/redirect-trailing-slash-handler)
      (reitit-ring/create-resource-handler {:path "/"})
      (reitit-ring/create-default-handler {:method :add}))
    {:middleware [parameters/parameters-middleware
                  muuntaja/format-negotiate-middleware
                  muuntaja/format-response-middleware
                  exception/exception-middleware]}))

(def app
  (-> app-routes
      (wrap-cors
        :access-control-allow-origin [#".*"]
        :access-control-allow-headers ["Content-Type"]
        :access-control-allow-methods [:get :put :post :delete :options])))

;;TODO doesn't work as of now: does not block
(defstate server
          :start (http/run-server #'app (:port mount/args))
          :stop (server :timeout 100))