(ns hcc.innovonto.kaleidoscope.server.db
  (:require [mount.core :refer [defstate]]
            [conman.core :as conman]))


(def pool-spec
  {:jdbc-url "jdbc:sqlite:kaleidoscope.db"})

(defstate ^:dynamic *db*
          :start (conman/connect! pool-spec)
          :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")