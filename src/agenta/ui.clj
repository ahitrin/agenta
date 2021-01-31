(ns agenta.ui
  (:import (agenta PanelViewer Viewer GameMap Unit MapCellType)
           (javax.swing JFrame JOptionPane JPanel)
           (java.util.function Consumer)
           (java.awt Graphics)
           (java.awt.image BufferedImage)))

(defn -add-to-content [^JFrame f ^JPanel p]
  (.add (.getContentPane f)
        p))

(defn wrap-into-frame [^JFrame f ^JPanel p]
  (doto f
    (.setSize (.-width (.getSize p)) (.-height (.getSize p)))
    (.setVisible true)
    (-add-to-content p)
    (.setDefaultCloseOperation JFrame/EXIT_ON_CLOSE)))

(def -tiles {MapCellType/GRASS "grass0"
             MapCellType/TREE "tree0"})

(defn -get-image [^JPanel panel state key]
  (.getImage (.getToolkit panel) (str "Pictures/" key ".gif")))

(defn -make-viewer [^JFrame f]
  (let [state (atom {:cache {} :current nil})]
    (proxy [JPanel Viewer] []
      (paint [^Graphics g]
        (.drawImage g (:current @state) 0 0 this))
      (update [^GameMap m]
        (when (nil? (:current @state))
          (.setSize this (* 25 (.getSizeX m)) (* 25 (.getSizeY m)))
          (.setVisible this true)
          (wrap-into-frame f this))
        (let [size (.getSize this)
              image (cast BufferedImage (.createImage this (.-width size) (.-height size)))
              currentGraph (.createGraphics image)]
          (doseq [i (range (.getSizeX m))
                  j (range (.getSizeY m))
                  :let [u (cast Unit (.getGroundObject m i j))
                        tile-name (if (some? u)
                                    (str (.getImage (.getType u)) (.getPlayer u))
                                    (-tiles (.getCellType m i j)))]]
            (.drawImage ^Graphics currentGraph
                        (-get-image this state tile-name)
                        (int (* 25 i))
                        (int (* 25 j))
                        this))
          (swap! state assoc :current image))))))

(defn make-viewer [^JFrame f]
  (PanelViewer. (proxy [Consumer] [] (accept [p1] (wrap-into-frame f p1))))
  ;(-make-viewer f)
  )

(defn show-end-message [^JFrame f ^PanelViewer p ^String m]
  (JOptionPane/showMessageDialog f m "End of game" JOptionPane/INFORMATION_MESSAGE)
  (.repaint p))
