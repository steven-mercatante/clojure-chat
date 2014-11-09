(ns chat.core
  (require [compojure.core :refer [routes GET]]
           [org.httpkit.server :as http]
           [net.cgrand.enlive-html :refer [deftemplate] :as enlive]
           [ring.middleware.params :refer [wrap-params]]))

(enlive/deftemplate root "templates/index.html"
  [request]
  [:#name] (enlive/set-attr :value (get-in request [:params "name"])))

(defn broadcast
  [channels message]
  (doseq [channel (deref channels)]
    (http/send! channel message)))

(defn new-client
  [channels channel name]
  (swap! channels conj channel)
  (doto channel
    (http/on-close (fn [_]
                     (swap! channels disj channel)
                     (broadcast channels (str name " has left"))))
    (http/on-receive (fn [msg]
                       (broadcast channels (str name ": " msg)))))
  (broadcast channels (str name " joined")))

(defn websocket
  [channels]
  (fn [request]
    (let [name (get-in request [:params "name"])]
      (http/with-channel request channel
        (if (http/websocket? channel)
          (new-client channels channel name)
          (http/send! channel {:status 426
                               :headers {"Content-Type" "text/plain"}
                               :body "Youo need Websockets"}))))))

(def app
  (let [channels (atom #{})]
    (wrap-params
     (routes
      (GET "/" [] root)
      (GET "/ws" [] (websocket channels))))))
