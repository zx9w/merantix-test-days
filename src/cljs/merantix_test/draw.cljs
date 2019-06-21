(ns merantix-test.draw
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

(defn draw-canvas-contents [ canvas img ]
  (let [ctx (.getContext canvas "2d")
        w (.-clientWidth canvas)
        h (.-clientHeight canvas)]
    (.drawImage ctx img 0 0)

    ; TODO should be adjusted dynamically based on img
    (set! (.-strokeStyle ctx) "#ffffff")

    ; This draws an X over the canvas, used for debugging.
    ;; (.beginPath ctx)
    ;; (.moveTo ctx 0 0)
    ;; (.lineTo ctx w h)
    ;; (.moveTo ctx w 0)
    ;; (.lineTo ctx 0 h)
    ;; (.stroke ctx)
    ))

; experiment
;; (def img-id "canvas-background")
;; (def preview-src (atom ""))

;; (defn load-image [file-added-event]
;;   (let [file (first (array-seq (.. file-added-event -target -files)))
;;         file-reader (js/FileReader.)]
;;     (set! (.-onload file-reader)
;;           (fn [file-load-event]
;;             (reset! preview-src (-> file-load-event .-target .-result))
;;             (let [img (.getElementById js/document img-id)]
;;               (set! (.-onload img)
;;                     (fn [image-load]
;;                       (.log js/console "dimensions:" (.-width img) "x" (.-height img)))))))
;;     (.readAsDataUrl file-reader file)))
;; (defn first-file
;;   "Accepts input change events and gets the first selected file."
;;   (map (fn [e]
;;          (let [target (.-currentTarget e)
;;                file (-> target .-files (aget 0))]
;;            (set! (.-value target) "")
;;            file))))

(def window-width (r/atom nil))

(defn Canvas []
  (let [dom-node (r/atom nil) s (r/atom {:clicks 0 :track-pts nil
                                         :width 600 :height 400})]
    (r/create-class
     {:display-name "a-canvas-component"
      :component-did-update
      (fn [ this ]
        (when (or (= 0 (@s :clicks))
                  (@s :allow-update?))
          (draw-canvas-contents
           (.-firstChild @dom-node)
           (.getElementById js/document "thepicture"))))

      :component-did-mount
      (fn [ this ]
        (reset! dom-node (r/dom-node this))
        (swap-as s :offset (.getBoundingClientRect (.-firstChild @dom-node)))
        )

      :reagent-render
      (fn [ ]
        @window-width
        [:div.with-canvas
         [:canvas
          (if-let [node @dom-node]
                    {:width (@s :width)
                     :height (@s :height)
                     :id "canvas-element"
                     :style {:border "1px solid #000000"}
                     :ref #(swap-as s :canvas %)
                     :on-mouse-move
                     (fn [e]
                       (let [chrome false]
                         ; This tracks the mouse position on the canvas.
                         (when chrome
                           (.persist e)
                           (swap-as s
                                    :location
                                    {:x (.-layerX (.-nativeEvent e))
                                     :y (.-layerY (.-nativeEvent e))}))

                         (when (not chrome)
                           (swap-as s
                                    :location
                                    {:x (int (- e.clientX (.-x (@s :offset))))
                                     :y (int (- e.clientY (.-y (@s :offset))))})))

                       ; (swap-as s :debug {:client {:x e.clientX :y e.clientY}})

                       ; tracks waypoints and draws lines between them, ca. 10px long
                       (when (and (= 1 (mod (@s :clicks) 2))
                                  (> (sq-dist (@s :location) (first (@s :track-pts)))))

                         (swap-as s :track-pts ; newest pt is first
                                  (conj (@s :track-pts)
                                        (@s :location)))

                         (let [ctx (.getContext (@s :canvas) "2d")]
                           (.beginPath ctx)
                           (.moveTo ctx
                                    (:x (first ( @s :track-pts )))
                                    (:y (first ( @s :track-pts ))))
                           (.lineTo ctx
                                    (:x (second ( @s :track-pts )))
                                    (:y (second ( @s :track-pts ))))
                           (.stroke ctx))))

                     :on-click
                     (fn [e]
                       ; Count clicks and save click locations.
                       (swap-as s :clicks (+ 1 (@s :clicks)))
                       (swap-as s :click-location ; newest is first
                                (conj (@s :click-location)
                                      (@s :location)))

                       ; track points are ~10px apart and follow mouse
                       (if (empty? (@s :track-pts))
                         (swap-as s :track-pts ; newest is first
                                  (conj (@s :track-pts)
                                        (@s :location))))

                       ; debug
                       (js/console.log @s)

                         (let [ctx (.getContext (@s :canvas) "2d")]
                           ; Saves canvas state in order to undo
                           (when (odd? (@s :clicks))
                             (.save ctx)) ; TODO debug why this isn't working

                           ; Draws a line between last two clicks.
                           (when (even? (@s :clicks))
                             (.beginPath ctx)
                             (.moveTo ctx
                                      (:x (first ( @s :click-location )))
                                      (:y (first ( @s :click-location ))))
                             (.lineTo ctx
                                      (:x (second ( @s :click-location )))
                                      (:y (second ( @s :click-location ))))
                             (.stroke ctx)
                             (swap-as s :track-pts nil)))
                       )})]
          [:img {
                 :src "/pics/somepicture.png" ; TODO make dynamic
                 :style {:display "none"}
                 :id "thepicture"
                 }]
          [:input
           {
            :type "file" ; TODO put this file in the img tag above.
            :id "fileUpload"
            :accept "image/png, image/jpeg"
           ; :on-change load-image
            }
           ]
         [:button
          {
           :on-click
           (fn []
             (let [ctx (.getContext (@s :canvas) "2d")]
               (.restore ctx)))
           }
          "Undo"] ; TODO something something memory canvas vs DOM canvas
         [:button
          {
           :id "download-button"
           :on-click
           (fn []
             (let [ctx (.getContext (@s :canvas) "2d")
                   button (.getElementById "download-button")]
               (.setAttribute button "download" "edited-image.png") 
               (.setAttribute button "href" ; this makes the image
                              (.replace
                               (.toDataURl ctx "image/png")
                               "image/png"
                               "image/octet-stream"))
               (.click button)
               )) ; TODO figure out how to spit out the octet-stream
           }
          "Save Result"]
         [:div
 ;         [:p (str (@s :debug))] ; show some debug info
          [:input {:type "text"
                  :value (@s :width)
                   :on-change #(do ; this is a hack... semaphore
                                 (swap-as s :allow-update? true)
                                 (swap-as s :width (-> % .-target .-value))
                                 (swap-as s :allow-update? false))}]
          [:input {:type "text"
                   :value (@s :height)
                   :on-change #(do
                                 (swap-as s :allow-update? true)
                                 (swap-as s :height (-> % .-target .-value))
                                 (swap-as s :allow-update? false))}]]
         ])})))


  (defn on-window-resize [evt]
    (reset! window-width (.-innerWidth js/window)))

