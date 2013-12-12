(defproject dumbbell "0.1.0-SNAPSHOT"
  :description "Simple browser game"
  :license {:name "MIT License"
            :url "https://github.com/graue/luasynth/blob/master/MIT-LICENSE.txt"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-2080"]
                 [rm-hull/monet "0.1.9"]]
  :plugins [[lein-cljsbuild "1.0.0"]]
  :hooks [leiningen.cljsbuild]
  :cljsbuild {:builds
               {:main {:source-paths ["src-cljs"]
                       :compiler {:output-to "js/main.js"
                                  :optimizations :whitespace
                                  :pretty-print true}}
                :optimized {:source-paths ["src-cljs"]
                            :compiler {:output-to "js/optimized/main.js"
                                       :optimizations :advanced
                                       :pretty-print false}}}}
  :source-paths ["no-clj-here"])
