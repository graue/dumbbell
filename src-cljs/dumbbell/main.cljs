(ns dumbbell.main
  (:require [monet.canvas :as canvas]
            [goog.events :as events]
            [goog.events.EventType]
            [dumbbell.vectors
             :refer [Vec2D VectorMath2D dir->vec2d v+ vfloor]]))

(def keycode->dir
  {38 :up
   40 :down
   37 :left
   39 :right})

(def opposite-dirs
  [#{:up :down}
   #{:left :right}])

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
(def next-snake-dir (atom :right))
(def snake-pos (atom (vfloor (Vec2D. (/ (:w game) 2)
                                     (/ (:h game) 2)))))
(def snake-speed (atom 30))  ; In pixels per second.
(def dumbbell-pos (atom nil))

(defn clear-bg []
  (canvas/fill-style @ctx (:bg game))
  (canvas/fill-rect @ctx {:x 0 :y 0 :w (:w game) :h (:h game)}))

(defn put-pixel [ctx {:keys [x y]}]
  (canvas/fill-rect ctx {:x x :y y :w 1 :h 1}))

(defn wrap-around [{:keys [x y]}]
  (Vec2D. (mod x (:w game))
          (mod y (:h game))))

(def dumbbell-pixels
  (for [y (range -2 3)
        x (range -2 3)
        :when (<= (Math/abs x) (Math/abs y))]
    (Vec2D. x y)))

(defn- real-dumbbell-pixels [dbpos]
  (map #(wrap-around (v+ dbpos %)) dumbbell-pixels))

(defn draw-dumbbell [pos]
  (canvas/fill-style @ctx (:fg game))
  (doseq [pxpos (real-dumbbell-pixels pos)]
    (put-pixel @ctx pxpos)))

(defn place-dumbbell []
  (reset! dumbbell-pos
          (Vec2D. (rand-int (:w game))
                  (rand-int (:h game))))
  (draw-dumbbell @dumbbell-pos))

(defn touching-dumbbell? [spos dbpos]
  (some #{spos} (real-dumbbell-pixels dbpos)))

(defn erase-dumbbell [pos]
  (canvas/fill-style @ctx (:bg game))
  (doseq [y (range -12 13)
          x (range -12 13)]
    (put-pixel @ctx (wrap-around (v+ pos (Vec2D. x y))))))

(defn advance-snake
  "Advance the snake one step in direction dir, returning
   an updated pos."
  [pos dir]
  (wrap-around
    (v+
      pos
      (dir->vec2d dir))))

(defn run-one-tick
  "Advance the snake by one pixel and check for resulting collisions.
  Since the game conflates drawing with data storage (we use get-pixel
  to see if you've collided with yourself!), this also updates the
  display."
  []
  (let [dir (reset! snake-dir @next-snake-dir)
        pos (swap! snake-pos advance-snake dir)]
    (cond
      (touching-dumbbell? pos @dumbbell-pos)
        (do
          (erase-dumbbell @dumbbell-pos)
          ;; todo: add to score
          (place-dumbbell))
      (= (canvas/get-pixel @ctx (:x pos) (:y pos))
          (:fg-as-rgba game))
        (do
          (reset! snake-speed 0) ;; todo: stop animation, proper game over
          (js/alert "Game over"))
      :else
        (do
          (canvas/fill-style @ctx (:fg game))
          (put-pixel @ctx pos)))))

(declare spawn-ticker)

(defn ticker [last-time fractional-ticks]
  (let [new-time (new js/Date)
        elapsed-ms (- new-time last-time)
        elapsed-ticks (+ (* 0.001 @snake-speed elapsed-ms)
                         fractional-ticks)
        full-ticks (Math/floor elapsed-ticks)]

    ;; Run logic for however many full ticks have passed, which may be zero.
    (dotimes [_ full-ticks] (run-one-tick))

    ;; Then spawn a new ticker, passing on any leftover fractional ticks.
    (spawn-ticker new-time (- elapsed-ticks full-ticks))))

(defn spawn-ticker [start-time fractional-ticks]
  (.requestAnimationFrame js/window (fn []
                                      (ticker start-time fractional-ticks))))

(defn start-game-loop []
  (spawn-ticker (new js/Date) 0))

(defn process-keydown [ev]
  (when-let [dir (keycode->dir (.-keyCode ev))]
    (when (not-any? #{#{dir @snake-dir}} opposite-dirs)
      (reset! next-snake-dir dir))))

(defn ^:export startgame [el]
  (reset! ctx (canvas/get-context el "2d"))
  (clear-bg)
  (place-dumbbell)
  (start-game-loop)
  (events/listen js/document goog.events.EventType.KEYDOWN
                 process-keydown))
