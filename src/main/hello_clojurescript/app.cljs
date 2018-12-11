(ns hello-clojurescript.app
  (:require [reagent.core :as r]
            ["@cjdev/visual-stack/lib/global" :as vs-global]
            ["@cjdev/visual-stack/lib/components/Button" :refer [Button]]
            ["@cjdev/visual-stack/lib/components/SideNav" :refer [SideNav]]
            ["@cjdev/visual-stack/lib/layouts/ApplicationLayout" :default Layout]
            ["@cjdev/visual-stack/lib/components/PageHeader" :refer [PageHeader, PageHeaderSection, PageTitle]];
            ["@cjdev/visual-stack/lib/components/PageContent" :default PageContent]
            ["@cjdev/visual-stack/lib/components/Form" :refer [Input]]
            ["@cjdev/visual-stack/lib/components/Panel" :refer [Panel] :rename {Header PanelHeader
                                                                                Body PanelBody
                                                                                Footer PanelFooter}]))

;; App state: equivalent to redux store
(defonce app-state
  (r/atom {:count {}
           :sideNavState true}))

;; State mutations: roughly equivalent to redux actions
(defn toggle-side-nav [app-state]
  (update-in app-state [:sideNavState] not))

(defn increment [app-state id event]
  (.preventDefault event)
  (update-in app-state [:count id] inc))

(defn decrement [app-state id event]
  (.preventDefault event)
  (update-in app-state [:count id] dec))

(defn reset-count [app-state id event]
  (.preventDefault event)
  (assoc-in app-state [:count id] 0))

(defn update-app-state! [action & args]
  (apply swap! app-state action args))

;; Custom components are just functions that return "Hiccu" syntax datastructures
(defn counter [{id :id}]
  [:div
   [:> Button {:type "solid-primary"
               :on-click #(update-app-state! decrement id %)}
    "-"]
   [:> Button {:type "solid-secondary"
               :on-click #(update-app-state! reset-count id %)}
    (get-in @app-state [:count id] 0)]
   [:> Button {:type "solid-primary"
               :on-click #(update-app-state! increment id %)}
    "+"]])

;; The toplevel component
(defn app []
  [:> Layout {:sideNav (r/as-component
                        [:> SideNav
                         {:onClick #(update-app-state! toggle-side-nav)
                          :collapsed (not (get @app-state :sideNavState))}])
              :sideNavState (get @app-state :sideNavState)}
   [:> PageHeader
    [:> PageTitle "This is the title"]]
   [:> PageContent
    [:> Panel
     [:> PanelHeader "First Counter"]
     [:> PanelBody [counter {:id 1}]]]
    [:> Panel
     [:> PanelHeader "Second Counter"]
     [:> PanelBody [counter {:id 5}]]]
    [:> Panel
     [:> PanelHeader "Third Counter"]
     [:> PanelBody [counter {:id 2}]]
     [:> PanelFooter "and that's all!"]]]])

;; Dev tooling integration + entrypoint
(defn ^:dev/after-load start []
  (r/render-component [app]
                      (.getElementById js/document "root")))

(defn init []
  (start))
