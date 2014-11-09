(defproject chat "0.1.0-SNAPSHOT"
  :main chat.main
  :profiles {:uberjar {:aot :all}}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [compojure "1.1.6"]
                 [enlive "1.1.5"]
                 [http-kit "2.1.16"]])
