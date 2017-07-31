.PHONY: test deploy

default: test

test:
	lein doo phantom test once

deploy:
	lein deploy clojars

clean:
	rm -rf target
