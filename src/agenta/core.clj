(ns agenta.core
  (:require agenta.ui)
  (:import (agenta Engine PanelViewer SingleRandom UnitTypeImpl GameMap Unit Selector)))

(defn make-unit [spec]
  (doto (UnitTypeImpl.)
    (.setName (:name spec))
    (.setBaseAttack (:baseAttack spec))
    (.setRandAttack (:randAttack spec))
    (.setRange (:range spec))
    (.setAttackSpeed (:attackSpeed spec))
    (.setVisibility (:visibility spec))
    (.setSpeed (:speed spec))
    (.setHitPoints (:hitPoints spec))
    (.setImage (:image spec))))

(defn select-random-memoized
  [^SingleRandom r]
  (let [target (atom nil)
        choose (fn [cur new-units]
                 (cond
                   (and (some? cur) (.isAlive cur) (.contains new-units cur)) cur
                   (false? (.isEmpty new-units)) (.get new-units (.nextInt r (count new-units)))
                   :else nil))]
    (proxy [Selector] []
      (apply [units]
        (swap! target choose units)))))

(defn init-units [^SingleRandom r setting]
  (for [ut (map make-unit (:unit-types setting))
        p (range 2)
        c (range 100)
        :let [max-num (get (get (:placement setting) p)
                           (.toLowerCase (.getName ut)) 0)]
        :when (< c max-num)]
    (Unit. ut p r (select-random-memoized r))))

(defn -main [& args]
  (let [s (clojure.edn/read-string (slurp "setting/demo.edn"))
        p (new PanelViewer)
        r (SingleRandom/get)
        m (GameMap. r)
        u (init-units r s)
        e (Engine. m u)
        f (agenta.ui/wrap-into-frame "Agenta demo" p)]
    (.renderTrees m)
    (doseq [unit u] (.placeWherePossible m unit))
    (.addViewer e p)
    (while (== (.getWinner e) -1)
      (.step e))
    (agenta.ui/show-end-message f p (format "Player %d has won after %d ticks!"
                                            (.getWinner e)
                                            (.getTicks e)))))
