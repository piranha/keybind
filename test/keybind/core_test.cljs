(ns keybind.core-test
  (:require [cljs.test :refer-macros [deftest is testing]]
            [goog.object :as gobj]
            [doo.runner :refer-macros [doo-all-tests]]

            [keybind.core :as key]))


(deftest check-parse
  (is (= (key/parse "a")
        [{:code 65 :shift false :ctrl false :alt false :meta false}]))
  (is (= (key/parse "ctrl-a a")
        [{:code 65 :shift false :ctrl true  :alt false :meta false}
         {:code 65 :shift false :ctrl false :alt false :meta false}]))
  (is (= (key/parse "C-a C-x j")
        [{:code 65 :shift false :ctrl true  :alt false :meta false}
         {:code 88 :shift false :ctrl true  :alt false :meta false}
         {:code 74 :shift false :ctrl false :alt false :meta false}]))
  (is (= (key/parse "C-A a")
        [{:code 65 :shift true  :ctrl true  :alt false :meta false}
         {:code 65 :shift false :ctrl false :alt false :meta false}])))


(let [MODS {:ctrl "ctrlKey" :shift "shiftKey" :alt "altKey" :meta "metaKey"}]
  (defn fire [key]
    (let [e (js/document.createEvent "Event")]
      (.initEvent e "keydown" true true)

      (aset e "keyCode" (:code key))
      (doseq [[name attr] MODS]
        (aset e attr (get key name false)))

      (js/document.dispatchEvent e))))


(deftest simple-shortcut
  (let [count (atom 0)]
    (key/bind! "a" ::a #(swap! count inc))

    (testing "fired correctly"
      (fire {:code 65})
      (is (= @count 1)))

    (testing "fired correctly again"
      (fire {:code 65})
      (is (= @count 2)))

    (testing "fired with a mod"
      (fire {:code 65 :shift true})
      (is (= @count 2)))

    (key/unbind! "a" ::a)))

(deftest shortcut-with-mod
  (let [count (atom 0)]
    (key/bind! "ctrl-alt-a" ::a #(swap! count inc))

    (testing "fired correctly"
      (fire {:code 65 :ctrl true :alt true})
      (is (= @count 1)))

    (testing "fired with wrong mod"
      (fire {:code 65 :ctrl true})
      (is (= @count 1)))

    (testing "fired with wrong key code"
      (fire {:code 66 :ctrl true :alt true})
      (is (= @count 1)))

    (key/unbind! "a" ::a)))


(deftest shortcut-with-minus
  (let [count (atom 0)]
    (key/bind! "-" ::minus #(swap! count inc))

    (testing "fired correctly"
      (fire {:code 189})
      (is (= @count 1)))

    (key/unbind! "-" ::minus)))


(deftest shortcut-sequence
  (let [count (atom 0)]
    (key/bind! "ctrl-a a" ::a #(swap! count inc))

    (testing "correct sequence"
      (fire {:code 65 :ctrl true})
      (fire {:code 65})
      (is (= @count 1)))

    (testing "wrong suffix"
      (fire {:code 65 :ctrl true})
      (fire {:code 66})
      (is (= @count 1)))

    (testing "wrong sequence"
      (fire {:code 65 :ctrl true})
      (fire {:code 65 :ctrl true})
      (fire {:code 65})
      (is (= @count 1)))

    (testing "but it still works"
      (fire {:code 65 :ctrl true})
      (fire {:code 65})
      (is (= @count 2)))

    (key/unbind! "a" ::a)))


(deftest emacs-style
  (let [count (atom 0)]
    (key/bind! "C-a C-x j" ::chord #(swap! count inc))

    (testing "emacs-style is working"
      (fire {:code 65 :ctrl true})
      (fire {:code 88 :ctrl true})
      (fire {:code 74})
      (is (= @count 1)))

    (key/bind! "C-A a" ::shift #(swap! count inc))
    (testing "shift is working"
      (fire {:code 65 :shift true :ctrl true})
      (fire {:code 65})
      (is (= @count 2)))

    (key/unbind! "C-a C-x j" ::chord)))


(set! doo.runner.exit!
  (fn [success?]
    (try
      (if-let [nodejs-exit (and (exists? js/process) (gobj/get js/process "exit"))]
        (nodejs-exit (if success? 0 1))
        (doo.runner/*exit-fn* success?))
      (catch :default e
        (println "WARNING: doo's exit function was not properly set")
        (println e)))))


(doo-all-tests #"keybind.*-test")
