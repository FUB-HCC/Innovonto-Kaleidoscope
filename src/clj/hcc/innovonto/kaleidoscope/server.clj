(ns hcc.innovonto.kaleidoscope.server
  (:require [cprop.core :as cprop]
            [org.httpkit.server :as server]
            [ring.middleware.cors :refer [wrap-cors]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :as res]
            [reitit.ring :as reitit-ring]
            [hcc.innovonto.kaleidoscope.rdf-backend :as rdf])
  (:gen-class))

;;TODO https://github.com/joelkuiper/yesparql#tdb

(defn get-all-ideas-handler [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (rdf/get-all-ideas)})

(defn get-available-marker-handler [req]
  {:status  200
   :headers {"Content-Type" "application/json"}
   :body    (rdf/get-available-marker)})

(defn find-by-marker-id-handler [req]
  (if (contains? (:path-params req) :id)
    {:status  200
     :headers {"Content-Type" "application/json"}
     :body    (rdf/ideas-containing-marker (:id (:path-params req)))}
    {
     :status  400
     :headers {"Content-Type" "application/json"}
     :body    (str "{\"error\":\"Missing required parameter 'id'\"}")
     }))

(def app-routes
  (reitit-ring/ring-handler
    (reitit-ring/router
      ["/api"
       ["/ideas" {:get get-all-ideas-handler}]
       ["/marker" {:get get-available-marker-handler}]
       ["/marker/:id/ideas" {:get find-by-marker-id-handler}]])
    (reitit-ring/routes
      (reitit-ring/redirect-trailing-slash-handler)
      (reitit-ring/create-resource-handler {:path "/"})
      (reitit-ring/create-default-handler {:method :add}))))

(def app
  (-> app-routes
      (wrap-cors
        :access-control-allow-origin [#".*"]
        :access-control-allow-headers ["Content-Type"]
        :access-control-allow-methods [:get :put :post :delete :options])))

(defn -main
  "This is our app's entry point"
  [& args]
  (let [config (cprop/load-config)
        port (get config :port 6001)]
    (println (str "Using config: " config))
    (server/run-server #'app {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))