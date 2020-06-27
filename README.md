**Working on your first Pull Request?** You can learn how from this *free* series [How to Contribute to an Open Source Project on GitHub](https://egghead.io/series/how-to-contribute-to-an-open-source-project-on-github)

# The ODF Toolkit

Visit our latest documentation on [GitHub](https://tdf.github.io/odftoolkit/docs/). 

The ODF Toolkit is a set of Java modules that allow programmatic
creation, scanning and manipulation of Open Document Format (ISO/IEC 26300 == ODF)
documents. Unlike other approaches which rely on runtime manipulation of heavy-weight
editors via an automation interface, the ODF Toolkit is lightweight and ideal for
server use.

The ODF Toolkit consists of four subcomponents:

1. ODFDOM (odfdom-java-*.jar)
    This is an Open Document Format (ODF) framework. Its purpose is to provide
    an easy, common way to create, access and manipulate ODF files, without
    requiring detailed knowledge of the ODF specification. It is designed to
    provide the ODF developer community with an easy, lightweight programming API
    portable to any object-oriented language.

2. Simple API (deprecated) - (simple-odf-*.jar)
    The Simple Java API for ODF is an easy-to-use, high-level Java API
    for creating, modifying and extracting data from ODF 1.2 documents.
    It is written in pure Java and does not require that you install any
    document editor on your system. The Simple Java API for ODF is a high
    level abstraction of the lower-level ODFDOM API

3. ODF Validator (odfvalidator-*.war)
    This is a tool that validates Open Document Format (ODF) files and checks them
    for conformance according to the ODF Standard. ODF Validator is available as an
    online service and as a command line tool. This page primarily describes the
    command line tool. Please visit the [ODF Validator documentation](https://tdf.github.io/odftoolkit/docs/conformance/ODFValidator.html) for details.

4. ODF XSLT Runner(xslt-runner-*.jar, xslt-runner-task-*.jar)
    ODF XSLT Runner is a small Java application that allows you to apply XSLT
    stylesheets to XML streams included in ODF packages without extracting them
    from the package. It can be used from the command line. A driver to use it
    within an Ant build file, ODF XSLT Runner Task, is also available.

## Getting Started

The ODF Toolkit is based on Java 8 and uses the Maven 3 <http://maven.apache.org/>
build system. To build ODF Toolkit, use the following command in this directory:

    mvn clean install

The simplest way to use these modules are just put the jars files in your classpath
directly. If you are not using maven you can see the versions of the major components for
your release in [CHANGES.txt](CHANGES.txt).

## Recent Releases

1. We have a beta release for 1.0.0 using >=JDK 9 and providing the [new collaboration API](https://tdf.github.io/odftoolkit/docs/odfdom/operations/operations.html):

    *RELEASE (1.0.0-BETA1)*:
    * [ODFDOM](https://repo1.maven.org/maven2/org/odftoolkit/odfdom-java/1.0.0-BETA1/)
    * [ODF Validator](https://repo1.maven.org/maven2/org/odftoolkit/odfvalidator/1.0.0-BETA1/)
    * [XSLT Runner](https://repo1.maven.org/maven2/org/odftoolkit/xslt-runner/1.0.0-BETA1/)

2. We have a new release 0.9.0 for the final time using JDK 8 and including the Simple API:

    *RELEASE (0.9.0)*:
    * [ODFDOM](https://repo1.maven.org/maven2/org/odftoolkit/odfdom-java/0.9.0/)
    * [ODF Validator](https://repo1.maven.org/maven2/org/odftoolkit/odfvalidator/0.9.0/)
    * [XSLT Runner](https://repo1.maven.org/maven2/org/odftoolkit/xslt-runner/0.9.0/)
    * [Simple API (deprecated)](https://repo1.maven.org/maven2/org/odftoolkit/simple-odf/0.9.0/)

For more details see the [release notes](https://tdf.github.io/odftoolkit/docs/odfdom/ReleaseNotes.html).

## Documentation

* The Home Page for the ODF Toolkit:http://odftoolkit.org/index.html
* ODFDOM Getting Start Guide: http://odftoolkit.org/odfdom/index.html
* Simple API (deprecated) - Getting Start Guide: http://odftoolkit.org/simple/gettingstartguide.html
* Simple API (deprecated) - Cookbook: http://odftoolkit.org/simple/document/cookbook/index.html
* Simple API (deprecated) - Demos: http://odftoolkit.org/simple/demo/index.html
* Simple API (deprecated) - Online JavaDoc: http://odftoolkit.org/simple/document/javadoc/index.html
* ODF Validator Getting Start Guide: http://odftoolkit.org/conformance/ODFValidator.html
* ODF XSLT Runner Getting Start Guide: http://odftoolkit.org/xsltrunner/ODFXSLTRunner.html

## License

See also [LICENSE](LICENSE).

Collective work:

* Copyright 2011-2018 The Apache Software Foundation.
* Copyright 2018-2020 The Document Foundation.

This product has been created by The Document Foundation, incorporating
many modifications from different contributors.

See the [NOTICE](NOTICE) file distributed with this work for additional information
regarding copyright ownership.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

 <http://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

The ODF Toolkit includes a number of subcomponents with separate copyright
notices and license terms. Your use of these subcomponents is subject to
the terms and conditions of the licenses listed in the LICENSE.txt file.

## Mailing Lists

Discussion about ODF Toolkit takes place on the following mailing lists:

* Development and Users Mailing List
  * Subscribe: dev+subscribe@odftoolkit.org
  * Post (after subscription): dev@odftoolkit.org
  * Unsubscribe: dev+unsubscribe@odftoolkit.org
  * [Mail archives](https://listarchives.odftoolkit.org/dev/)

The mailing lists are open to anyone and publicly archived.

## Issue Tracker

If you encounter errors in ODF Toolkit or want to suggest an improvement or
a new feature, please visit the [ODF Toolkit issue tracker](https://github.com/tdf/odftoolkit/issues). There you can also find the
latest information on known issues and recent bug fixes and enhancements.
