(defproject twitteruption "0.1.0-SNAPSHOT"

  :description "FIXME: write description"
  :url "http://example.com/FIXME"

  :dependencies [[bouncer "1.0.0"]
                 [ch.qos.logback/logback-classic "1.1.7"]
                 [cljs-ajax "0.5.8"]
                 [compojure "1.5.2"]
                 [cprop "0.1.10"]
                 [luminus-aleph "0.1.5"]
                 [luminus-nrepl "0.1.4"]
                 [luminus/ring-ttl-session "0.3.1"]
                 [markdown-clj "0.9.94"]
                 [metosin/ring-http-response "0.8.1"]
                 [mount "0.1.11"]
                 [org.clojure/clojure "1.8.0"]
                 [org.clojure/core.cache "0.6.5"]
                 [org.clojure/clojurescript "1.9.473" :scope "provided"]
                 [org.clojure/tools.cli "0.3.5"]
                 [org.clojure/tools.logging "0.3.1"]
                 [org.webjars.bower/tether "1.4.0"]
                 [org.webjars/bootstrap "4.0.0-alpha.5"]
                 [org.webjars/font-awesome "4.7.0"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [re-frame "0.9.1"]
                 [reagent "0.6.0"]
                 [reagent-utils "0.2.0"]
                 [ring-middleware-format "0.7.2"]
                 [ring-webjars "0.1.1"]
                 [ring/ring-core "1.5.1"]
                 [ring/ring-defaults "0.2.3"]
                 [secretary "1.2.3"]
                 [selmer "1.10.6"]
                 [environ "1.1.0"]
                 [clj-oauth "1.5.5"]
                 [camel-snake-kebab "0.4.0"]
                 [aleph "0.4.2-alpha6"]
                 [byte-streams "0.2.2"]
                 [ring/ring-json "0.4.0"]
                 [cheshire "5.7.0"]
                 [manifold "0.1.6"]
                 [com.cognitect/transit-cljs "0.8.239"]]

  :min-lein-version "2.0.0"

  :jvm-opts ["-server" "-Dconf=.lein-env"]
  :source-paths ["src/clj" "src/cljc"]
  :resource-paths ["resources" "target/cljsbuild"]
  :target-path "target/%s/"
  :main twitteruption.core

  :plugins [[lein-cprop "1.0.1"]
            [lein-cljsbuild "1.1.4"]]
  :clean-targets ^{:protect false}
  [:target-path [:cljsbuild :builds :app :compiler :output-dir] [:cljsbuild :builds :app :compiler :output-to]]
  :figwheel
  {:http-server-root "public"
   :nrepl-port 7002
   :css-dirs ["resources/public/css"]
   :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
  

  :profiles
  {:uberjar {:omit-source true
             :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
             :cljsbuild
             {:builds
              {:min
               {:source-paths ["src/cljc" "src/cljs" "env/prod/cljs"]
                :compiler
                {:output-to "target/cljsbuild/public/js/app.js"
                 :optimizations :advanced
                 :pretty-print false
                 :closure-warnings
                 {:externs-validation :off :non-standard-jsdoc :off}
                 :externs ["react/externs/react.js"]}}}}
             
             
             :aot :all
             :uberjar-name "twitteruption.jar"
             :source-paths ["env/prod/clj"]
             :resource-paths ["env/prod/resources"]}

   :dev           [:project/dev :profiles/dev]
   :test          [:project/dev :project/test :profiles/test]

   :project/dev  {:dependencies [[prone "1.1.4"]
                                 [ring/ring-mock "0.3.0"]
                                 [ring/ring-devel "1.5.1"]
                                 [pjstadig/humane-test-output "0.8.1"]
                                 [binaryage/devtools "0.9.0"]
                                 [com.cemerick/piggieback "0.2.2-SNAPSHOT"]
                                 [doo "0.1.7"]
                                 [figwheel-sidecar "0.5.9"]]
                  :plugins      [[com.jakemccrary/lein-test-refresh "0.18.1"]
                                 [lein-doo "0.1.7"]
                                 [lein-figwheel "0.5.9"]
                                 [org.clojure/clojurescript "1.9.473"]]
                  :cljsbuild
                  {:builds
                   {:app
                    {:source-paths ["src/cljs" "src/cljc" "env/dev/cljs"]
                     :compiler
                     {:main "twitteruption.app"
                      :asset-path "/js/out"
                      :output-to "target/cljsbuild/public/js/app.js"
                      :output-dir "target/cljsbuild/public/js/out"
                      :source-map true
                      :optimizations :none
                      :pretty-print true}}
                    :test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "twitteruption.doo-runner"}}}}
                  
                  
                  
                  :doo {:build "test"}
                  :source-paths ["env/dev/clj" "test/clj"]
                  :resource-paths ["env/dev/resources"]
                  :repl-options {:init-ns user}
                  :injections [(require 'pjstadig.humane-test-output)
                               (pjstadig.humane-test-output/activate!)]}
   :project/test {:resource-paths ["env/test/resources"]
                  :cljsbuild
                  {:builds
                   {:test
                    {:source-paths ["src/cljc" "src/cljs" "test/cljs"]
                     :compiler
                     {:output-to "target/test.js"
                      :main "twitteruption.doo-runner"
                      :optimizations :whitespace
                      :pretty-print true}}}}
                  
                  }
   :profiles/dev {}
   :profiles/test {}})
