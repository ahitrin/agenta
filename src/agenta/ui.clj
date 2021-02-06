(ns agenta.ui
  (:import (agenta GameMap Unit MapCellType ImagePanel)
           (javax.swing JFrame JOptionPane JPanel)
           (java.awt Graphics Component)
           (java.awt.image BufferedImage)))

(def -tiles {MapCellType/GRASS "grass0"
             MapCellType/TREE "tree0"})

(defn -get-image [^JPanel panel state key]
  (let [val (get-in @state
                    [:cache key]
                    (.getImage (.getToolkit panel) (str "Pictures/" key ".gif")))]
    (swap! state assoc-in [:cache key] val)
    val))

(defn wrap-viewer [^JFrame f ^ImagePanel p]
  (let [state (atom {:cache {} :need-init true})]
    (fn [^GameMap m]
      (let [size-x (.getSizeX m)
            size-y (.getSizeY m)
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
                  :let [u (cast Unit (.getGroundObject m i j))
                        tile-name (if (some? u)
                                    (str (.getImage (.getType u)) (.getPlayer u))
                                    (-tiles (.getCellType m i j)))]]
            (.drawImage ^Graphics currentGraph
                        (-get-image p state tile-name)
                        (int (* 25 i))
                        (int (* 25 j))
                        p))
          (.setImage p image)
          (.repaint p))))))

(defn show-end-message [^JFrame f ^JPanel p ^String m]
  (JOptionPane/showMessageDialog f m "End of game" JOptionPane/INFORMATION_MESSAGE)
  (.repaint p))
