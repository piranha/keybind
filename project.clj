(defproject keybind "2.2.0"
  :description "ClojureScript key bindings (shortcut) library"
  :url "https://github.com/piranha/keybind"
  :scm {:name "git" :url "https://github.com/piranha/keybind"}
  :license {:name "ISC License"
            :url "http://www.isc.org/downloads/software-support-policy/isc-license/"}
  :plugins [[lein-cljsbuild "1.1.6"]
            [lein-doo "0.1.8"]]

  :profiles
  {:dev {:dependencies [[org.clojure/clojure "1.8.0"]
                        [org.clojure/clojurescript "1.9.946" :scope "test"]]}}

  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler {:output-to "target/test.js"
                        :output-dir "target/test.out"
                        :main "keybind.core-test"
                        :optimizations :none}}]})
