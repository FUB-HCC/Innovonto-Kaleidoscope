(ns hcc.innovonto.kaleidoscope.components.cellmap
  (:require [re-frame.core :as rf]
            [hcc.innovonto.kaleidoscope.subs :as subs]
            [hcc.innovonto.kaleidoscope.events :as events]))


;;TODO shorten to X characters: either via js or via css
(defn idea-tooltip [content]
  [:div.tooltip
   [:div.tooltip-body
    [:span content]]])

(defn cell-marker [{:keys [:id :icon :color]}]
  [:div {:key id}
   [:i.material-icons {:style {:color color}} icon]])

;;Every cell has max 4 markers
;;The key can be given either (as in this example) as meta-data, or as a :key item in the first argument to a component (if it is a map). See React’s documentation for more info.
;; TODO events/show-idea details
(defn map-cell [[_ idea]]
  [:div.idea-tile {:key      (:id idea)
                   :on-click #(rf/dispatch [::events/show-idea-details (:id idea)])}
   [idea-tooltip (:content idea)]
   [:div.annotations
    (when-let [marker (not-empty (:marker idea))]
      (map cell-marker marker))]
   [:div.ratings]
   [:div.marker]])

;; TODO subs/all-ideas
;; TODO state: empty
(defn idea-grid []
  (let [all-ideas @(rf/subscribe [::subs/all-ideas])
        sync-state @(rf/subscribe [::subs/sync-state])]
    [:div.idea-grid
     (case sync-state
       :up-to-date (doall (map map-cell all-ideas))
       :loading [:div.loader]
       [:p "Something went wrong. Please try reloading the page."]
       )]))