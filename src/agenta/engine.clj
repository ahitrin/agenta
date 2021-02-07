(ns agenta.engine
  (:require [agenta.game-map :as gm]
            [agenta.perk])
  (:import (agenta UnitType Unit SingleRandom)))

(defn make-unit [spec]
  (proxy [UnitType] []
    (getName [] (:name spec))
    (getBaseAttack [] (:baseAttack spec))
    (getRandAttack [] (:randAttack spec))
    (getRange [] (:range spec))
    (getAttackSpeed [] (:attackSpeed spec))
    (getVisibility [] (:visibility spec))
    (getSpeed [] (:speed spec))
    (getHitPoints [] (:hitPoints spec))
    (getHealthLimit [lvl] (/ (* (- 4 lvl) (:hitPoints spec)) 5))
    (getImage [] (:image spec))
    (toString [] (:name spec))))

(defn init-units [^SingleRandom r setting]
  (for [ut (:unit-types setting)
        p (range 2)
        c (range 100)
        :let [max-num (get (get (:placement setting) p)
                           (.toLowerCase (:name ut)) 0)]
        :when (< c max-num)]
    (Unit. (make-unit ut) p r ((resolve (.get (:perk ut) "select")) r))))

(defn -run-unit-action [^Unit u m]
  (let [visible-objects (gm/objects-in-radius m u (.getVisibility (.getType u)))]
    (first (.act u visible-objects))))

(defn -perform-attack [m ^Unit actor adata]
  (let [target (cast Unit (.get adata "target"))
        damage (.doAttack actor)]
    (when (pos? damage)
      (printf "%s strikes %s with %d%n" actor target damage)
      (.sufferDamage target damage)
      (when-not (.isAlive target)
        (printf "%s is dead%n" target)
        (set! (.-kills actor) (inc (.-kills actor)))
        (gm/remove-object m target)))))

(defn -perform-move [m ^Unit actor adata]
  (let [dx (int (.get adata "dx"))
        dy (int (.get adata "dy"))]
    (when (and (zero? (.-speedCounter actor))
               (gm/try-move m actor dx dy))
      (set! (.-speedCounter actor) (.getSpeed (.getType actor))))))

(defn run [setting viewer]
  (let [g (SingleRandom/get)
        m (gm/make-map g (:map setting))
        u (init-units g setting)
        limit (:max-ticks (:experiment setting))]
    (doseq [unit u] (gm/place-where-possible g m unit))
    (loop [units u winner -1 steps 0]
      (let [alive-units (filter #(.isAlive %) units)
            units-per-player (group-by #(.getPlayer %) alive-units)]
        (viewer m)
        (if (or (<= 0 winner) (<= limit steps))
          {:winner winner :steps steps}
          (let [all-actions (map #(-run-unit-action % m) alive-units)
                good-actions (set (filter some? all-actions))]
            (doseq [a good-actions
                    :let [actor (.getActor a)
                          adata (.getData a)
                          atype (.get adata "type")]
                    :when (.isAlive actor)]
              (case atype
                "attack" (-perform-attack m actor adata)
                "move" (-perform-move m actor adata)))
            (recur alive-units
                   (cond (empty? (units-per-player 0)) 1
                         (empty? (units-per-player 1)) 0
                         :else -1)
                   (inc steps))))))))
