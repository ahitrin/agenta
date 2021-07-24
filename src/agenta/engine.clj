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
    (getVisibility [] (:visibility spec))
    (getSpeed [] (:speed spec))
    (getHitPoints [] (:hitPoints spec))
    (getHealthLimit [lvl] (/ (* (- 4 lvl) (:hitPoints spec)) 5))
    (toString [] (:name spec))))

(defn- init-units [setting]
  (for [ut (:unit-types setting)
        p (range 2)
        c (range 100)
        :let [max-num (get (get (:placement setting) p)
                           (.toLowerCase (:name ut)) 0)]
        :when (< c max-num)]
    {:old   (Unit. (make-unit ut) p (rnd/get-generator) ((resolve (.get (:perk ut) "select"))))
     :kills 0
     :img   (str (:image ut) p)}))

(defn- run-unit-action! [[[x y] u] m]
  (let [visible-objects (gm/objects-in-radius m x y (.getVisibility (.getType (:old u))))]
    [u (first (.act (:old u) (map :old visible-objects))) [x y]]))

(defn- perform-attack! [m actor action [x y]]
  (let [target (cast Unit (.get action "target"))
        damage (.doAttack (:old actor))
        hp (.-currentHitPoints target)
        new-hp (- hp damage)
        ctr (.-healthCounter target)
        prio (.getPriority (.-currentCommand target))
        limit (.getHealthLimit (.-type target) prio)]
    (if (and (pos? damage) (pos? hp))
      (do
        (log/debugf "%s strikes %s with %d" (dissoc actor :img) target damage)
        (set! (.-currentHitPoints target) new-hp)
        (when (neg-int? ctr)
          (set! (.-healthCounter target) 100))
        (when (< new-hp limit)
          (.obtain target (UnitCommand. UnitState/ESCAPE (inc prio))))
        (if-not (pos-int? new-hp)
          (do
            (log/debugf "%s is dead" target)
            (-> m
                (gm/new-map #(update-in % [[x y] :kills] inc))
                (gm/new-map #(dissoc % [(.getX target) (.getY target)]))))
          m))
      m)))

(defn- perform-move! [m actor action [x y]]
  (let [dx (int (.get action "dx"))
        dy (int (.get action "dy"))]
    (if (zero? (.-speedCounter (:old actor)))
      (let [nx (+ x dx) ny (+ y dy)]
        (if (gm/can-place? m (list nx ny))
          (do
            (.moveTo (:old actor) nx ny)
            (set! (.-speedCounter (:old actor)) (.getSpeed (.getType (:old actor))))
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
    (loop [m start-map winner -1 steps 0]
      (let [objs (:objs m)]
        (viewer m objs)
        (if (or (<= 0 winner) (<= limit steps))
          {:winner winner :steps steps}
          (let [actions (set (filter #(some? (second %)) (map #(run-unit-action! % m) objs)))
                new-m (apply-actions! actions m)
                units-per-player (group-by #(.getPlayer (:old %)) (vals (:objs new-m)))]
            (recur new-m
                   (cond (empty? (units-per-player 0)) 1
                         (empty? (units-per-player 1)) 0
                         :else -1)
                   (inc steps))))))))
