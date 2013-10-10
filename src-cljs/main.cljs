(ns dumbbell.main
  (:require [monet.canvas :as canvas]))

(def ctx (atom nil))
(def snake-dir (atom :right))
(def snake-pos (atom {:x 200 :y 200}))

(defn ^:export startgame
  [el]
  (reset! ctx (canvas/get-context el "2d"))
  (canvas/fill-style @ctx :black)
  (canvas/fill-rect @ctx {:x 0 :y 0 :w 400 :h 400}))
