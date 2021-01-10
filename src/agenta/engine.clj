(ns agenta.engine
  (:import (agenta SingleRandom Unit UnitType)))

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
