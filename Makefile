.PHONY: test

default: test

test:
	lein cljsbuild auto test
