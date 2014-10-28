(defproject keybind "1.0.0"
  :description "ClojureScript key bindings (shortcut) library"
  :url "https://github.com/piranha/keybind"
  :scm {:name "git" :url "https://github.com/piranha/keybind"}
  :license {:name "ISC License"
            :url "http://www.isc.org/downloads/software-support-policy/isc-license/"}
  :plugins [[lein-cljsbuild "1.0.3"]
            [com.cemerick/clojurescript.test "0.3.1"]]

  :profiles
  {:dev {:dependencies [[org.clojure/clojure "1.6.0"]
                        [org.clojure/clojurescript "0.0-2371"]]}}

  :cljsbuild
  {:builds [{:id "main"
             :source-paths ["src"]
             :jar true
             :compiler {:output-to "target/keybind.js"
                        :output-dir "target/out-main"
                        :optimizations :whitespace}}
            {:id "test"
             :source-paths ["src" "test"]
             :notify-command ["phantomjs" :cljs.test/runner "target/testable.js"]
             :compiler {:output-to "target/testable.js"
                        :output-dir "target/out-test"
                        :optimizations :whitespace
                        :pretty-print true}}]})
