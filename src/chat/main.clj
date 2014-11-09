(ns chat.main
  (:require [chat.core :refer [app]]
            [org.httpkit.server :as http])
  (gen-class))

(defn -main []
  (http/run-server app {:port 8080})
  (println "The server is running..."))
