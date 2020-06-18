(ns sparql-client.format
  (:require [clojure.string :as string]))


;;TODO include formal sparql syntax
;;TODO read up on datalog syntax
;;TODO build testsuite that generates string from examples and validates if those are okay
;;https://github.com/RubenVerborgh/SPARQL.js/tree/master/queries
;;TODO use sparql.js to test it?
;;TODO namespaces vs uri-prefixes

;PREFIX gi2mo: <http://purl.org/gi2mo/ns#>
;SELECT ?idea ?content WHERE {   ?idea a gi2mo:Idea;         gi2mo:content ?content } LIMIT 1

;;TODO SELECT queries
;;PREFIX gi2mo:<http://purl.org/gi2mo/ns#>
;
;SELECT ?idea ?content
;WHERE {
;  ?idea a gi2mo:Idea.
;  ?idea gi2mo:content ?content
;}
;LIMIT 80

;;https://github.com/Engelberg/instaparse
;;TODO can i do datalog-similar things here?
(def example-query
  {
   :prefixes {
              :gi2mo "http://purl.org/gi2mo/ns#"
              }
   :select   ["?idea" "?content"]
   :where    [
              ["?idea" "a" "gi2mo:Idea"]
              ["?idea" "gi2mo:content" "?content"]]
   :limit    2
   })


(defn prefix->string [acc [key value]]
  (let [result (str "PREFIX " (name key) ":" "<" value "> ")]
    (str acc result)))

(defn select->string [vars]
  (str "SELECT " (string/join " " vars)))

;;TODO . vs ;
;;TODO can i use namespaced keywords here?
(defn clause->string [clause]
  (str (string/join " " clause) "."))

;;TODO can i use namespaced keywords here?
(defn where->string [clauses]
  (str "WHERE {" (string/join " " (map clause->string clauses)) "}"))

(defn unparse [query]
  (let [prefix-result (reduce prefix->string "" (:prefixes query))
        select-result (select->string (:select query))
        where-result (where->string (:where query))
        limit-result (if (:limit query) (str "LIMIT " (str (:limit query))) "")]
    (println where-result)
    (println limit-result)
    (str prefix-result " " select-result " " where-result " " limit-result)))

(def example-response
  {:head
   {:vars ["idea" "content"]},
   :results {:bindings [
               {:idea    {
                          :type  "uri",
                          :value "https://innovonto-core.imp.fu-berlin.de/entities/ideas/cf65b021-620f-43fe-9473-1712be788cde"
                          },
                :content {
                          :type "literal", :value "This can enable me to interact and see where others are in my home."
                          }
                }
               ]
    }})

(defn convert-binding [binding]
  ;;for all keys, extract the value and add it directly to the map
  )

;;Bindings can be either [{}{}] or {}
(defn convert-response-to-vector [response]
  (into [] (map convert-binding (get-in response [:results :bindings]))))
