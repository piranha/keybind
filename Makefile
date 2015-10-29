.PHONY: test deploy

SLIMERJSLAUNCHER=/Applications/Firefox.app/Contents/MacOS/firefox

default: test

# TODO: phantomjs when it'll be installable on el capitan
test:
	lein doo slimer

deploy:
	lein deploy clojars
