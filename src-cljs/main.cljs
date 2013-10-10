(ns dumbbell.main
  (:require [monet.canvas :as canvas]))

(def ctx (atom nil))
(def snake-dir (atom :right))
(def snake-pos (atom {:x 200 :y 200}))

(defn clear-bg
  []
  (canvas/fill-style @ctx :black)
  (canvas/fill-rect @ctx {:x 0 :y 0 :w 400 :h 400}))

(defn tick
  []
  (comment (.log js/console "Tick!"))
  (.requestAnimationFrame js/window tick))

(defn start-game-loop
  []
  (.requestAnimationFrame js/window tick))

(defn ^:export startgame
  [el]
  (reset! ctx (canvas/get-context el "2d"))
  (clear-bg)
  (start-game-loop))
