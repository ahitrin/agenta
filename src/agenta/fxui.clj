(ns agenta.fxui
  (:require [clojure.java.io :as io]
            [cljfx.api :as fx]
            [agenta.game-map :as gm]
            [agenta.math :as m]))

(def player-as-text
  {-1 "Undefined"
   0  "Player 0"
   1  "Player 1"})

(def -tiles {:grass "grass0"
             :tree  "tree0"})

(def *state
  (atom {:stage :init :winner -1 :tick 0 :end-tick 0 :game-map nil :run-fn nil}))

(defmulti event-handler :event/type)

(defn img-url [img-name]
  (let [file-name (str "Pictures/" img-name ".gif")]
    (str "file://" (.getAbsolutePath (io/file file-name)))))

(defn props-pane [tick winner stage]
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
               :grid-pane/column 1}
              {:fx/type :button
               :text "▶ / Ⅱ"
               :on-action {:event/type ::start-pause}
               :disable (not= :init stage)
               :grid-pane/row 3
               :grid-pane/column 0}
              {:fx/type :button
               :text "Exit"
               :on-action (fn [_] ())
               :grid-pane/row 3
               :grid-pane/column 1}]})

(defn image-at [m x y]
  (let [xy (m/xy x y)
        obj (gm/object-at m xy)]
    (if (some? obj)
      (:img obj)
      (-tiles (gm/cell-type m xy)))))

(defn grid-pane [m]
  (if (some? m)
    {:fx/type :grid-pane
     :children (for [i (range (:size-y m))
                     j (range (:size-x m))]
                 {:fx/type :image-view
                  :grid-pane/row i
                  :grid-pane/column j
                  :image {:url (img-url (image-at m j i))}})}
    {:fx/type :grid-pane
     :children (for [i (range 16)
                     j (range 24)]
                 {:fx/type :image-view
                  :grid-pane/row i
                  :grid-pane/column j
                  :image {:url (img-url "tree0")}})}))

(defn root-view [{{:keys [game-map tick winner stage]} :state}]
  {:fx/type :stage
   :showing true
   :scene {:fx/type :scene
           :root {:fx/type :h-box
                  :children [(grid-pane game-map) (props-pane tick winner stage)]}}})

(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc (fn [state]
                                   {:fx/type root-view
                                    :state state}))
   :opts {:fx.opt/map-event-handler event-handler}))

(defn show [g run-fn]
  (swap! *state assoc :tick (:tick g) :game-map (:map g) :end-tick (:end-tick g) :run-fn run-fn)
  (fx/mount-renderer *state renderer))

(defn update-state! [m opts]
  (swap! *state assoc :tick (:tick opts) :winner (:winner opts) :game-map m))

(defn next-stage [s]
  ":init -> :run <-> :pause"
  (case (:stage s)
    :init   (assoc s :stage :run)
    :run    (assoc s :stage :pause)
    :pause  (assoc s :stage :run)))

(defmethod event-handler ::start-pause [e]
  (swap! *state next-stage)
  (.start
    (Thread. #((:run-fn @*state)
                 update-state!
                 (:game-map @*state)
                 (:winner @*state)
                 (:tick @*state)
                 (:end-tick @*state)
                 (fn [] false)))))
