(ns hcc.innovonto.kaleidoscope.init.views
  (:require [re-frame.core :as rf]
            [hcc.innovonto.kaleidoscope.init.subs :as subs]
            [hcc.innovonto.kaleidoscope.init.events :as events]))



#_[:input {:type "file" :id "file" :name "file"
           :on-change
                 #(dispatch [:save-rm-file :file (-> % .-target .-files (aget 0))])}]

;TODO how to get the file here?
(defn file-upload-input []
  [:div
   [:input {:type      "file" :name "source-file"
            :on-change #(println (str "File Changed to:" (-> % .-target .-files (aget 0))))}]])

(defn sparql-endpoint-input []
  (let [sparql-endpoint @(rf/subscribe [::subs/sparql-endpoint])]
    [:div (when @(rf/subscribe [::subs/sparql-endpoint-invalid?]) {:class "invalid"})
     [:label [:input {:name "sparql-endpoint" :value sparql-endpoint :on-change #(rf/dispatch [::events/change-sparql-endpoint (-> % .-target .-value)])}] "SPARQL-Endpoint URL"]]))

(defn datasource-additional-params []
  (let [datasource-type @(rf/subscribe [::subs/datasource-type])]
    (case datasource-type
      :file-upload [file-upload-input]
      :sparql-endpoint [sparql-endpoint-input]
      [:span])))

(defn init-modal []
  (let [modal-open? @(rf/subscribe [::subs/modal-open?])]
    (println (str "Modal-open?" modal-open?))
    [:div.modal {:style {:display (if modal-open? "block" "none")}}
     [:div.modal-content
      ;;[:span.close {:on-click close-modal} "×"]
      [:div.modal-header
       [:img {:src ""}]
       [:h1 "Welcome to Kaleidoscope"]]
      [:div.modal-body
       [:p "To start, please select a datasource that you want to explore:"]
       [:div.form-control-vertical
        [:label [:input {:type "radio" :name "datasource" :on-click #(rf/dispatch [::events/change-datasource :innovonto-core])}] "Use demo ideas from innovonto-core"]
        [:label [:input {:type "radio" :name "datasource" :disabled true :on-click #(rf/dispatch [::events/change-datasource :file-upload])}] "Upload a file"]
        [:label [:input {:type "radio" :name "datasource" :on-click #(rf/dispatch [::events/change-datasource :sparql-endpoint])}] "Use a public SPARQL endpoint"]
        ]
       [datasource-additional-params]
       [:div [:span "This is a DEMO. It will not save your configuration or markers. If you have questions
     or problems, please contact us at kaleidoscope@zvaadw.de"]]
       [:div
        [:button
         ;;TODO loading state for the button
         (if @(rf/subscribe [::subs/configured?])
           {:on-click #(rf/dispatch [::events/initialize-session])}
           {:disabled true})
         "Continue"]]]]]))