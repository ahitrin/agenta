(ns agenta.ui
  (:require [agenta.game-map :as gm])
  (:import (agenta ImagePanel)
           (javax.swing JFrame JOptionPane JPanel)
           (java.awt Graphics Component)
           (java.awt.image BufferedImage)
           (javax.imageio ImageIO)
           (java.io File)))

(def -tiles {:grass "grass0"
             :tree "tree0"})

(defn simple-cache []
  (let [cache (atom {})]
    (fn [key builder]
      (let [val (get-in @cache key builder)]
        (swap! cache assoc key val)
        val))))

(defn -copy-image [^BufferedImage i]
  (let [cm (.getColorModel i)
        is-alpha (.isAlphaPremultiplied cm)
        raster (.copyData i nil)]
    (BufferedImage. cm raster is-alpha nil)))

(defn draw-image [^Graphics graphics image-cache tile-name x y]
  (.drawImage graphics
              (image-cache tile-name (ImageIO/read (File. (str "Pictures/" tile-name ".gif"))))
              (int (* 25 x))
              (int (* 25 y))
              nil))

(defn wrap-viewer [^JFrame f ^ImagePanel p]
  (let [bg (atom nil) image-cache (simple-cache)]
    (fn [m objs]
      (let [size-x (:size-x m)
            size-y (:size-y m)
            pix-x (* 25 size-x)
            pix-y (* 25 size-y)]
        (when (nil? @bg)
          (.setSize p pix-x pix-y)
          (.setVisible p true)
          (.setSize f pix-x pix-y)
          (.setVisible f true)
          (.add (.getContentPane f) ^Component p)
          (.setDefaultCloseOperation f JFrame/EXIT_ON_CLOSE)
          (let [new-bg (BufferedImage. pix-x pix-y BufferedImage/TYPE_INT_RGB)
                graphics (.createGraphics new-bg)]
            (doseq [i (range size-x)
                    j (range size-y)
                    :let [tile-name (-tiles (gm/cell-type m i j))]]
              (draw-image graphics image-cache tile-name i j))
            (reset! bg new-bg)))
        (let [image (-copy-image @bg)
              currentGraph (.createGraphics image)]
          (doseq [o objs
                  :let [[x y] (key o)
                        u (val o)
                        tile-name (str (.getImage (.getType u)) (.getPlayer u))]]
            (draw-image currentGraph image-cache tile-name x y))
          (.setImage p image)
          (.repaint p))))))

(defn show-end-message [^JFrame f ^JPanel p ^String m]
  (JOptionPane/showMessageDialog f m "End of game" JOptionPane/INFORMATION_MESSAGE)
  (.repaint p))
