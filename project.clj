(defproject innovonto-kaleidoscope-reframe "0.1.0-SNAPSHOT"
  :description "Kaleidoscope is a tool to explore idea spaces"
  :url "https://github.com/FUB-HCC/Innovonto-Kaleidoscope"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  ;;https://github.com/bhauman/lein-figwheel/issues/612
  :dependencies [[org.clojure/clojure "1.10.0"]
                 ;;SERVER
                 [bidi "2.1.6"]
                 [yesparql "0.3.2"]
                 [http-kit "2.3.0"]
                 ;;CLIENT
                 [org.clojure/clojurescript "1.10.520"]
                 [reagent "0.8.1"]
                 [re-frame "0.10.6"]
                 [re-com "2.5.0"]
                 [day8.re-frame/http-fx "0.1.6"]]

  :source-paths ["src/clj", "src/cljs"]
  :test-paths ["test/clj", "test/cljs"]
  :resource-paths ["resources"]
  :target-path "target/%s/"
  :main ^:skip-aot hcc.innovonto.kaleidoscope.server


  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" hcc.innovonto.kaleidoscope.test-runner]}

  :profiles {:dev {:dependencies [[com.bhauman/figwheel-main "0.1.9"]
                                  [com.bhauman/rebel-readline-cljs "0.1.4"]]
                   }})

