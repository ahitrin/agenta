(ns agenta.engine
  (:require [agenta.game-map :as gm]
            [agenta.perk]
            [clojure.tools.logging :as log])
  (:import (agenta UnitType Unit SingleRandom UnitCommand UnitState)))

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
        damage (.doAttack actor)
        hp (.-currentHitPoints target)
        new-hp (- hp damage)
        ctr (.-healthCounter target)
        prio (.getPriority (.-currentCommand target))
        limit (.getHealthLimit (.-type target) prio)]
    (when (and (pos? damage) (pos? hp))
      (log/debugf "%s strikes %s with %d" actor target damage)
      (set! (.-currentHitPoints target) new-hp)
      (when (neg-int? ctr)
        (set! (.-healthCounter target) 100))
      (when (< new-hp limit)
        (.obtain target (UnitCommand. UnitState/ESCAPE (inc prio))))
      (when-not (.isAlive target)
        (log/debugf "%s is dead" target)
        (set! (.-kills actor) (inc (.-kills actor)))
        (gm/remove-object m target)))))

(defn -perform-move [m ^Unit actor adata]
  (let [dx (int (.get adata "dx"))
        dy (int (.get adata "dy"))]
    (when (and (zero? (.-speedCounter actor))
               (gm/try-move m actor dx dy))
      (set! (.-speedCounter actor) (.getSpeed (.getType actor))))))

(defn apply-actions [actions m]
  (doseq [a actions
          :let [actor (.getActor a)
                adata (.getData a)
                atype (.get adata "type")]
          :when (.isAlive actor)]
    (case atype
      "attack" (-perform-attack m actor adata)
      "move" (-perform-move m actor adata)))
  m)

(defn run [setting viewer]
  (let [g (SingleRandom/get)
        u (init-units g setting)
        start-map (gm/make-map g (:map setting) u)
        limit (:max-ticks (:experiment setting))]
    (loop [units u m start-map winner -1 steps 0]
      (let [alive-units (filter #(.isAlive %) units)
            units-per-player (group-by #(.getPlayer %) alive-units)]
        (viewer m alive-units)
        (if (or (<= 0 winner) (<= limit steps))
          {:winner winner :steps steps}
          (let [all-actions (map #(-run-unit-action % m) alive-units)
                good-actions (set (filter some? all-actions))
                new-m (apply-actions good-actions m)]
            (recur alive-units
                   new-m
                   (cond (empty? (units-per-player 0)) 1
                         (empty? (units-per-player 1)) 0
                         :else -1)
                   (inc steps))))))))
