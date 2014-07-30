(ns keybind-test
  (:require-macros [cemerick.cljs.test
                    :refer (is deftest with-test run-tests testing test-var)])
  (:require [cemerick.cljs.test :as t]
            [keybind :as key]))

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
