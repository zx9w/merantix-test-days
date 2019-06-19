(ns merantix-test.picture
  (:require [reagent.core :as r]
  ))

(defn swap-as [col key val]
  (swap! col assoc key val))

(defn sq-dist [pt1 pt2]
  (+ (* (- (pt2 :x) (pt1 :x)) (- (pt2 :x) (pt1 :x)))
     (* (- (pt2 :y) (pt1 :y)) (- (pt2 :y) (pt1 :y)))))

(defn print-pt [pt]
  (if (and (contains? pt :x) (contains? pt :y))
    (str "(" (pt :x) ", " (pt :y) ")")
    (str "(-,-)")))

(defn draw-canvas-contents [ canvas ]
  (let [ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.beginPath ctx)
    (.moveTo ctx 0 0)
    (.lineTo ctx w h)
    (.moveTo ctx w 0)
    (.lineTo ctx 0 h)
    (.stroke ctx)
    ))


(def window-width (r/atom nil))

(defn Canvas []
  (let [dom-node (r/atom nil) s (r/atom {:clicks 0})]
    (r/create-class
     {:display-name "a-canvas-component"
      :component-did-update
      (fn [ this ]
        (draw-canvas-contents (.-firstChild @dom-node)))

      :component-did-mount
      (fn [ this ]
        (reset! dom-node (r/dom-node this)))

      :reagent-render
      (fn [ ]
        @window-width
        [:div.with-canvas
         [:canvas
          (if-let [node @dom-node]
                    {:width 600
                     :height 400
                     :style {:border "1px solid #000000"}
                     :ref #(swap-as s :canvas %)
                     :on-mouse-move
                     (fn [e]
                       (.persist e)
                       (swap-as s :location {:x (.-layerX (.-nativeEvent e))
                                             :y (.-layerY (.-nativeEvent e))})
                      )
                     :on-click
                     (fn [e]
                       (swap-as s :clicks (+ 1 (@s :clicks)))
                       (swap-as s :click-location
                                (conj (@s :click-location)
                                      (@s :location)))
                       ; debug info
                       ; (js/console.log (.-firstChild @dom-node)) ; canvas html element
                       (js/console.log @s)

                       (let [ctx (.getContext (@s :canvas) "2d")]
                         (js/console.log ctx)
                         (when (= 0 (mod (@s :clicks) 2))
                             (.beginPath ctx)
                             (.moveTo ctx 
                                     (:x (first ( @s :click-location )))
                                     (:y (first ( @s :click-location ))))
                             (.lineTo ctx
                                      (:x (second ( @s :click-location )))
                                      (:y (second ( @s :click-location ))))
                             (.stroke ctx)))

                       ;; (defn start-path [canvas pt]
                       ;;   (let [ctx (.getContext canvas "2d")]
                       ;;     (.moveTo ctx x y)))



                       ;; (.beginPath ctx)
                       ;; (.moveTo ctx 0 0)
                       ;; (.lineTo ctx w h)
                       ;; (.moveTo ctx w 0)
                       ;; (.lineTo ctx 0 h)
                       ;; (.stroke ctx)


                       )})]])})))


  (defn on-window-resize [evt]
    (reset! window-width (.-innerWidth js/window)))


;; {:clicks 0
;;  :location {:x "-" :y "-"}
;;  :track-pt {:x "-" :y "-"}
;;  :path-start {:x "-" :y "-"}
;;  :path-end {:x "-" :y "-"}}

      ;; (fn []
      ;;   [:table
      ;;    [:tbody
      ;;     [:tr
      ;;      [:td {:col-span 4}
      ;;       [:canvas {:id "background"
      ;;                 :height 400
      ;;                 :width 600
      ;;                 :style {:border "1px solid #000000"}
      ;;                 :ref #(swap! s assoc :canvas %)
      ;;                 :on-mouse-move (fn [e]
      ;;                                  (swap-as s :location
      ;;                                           {:x (int (- e.clientX (.-x (@s :offset))))
      ;;                                            :y (int (- e.clientY (.-y (@s :offset))))})
      ;;                                  (if (number? ((@s :track-pt) :x))
      ;;                                    (if (> (sq-dist (@s :track-pt) (@s :location)) 100)
      ;;                                      (swap-as s :track-pt (@s :location)))))
      ;;                 ;:on-mouse-out (fn [e]
      ;;                 ;                (swap-as s :location {:x "-" :y "-"}))
      ;;                 :on-click (fn [e]
      ;;                             (swap-as s :clicks (+ 1 (@s :clicks)))
      ;;                             (js/console.log @s)
      ;;                           ;;;  (. ((@s :mc) :ctx) (beginPath))
      ;;                             (circle ((@s :mc) :ctx) (@s :location) 4)
      ;;                             (if (= 1 ( mod (@s :clicks) 2))
      ;;                               ((swap-as s :path-start (@s :location))
      ;;                                (swap-as s :path-end {:x "-" :y "-"})
      ;;                                (swap-as s :track-pt (@s :path-start)))
      ;;                               ((swap-as s :path-end (@s :location))
      ;;                                (swap-as s :track-pt {:x "-" :y "-"}))))
      ;;                 }]]]
      ;;     [:tr
      ;;      [:td [:p "Coordinates: " (print-pt (@s :location))]]
      ;;      [:td [:p "Track Point: " (print-pt (@s :track-pt)) ]]
      ;;      [:td [:p (print-pt (@s :path-start))
      ;;            " -> " (print-pt (@s :path-end))]]
      ;;      [:td [:p (@s :debug)]]
      ;;      ]]])})))
