(ns hcc.innovonto.kaleidoscope.views
  (:require [hcc.innovonto.kaleidoscope.db :as db]
            [hcc.innovonto.kaleidoscope.subs :as subs]
            [hcc.innovonto.kaleidoscope.events :as events]
            [hcc.innovonto.kaleidoscope.init.events :as init-events]
            [hcc.innovonto.kaleidoscope.init.views :as init-views]
            [hcc.innovonto.kaleidoscope.components.cellmap :as cellmap]
            [re-com.popover :as popover]
            [re-frame.core :as rf]
            [reagent.core :as reagent]))


;this.titleComp = new TextLayer({
;                                parent: this,
;                                x: 8, y: 0, color: '#333',
;    width: Canvas.width,
;                                height: 42,
;                                padding: 10,
;                                text: 'Kaleidoscope 0.4.1',
;                                            font: BOLD_FONT_LARGE,
;                                backgroundColor: Colors.c_none,
;                                });
(defn header []
  [:h1 "Kaleidoscope 0.5.0"])

(defn add-new-marker []
  [:div
   [:i.material-icons "add"]
   [:input {:type "text" :placeholder "Search marker..."}]])

(defn select-color-icon [marker-id [_ val]]
  [:i.material-icons {:on-click #(rf/dispatch [::events/update-color marker-id val])
                      :style    {:color val}} "fiber_manual_record"])

(defn select-shape-icon [marker-id shape]
  [:i.material-icons {:on-click #(rf/dispatch [::events/update-icon marker-id shape])
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
               [:i.material-icons {:style {:color color}} icon]]
      :popover [popover/popover-content-wrapper
                :body [marker-selection-menu id]]]
     [:div label]
     [:div.active-marker-toolbar
      [:div [:i.material-icons "visibility"]]
      [:div [:i.material-icons "settings"]]
      [:div [:i.material-icons {:on-click #(rf/dispatch [::events/remove-marker id])} "delete"]]]
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
  [:div.toolbox-row-element.available-marker {:key id}
   [:i.material-icons {:on-click #(rf/dispatch [::events/add-marker id])} "add"]
   [:span label]])

(defn available-marker-pane []
  (let [available-marker-list @(rf/subscribe [::subs/ordered-available-marker])]
    [:div
     [:h3 "Marker"]
     (doall (map available-marker available-marker-list))]))

(defn marker-toolbox []
  [:div.toolbox
   [:h2 "Marker"]
   [:div.toolbox-body
    [selected-marker-pane]
    [snapshot-pane]
    [available-marker-pane]]])

(defn idea-toolbox [active-idea-id]
  (let [active-idea @(rf/subscribe [::subs/idea-details active-idea-id])]
    [:div.toolbox
     [:div.idea-toolbox-header
      [:span.close {:on-click #(rf/dispatch [::events/close-idea-toolbox])} "×"]]
     [:div.idea-toolbox-body
      [:h3 (:id active-idea)]
      [:span "Created By Foo"]
      [:div
       [:p (:content active-idea)]]
      ;;[:h4 "Similar Ideas"]
      ;;[:span "TODO"]
      ]
     [:div.idea-toolbox-annotations]
     ]))

(defn idea-grid-tab []
  [:div.idea-grid-container
   [cellmap/idea-grid]
   (let [active-toolbox @(rf/subscribe [::subs/active-toolbox])]
     (case (:title active-toolbox)
       "marker-toolbox" [marker-toolbox]
       "idea-details" [idea-toolbox (:idea active-toolbox)]
       [:span "Default Case: Error"]))])

(defn test-tab []
  [:div.cell-container
   [:div.cell.clusterA]
   [:div.cell.clusterA]
   [:div.cell.clusterA]
   [:div.cell.clusterA]
   [:div.cell.clusterB]
   [:div.cell.clusterB]
   [:div.cell.clusterB]
   [:div.cell.clusterC]
   [:div.cell.clusterB]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]
   [:div.cell]])

(defn switch-pane [current-active]
  [:div
   [:div.tab-bar
    [:div.tab.active "IdeaMap"]
    [:div.tab "Review"]
    [:div.tab "Export"]]
   (case current-active
     :idea-grid [idea-grid-tab]
     :review [:span "not implemented"]
     :export [:span "not implemented"]
     :test [test-tab]
     [:span "Default Case: Error"])
   ])

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
    [switch-pane :idea-grid]
    ]])