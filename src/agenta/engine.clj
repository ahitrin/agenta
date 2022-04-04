(ns agenta.engine
  (:require [agenta.game-map :as gm]
            [agenta.perk]
            [agenta.random :as rnd]
            [clojure.tools.logging :as log])
  (:import (agenta UnitType Unit UnitCommand UnitState)))

(defn- make-unit [spec]
  (proxy [UnitType] []
    (getBaseAttack [] (:baseAttack spec))
    (getRandAttack [] (:randAttack spec))
    (getRange [] (:range spec))
    (getAttackSpeed [] (:attackSpeed spec))
    (getHitPoints [] (:hitPoints spec))
    (getHealthLimit [lvl] (/ (* (- 4 lvl) (:hitPoints spec)) 5))
    (toString [] (:name spec))))

(defn- init-units [setting]
  (let [defs (for [ut (:unit-types setting)
                   p (range 2)
                   c (range (-> (:placement setting)
                                (get p)
                                (get (.toLowerCase (:name ut)) 0)))]
               [ut p c])
        idx (range)
        g (rnd/get-generator)
        defs+ (map flatten (map vector defs idx))]
    (for [[ut p _ id] defs+
          :let [spd-counter (inc (rnd/i! (:speed ut)))
                att-counter (inc (rnd/i! (:attackSpeed ut)))
                hlt-counter (inc (rnd/i! 100))]]
      {
       ; "static" properties (do not change during game)
       :max-spd        (:speed ut)
       :visibility     (:visibility ut)
       :img            (str (:image ut) p)
       :id             id
       ; Unit instance (should be removed)
       :old            (Unit. (make-unit ut)
                              id
                              p
                              spd-counter
                              att-counter
                              hlt-counter
                              g
                              ((resolve (.get (:perk ut) "select"))))
       ; "dynamic" properties (change during game)
       :speed-counter  spd-counter
       :attack-counter att-counter
       :health-counter hlt-counter
       :kills          0})))

(defn regen [ctr hp max-hp]
  (cond
    (neg-int? ctr) [ctr hp]
    (pos-int? ctr) [(dec ctr) hp]
    (>= hp max-hp) [-1 hp]
    :else [100 (inc hp)]))

(defn- run-unit-action! [[[x y] u] m]
  (let [unit (:old u)
        visible-objects (gm/objects-in-radius m x y (:visibility u))]
    (let [hc (.-healthCounter unit)
          hp (.-currentHitPoints unit)
          max-hp (.getHitPoints (.getType unit))
          [new-ctr new-hp] (regen hc hp max-hp)]
      (set! (.-healthCounter unit) new-ctr)
      (set! (.-currentHitPoints unit) new-hp))
    [u (first (.act unit (map :old visible-objects))) [x y]]))

(defn- perform-attack! [m actor action [x y]]
  (let [target-id (int (.get action "target"))
        ; TODO replace this (very ineffective) calculation with some kind of map :id -> unit
        u (first (filter #(= target-id (:id %)) (vals (:objs m))))
        target (:old u)]
    (if (some? target)
      (let [damage (.doAttack (:old actor))
            hp (.-currentHitPoints target)
            new-hp (- hp damage)
            ctr (.-healthCounter target)
            prio (.getPriority (.-currentCommand target))
            limit (.getHealthLimit (.-type target) prio)]
        (if (and (pos? damage) (pos? hp))
          (do
            (log/debugf "%s strikes %s with %d" (dissoc actor :img :max-spd :visibility) target damage)
            (set! (.-currentHitPoints target) new-hp)
            (when (neg-int? ctr)
              (set! (.-healthCounter target) 100))
            (if-not (pos-int? new-hp)
              (do
                (log/debugf "%s is dead" target)
                (-> m
                    (gm/new-map #(update-in % [[x y] :kills] inc))
                    (gm/new-map #(dissoc % [(.getX target) (.getY target)]))))
              m))
          m))
      m)))

(defn- perform-move! [m actor action [x y]]
  (let [dx (int (.get action "dx"))
        dy (int (.get action "dy"))]
    (if (zero? (.-speedCounter (:old actor)))
      (let [nx (+ x dx) ny (+ y dy)]
        (if (gm/can-place? m [nx ny])
          (do
            (.moveTo (:old actor) nx ny)
            (set! (.-speedCounter (:old actor)) (:max-spd actor))
            (gm/new-map m #(assoc (dissoc % [x y]) [nx ny] actor)))
          m))
      m)))

(def action-selector
  {"attack" perform-attack!
   "move"   perform-move!})

(defn- apply-action! [m [actor action [x y]]]
  (let [atype (.get action "type")]
    (apply (action-selector atype) [m actor action [x y]])))

(defn- apply-actions! [actions m]
  (reduce apply-action! m
          (filter #(.isAlive (:old (first %))) actions)))

(defn run-game! [setting viewer]
  (let [u (init-units setting)
        start-map (gm/make-map (:map setting) u)
        limit (:max-ticks (:experiment setting))]
    (loop [m start-map winner -1 tick 0]
      (let [objs (:objs m)]
        (viewer m objs)
        (if (or (<= 0 winner) (<= limit tick))
          {:winner winner :steps tick}
          (let [actions (set (filter #(some? (second %)) (map #(run-unit-action! % m) objs)))
                new-m (apply-actions! actions m)
                units-per-player (group-by #(.getPlayer (:old %)) (vals (:objs new-m)))]
            (recur new-m
                   (cond (empty? (units-per-player 0)) 1
                         (empty? (units-per-player 1)) 0
                         :else -1)
                   (inc tick))))))))
