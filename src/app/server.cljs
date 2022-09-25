(ns app.server
  (:require ["https://deno.land/std@0.157.0/http/server.ts" :refer [serve]]
            ["https://crux.land/router@0.0.5" :refer [router]]
            [app.util :as u]))

(def default-response-opts
  {:status 200
   :headers {:content-type "application/json"}})

(defn response
  ([payload]
   (response payload default-response-opts))
  ([payload opts]
   (new js/Response (u/clj->json payload) (u/c->j (u/deep-merge default-response-opts opts)))))

(defn ->handler
  [handler req params]
  (handler req (u/j->c params)))

;; Handler

(defn routes->router-js
  [routes]
  (->> routes
       (map (fn [[method path handler]]
              {(str (name method) "@" path) #(-> (->handler handler %1 %2)
                                                 response)}))
       (reduce merge)
       (u/c->j)))

(defn serve-routes
  [routes]
  (serve (router (routes->router-js routes)) #js {:port 1338}))