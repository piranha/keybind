.PHONY: test deploy

default: test

# TODO: phantomjs when it'll be installable on el capitan
test:
	SLIMERJSLAUNCHER=/Applications/Firefox.app/Contents/MacOS/firefox lein doo slimer

deploy:
	lein deploy clojars
