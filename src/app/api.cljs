(ns app.api)

;; Utils
(defn j->c [o] (js->clj o :keywordize-keys true))
(defn c->j [o] (clj->js o))

(defn json->clj
  [s]
  (-> s js/JSON.parse j->c))

(defn clj->json
  [o]
  (-> o c->j js/JSON.stringify))

(defn deep-merge
  "Recursively merges maps. Similar to core's `merge` function."
  [& maps]
  (letfn [(m [& xs]
            (if (some #(and (map? %)
                            (not (record? %))) xs)
              (apply merge-with m xs)
              (last xs)))]
    (reduce m maps)))

(def default-response-opts
  {:status 200
   :headers {:content-type "application/json"}})

(defn response
  ([payload]
   (response payload default-response-opts))
  ([payload opts]
   (new js/Response (clj->json payload) (c->j (deep-merge default-response-opts opts)))))


;; Routes

(defn index
  [req]
  (response
   "/Index"))

(defn hello
  [req {:keys [name]}]
  (response
   {:msg (str "/Hello " name)}))

(def routes [[:GET "/" index]
             [:GET "/hello/:name" hello]])

(defn ->handler
  [handler req params]
  (handler req (j->c params)))

;; Handler

(defn routes->router-js
  [routes]
  (->> routes
       (map (fn [[method path handler]]
              {(str (name method) "@" path) #(->handler handler %1 %2)}))
       (reduce merge)
       (c->j)))

(def entries (routes->router-js routes))

;; (def handler (router (routes->router-js routes)))

;; (defn init []
;;   (serve handler #js {:port 1338}))
