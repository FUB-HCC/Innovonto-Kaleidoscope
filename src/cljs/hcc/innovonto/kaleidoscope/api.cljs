(ns hcc.innovonto.kaleidoscope.api
  (:require [clojure.string :as str]
            [reitit.core :as r]))

(def test-response {:head {:vars ["idea" "content"]}, :results {:bindings [{:idea {:type "uri", :value "http://purl.org/innovonto/ideas/47fbb730-6a69-4842-9289-5e5ca1dc8eee"}, :content {:type "literal", :value "A camera system that can detect individuals by the unique way they walk. "}} {:idea {:type "uri", :value "http://purl.org/innovonto/ideas/fce9ab17-6ef2-42db-8745-368eeaee99c1"}, :content {:type "literal", :value "This would be useful for locating and tracking animals on land whom are endangered."}}]}})

;;TODO how to configure this?
(def backend-endpoint "http://localhost:6001")

;;;;TODO include session
(def api-router
  (r/router
    ["/api"
     ["/ideas" ::all-ideas]
     ["/marker" ::available-marker]
     ["/marker/:id/ideas" ::ideas-by-marker]]))

(defn backend-url-for [name params]
  (str backend-endpoint (:path (r/match-by-name api-router name params))))

;;API: GET ALL
(defn get-id [binding-response]
  (last (str/split (:value (:idea binding-response)) #"/")))

(defn convert [element]
  {:id      (get-id element)
   :content (:value (:content element))
   :marker  #{}})

(defn convert-one [result [head & tail]]
  (if (nil? head)
    result
    (convert-one (assoc result (get-id head) (convert head)) tail)))

(defn convert-to-db-structure [server-response]
  (let [response-ideas (:bindings (:results server-response))
        result-map {}]
    (convert-one result-map response-ideas)))

;;API SELECT-BY-MARKER


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