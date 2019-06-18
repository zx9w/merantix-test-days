(ns merantix-test.drawing
  (:require [reagent.core :as r]
            [monet.canvas :as canvas]
            ))


(def click-count (r/atom 0))

(defn Canvas []
  (let [s (r/atom {})]
    (r/create-class
     {:component-did-mount
      (fn []
        :display-name "the-canvas-component"
        (def canvas-dom (.getElementById js/document "background"))
        (def monet-canvas (canvas/init canvas-dom "2d"))
        (canvas/add-entity monet-canvas :background
                           (canvas/entity {:x 0 :y 0 :w 600 :h 600} ; val
                                          nil                       ; update function
                                          (fn [ctx val]             ; draw function
                                            (-> ctx
                                                (canvas/fill-style "#09f123")
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
