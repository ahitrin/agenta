(ns agenta.ui
  (:import (agenta Viewer GameMap Unit MapCellType)
           (javax.swing JFrame JOptionPane JPanel)
           (java.awt Graphics)
           (java.awt.image BufferedImage)))

(defn -add-to-content [^JFrame f ^JPanel p]
  (.add (.getContentPane f)
        p))

(def -tiles {MapCellType/GRASS "grass0"
             MapCellType/TREE "tree0"})

(defn -get-image [^JPanel panel state key]
  (let [val (get-in @state
                    [:cache key]
                    (.getImage (.getToolkit panel) (str "Pictures/" key ".gif")))]
    (swap! state assoc-in [:cache key] val)
    val))

(defn make-viewer [^JFrame f]
  (let [state (atom {:cache {} :current nil})]
    (proxy [JPanel Viewer] []
      (paint [^Graphics g]
        (.drawImage g (:current @state) 0 0 this))
      (update [^GameMap m]
        (let [size-x (.getSizeX m)
              size-y (.getSizeY m)
              pix-x (* 25 size-x)
              pix-y (* 25 size-y)]
          (when (nil? (:current @state))
            (.setSize this pix-x pix-y)
            (.setVisible this true)
            (.setSize f pix-x pix-y)
            (.setVisible f true)
            (-add-to-content f this)
            (.setDefaultCloseOperation f JFrame/EXIT_ON_CLOSE))
          (let [image (cast BufferedImage (.createImage this pix-x pix-y))
                currentGraph (.createGraphics image)]
            (doseq [i (range size-x)
                    j (range size-y)
                    :let [u (cast Unit (.getGroundObject m i j))
                          tile-name (if (some? u)
                                      (str (.getImage (.getType u)) (.getPlayer u))
                                      (-tiles (.getCellType m i j)))]]
              (.drawImage ^Graphics currentGraph
                          (-get-image this state tile-name)
                          (int (* 25 i))
                          (int (* 25 j))
                          this))
            (swap! state assoc :current image)
            (.repaint this)))))))

(defn show-end-message [^JFrame f ^JPanel p ^String m]
  (JOptionPane/showMessageDialog f m "End of game" JOptionPane/INFORMATION_MESSAGE)
  (.repaint p))
