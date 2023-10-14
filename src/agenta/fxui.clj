(ns agenta.fxui
  (:require [clojure.java.io :as io]
            [cljfx.api :as fx]))

(def player-as-text
  {-1 "Undefined"
   0  "Player 0"
   1  "Player 1"})

(def *state
 (atom {:winner -1 :tick 0}))

(defmulti event-handler :event/type)

(defn img-url [img-name]
  (let [file-name (str "Pictures/" img-name ".gif")]
    (str "file://" (.getAbsolutePath (io/file file-name)))))

(defn props-pane [tick winner]
  {:fx/type :grid-pane
   :children [{:fx/type :label
               :text "Properties"
               :grid-pane/row 0
               :grid-pane/column 0
               :grid-pane/column-span 2}
              {:fx/type :label
               :text "Tick"
               :grid-pane/row 1
               :grid-pane/column 0}
              {:fx/type :text-field
               :text (str tick)
               :editable false
               :grid-pane/row 1
               :grid-pane/column 1}
              {:fx/type :label
               :text "Winner"
               :grid-pane/row 2
               :grid-pane/column 0}
              {:fx/type :text-field
               :text (get player-as-text winner)
               :editable false
               :grid-pane/row 2
               :grid-pane/column 1}]})

(def grid-pane
  {:fx/type :grid-pane
  :children (for [i (range 10)
                  j (range 10)]
              {:fx/type :image-view
               :grid-pane/row i
               :grid-pane/column j
               :image {:url (img-url "tree0")}})})

(defn root-view [{{:keys [tick winner]} :state}]
  {:fx/type :stage
   :showing true
   :scene {:fx/type :scene
           :root {:fx/type :h-box
                  :children [grid-pane (props-pane tick winner)]}}})

(def renderer
  (fx/create-renderer
    :middleware (fx/wrap-map-desc (fn [state]
                                    {:fx/type root-view
                                     :state state}))
    :opts {:fx.opt/map-event-handler event-handler}))

(fx/mount-renderer *state renderer)

(comment
    (renderer))
