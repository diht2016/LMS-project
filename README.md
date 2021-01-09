# LMS Example Project

[![Build Status](https://travis-ci.com/diht2016/LMS-project.svg?branch=main)](https://travis-ci.com/diht2016/LMS-project)
[![codecov](https://codecov.io/gh/diht2016/LMS-project/branch/main/graph/badge.svg)](https://codecov.io/gh/diht2016/LMS-project)
[![Maintainability](https://api.codeclimate.com/v1/badges/e42784b51437dfab5dee/maintainability)](https://codeclimate.com/github/diht2016/LMS-project/maintainability)

This project is a homework task. The aim is to create a scalable application with [REST API](https://restfulapi.net/), a database, tests, and continuous integration.

The database tables and REST API methods are described in [structure.md](structure.md).

The REST API is implemented using [AKKA HTTP](https://doc.akka.io/docs/akka-http/current/index.html) framework with [Play json support](https://github.com/playframework/play-json). The app is also using [Slick](https://scala-slick.org/) framework, which allows to describe table schemas in a type-safe way and connect to [H2 database](https://www.h2database.com/).

## How to run the app

This app requires [Scala Build Tool](https://www.scala-sbt.org/) to build and run. Run `sbt` command in terminal. If SBT is installed, it will start the SBT console (might take some time), which has its own set of commands.

When the SBT console is loaded, enter `run` command to run the server, the compiler will build the project if not compiled yet.

The application server will be available at http://localhost:8080, however, the database is empty.

To stop the server, simply press Enter in the command line.

Enter `test` command to launch all tests. Enter `exit` command to leave SBT console.

## Example application with pre-filled database

Enter `test:run` command, application server will be available at http://localhost:7357, and you can make requests to it.

Sample database contents will regenerate on every launch.

There is a [javascript file](./src/test/js/test-requests.js) which can make the interaction with REST API easier. Open app's website in browser, open Dev Tools JavaScript Console (press `Ctrl + Shift + J`), paste file contents in it and execute. Then, you can do requests in this console, for example, `get('/users/me')` or `post('/auth/change-password', {oldPassword: '...', newPassword: '...'})`.

Have fun!
