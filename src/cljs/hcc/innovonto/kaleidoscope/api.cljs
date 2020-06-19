(ns hcc.innovonto.kaleidoscope.api
  (:require [clojure.string :as str]
            [reitit.core :as r]))

(def test-response {:head {:vars ["idea" "content"]}, :results {:bindings [{:idea {:type "uri", :value "http://purl.org/innovonto/ideas/47fbb730-6a69-4842-9289-5e5ca1dc8eee"}, :content {:type "literal", :value "A camera system that can detect individuals by the unique way they walk. "}} {:idea {:type "uri", :value "http://purl.org/innovonto/ideas/fce9ab17-6ef2-42db-8745-368eeaee99c1"}, :content {:type "literal", :value "This would be useful for locating and tracking animals on land whom are endangered."}}]}})

;;TODO atom
(def sparql-endpoint "https://innovonto-core.imp.fu-berlin.de/management/core/query")

;;TODO doch lieber string?
(defn all-ideas-query [limit]
  {
   :prefixes {
              :gi2mo "http://purl.org/gi2mo/ns#"}
   :select   ["?idea" "?content"]
   :where    [
              ["?idea" "a" "gi2mo:Idea"]
              ["?idea" "gi2mo:content" "?content"]]
   :limit    limit})

;;TODO doch lieber string? möglichst wenig veränderung gegenüber dem original -> drag & drop
(defn available-markers-query []
  {
   :prefixes {
              :gi2mo "http://purl.org/gi2mo/ns#"
              :inov  "http://purl.org/innovonto/types/#"
              :xsd   "http://www.w3.org/2001/XMLSchema#"
              }
   :select   ["?linkedResource" "(COUNT(?linkedResource) as ?resourceCount)"]
   :where    [
              ["?idea" "a" "gi2mo:Idea"]
              ["?idea" "inov:hasAnnotation" "?annotation"]
              ["?annotation" "inov:annotationState" "approved"]
              ["?annotation" "inov:hasResourceCandidate" "?resourceCandidate"]
              ["?resourceCandidate" "inov:selected" "?selected"]
              ["?resourceCandidate" "inov:hasLinkedResource" "?linkedResource"]
              [:filter "?selected = \"true\"^^xsd:boolean"]
              ]
   :group-by "?linkedResource"
   :order-by "?resourceCount"
   })

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