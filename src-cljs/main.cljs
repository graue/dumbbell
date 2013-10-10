(ns dumbbell.main
  (:require [monet.canvas :as canvas]))

(def ctx (atom nil))
(def snake-dir (atom :right))
(def snake-pos (atom {:x 200 :y 200}))
(def snake-speed (atom 0.2))

(defn clear-bg
  []
  (canvas/fill-style @ctx :black)
  (canvas/fill-rect @ctx {:x 0 :y 0 :w 400 :h 400}))

(defn put-pixel
  [ctx {:keys [x y]}]
  (canvas/fill-rect ctx {:x x :y y :w 1 :h 1}))

(defn advance-snake
  "Advance the snake one step in direction dir."
  [pos dir speed]
  (cond
    (= dir :left)  {:x (- (:x pos) speed) :y (:y pos)}
    (= dir :right) {:x (+ (:x pos) speed) :y (:y pos)}
    (= dir :up)    {:y (- (:y pos) speed) :x (:x pos)}
    (= dir :down)  {:y (+ (:y pos) speed) :x (:x pos)}))

(defn update-game-state
  []
  (swap! snake-pos advance-snake @snake-dir @snake-speed)
  (canvas/fill-style @ctx :white)
  (put-pixel @ctx {:x (Math/floor (:x @snake-pos))
                   :y (Math/floor (:y @snake-pos))}))

(defn tick
  []
  (update-game-state)
  (.requestAnimationFrame js/window tick))

(defn start-game-loop
  []
  (.requestAnimationFrame js/window tick))

(defn ^:export startgame
  [el]
  (reset! ctx (canvas/get-context el "2d"))
  (clear-bg)
  (start-game-loop))
