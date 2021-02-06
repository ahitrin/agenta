(ns agenta.ui
  (:require [agenta.game-map :as gm])
  (:import (agenta MapCellType ImagePanel)
           (javax.swing JFrame JOptionPane JPanel)
           (java.awt Graphics Component)
           (java.awt.image BufferedImage)))

(def -tiles {MapCellType/GRASS "grass0"
             MapCellType/TREE "tree0"})

(defn simple-cache []
  (let [cache (atom {})]
    (fn [key builder]
      (let [val (get-in @cache key builder)]
        (swap! cache assoc key val)
        val))))

(defn wrap-viewer [^JFrame f ^ImagePanel p]
  (let [state (atom {:need-init true})
        image-cache (simple-cache)]
    (fn [m]
      (let [size-x (gm/size-x m)
            size-y (gm/size-y m)
            pix-x (* 25 size-x)
            pix-y (* 25 size-y)]
        (when (:need-init @state)
          (.setSize p pix-x pix-y)
          (.setVisible p true)
          (.setSize f pix-x pix-y)
          (.setVisible f true)
          (.add (.getContentPane f) ^Component p)
          (.setDefaultCloseOperation f JFrame/EXIT_ON_CLOSE)
          (swap! state assoc :need-init false))
        (let [image (cast BufferedImage (.createImage p pix-x pix-y))
              currentGraph (.createGraphics image)]
          (doseq [i (range size-x)
                  j (range size-y)
                  :let [u (gm/object-at m i j)
                        tile-name (if (some? u)
                                    (str (.getImage (.getType u)) (.getPlayer u))
                                    (-tiles (gm/cell-type m i j)))]]
            (.drawImage ^Graphics currentGraph
                        (image-cache tile-name
                               (.getImage (.getToolkit p) (str "Pictures/" tile-name ".gif")))
                        (int (* 25 i))
                        (int (* 25 j))
                        p))
          (.setImage p image)
          (.repaint p))))))

(defn show-end-message [^JFrame f ^JPanel p ^String m]
  (JOptionPane/showMessageDialog f m "End of game" JOptionPane/INFORMATION_MESSAGE)
  (.repaint p))
