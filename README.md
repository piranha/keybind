# keybind

Small library to handle key bindings (shortcuts) in browser, for ClojureScript.

## Features

* Simple format for defining bindings
* Emacs-like key sequences
* Default modifier (`defmod` is parsed as `cmd` on OS X and `ctrl` elsewhere)

## Changelog

### 2.2.0

- handle Emacs-style key bindings (like `C-M-x C-j`)

### 2.1.0

- added global `disable!`/`enable!` functions (see further for instructions)

### 2.0.1

- fixed binding to `-` (and `minus`), now if you need to bind to minus on
  keypad, use `kpminus`.

### 2.0.0

- renamed `keybind` to `keybind.core`
- cleaned up code a bit

## Usage

Add this to your `:dependencies` vector:

[![Clojars Project](http://clojars.org/keybind/latest-version.svg)](http://clojars.org/keybind)

And then:

```clj

(require '[keybind.core :as key])

(key/bind! "ctrl-c" ::my-trigger #(js/console.log "Sequence fired properly"))
```

where `"ctrl-c"` is a button sequence to register on, and `::my-trigger` is a key
unique for this sequence - you can use this key to remove binding later on.

### Format description

If you know Emacs' format, you're all set. Not exactly Emacs - I decided to
resort to more common names of modifiers, though Emacs-style `C-` and `M-` are
also supported.

In other case, you have to provide a list of modifiers (some of `shift`, `ctrl`,
`alt`, `win`, `cmd`, `defmod`), followed by a key name. All of those should be
separated by `-`, i.e.: `ctrl-k`, `alt-m`, `shift-r`.

Combining few such "chords" in a sequence, like `ctrl-k ctrl-m`, will register a
key sequence. To trigger you have to press `ctrl` and `k` simultaneously,
release them and then press `ctrl` and `m` simultaneously.

**Note 1**: if you want to register on a big letter, use `shift-a`.

**Note 2**: `ctrl-j` in most browsers opens a "Downloads" window. I have thoughts
how to prevent that (for the sequence `ctrl-t ctrl-j k` for example), but didn't
do anything yet. Report an issue if you have a problem with that.

**Note 3**: looking at the [source][] as a reference for key names makes sense. :)

[source]: https://github.com/piranha/keybind/blob/master/src/keybind/core.cljs

## Examples

```clojure
(require '[keybind.core :as key])

(defn some-mount-function [items current-item]
  (key/bind! "j" ::next #(go-next items current-item))
  (key/bind! "shift-space" ::prev #(go-prev items current-item))
  (key/bind! "C-c C-x j" ::chord #(js/alert "ever heard of emacs chords?")))

(defn some-unmount-function []
  (key/unbind! "j" ::next)
  (key/unbind! "shift-space" ::prev)
  (key/unbind! "C-c C-x j" ::chord)
  ;; or simply:
  (key/unbind-all!))
```

## Disable key bindings temporarily

You may want to disable key bindings without actually removing them (e.g. while focus is on input/textarea elements). This can be accomplished via the `disable!` and `enable!` functions which don't affect the registration of bindings but simply control the dispatching of key events:

```clojure
(defn some-reagent-component []
  [:textarea
    {:value "some text"
     :on-change handle-change
     :on-focus key/disable!
     :on-blur key/enable!}]))
```

## How it works

Library binds global key handler to check all keypresses. The reason for this is
that focus in browsers is often hard to handle and define properly, and most of
the time it makes no sense to bind against some element.

## Bindings storage

Also, you have to be aware that `bind!` and `unbind!` use global `BINDINGS`
atom. If you want to use your own atom, just use `bind` and `unbind` versions
(`swap!` your atom with them). You'll have to bind `dispatcher!` on your own
though.

I was also thinking how would you have more than a single atom - if you want to
organize some contexts (on one page you have one set of bindings and another
page obviously has different actions and different bindings) - and it seems to
me it's easier to just `reset!` one single atom with necessary bindings when
it's necessary. That's why `BINDINGS` is public.

Or just `bind!`/`unbind!` all the time, whatever floats your boat.

## Issues

Please notify me if you don't understand something, I would like to improve
documentation but not sure exactly what to do.

Plus it isn't possible to have custom key modifiers right now. Ideally I'd like
to have that, but right now we're limited to Shift/Control/Alt/Command.
