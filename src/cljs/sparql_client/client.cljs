(ns sparql-client.client
  (:require [sparql-client.format :as sparql]
            [ajax.core :as ajax]))

;;https://innovonto-core.imp.fu-berlin.de/management/core/query?query=%0A++++PREFIX+gi2mo:%3Chttp:%2F%2Fpurl.org%2Fgi2mo%2Fns%23%3E%0A++++PREFIX+rdfs:+%3Chttp:%2F%2Fwww.w3.org%2F2000%2F01%2Frdf-schema%23%3E%0A++++PREFIX+inov:%3Chttp:%2F%2Fpurl.org%2Finnovonto%2Ftypes%2F%23%3E%0A++++PREFIX+dcterms:+%3Chttp:%2F%2Fpurl.org%2Fdc%2Fterms%2F%3E%0A++++PREFIX+rdf:+%3Chttp:%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0A++++DESCRIBE+%3Chttps:%2F%2Finnovonto-core.imp.fu-berlin.de%2Fentities%2Fideas%2Fe9077173-567f-49cf-979a-0a361aa08cd8%3E%0A&format=json
;;TODO: component? endpoint == atom?
(def sparql-endpoint "https://innovonto-core.imp.fu-berlin.de/management/core/query")

;;TODO mh. this is http-xhrio-specific :/
(defn query-request [{:keys [query on-success on-failure]}]
  {:method          :get
   :uri             sparql-endpoint
   :params          {
                     :query  (sparql/unparse query)
                     :format "JSON"
                     }
   :format          (ajax/json-request-format)
   :response-format (ajax/json-response-format {:keywords? true})
   :on-success      on-success
   :on-failure      on-failure})

