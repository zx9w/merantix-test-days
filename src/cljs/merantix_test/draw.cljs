(ns merantix-test.drawing
  (:require [reagent.core :as r]
            [monet.canvas :as canvas]
            ))



(defn Canvas []
  (let [s (r/atom {})]
    (r/create-class
     {:component-did-mount
      (fn []
        :display-name "the-canvas-component"
        (swap! s assoc :canvas-dom (.getElementById js/document "background"))
        (swap! s assoc :monet-canvas (canvas/init (:canvas-dom @s) "2d")) ; this is the ctx
        (canvas/add-entity (:monet-canvas @s) :background
                           (canvas/entity {:x 0 :y 0 :w 600 :h 400} ; val
                                          nil                       ; update function
                                          (fn [ctx val]             ; draw function
                                            (-> ctx
                                                (canvas/fill-style "#dddddd") ; "#09f123"
                                                (canvas/fill-rect val))))))

      :reagent-render
      (fn []
        [:canvas {:id "background"
                  :height 400
                  :width 600
                  :style {:border "1px solid #000000"}
                  :ref #(swap! s assoc :canvas %)}])})))



;(defn )

; I make a component where the user clicks, then I track movement?
