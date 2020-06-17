(ns hcc.innovonto.kaleidoscope.views
  (:require [hcc.innovonto.kaleidoscope.db :as db]
            [hcc.innovonto.kaleidoscope.subs :as subs]
            [hcc.innovonto.kaleidoscope.events :as events]
            [hcc.innovonto.kaleidoscope.init.views :as init-views]
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
(defn map-cell [[_ idea]]
  [:div.idea-tile {:key      (:id idea)
                   :on-click #(rf/dispatch [::events/show-idea-details (:id idea)])}
   [idea-tooltip (:content idea)]
   [:div.annotations
    (when-let [marker (not-empty (:marker idea))]
      (map cell-marker marker))]
   [:div.ratings]
   [:div.marker]])

(defn idea-grid []
  (let [all-ideas @(rf/subscribe [::subs/all-ideas])]
    [:div.idea-grid
     (doall (map map-cell all-ideas))]))

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
   [idea-grid]
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

(defn open-modal [e]
  (let [modal (-> js/document (.getElementById "init-modal"))]
    (set! (.-display (.-style modal)) "block")))

(defn debug-buttons []
  [:div
   [:button {:on-click #(rf/dispatch [::events/request-app-init])} "Reset"]
   [:button {:on-click #(rf/dispatch [::events/initialize-available-marker])} "Available Marker"]
   [:button {:on-click open-modal} "Open Modal"]])

(defn kaleidoscope-app []
  [:div
   [init-views/init-modal]
   [:div.container
    [header]
    [debug-buttons]
    [switch-pane :idea-grid]
    ]])