(ns agenta.ui
  (:import (agenta PanelViewer)
           (javax.swing JFrame JOptionPane)
           (java.util.function Consumer)))

(defn -add-to-content [^JFrame f ^PanelViewer p]
  (.add (.getContentPane f)
        p))

(defn wrap-into-frame [^JFrame f ^PanelViewer p]
  (doto f
    (.setSize (.-width (.getSize p)) (.-height (.getSize p)))
    (.setVisible true)
    (-add-to-content p)
    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))

(defn make-viewer [^JFrame f]
  (PanelViewer. (proxy [Consumer] [] (accept [p1] (wrap-into-frame f p1)))))

(defn show-end-message [^JFrame f ^PanelViewer p ^String m]
  (JOptionPane/showMessageDialog f m "End of game" JOptionPane/INFORMATION_MESSAGE)
  (.repaint p))
