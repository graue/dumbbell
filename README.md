Dumbbell is a simple snake-like game for browsers. The goal is to
collect dumbbells while not running into your own ever-growing tail.
When you collect a dumbbell, it erases a small section of your tail,
leaving a gap you can pass through in the future.

## Status

Unfinished. Needs collision detection, frame-dropping to maintain
consistent speed, scoring, and needs to prevent you from turning
around 180° (which would mean instant death with collision detection).

This is one of my first ClojureScript projects, and I emphasized
getting it done over code cleanliness. The code is no doubt more
imperative than it needs to be. Code review and suggestions welcome.

## Making

`lein cljsbuild once`, then start up a static file webserver in the
project directory and navigate to it. I personally still use `python
-m SimpleHTTPServer` for this. (It's not very Clojure-y, but it beats
putting a bunch of Ring boilerplate in the project just to serve
static files, IMHO.)

## Playing

Use arrow keys to change the snake's direction.
