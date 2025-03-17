# The ODF Toolkit

Visit our latest documentation on [our GitHub Pages site](https://tdf.github.io/odftoolkit/).

[The ODF Toolkit](http://odftoolkit.org) is a set of Java modules that allow programmatic
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
    command line tool. Please visit the [ODF Validator documentation](https://tdf.github.io/odftoolkit/conformance/ODFValidator.html) for details.

4. ODF XSLT Runner(xslt-runner-*.jar, xslt-runner-task-*.jar)
    ODF XSLT Runner is a small Java application that allows you to apply XSLT
    stylesheets to XML streams included in ODF packages without extracting them
    from the package. It can be used from the command line. A driver to use it
    within an Ant build file, ODF XSLT Runner Task, is also available.

People interested should follow the [mail list](https://tdf.github.io/odftoolkit/mailing-lists.html) to track progress.

## Getting Started

The ODF Toolkit is based on Java (tested with JDK 11) and uses the Maven 3 <http://maven.apache.org/>
build system. To build ODF Toolkit, use the following command in this directory:

    mvn clean install

## Recent Releases

1. We have a *release 0.12.0* using >=JDK 11 for ODF 1.2-
   Mainly maintenance updating the dependenciy versions<br/>
    **RELEASE (0.12.0)**:
    * [ODFDOM](https://repo1.maven.org/maven2/org/odftoolkit/odfdom-java/0.12.0/)
    * [ODF Validator](https://repo1.maven.org/maven2/org/odftoolkit/odfvalidator/0.12.0/)
    * [XSLT Runner](https://repo1.maven.org/maven2/org/odftoolkit/xslt-runner/0.12.0/)

For more details see the [release notes](https://tdf.github.io/odftoolkit/ReleaseNotes.html).</br>
   *NOTE*: The prior 0.11.0 release was doing a full refactoring of the ODFDOM code generation and containing updates to the [new collaboration API](https://tdf.github.io/odftoolkit/odfdom/operations/operations.html).<br/>

## Documentation

* [The Home Page for the ODF Toolkit](https://tdf.github.io/odftoolkit)
* [ODFDOM Getting Start Guide](https://tdf.github.io/odftoolkit/odfdom/index.html)
* [Simple API (deprecated) - Getting Start Guide](https://tdf.github.io/odftoolkit/simple/gettingstartguide.html)
* [Simple API (deprecated) - Cookbook](https://tdf.github.io/odftoolkit/simple/document/cookbook/index.html)
* [Simple API (deprecated) - Demos](https://tdf.github.io/odftoolkit/simple/demo/index.html)
* [Simple API (deprecated) - Online JavaDoc](https://tdf.github.io/odftoolkit/simple/document/javadoc/index.html)
* [ODF Validator Getting Start Guide](https://tdf.github.io/odftoolkit/conformance/ODFValidator.html)
* [ODF XSLT Runner Getting Start Guide](https://tdf.github.io/odftoolkit/xsltrunner/ODFXSLTRunner.html)

## Mailing Lists

Discussion about ODF Toolkit takes place on the following mailing lists:

* Development and Users Mailing List
  * Subscribe: <dev+subscribe@odftoolkit.org>
  * Post (after subscription): <dev@odftoolkit.org>
  * Unsubscribe: <dev+unsubscribe@odftoolkit.org>
  * [Mail archives](https://listarchives.odftoolkit.org/dev/)

The mailing lists are open to anyone and publicly archived.

* To confidentially report a **security issue**, please mail to: <security@documentfoundation.org>

## Issue Tracker

If you encounter errors in ODF Toolkit or want to suggest an improvement or
a new feature, please visit the [ODF Toolkit issue tracker](https://github.com/tdf/odftoolkit/issues). There you can also find the
latest information on known issues and recent bug fixes and enhancements.

## License

The ODF Toolkit includes a number of subcomponents with separate copyright
notices and license terms. Your use of these subcomponents is subject to
the terms and conditions of the licenses listed in the [LICENSE](LICENSE) file.
Copyright ownership information can be found in [NOTICE](NOTICE).
