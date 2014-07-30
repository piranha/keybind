NAME := $(shell awk '/defproject/ { print $$2 }' project.clj)
VERSION := $(shell awk '/defproject/ { gsub("\"", "", $$3); print $$3 }' project.clj)
JAR := target/$(NAME)-$(VERSION).jar

default: test

test:
	lein cljsbuild auto test

pub: pom.xml $(JAR)
	scp $^ clojars@clojars.org:

pom.xml: project.clj
	lein pom

$(JAR): $(shell find src -name '*.cljs')
	lein jar

