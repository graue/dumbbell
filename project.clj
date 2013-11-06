(defproject dumbbell "0.1.0-SNAPSHOT"
  :description "Simple browser game"
  :license {:name "MIT License"
            :url "https://github.com/graue/luasynth/blob/master/MIT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1978"]
                 [rm-hull/monet "0.1.8"]]
  :plugins [[lein-cljsbuild "0.3.3"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds
               {:main {:source-paths ["src-cljs"]
                       :compiler {:output-to "js/main.js"
                                  :optimizations :advanced
                                  :pretty-print false}}}}
  :source-paths ["no-clj-here"])
