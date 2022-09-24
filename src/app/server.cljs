(ns app.server
  (:require ["https://deno.land/std@0.157.0/http/server.ts" :refer [serve]]
            ["https://crux.land/router@0.0.5" :refer [router]]))

;; Utils
(defn j->c [o] (js->clj o :keywordize-keys true))
(defn c->j [o] (clj->js o))

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

(defn ->handler
  [handler req params]
  (handler req (j->c params)))

;; Handler

(defn routes->router-js
  [routes]
  (->> routes
       (map (fn [[method path handler]]
              {(str (name method) "@" path) #(-> (->handler handler %1 %2)
                                                 response)}))
       (reduce merge)
       (c->j)))

(defn serve-routes
  [routes]
  (serve (router (routes->router-js routes)) #js {:port 1338}))