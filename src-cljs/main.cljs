(ns dumbbell.main
  (:require [monet.canvas :as canvas]
            [goog.events :as events]
            [goog.events.EventType]))

(def keycode->dir
  {38 :up
   40 :down
   37 :left
   39 :right})

(def dir->vek
  {:up    {:y -1}
   :down  {:y  1}
   :left  {:x -1}
   :right {:x  1}})

(defn mapmap
  "Return a hashmap like m, but with f applied to each value."
  ;; XXX: can I use algo.generic.functor from CLJS? This is just fmap
  [f m]
  (into {} (for [[k v] m] [k (f v)])))

(defn add-vek
  "For every key in pos, add the corresponding value from vek,
   if it exists."
  [pos vek]
  (into {}
    (for [[k v] pos]
         [k (+ v (get vek k 0))])))

;; Gameplay constants.
(def game
  {:w 400 :h 400
   :bg :black
   :fg :white})

(def ctx (atom nil))
(def snake-dir (atom :right))
(def snake-pos (atom {:x (/ (:w game) 2)
                      :y (/ (:h game) 2)}))
(def snake-speed (atom 0.4))

(defn clear-bg
  []
  (canvas/fill-style @ctx (:bg game))
  (canvas/fill-rect @ctx {:x 0 :y 0 :w (:w game) :h (:h game)}))

(defn put-pixel
  [ctx {:keys [x y]}]
  (canvas/fill-rect ctx {:x x :y y :w 1 :h 1}))

(defn wrap-around
  [{:keys [x y]}]
  {:x (mod x (:w game))
   :y (mod y (:h game))})

(defn advance-snake
  "Advance the snake one step in direction dir, returning
   an updated pos."
  [pos dir speed]
  (wrap-around
    (add-vek
      pos
      (mapmap (partial * speed) (dir->vek dir)))))

(defn update-game-state
  []
  (swap! snake-pos advance-snake @snake-dir @snake-speed)
  (canvas/fill-style @ctx (:fg game))
  (put-pixel @ctx {:x (Math/floor (:x @snake-pos))
                   :y (Math/floor (:y @snake-pos))}))

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
    (reset! snake-dir dir)))

(defn ^:export startgame
  [el]
  (reset! ctx (canvas/get-context el "2d"))
  (clear-bg)
  (start-game-loop)
  (events/listen js/document goog.events.EventType.KEYDOWN
                 process-keydown))
