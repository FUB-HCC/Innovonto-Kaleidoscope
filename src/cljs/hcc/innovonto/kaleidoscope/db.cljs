(ns hcc.innovonto.kaleidoscope.db)

(declare available-icon-colors)

(def default-db {
                 :sync-state    :up-to-date
                 :active-panel   :idea-grid
                 :active-toolbox {
                                  :title "marker-toolbox"
                                  }
                 :marker         {
                                  "http://dbpedia.org/resource/Material"   {:id    "http://dbpedia.org/resource/Material"
                                                                            :label "Material"
                                                                            :state :selected
                                                                            :icon  "adjust"
                                                                            :color (:green available-icon-colors)}
                                  "http://dbpedia.org/resource/Clothing"   {:id    "http://dbpedia.org/resource/Clothing"
                                                                            :label "Clothing"
                                                                            :state :selected
                                                                            :icon  "fiber_manual_record"
                                                                            :color (:black available-icon-colors)}
                                  "http://dbpedia.org/resource/Technology" {:id    "http://dbpedia.org/resource/Technology"
                                                                            :label "Technology"}
                                  "http://dbpedia.org/resource/Animal"     {:id    "http://dbpedia.org/resource/Animal"
                                                                            :label "Animal"}
                                  "http://dbpedia.org/resource/Human"      {:id    "http://dbpedia.org/resource/Human"
                                                                            :label "Human"}
                                  }
                 :marker-list ["http://dbpedia.org/resource/Animal" "http://dbpedia.org/resource/Clothing" "http://dbpedia.org/resource/Technology" "http://dbpedia.org/resource/Human" "http://dbpedia.org/resource/Material"]
                 :all-ideas      {
                                  "019cb2eb-d6ee-4fba-abfc-181ce89fa213" {
                                                                          :id          "019cb2eb-d6ee-4fba-abfc-181ce89fa213"
                                                                          :content     "The technology can be used to monitor air craft."
                                                                          :marker      #{
                                                                                         "http://dbpedia.org/resource/Clothing"
                                                                                         "http://dbpedia.org/resource/Material"
                                                                                         }
                                                                          :annotations {}
                                                                          }
                                  "033ae853-c609-4594-a4b1-699ce2d4b248" {
                                                                          :id          "033ae853-c609-4594-a4b1-699ce2d4b248"
                                                                          :content     "the device could be used in a detective manner that is to say that it could be useful to predicting behavioral patterns of say criminal offenders, students, to perform research perhaps by tracking the physical lives of athletes or top achieving businessman or for companies to isolate expected behaviors of their employees and how they anticipate employees to move during a work day to establish fair and true standards"
                                                                          :marker      []
                                                                          :annotations {}
                                                                          }
                                  "10251071-fffb-4d49-baaa-9b23e984b55c" {
                                                                          :id          "10251071-fffb-4d49-baaa-9b23e984b55c"
                                                                          :content     "An application could be created for pet owners who have pets who have ran away from home. The application could be downloaded right onto a persons phone. the hand sized device could be put into a collar of a pet, or a chip like device under the skin. The system would be able to pin point a pets location to the direct coordinates. "
                                                                          :marker      []
                                                                          :annotations {}
                                                                          }
                                  "12b66ef6-05e2-4020-8933-e6a28e43c8c1" {
                                                                          :id          "12b66ef6-05e2-4020-8933-e6a28e43c8c1"
                                                                          :content     "The technology can assist with traffic management by analyzing and predicting patterns. "
                                                                          :marker      []
                                                                          :annotations {}
                                                                          }
                                  }
                 })

(def available-icons ["fiber_manual_record" "adjust" "panorama_fish_eye" "radio_button_checked" "stars" "highlight_off"
                      "gps_fixed" "gps_not_fixed" "check" "clear" "graphic_eq" "grain" "location_on"
                      "arrow_back" "arrow_forward" "arrow_downward" "arrow_upward"])

(def available-icon-colors {
                            :red   "#f74a46"
                            :green "#38ca27"
                            :blue  "#25a8f9"
                            :black "#222222"
                            :gray  "#9C9C9C"
                            })