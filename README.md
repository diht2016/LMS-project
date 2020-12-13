# LMS Example Project

[![Build Status](https://travis-ci.com/diht2016/LMS-project.svg?branch=main)](https://travis-ci.com/diht2016/LMS-project)
[![codecov](https://codecov.io/gh/diht2016/LMS-project/branch/main/graph/badge.svg)](https://codecov.io/gh/diht2016/LMS-project)

This project is a homework task. The aim is to create a scalable application with [REST API](https://restfulapi.net/), a database, tests, and continuous integration.

The REST API is implemented using [AKKA HTTP](https://doc.akka.io/docs/akka-http/current/index.html) framework with [Play json support](https://github.com/playframework/play-json). The app is also using [Slick](https://scala-slick.org/) framework, which allows to describe table schemas in a type-safe way and connect to [H2 database](https://www.h2database.com/).

The tables and REST API methods are described in [structure.md](structure.md).

## How to run the app

This app requires [Scala Build Tool](https://www.scala-sbt.org/) to build and run.

Just use the command `sbt run` to run the server, the compiler will build the project if not compiled yet.

The server will be available at http://localhost:8080

To stop the server, press Enter in the command line.

You can also use the command `sbt test` to launch all tests.
