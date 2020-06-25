(defproject innovonto-kaleidoscope-reframe "0.1.0-SNAPSHOT"
  :description "Kaleidoscope is a tool to interactively explore idea spaces."
  :url "https://github.com/FUB-HCC/Innovonto-Kaleidoscope"
  :license {:name "Eclipse Public License"
            :url  "http://www.eclipse.org/legal/epl-v10.html"}

  :min-lein-version "2.7.1"

  :dependencies [[org.clojure/clojure "1.10.1"]
                 ;; COMMON
                 [metosin/reitit "0.5.2"]
                 ;;SERVER
                 [metosin/reitit-ring "0.5.2"]
                 [yesparql "0.3.3"]
                 [http-kit "2.3.0"]
                 [ring/ring-core "1.8.1"]
                 [ring-cors "0.1.13"]
                 [ring/ring-json "0.5.0"]
                 [cprop "0.1.17"]
                 ;;CLIENT
                 [org.clojure/clojurescript "1.10.773"]
                 [reagent "0.10.0"]
                 [re-frame "0.12.0"]
                 [re-com "2.8.0"]
                 [day8.re-frame/http-fx "v0.2.0"]]

  :source-paths ["src/clj" "src/cljs"]
  :resource-paths ["resources"]
  :test-paths ["test/clj", "test/cljs"]
  :target-path "target/%s/"

  :clean-targets ^{:protect false} ["target/%s/" "resources/public/cljs-out"]
  :main ^:skip-aot hcc.innovonto.kaleidoscope.server

  :aliases {"fig"       ["trampoline" "run" "-m" "figwheel.main"]
            "fig:build" ["trampoline" "run" "-m" "figwheel.main" "-b" "dev" "-r"]
            "fig:min"   ["run" "-m" "figwheel.main" "-O" "advanced" "-bo" "dev"]
            "fig:test"  ["run" "-m" "figwheel.main" "-co" "test.cljs.edn" "-m" hcc.innovonto.kaleidoscope.test-runner]}

  :profiles {
             :uberjar {:omit-source  true
                       :aot          :all
                       :dependencies [[com.bhauman/figwheel-main "0.2.8"]]
                       :prep-tasks   ["compile" ["fig:min"]]
                       :uberjar-name "kaleidoscope.jar"
                       }
             :dev     {:jvm-opts     ["-Dconf=dev-config.edn"]
                       ;;TODO env
                       :dependencies [[com.bhauman/figwheel-main "0.2.8"]
                                      [com.bhauman/rebel-readline-cljs "0.1.4"]]
                       }})

