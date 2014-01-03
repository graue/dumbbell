(ns dumbbell.vectors)

(defprotocol VectorMath2D
  (v+ [v1 v2] "Adds two vectors")
  (vfloor [v] "Rounds both components down to nearest integer"))

(declare map->Vec2D)

(defrecord Vec2D [x y]
  VectorMath2D
    (v+ [v1 v2]
      (map->Vec2D (merge-with + v1 v2)))
    (vfloor [v]
      (Vec2D. (Math/floor (:x v))
              (Math/floor (:y v)))))

(defn vec2d [x y]
  (Vec2D. x y))

(def dir->vec2d
  {:up    (Vec2D.  0 -1)
   :down  (Vec2D.  0  1)
   :left  (Vec2D. -1  0)
   :right (Vec2D.  1  0)})
