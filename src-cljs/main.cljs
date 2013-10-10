(ns dumbbell.main
  (:require [monet.canvas :as canvas]))

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
  (canvas/fill-style @ctx :black)
  (canvas/fill-rect @ctx {:x 0 :y 0 :w (:w game) :h (:h game)}))

(defn put-pixel
  [ctx {:keys [x y]}]
  (canvas/fill-rect ctx {:x x :y y :w 1 :h 1}))

(defn advance-snake
  "Advance the snake one step in direction dir, returning
   an updated pos."
  [pos dir speed]
  (cond
    (= dir :left)  {:x (mod (- (:x pos) speed)
                            (:w game))
                    :y (:y pos)}
    (= dir :right) {:x (mod (+ (:x pos) speed)
                            (:w game))
                    :y (:y pos)}
    (= dir :up)    {:y (mod (- (:y pos) speed)
                            (:h game))
                    :x (:x pos)}
    (= dir :down)  {:y (mod (+ (:y pos) speed)
                            (:h game))
                    :x (:x pos)}))

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
