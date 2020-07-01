(ns hcc.innovonto.kaleidoscope.server.core
  (:require [mount.core :as mount]
            [hcc.innovonto.kaleidoscope.server.db :as db]
            [hcc.innovonto.kaleidoscope.server.handler :as handler]
            [cprop.core :as cprop]
            [org.httpkit.server :as http])
  (:gen-class))

;;This does not block.
(defn -main
  "Kaleidoscope server main: starts database, http server and rdf utils. "
  [& args]
  (let [config (cprop/load-config)
        port (get config :port 6001)]
    (println (str "Using config: " config))
    (mount/start db/*db*)
    ;;(mount/start-with-args handler/server {:port port})
    (http/run-server #'handler/app {:port port})
    (println (str "Running webserver at http:/127.0.0.1:" port "/"))))

