(ns dumbbell.main
  (:require [monet.canvas :as canvas]))

(defn ^:export startgame
  [el]
  (let [ctx (canvas/get-context el "2d")]
    (canvas/fill-style ctx :black)
    (canvas/fill-rect ctx {:x 0 :y 0 :w 400 :h 400})))
