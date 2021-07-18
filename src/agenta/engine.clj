(ns agenta.engine
  (:require [agenta.game-map :as gm]
            [agenta.perk]
            [clojure.tools.logging :as log])
  (:import (agenta UnitType Unit SingleRandom UnitCommand UnitState Action)))

(defn- make-unit [spec]
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

(defn- init-units [^SingleRandom r setting]
  (for [ut (:unit-types setting)
        p (range 2)
        c (range 100)
        :let [max-num (get (get (:placement setting) p)
                           (.toLowerCase (:name ut)) 0)]
        :when (< c max-num)]
    (Unit. (make-unit ut) p r ((resolve (.get (:perk ut) "select")) r))))

(defn- run-unit-action! [[[x y] u] m]
  (let [visible-objects (gm/objects-in-radius m x y (.getVisibility (.getType u)))]
    (first (.act u visible-objects))))

(defn- perform-attack! [m ^Unit actor adata]
  (let [target (cast Unit (.get adata "target"))
        damage (.doAttack actor)
        hp (.-currentHitPoints target)
        new-hp (- hp damage)
        ctr (.-healthCounter target)
        prio (.getPriority (.-currentCommand target))
        limit (.getHealthLimit (.-type target) prio)]
    (if (and (pos? damage) (pos? hp))
      (do
        (log/debugf "%s strikes %s with %d" actor target damage)
        (set! (.-currentHitPoints target) new-hp)
        (when (neg-int? ctr)
          (set! (.-healthCounter target) 100))
        (when (< new-hp limit)
          (.obtain target (UnitCommand. UnitState/ESCAPE (inc prio))))
        (if-not (pos-int? new-hp)
          (do
            (log/debugf "%s is dead" target)
            (set! (.-kills actor) (inc (.-kills actor)))
            (gm/remove-object m (.getX target) (.getY target)))
          m))
      m)))

(defn- perform-move! [m ^Unit actor adata]
  (let [dx (int (.get adata "dx"))
        dy (int (.get adata "dy"))]
    (if (zero? (.-speedCounter actor))
      (gm/try-move! m actor (.getX actor) (.getY actor) dx dy)
      m)))

(def action-selector
  {"attack" perform-attack!
   "move"   perform-move!})

(defn- apply-action! [m ^Action action]
  (let [actor (.getActor action)
        adata (.getData action)
        atype (.get adata "type")]
    (apply (action-selector atype) [m actor adata])))

(defn- apply-actions! [actions m]
  (reduce apply-action! m
          (filter #(.isAlive (.getActor %)) actions)))

(defn run-game! [setting viewer]
  (let [g (SingleRandom/get)
        u (init-units g setting)
        start-map (gm/make-map g (:map setting) u)
        limit (:max-ticks (:experiment setting))]
    (loop [m start-map winner -1 steps 0]
      (let [objs (:objs m)
            units-per-player (group-by #(.getPlayer %) (vals objs))]
        (viewer m (vals objs))
        (if (or (<= 0 winner) (<= limit steps))
          {:winner winner :steps steps}
          (let [actions (set (filter some? (map #(run-unit-action! % m) objs)))
                new-m (apply-actions! actions m)]
            (recur new-m
                   (cond (empty? (units-per-player 0)) 1
                         (empty? (units-per-player 1)) 0
                         :else -1)
                   (inc steps))))))))
