(ns hcc.innovonto.kaleidoscope.components.exporttab)


(defn export-tab []
  [:div.tab-container
   [:div.export-header
    [:p "Filter Ideas"]
    [:div "Comments Any"]
    [:div "Ratings Any"]
    [:div "Labels Any"]
    [:div "Favorites Any"]]
   [:div.export-body
    [:pre "List with Ideas"]]])

