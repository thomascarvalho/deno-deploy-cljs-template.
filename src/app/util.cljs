(ns app.util)
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