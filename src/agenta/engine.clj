(ns agenta.engine
  (:require [agenta.counter :as ctr]
            [agenta.game-map :as gm]
            [agenta.math :as m]
            [agenta.random :as rnd]
            [agenta.unit :as u]
            [clojure.tools.logging :as log]))

(defn choose-enemy [actor enemies]
  (:target (apply (:select-perk actor) actor [(map second enemies)])))

(defn act! [xy actor visible-objects]
  (let [enemies         (filter #(not (u/friends? actor (second %))) visible-objects)
        closest-enemies (filter #(m/in-radius? xy (:range actor) (first %)) enemies)]
    (case (:state actor)
      :escape
      (let [direction (->> enemies
                           (map #(m/v-> (first %) xy))
                           (map m/normalize-length)
                           (reduce m/v+v (m/v 0 0)))]
        {:type :move :dx (m/sign (:dx direction)) :dy (m/sign (:dy direction))})
      :attack
      (cond
        ; attack achievable enemy
        (seq closest-enemies)
        (let [chosen (choose-enemy actor closest-enemies)
              ids (clojure.string/join "," (map #(str (:id (second %))) closest-enemies))]
          {:type :attack :target (:id chosen) :ids ids})
        ; approach to enemy
        (seq enemies)
        (let [enemies-without-xy    (map second enemies)
              chosen-enemy          (choose-enemy actor enemies)
              chosen-idx            (.indexOf enemies-without-xy chosen-enemy)
              enemy-with-xy         (nth enemies chosen-idx)
              dx                    (m/sign (- (:x (first enemy-with-xy)) (:x xy)))
              dy                    (m/sign (- (:y (first enemy-with-xy)) (:y xy)))]
          {:type :move :dx dx :dy dy})
        ; random move
        :else
        (apply (:move-perk actor) [xy actor visible-objects])))))

(defn unit-action! [[xy u] m]
  (let [max-hp (:max-health u)
        new-hp (:health u)]
    (if (ctr/ready? (:think-counter u))
      (let [u1              (u/update-state (update u :think-counter ctr/reset) new-hp max-hp)
            visible-objects (gm/objects-in-radius m (:id u) (:visibility u))
            action          (act! xy u1 visible-objects)]
        (log/debugf "%s wants %s" (u/pretty u1) action)
        [u1 action xy])
      [u nil xy])))

(defn perform-attack! [m actor action xy]
  (let [obj (gm/obj-by-id m (:target action))]
    (if (and
         (ctr/ready? (:attack-counter actor))
         (some? obj))
      (let [u           (if (some? obj) (val obj) {})
            target-xy   (if (some? obj) (key obj) (m/xy 0 0))
            damage      (+ (rnd/i! (:rnd-attack actor)) (:base-attack actor))
            hp          (:health u)
            new-hp      (- hp damage)]
        (if (and (pos? damage) (pos? hp))
          (let [m1 (gm/new-map m #(update-in % [xy :attack-counter] ctr/reset))
                m2 (gm/new-map m1 #(assoc-in % [target-xy :health] new-hp))
                u1 (assoc-in u [:health] new-hp)]
            (log/debugf "%s strikes %s with %d" (u/pretty actor) (u/pretty u) damage)
            (if-not (pos-int? new-hp)
              (do
                (log/debugf "%s is dead" (u/pretty u1))
                (-> m2
                    (gm/new-map #(update-in % [xy :kills] inc))
                    (gm/new-map #(dissoc % target-xy))))
              m2))
          m))
      m)))

(defn perform-move [m actor action xy]
  (if (ctr/ready? (:speed-counter actor))
    (let [nx    (+ (:x xy) (int (:dx action)))
          ny    (+ (:y xy) (int (:dy action)))
          xy'   (m/xy nx ny)]
      (if (gm/can-place? m xy')
        (let [actor1 (update actor :speed-counter ctr/reset)]
          (gm/new-map m #(assoc (dissoc % xy) xy' actor1)))
        m))
    m))

(def action-selector
  {:attack perform-attack!
   :move   perform-move})

(defn apply-action! [m [actor action xy]]
  (let [obj     (gm/obj-by-id m (:id actor))
        atype   (:type action)]
    (if (and (some? obj)
             ; probably, an excessive check: actors with neg health are removed from map
             (pos? (:health actor)))
      (apply (action-selector atype) [m actor action xy])
      m)))

(defn apply-actions! [actions m]
  (reduce apply-action! m actions))

(defn on-hp-tick [m]
  (let [m1 (-> m
               (update :health-counter ctr/tick)
               (update :speed-counter ctr/tick)
               (update :attack-counter ctr/tick)
               (update :think-counter ctr/tick))
        grow (if (< (:health m1) (:max-health m1)) 1 0)
        m2 (if (ctr/ready? (:health-counter m1))
             (-> m1
                 (update :health-counter ctr/reset)
                 (update :health + grow))
             m1)]
    m2))

(defn phase-regeneration [m [xy a]]
  (when (< (:health a) (:max-health a))
    {:sender :engine :receiver (:id a) :message {:hp 1}}))

(defn tick-health [objs]
  (reduce-kv #(assoc %1 %2 (on-hp-tick %3)) {} objs))

(defn init-game [setting]
  {:map         (gm/make-map setting)
   :tick        0
   :end-tick    (:max-ticks (:experiment setting))})

(def all-phases [[:health-counter   phase-regeneration]])

(defn produce-messages [m phases]
  "Run all given phases against ready actors, and return all produced messages."
  (reduce concat
          (for [[ctr phase] phases]
            (->> m
                 :objs
                 (filter #(ctr/ready? (get (val %) ctr)))
                 (mapv (partial phase m))
                 (filter some?)))))

(defn single-step! [m]
  (let [msgs    (produce-messages m all-phases)
        objs    (gm/xy-to-unit m)
        actions (set (filter #(some? (second %)) (map #(unit-action! % m) objs)))
        new-m   (gm/new-map (apply-actions! actions m) tick-health)]
    (when (pos? (count msgs))
      (log/debugf msgs))
    new-m))

(defn current-winner [m]
  (let [units-per-player (group-by :player (vals (gm/xy-to-unit m)))]
    (cond (empty? (units-per-player 0)) 1
          (empty? (units-per-player 1)) 0
          :else -1)))

(defn run-loop! [viewer m winner tick end-tick stopper]
  (viewer m {:winner winner :tick tick})
  (if (or (<= 0 winner) (<= end-tick tick) (stopper))
    {:winner winner :steps tick}
    (let [new-m         (single-step! m)
          new-winner    (current-winner new-m)]
      (recur viewer
             new-m
             new-winner
             (inc tick)
             end-tick
             stopper))))

(defn run-game! [setting viewer]
  (let [game    (init-game setting)
        stopper (fn [] false)]
    (run-loop! viewer (:map game) -1 (:tick game) (:end-tick game) stopper)))
