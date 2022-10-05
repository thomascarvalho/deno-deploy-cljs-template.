(ns app.api
  (:require [app.server :as server]))

;; Routes

(defn index
  [req] 
  {:message "/Index"})

(defn hello
  [req {:keys [name]}]
  {:msg (str "/Hello " name)})

(def routes [[:GET "/" index]
             [:GET "/hello/:name" hello]])

(defn init []
  (server/serve-routes routes))
