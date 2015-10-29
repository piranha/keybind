# keybind

Small library to handle key bindings (shortcuts) in browser, for ClojureScript.

## Features

* Simple format for defining bindings
* Emacs-like key sequences
* Default modifier (`defmod` is parsed as `cmd` on OS X and `ctrl` elsewhere)

## Usage

Add this to your `:dependecies` vector:

[![Clojars Project](http://clojars.org/keybind/latest-version.svg)](http://clojars.org/keybind)

And then:

```clj

(require '[keybind :as key])

(key/bind! "ctrl-c" ::my-trigger #(js/console.log "Sequence fired properly"))
```

where `"ctrl-c"` is a button sequence to register on, and `::my-trigger` is a key
unique for this sequence - you can use this key to remove binding later on.

### Format description

If you know Emacs format, you're all set.

In other case, you have to provide a list of modifiers (some of `shift`, `ctrl`,
`alt`, `win`, `cmd`, `defmod`), followed by a key name. All of those should be
separated by `-`, i.e.: `ctrl-k`, `alt-m`, `shift-r`.

Combining few such "chords" in a sequence, like `ctrl-k ctrl-m`, will register a
key sequence. To trigger you have to press `ctrl` and `k` simultaneously,
release them and then press `ctrl` and `m` simultaneously.

**Note 1**: if you want to register on a big letter, use `shift-a`.

**Note 2**: `ctrl-j` in most browsers opens a download window. I have thoughts
how to prevent that (for the sequence `ctrl-t ctrl-j k` to word), but didn't do
anything yet. Report an issue if you have a problem with that.

## Examples

```clojure
(require '[keybind :as key])

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

## How it works

Library binds global key handler to check all keypresses. The reason for this is
that focus in browsers is often hard to handle and define properly, and most of
the time it makes no sense to bind against some element.

## Bindings storage

Also, you have to be aware that `keybind/bind!` and `keybind/unbind!`
use global `keybind/BINDINGS` atom. If you want to use your own atom, just use
`keybind/bind` and `keybind/unbind` versions (`swap!` your atom with
them). You'll have to bind `keybind/dispatcher!` on your own though.

I was also thinking how would you have more than a single atom - if you want to
organize some contexts (on one page you have one set of bindings and another
page obviously has different actions and different bindings) - and it seems to
me it's easier to just `reset!` one single atom with necessary bindings when
it's necessary. That's why `keybind/BINDINGS` is public.

Or just `bind!`/`unbind!` all the time, whatever floats your boat.

## Issues

Please notify me if you don't understand something, I would like to improve
documentation but not sure exactly what to do.
