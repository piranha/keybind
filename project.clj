(defproject keybind "1.0.0"
  :description "ClojureScript key bindings (shortcut) library"
  :url "https://github.com/piranha/keybind"
  :scm {:name "git" :url "https://github.com/piranha/keybind"}
  :license {:name "ISC License"
            :url "http://www.isc.org/downloads/software-support-policy/isc-license/"}
  :plugins [[lein-cljsbuild "1.1.0"]
            [lein-doo "0.1.6-SNAPSHOT"]]

  :profiles
  {:dev {:dependencies [[org.clojure/clojurescript "1.7.145" :scope "test"]]}}

  :cljsbuild
  {:builds [{:id "test"
             :source-paths ["src" "test"]
             :compiler {:output-to "target/test.js"
                        :output-dir "target/test.out"
                        :main 'keybind-test
                        :optimizations :none}}]}

  :doo {:build "test"})
