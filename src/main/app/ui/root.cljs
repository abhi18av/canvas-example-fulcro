(ns app.ui.root
  (:require
    [goog.object :as gobj]
    [app.model.session :as session]
    [com.fulcrologic.fulcro.dom :as dom :refer [div ul li p h3 button]]
    [com.fulcrologic.fulcro.dom.html-entities :as ent]
    [com.fulcrologic.fulcro.dom.events :as evt]
    [com.fulcrologic.fulcro.components :as prim :refer [defsc]]
    [com.fulcrologic.fulcro.routing.dynamic-routing :as dr]
    [com.fulcrologic.fulcro.ui-state-machines :as uism :refer [defstatemachine]]
    [com.fulcrologic.fulcro.components :as comp]
    [com.fulcrologic.fulcro.mutations :as m :refer [defmutation]]
    [taoensso.timbre :as log]
    [com.fulcrologic.fulcro-css.css :as css]
    [com.fulcrologic.fulcro.algorithms.form-state :as fs]
    [clojure.string :as str]))

;; ;;;;;;;;;;;;;;;;
;; ;; Utils
;; ;;;;;;;;;;;;;;;;

;; (defn field [{:keys [label valid? error-message] :as props}]
;;   (let [input-props (-> props (assoc :name label) (dissoc :label :valid? :error-message))]
;;     (div :.ui.field
;;       (dom/label {:htmlFor label} label)
;;       (dom/input input-props)
;;       (dom/div :.ui.error.message {:classes [(when valid? "hidden")]}
;;         error-message))))

;; ;;;;;;;;;;;;;;;;
;; ;; SignUp
;; ;;;;;;;;;;;;;;;;

;; (defsc SignupSuccess [this props]
;;   {:query         ['*]
;;    :initial-state {}
;;    :ident         (fn [] [:component/id :signup-success])
;;    :route-segment ["signup-success"]
;;    :will-enter    (fn [app _] (dr/route-immediate [:component/id :signup-success]))}
;;   (div
;;     (dom/h3 "Signup Complete!")
;;     (dom/p "You can now log in!")))

;; (defsc Signup [this {:account/keys [email password password-again] :as props}]
;;   {:query             [:account/email :account/password :account/password-again fs/form-config-join]
;;    :initial-state     (fn [_]
;;                         (fs/add-form-config Signup
;;                           {:account/email          ""
;;                            :account/password       ""
;;                            :account/password-again ""}))
;;    :form-fields       #{:account/email :account/password :account/password-again}
;;    :ident             (fn [] session/signup-ident)
;;    :route-segment     ["signup"]
;;    :componentDidMount (fn [this]
;;                         (comp/transact! this [(session/clear-signup-form)]))
;;    :will-enter        (fn [app _] (dr/route-immediate [:component/id :signup]))}
;;   (let [submit!  (fn [evt]
;;                    (when (or (identical? true evt) (evt/enter-key? evt))
;;                      (comp/transact! this [(session/signup! {:email email :password password})])
;;                      (log/info "Sign up")))
;;         checked? (log/spy :info (fs/checked? props))]
;;     (div
;;       (dom/h3 "Signup")
;;       (div :.ui.form {:classes [(when checked? "error")]}
;;         (field {:label         "Email"
;;                 :value         (or email "")
;;                 :valid?        (session/valid-email? email)
;;                 :error-message "Must be an email address"
;;                 :autoComplete  "off"
;;                 :onKeyDown     submit!
;;                 :onChange      #(m/set-string! this :account/email :event %)})
;;         (field {:label         "Password"
;;                 :type          "password"
;;                 :value         (or password "")
;;                 :valid?        (session/valid-password? password)
;;                 :error-message "Password must be at least 8 characters."
;;                 :onKeyDown     submit!
;;                 :autoComplete  "off"
;;                 :onChange      #(m/set-string! this :account/password :event %)})
;;         (field {:label         "Repeat Password" :type "password" :value (or password-again "")
;;                 :autoComplete  "off"
;;                 :valid?        (= password password-again)
;;                 :error-message "Passwords do not match."
;;                 :onChange      #(m/set-string! this :account/password-again :event %)})
;;         (dom/button :.ui.primary.button {:onClick #(submit! true)}
;;           "Sign Up")))))

;; (declare Session)

;; ;;;;;;;;;;;;;;;;
;; ;; LogIn
;; ;;;;;;;;;;;;;;;;

;; (defsc Login [this {:account/keys [email]
;;                     :ui/keys      [error open?] :as props}]
;;   {:query         [:ui/open? :ui/error :account/email
;;                    {[:component/id :session] (comp/get-query Session)}
;;                    [::uism/asm-id ::session/session]]
;;    :css           [[:.floating-menu {:position "absolute !important"
;;                                      :z-index  1000
;;                                      :width    "300px"
;;                                      :right    "0px"
;;                                      :top      "50px"}]]
;;    :initial-state {:account/email "" :ui/error ""}
;;    :ident         (fn [] [:component/id :login])}
;;   (let [current-state (uism/get-active-state this ::session/session)
;;         {current-user :account/name} (get props [:component/id :session])
;;         initial?      (= :initial current-state)
;;         loading?      (= :state/checking-session current-state)
;;         logged-in?    (= :state/logged-in current-state)
;;         {:keys [floating-menu]} (css/get-classnames Login)
;;         password      (or (comp/get-state this :password) "")] ; c.l. state for security
;;     (dom/div
;;       (when-not initial?
;;         (dom/div :.right.menu
;;           (if logged-in?
;;             (dom/button :.item
;;               {:onClick #(uism/trigger! this ::session/session :event/logout)}
;;               (dom/span current-user) ent/nbsp "Log out")
;;             (dom/div :.item {:style   {:position "relative"}
;;                              :onClick #(uism/trigger! this ::session/session :event/toggle-modal)}
;;               "Login"
;;               (when open?
;;                 (dom/div :.four.wide.ui.raised.teal.segment {:onClick (fn [e]
;;                                                                         ;; Stop bubbling (would trigger the menu toggle)
;;                                                                         (evt/stop-propagation! e))
;;                                                              :classes [floating-menu]}
;;                   (dom/h3 :.ui.header "Login")
;;                   (div :.ui.form {:classes [(when (seq error) "error")]}
;;                     (field {:label    "Email"
;;                             :value    email
;;                             :onChange #(m/set-string! this :account/email :event %)})
;;                     (field {:label    "Password"
;;                             :type     "password"
;;                             :value    password
;;                             :onChange #(comp/set-state! this {:password (evt/target-value %)})})
;;                     (div :.ui.error.message error)
;;                     (div :.ui.field
;;                       (dom/button :.ui.button
;;                         {:onClick (fn [] (uism/trigger! this ::session/session :event/login {:username email
;;                                                                                              :password password}))
;;                          :classes [(when loading? "loading")]} "Login"))
;;                     (div :.ui.message
;;                       (dom/p "Don't have an account?")
;;                       (dom/a {:onClick (fn []
;;                                          (uism/trigger! this ::session/session :event/toggle-modal {})
;;                                          (dr/change-route this ["signup"]))}
;;                         "Please sign up!"))))))))))))

;; (def ui-login (comp/factory Login))

;; ;;;;;;;;;;;;;;;;
;; ;; Main
;; ;;;;;;;;;;;;;;;;


;; (defsc Main [this props]
;;   {:query         [:main/welcome-message]
;;    :initial-state {:main/welcome-message "Hi!"}
;;    :ident         (fn [] [:component/id :main])
;;    :route-segment ["main"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :main]))}
;;   (div :.ui.container.segment
;;     (h3 "Main")))




;; ;;;;;;;;;;;;;;;;
;; ;; Settings
;; ;;;;;;;;;;;;;;;;

;; (defsc Settings [this {:keys [:account/time-zone :account/real-name] :as props}]
;;   {:query         [:account/time-zone :account/real-name]
;;    :ident         (fn [] [:component/id :settings])
;;    :route-segment ["settings"]
;;    :will-enter    (fn [_ _] (dr/route-immediate [:component/id :settings]))
;;    :initial-state {}}
;;   (div :.ui.container.segment
;;     (h3 "Settings")))

;; (dr/defrouter TopRouter [this props]
;;   {:router-targets [Main Signup SignupSuccess Settings]})

;; (def ui-top-router (comp/factory TopRouter))

;; ;;;;;;;;;;;;;;;;
;; ;; Session
;; ;;;;;;;;;;;;;;;;

;; (defsc Session
;;   "Session representation. Used primarily for server queries. On-screen representation happens in Login component."
;;   [this {:keys [:session/valid? :account/name] :as props}]
;;   {:query         [:session/valid? :account/name]
;;    :ident         (fn [] [:component/id :session])
;;    :pre-merge     (fn [{:keys [data-tree]}]
;;                     (merge {:session/valid? false :account/name ""}
;;                       data-tree))
;;    :initial-state {:session/valid? false :account/name ""}})

;; (def ui-session (prim/factory Session))

;; ;;;;;;;;;;;;;;;;
;; ;; TopChrome
;; ;;;;;;;;;;;;;;;;

;; (defsc TopChrome [this {:root/keys [router current-session login]}]
;;   {:query         [{:root/router (comp/get-query TopRouter)}
;;                    {:root/current-session (comp/get-query Session)}
;;                    [::uism/asm-id ::TopRouter]
;;                    {:root/login (comp/get-query Login)}]
;;    :ident         (fn [] [:component/id :top-chrome])
;;    :initial-state {:root/router          {}
;;                    :root/login           {}
;;                    :root/current-session {}}}
;;   (let [current-tab (some-> (dr/current-route this this) first keyword)]
;;     (div :.ui.container
;;       (div :.ui.secondary.pointing.menu
;;         (dom/a :.item {:classes [(when (= :main current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["main"]))} "Main")
;;         (dom/a :.item {:classes [(when (= :settings current-tab) "active")]
;;                        :onClick (fn [] (dr/change-route this ["settings"]))} "Settings")
;;         (div :.right.menu
;;           (ui-login login)))
;;       (div :.ui.grid
;;         (div :.ui.row
;;           (ui-top-router router))))))

;; (def ui-top-chrome (comp/factory TopChrome))

;; ;;;;;;;;;;;;;;;;
;; ;; Bump Number
;; ;;;;;;;;;;;;;;;;

;; (defmutation bump-number [ignored]
;;   (action [{:keys [state]}]
;;           (swap! state update :root/number inc)))

;; (defsc BumpNumber [this {:root/keys [number]}]
;;        {:query         [:root/number]
;;         :initial-state {:root/number 0}}
;;        (dom/div
;;          (dom/h4 "This is an example.")
;;          (dom/button {:onClick #(comp/transact! this `[(bump-number {})])}
;;                      "You've clicked this button " number " times.")))

;; (def ui-bump-number (comp/factory BumpNumber))

;; ;;;;;;;;;;;;;;;;
;; ;; ROOT
;; ;;;;;;;;;;;;;;;;


;; (defsc Root [this {:root/keys [top-chrome]}]
;;   {:query             [{:root/top-chrome (comp/get-query TopChrome)}]
;;    :ident             (fn [] [:component/id :ROOT])
;;    :initial-state     {:root/top-chrome {}}}
;;   (div
;;     (ui-top-chrome top-chrome)
;;     (ui-bump-number {})))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;
;; VANILLA EXAMPLES
;;;;;;;;;;;;;;;;


;;;;;;;;;;;;;;;;
;; CANVAS EXAMPLE
;;;;;;;;;;;;;;;;


(defn change-size*
      "Change the size of the canvas by some (pos or neg) amount.."
      [state-map amount]
      (let [current-size (get-in state-map [:child/by-id 0 :size])
            new-size     (+ amount current-size)]
           (assoc-in state-map [:child/by-id 0 :size] new-size)))

; Make the canvas smaller. This will cause
(defmutation ^:intern make-smaller [p]
             (action [{:keys [state]}]
                     (swap! state change-size* -20)))

(defmutation ^:intern make-bigger [p]
             (action [{:keys [state]}]
                     (swap! state change-size* 20)))

(defmutation ^:intern update-marker [{:keys [coords]}]
             (action [{:keys [state]}]
                     (swap! state assoc-in [:child/by-id 0 :marker] coords)))


(defn event->dom-coords
      "Translate a javascript evt to a clj [x y] within the given dom element."
      [evt dom-ele]
      (let [cx (.-clientX evt)
            cy (.-clientY evt)
            BB (.getBoundingClientRect dom-ele)
            x  (- cx (.-left BB))
            y  (- cy (.-top BB))]
           [x y]))

(defn event->normalized-coords
      "Translate a javascript evt to a clj [x y] within the given dom element as normalized (0 to 1) coordinates."
      [evt dom-ele]
      (let [cx (.-clientX evt)
            cy (.-clientY evt)
            BB (.getBoundingClientRect dom-ele)
            w  (- (.-right BB) (.-left BB))
            h  (- (.-bottom BB) (.-top BB))
            x  (/ (- cx (.-left BB))
                  w)
            y  (/ (- cy (.-top BB))
                  h)]
           [x y]))

(defn render-hover-and-marker
      "Render the graphics in the canvas. Pass the component props and state. "
      [canvas props coords]
      (let [marker             (:marker props)
            size               (:size props)
            real-marker-coords (mapv (partial * size) marker)
            ; See HTML5 canvas docs
            ctx                (.getContext canvas "2d")
            clear              (fn []
                                   (set! (.-fillStyle ctx) "white")
                                   (.fillRect ctx 0 0 size size))
            drawHover          (fn []
                                   (set! (.-strokeStyle ctx) "gray")
                                   (.strokeRect ctx (- (first coords) 5) (- (second coords) 5) 10 10))
            drawMarker         (fn []
                                   (set! (.-strokeStyle ctx) "red")
                                   (.strokeRect ctx (- (first real-marker-coords) 5) (- (second real-marker-coords) 5) 10 10))]
           (.save ctx)
           (clear)
           (drawHover)
           (drawMarker)
           (.restore ctx)))

(defn place-marker
      "Update the marker in app state. Derives normalized coordinates, and updates the marker in application state."
      [child evt]
      (let [canvas (gobj/get child "canvas")]
           (comp/transact! child `[(update-marker
                                     {:coords ~(event->normalized-coords evt canvas)})])))

(defn hover-marker
      "Updates the hover location of a proposed marker using canvas coordinates. Hover location
       is stored in component local state (meaning that a low-level app database query will not
       run to do the render that responds to this change)"
      [child evt]
      (let [canvas         (gobj/get child "canvas")
            updated-coords (event->dom-coords evt canvas)]
           (comp/set-state! child {:coords updated-coords})
           (render-hover-and-marker canvas (comp/props child) updated-coords)))

(defsc Child [this {:keys [id size] :as props}]
       {:query          [:id :size :marker]
        :initial-state  (fn [_] {:id 0 :size 50 :marker [0.5 0.5]})
        :ident          (fn [] [:child/by-id id])
        :initLocalState (fn [this _] {:coords [-50 -50]})}
       ; Remember that this "render" just renders the DOM (e.g. the canvas DOM element). The graphical
       ; rendering within the canvas is done during event handling.
       ; size comes from props. Transactions on size will cause the canvas to resize in the DOM
       (when-let [canvas (gobj/get this "canvas")]
                 (render-hover-and-marker canvas props (comp/get-state this :coords)))
       (dom/canvas {:width       (str size "px")
                    :height      (str size "px")
                    :onMouseDown (fn [evt] (place-marker this evt))
                    :onMouseMove (fn [evt] (hover-marker this evt))
                    ; This is a pure React mechanism for getting the underlying DOM element.
                    ; Note: when the DOM element changes this fn gets called with nil
                    ; (to help you manage memory leaks), then the new element
                    :ref         (fn [r]
                                     (when r
                                           (gobj/set this "canvas" r)
                                           (render-hover-and-marker r props (comp/get-state this :coords))))
                    :style       {:border "1px solid black"}}))

(def ui-child (comp/factory Child))

(defsc Root [this {:keys [child]}]
       {:query         [{:child (comp/get-query Child)}]
        :initial-state (fn [params] {:ui/react-key "K" :child (comp/get-initial-state Child nil)})}
       (dom/div
         (dom/button {:onClick #(comp/transact! this `[(make-bigger {})])} "Bigger!")
         (dom/button {:onClick #(comp/transact! this `[(make-smaller {})])} "Smaller!")
         (dom/br)
         (dom/br)
         (ui-child child)))


