(ns hcc.innovonto.kaleidoscope.views
  (:require [hcc.innovonto.kaleidoscope.db :as db]
            [hcc.innovonto.kaleidoscope.subs :as subs]
            [hcc.innovonto.kaleidoscope.events :as events]
            [hcc.innovonto.kaleidoscope.init.events :as init-events]
            [hcc.innovonto.kaleidoscope.init.views :as init-views]
            [hcc.innovonto.kaleidoscope.components.cellmap :as cellmap]
            [hcc.innovonto.kaleidoscope.components.configtab :as config]
            [hcc.innovonto.kaleidoscope.components.exporttab :as export]
            [hcc.innovonto.kaleidoscope.components.reviewtab :as review]
            [hcc.innovonto.kaleidoscope.components.idea-details :as idea-details]
            [thereisnodot.reagent-autocomplete.core :as autocomplete]
            [re-com.popover :as popover]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))


(defn header []
  [:h1 "Kaleidoscope 0.5.0"])

(defn add-new-marker []
  (let [available-marker-list @(rf/subscribe [::subs/ordered-available-marker])
        available-marker-labels (into [] (map :label available-marker-list))]
    [:div.marker-search-box
     [:i.material-icons.marker-search-item "add"]
     [autocomplete/autocomplete_widget
      available-marker-labels
      {:can-enter-new?     false
       :display-size       10
       :placeholder        "Search marker..."
       :parent-div-style   {:width "100%" :position "relative"}
       :click-submit-style {:position "absolute"
                            :right    "0"
                            :top      "0"
                            :padding  "0"}
       :dropdown-style     {:position   "absolute"
                            :right      "0"
                            :left       "0"
                            :top        "2em"
                            :box-shadow "grey 1px 2px 1px 0px"
                            :background "white"
                            :overflow   "hidden"
                            :border     "none" :z-index "999"}
       :submit-fn          #(rf/dispatch [::events/add-marker-by-label %1])}]]))

(defn select-color-icon [marker-id [_ val]]
  [:i.material-icons {:key      val
                      :on-click #(rf/dispatch [::events/update-color marker-id val])
                      :style    {:color val}} "fiber_manual_record"])

(defn select-shape-icon [marker-id shape]
  [:i.material-icons {:key      shape
                      :on-click #(rf/dispatch [::events/update-icon marker-id shape])
                      :style    {:color (:gray db/available-icon-colors)}} shape])

;;TODO add backdrop
(defn marker-selection-menu [marker-id]
  [:div.marker-selection-menu
   (doall (map (partial select-color-icon marker-id) db/available-icon-colors))
   (doall (map (partial select-shape-icon marker-id) db/available-icons))
   ])


(defn active-marker [[_ {:keys [:id :icon :label :color]}]]
  (let [show-icon-selector? (reagent/atom false)]
    [:div.toolbox-row-element.active-marker {:key id}
     [popover/popover-anchor-wrapper
      :showing? show-icon-selector?
      :position :below-center
      :anchor [:div
               {:on-click #(swap! show-icon-selector? not)}
               [:i.material-icons.toolbox-row-button {:style {:color color}} icon]]
      :popover [popover/popover-content-wrapper
                :body [marker-selection-menu id]]]
     [:span.active-marker-label label]
     [:div.active-marker-toolbar
      [:div.toolbar-item.disabled [:i.material-icons "visibility"]]
      [:div.toolbar-item.disabled [:i.material-icons "settings"]]
      [:div.toolbar-item [:i.material-icons {:on-click #(rf/dispatch [::events/remove-marker id])} "delete"]]]
     ]))

(defn selected-marker-pane []
  [:div
   [:h3 "Selected"]
   [:div.selected-marker-list
    [add-new-marker]
    [:div
     (let [selected-marker @(rf/subscribe [::subs/selected-marker])]
       (map active-marker selected-marker))]
    ]])

(defn snapshot-element [{:keys [:timestamp :user]}]
  [:div.toolbox-row-element.snapshot-element
   [:div timestamp]
   [:div user]
   [:div.snapshot-element-toolbar
    [:div "..."]
    [:div [:i.material-icons "restore"]]
    [:div [:i.material-icons "delete"]]]])

(defn snapshot-pane []
  [:div.snapshot-pane
   [:h3 "Snapshot"]
   [:div.snapshot-list
    [snapshot-element {:timestamp "just now" :user "Ares"}]
    [snapshot-element {:timestamp "yesterday" :user "Hera"}]
    [snapshot-element {:timestamp "last week" :user "Aphrodite"}]]])

(defn available-marker [{:keys [:id :label]}]
  [:div.toolbox-row-element.available-marker {:key      id
                                              :on-click #(rf/dispatch [::events/add-marker id])}
   [:div
    [:i.material-icons.toolbox-row-button "add"]]
   [:span.active-marker-label label]])

(defn available-marker-pane []
  (let [paging-config @(rf/subscribe [::subs/available-marker-paging-config])
        current-page @(rf/subscribe [::subs/current-available-marker-page])]
    [:div
     [:h3 "Marker"]
     (doall (map available-marker current-page))
     [:div.available-marker-paging
      [:div {:class (if (= (:current-page paging-config) 0) "disabled")}
       [:i.material-icons
        {:on-click #(rf/dispatch [::events/available-marker-page-down])}
        "navigate_before"]]
      [:div
       [:span (:current-page paging-config)]]
      [:div {:class (if (= (:current-page paging-config) (:max-page paging-config)) "disabled")}
       [:i.material-icons
        {:on-click #(rf/dispatch [::events/available-marker-page-up])}
        "navigate_next"]]]]))

(defn marker-toolbox []
  [:div.toolbox
   [:div.toolbox-header
    [:div.marker-toolbox-header
     [:h2 "Marker"]]]
   [:div.toolbox-body
    [selected-marker-pane]
    [snapshot-pane]
    [available-marker-pane]]])

(defn idea-grid-tab []
  [:div.tab-container
   [cellmap/idea-grid]
   (let [active-toolbox @(rf/subscribe [::subs/active-toolbox])]
     (case (:title active-toolbox)
       "marker-toolbox" [marker-toolbox]
       "idea-details" [idea-details/toolbox (:idea active-toolbox)]
       [:span "Idea Grid Tab failed to load."]))])

(defn tab [title keyword current-active]
  [:div.tab (when (= current-active keyword) {:class "active"})
   [:span {:on-click #(rf/dispatch [::events/switch-tab keyword])} title]])

(defn switch-pane []
  (let [current-active @(rf/subscribe [::subs/active-tab])]
    [:div
     [:div.tab-bar
      [tab "IdeaMap" :idea-grid current-active]
      [tab "Review" :review current-active]
      [tab "Export" :export current-active]
      [tab "Config" :config current-active]]
     (case current-active
       :idea-grid [idea-grid-tab]
       :review [review/review-tab]
       :export [export/export-tab]
       :config [config/config-tab]
       [:span "Switch Pane failed to load."])
     ]))

(defn debug-buttons []
  [:div
   [:button {:on-click #(rf/dispatch [::init-events/reset])} "Reset"]
   [:button {:on-click #(rf/dispatch [::init-events/toggle-modal])} "Open Modal"]])

;;TODO toast/alert for generic ajax error
(defn kaleidoscope-app []
  [:div
   [init-views/init-modal]
   [:div.container
    [header]
    [debug-buttons]
    [switch-pane]
    ]])