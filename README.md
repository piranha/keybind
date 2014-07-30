# keybind

Small library to handle key bindings (shortcuts) in browser, for ClojureScript.

## Features

* Simple format for defining bindings
* Emacs-like key sequences
* Default modifier (`defmod` is parsed as `cmd` on OS X and `ctrl` elsewhere)

## Usage

```clj

(require '[keybind :as key])

(key/bind! "ctrl-c" ::my-trigger #(js/console.log "Sequence fired properly"))
```

where `"ctrl-c"` is a button sequence to register on, and `::my-trigger` is a key
unique for this sequence.

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

## How it works

Library binds global key handler to check all keypresses. The reason for this is
that focus in browsers is often hard to handle and define properly, and most of
the time it makes no sense to bind against some element.

## Issues

Please notify me if you don't understand something, I would like to improve
documentation but not sure exactly what to do.

One major issue with the library right now is that the `bindings` atom is global
right now. I'm thinking about best way to mitigate this, especially for the case
where you have multiple contexts with different keybindings. Not sure this is an
issue though.
