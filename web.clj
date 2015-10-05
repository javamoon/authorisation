(use '[ring.adapter.jetty :only (run-jetty)])
(defn app
  [{:keys [uri]}]
  {:body (format "You  requested %s" uri)})
(def server (run-jetty #'app {:port 8080 :join? false}))
#_(defn app
  [{:keys [uri query-string]}]
  {:body (format "You requested %s with query %s" uri query-string)})
