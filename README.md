# releasecat-maven-plugin ![icon](/src/site/resources/images/icon_64.png)

[![EO principles respected here](http://www.elegantobjects.org/badge.svg)](http://www.elegantobjects.org)
[![codecov](https://codecov.io/gh/llorllale/releasecat-maven-plugin/branch/master/graph/badge.svg)](https://codecov.io/gh/llorllale/releasecat-maven-plugin)
[![Build Status](https://travis-ci.org/llorllale/releasecat-maven-plugin.svg?branch=master)](https://travis-ci.org/llorllale/releasecat-maven-plugin)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.llorllale/releasecat-maven-plugin/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.llorllale/releasecat-maven-plugin)
[![PDD status](http://www.0pdd.com/svg?name=llorllale/releasecat-maven-plugin)](http://www.0pdd.com/p?name=llorllale/releasecat-maven-plugin)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://llorllale.github.io/releasecat-maven-plugin/license.html)

Don't panic! This is just a maven plugin that uploads/creates GitHub releases for your project. :) It is based on the excellent [jcabi-github](https://github.com/jcabi/jcabi-github) API, go check it out!

## Why another GitHub-releases plugin?
* Travis doesn't provide adequate support for release descriptions (aka changelog), see [1](https://github.com/travis-ci/travis-ci/issues/8568), [2](https://github.com/travis-ci/dpl/issues/155), [3](https://github.com/travis-ci/travis-ci/issues/6423), [4](https://docs.travis-ci.com/user/deployment/releases)
* Ability to specify parameters from the command line or `settings.xml`. This allows dynamic deployments, free from the static configuration of your project's POM.
* Project activity (there are very few projects and they're basically dead)

## Feedback
Please direct any questions, feature requests or bugs to the [issue tracker](https://github.com/llorllale/releasecat-maven-plugin/issues/).

## How to contribute?
See [CONTRIBUTING](./CONTRIBUTING.md).

## License
`releasecat-maven-plugin` is licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0). A copy of the license has been included in [LICENSE](./LICENSE).

<br/>

<div>Icon made by <a href="http://www.freepik.com" title="Freepik">Freepik</a> from <a href="https://www.flaticon.com/" title="Flaticon">www.flaticon.com</a> is licensed by <a href="http://creativecommons.org/licenses/by/3.0/" title="Creative Commons BY 3.0" target="_blank">CC BY 3.0</a></div>