# Contributing to this library

LightCouch is written in Java and uses [maven](https://maven.apache.org/) as its build tool.

## Requirements

The main requirements are:

* Java 1.8
* CouchDB

## Coding guidelines

Adopting the [Google Java Style](https://google-styleguide.googlecode.com/svn/trunk/javaguide.html)
with the following changes:

```
4.2
    Our block indent is +4 characters

4.4
    Our line length is 100 characters.

4.5.2
    Indent continuation of +4 characters fine, but I think
    IDEA defaults to 8, which is okay too.
```

## Releasing

Remove `-SNAPSHOT` from the release version in `pom.xml`, then commit and push
the change. Tag that commit with the matching `MAJOR.MINOR.PATCH` version. For
version `0.3.3`, run the following commands from the release commit.

```sh
git tag 0.3.3
git push origin 0.3.3
```

The Release workflow runs the full CouchDB test matrix, then publishes a GitHub
release with generated notes and the tested JAR. The tag is also the version
consumers use with JitPack.
