(ns keybind)

;; Definitions

(def ^:private MODS
  {"shift" :shift
   "ctrl" :ctrl "control" :ctrl
   "alt" :alt "option" :alt
   "win" :meta "cmd" :meta "super" :meta "meta" :meta
   ;; default modifier for OS X is cmd and for others is ctrl
   "defmod" (if (neg? (.indexOf js/navigator.userAgent "Mac OS X"))
              :ctrl :meta)})

(def ^:private KEYATTRS
  {"shiftKey" :shift "ctrlKey" :ctrl "altKey" :alt "metaKey" :meta
   "keyCode" :code})

(def ^:private DEFCHORD {:shift false :ctrl false :alt false :meta false})

(def ^:private KEYS
  (merge {"backspace" 8,
          "tab" 9,
          "enter" 13, "return" 13,
          "pause" 19,
          "caps" 20, "capslock" 20,
          "escape" 27, "esc" 27,
          "space" 32,
          "pgup" 33, "pageup" 33,
          "pgdown" 34, "pagedown" 34,
          "end" 35,
          "home" 36,
          "ins" 45, "insert" 45,
          "del" 46, "delete" 46,

          "left" 37,
          "up" 38,
          "right" 39,
          "down" 40,

          "*" 106,
          "+" 107, "plus" 107,
          "-" 109, "minus" 109,
          ";" 186,
          "=" 187,
          "," 188,
          "." 190,
          "/" 191,
          "`" 192,
          "[" 219,
          "\\" 220,
          "]" 221,
          "'" 222
          }

    ;; numpad
    (into {} (for [i (range 10)]
               [(str "num-" i) (+ 95 i)]))

    ;; top row 0-9
    (into {} (for [i (range 10)]
               [(str i) (+ 48 i)]))

    ;; f1-f24
    (into {} (for [i (range 1 25)]
               [(str "f" i) (+ 111 i)]))

    ;; alphabet
    (into {} (for [i (range 65 91)]
               [(.toLowerCase (js/String.fromCharCode i)) i]))))

(def ^:private KEYREV
  (into {} (for [[k v] KEYS]
             [v k])))

;; Data

(defonce bindings (atom {}))
(defonce pressed (atom []))

;; Behavior

(defn parse-chord [keystring]
  (let [bits (.split keystring "-")
        [mods [button]] (split-at (dec (count bits)) bits)
        code (get KEYS button)]
    (when-not code
      (throw (js/Error. (str "Unknown key '" button
                          "' in keystring '" keystring "'"))))

    (into (assoc DEFCHORD :code code)
      (for [mod mods]
        (if-not (get MODS mod)
          (throw (js/Error. (str "Unknown modified '" mod
                              "' in keystring '" keystring "'")))
          [(get MODS mod) true])))))

(defn parse [chain]
  (let [bits (.split chain " ")]
    (mapv parse-chord bits)))

(defn e->chord [e]
  (into {} (for [[attr key] KEYATTRS]
             [key (aget e attr)])))

(defn reset-sequence! []
  (swap! pressed empty))

(defn dispatch! [e]
  (when (get KEYREV (.-keyCode e))
    (let [chord (e->chord e)
          sequence (conj @pressed chord)
          inner (get-in @bindings sequence)]
      (if-not inner
        (reset-sequence!)
        (if-not (:handlers inner)
          (reset! pressed sequence)
          (do
            (doseq [[_ handler] (:handlers inner)]
              (handler e sequence))
            (reset-sequence!)))))))

(defn bind [bindings spec key cb]
  (let [parsed (parse spec)]
    (swap! bindings assoc-in (conj parsed :handlers key) cb)))

(defn unbind [bindings spec key]
  (let [parsed (parse spec)]
    (swap! bindings update-in (conj parsed :handlers) dissoc key)))

(defn bind! [spec key cb]
  (bind bindings spec key cb))

(defn unbind! [spec key]
  (unbind bindings spec key))

(defn unbind-all! []
  (reset-sequence!)
  (swap! bindings empty))

(defonce bind-keypress-listener
  (js/addEventListener "keydown" dispatch! false))
