(ns hcc.innovonto.kaleidoscope.components.idea-details
  (:require [re-frame.core :as rf]
            [hcc.innovonto.kaleidoscope.subs :as subs]
            [hcc.innovonto.kaleidoscope.events :as events]
            [hcc.innovonto.kaleidoscope.db :as db]))

(defn visibility-indicator [idea]
  [:i.material-icons {:on-click #(rf/dispatch [::events/toggle-visibility idea])} (if (:visible idea) "visibility" "visibility_off")])

(defn favorite-indicator [idea]
  [:i.material-icons {:on-click #(rf/dispatch [::events/toggle-favorite idea])}
   (if (:favorite idea) "favorite" "favorite_border")])

(defn handle-star-click [idea current-rating star-rating]
  (if (= current-rating star-rating)
    (rf/dispatch [::events/reset-rating idea 0])
    (rf/dispatch [::events/reset-rating idea star-rating])))

(defn rating-star [idea current-rating star-rating]
  [:i.material-icons
   {:on-click #(handle-star-click idea current-rating star-rating)}
   (if (>= current-rating star-rating)
     "star"
     "star_border")])

(defn rating-indicator [idea]
  (let [rating (get idea :rating 0)]
    [:span.label-bar
     [rating-star idea rating 1]
     [rating-star idea rating 2]
     [rating-star idea rating 3]]))

(defn colored-label [idea color]
  [:i.material-icons {:style    {:color (color db/available-icon-colors)}
                      :on-click #(rf/dispatch [::events/toggle-label idea color])}
   (if (contains? (:label idea) color) "label" "label_off")])

(defn label-indicator [idea]
  [:span.label-bar
   [colored-label idea :blue]
   [colored-label idea :red]
   [colored-label idea :green]])

(defn toolbox [active-idea-id]
  (let [active-idea @(rf/subscribe [::subs/idea-details active-idea-id])]
    [:div.toolbox
     [:div.toolbox-header
      [:div.idea-details-header
       [:span.idea-id "i"]
       [visibility-indicator active-idea]
       [rating-indicator active-idea]
       [label-indicator active-idea]
       [favorite-indicator active-idea]
       [:i.material-icons {:on-click #(rf/dispatch [::events/close-idea-toolbox])} "close"]]
      ]
     [:div.idea-toolbox-body
      [:h2.idea-title (:id active-idea)]
      [:span "Created By " (get active-idea :creator "Unknown User")]
      [:div
       [:p (:content active-idea)]]
      [:h4 "Similar Ideas"]
      [:div
       [:i.material-icons "north_east"]]
      ]
     [:div.idea-toolbox-annotations]
     ]))