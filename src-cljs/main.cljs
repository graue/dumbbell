(ns dumbbell.main
  (:require [monet.canvas :as canvas]
            [goog.events :as events]
            [goog.events.EventType]))

(def keycode->dir
  {38 :up
   40 :down
   37 :left
   39 :right})

(def opposite-dirs
  [#{:up :down}
   #{:left :right}])

(defprotocol VectorMath2D
  (v+ [v1 v2] "Adds two vectors")
  (vfloor [v] "Rounds both components down to nearest integer"))

(defrecord Vec2D [x y]
  VectorMath2D
    (v+ [v1 v2]
      (Vec2D. (+ (:x v1)
                 (get v2 :x 0))
              (+ (:y v1)
                 (get v2 :y 0))))
    (vfloor [v]
      (Vec2D. (Math/floor (:x v))
              (Math/floor (:y v)))))

(def dir->vec2d
  {:up    (Vec2D.  0 -1)
   :down  (Vec2D.  0  1)
   :left  (Vec2D. -1  0)
   :right (Vec2D.  1  0)})

(defn mapmap
  "Return a hashmap like m, but with f applied to each value."
  ;; XXX: can I use algo.generic.functor from CLJS? This is just fmap
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

;; Gameplay constants.
(def game
  {:w 400 :h 400
   :bg :black
   :fg :white
   :fg-as-rgba {:red 255 :green 255 :blue 255 :alpha 255}})

(def ctx (atom nil))
(def snake-dir (atom :right))
(def snake-pos (atom (Vec2D. (/ (:w game) 2)
                             (/ (:h game) 2))))
(def snake-speed (atom 0.4))
(def dumbbell-pos (atom nil))

(defn clear-bg
  []
  (canvas/fill-style @ctx (:bg game))
  (canvas/fill-rect @ctx {:x 0 :y 0 :w (:w game) :h (:h game)}))

(defn put-pixel
  [ctx {:keys [x y]}]
  (canvas/fill-rect ctx {:x x :y y :w 1 :h 1}))

(defn wrap-around
  [{:keys [x y]}]
  (Vec2D. (mod x (:w game))
          (mod y (:h game))))

(def dumbbell-pixels
  (for [y (range -2 3)
        x (range -2 3)
        :when (<= (Math/abs x) (Math/abs y))]
    (Vec2D. x y)))

(defn- real-dumbbell-pixels
  [dbpos]
  (map #(wrap-around (v+ dbpos %)) dumbbell-pixels))

(defn draw-dumbbell
  [pos]
  (canvas/fill-style @ctx (:fg game))
  (doseq [pxpos (real-dumbbell-pixels pos)]
    (put-pixel @ctx pxpos)))

(defn place-dumbbell
  []
  (reset! dumbbell-pos
          (Vec2D. (rand-int (:w game))
                  (rand-int (:h game))))
  (draw-dumbbell @dumbbell-pos))

(defn touching-dumbbell?
  [spos dbpos]
  (some #{spos} (real-dumbbell-pixels dbpos)))

(defn erase-dumbbell
  [pos]
  (canvas/fill-style @ctx (:bg game))
  (doseq [y (range -12 13)
          x (range -12 13)]
    (put-pixel @ctx (wrap-around (v+ pos (Vec2D. x y))))))

(defn advance-snake
  "Advance the snake one step in direction dir, returning
   an updated pos."
  [pos dir speed]
  (wrap-around
    (v+
      pos
      (mapmap (partial * speed) (dir->vec2d dir)))))

(defn update-game-state
  []
  (let [old-pix-pos (vfloor @snake-pos)
        pos (swap! snake-pos advance-snake @snake-dir @snake-speed)
        pix-pos (vfloor pos)]
    (when (not= pix-pos old-pix-pos)
      (cond
        (touching-dumbbell? pix-pos @dumbbell-pos)
          (do
            (erase-dumbbell @dumbbell-pos)
            ;; todo: add to score
            (place-dumbbell))
        (= (canvas/get-pixel @ctx (:x pix-pos) (:y pix-pos))
           (:fg-as-rgba game))
          (do
            (reset! snake-speed 0) ;; todo: stop animation, proper game over
            (js/alert "Game over"))
        :else
          (do
            (canvas/fill-style @ctx (:fg game))
            (put-pixel @ctx pix-pos))))))

(defn tick
  []
  (update-game-state)
  (.requestAnimationFrame js/window tick))

(defn start-game-loop
  []
  (.requestAnimationFrame js/window tick))

(defn process-keydown
  [ev]
  (when-let [dir (keycode->dir (.-keyCode ev))]
    (when (not-any? #{#{dir @snake-dir}} opposite-dirs)
      (reset! snake-dir dir))))

(defn ^:export startgame
  [el]
  (reset! ctx (canvas/get-context el "2d"))
  (clear-bg)
  (place-dumbbell)
  (start-game-loop)
  (events/listen js/document goog.events.EventType.KEYDOWN
                 process-keydown))
