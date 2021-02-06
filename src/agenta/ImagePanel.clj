(ns agenta.ImagePanel
  (:gen-class
    :extends javax.swing.JPanel
    :init init
    :state state
    :methods [[setImage [java.awt.image.BufferedImage] void]])
  (:import (java.awt Graphics)))

(defn -init []
  [[] (atom {:img nil})])

(defn -paint [this ^Graphics g]
  (let [state (.state this)]
    (.drawImage g (:img @state) 0 0 this)))

(defn -setImage [this img]
  (swap! (.state this) assoc :img img))
