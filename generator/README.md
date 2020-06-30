# Schema2template Java Library

## About Schema2template

The schema2template library loads arbitrary XML schemata (XML grammar) 
into the most powerful grammar model, which is the one given by RelaxNG XML.
Based on loaded XML model given by the grammar Schema2template offers functionality 
to fill arbitrary user templates files with data.

The ODF Toolkit uses Schema2template to generate the XML layer.
Every XML element and XML attribute from the grammar is given a Java class in the ODFDOM project.
In the ODFDOM project these classes have the following locations:

* ODF attributes [odfdom/src/main/java/org/odftoolkit/odfdom/dom/attribute](../odfdom/src/main/java/org/odftoolkit/odfdom/dom/attribute)
* ODF elements: [odfdom/src/main/java/org/odftoolkit/odfdom/dom/element](../odfdom/src/main/java/org/odftoolkit/odfdom/dom/element)

## Architecture

The library is build upon two powerful open source tools:

1. [Multi Schema Validator (MSV)](https://github.com/xmlark/msv)
Used to read arbitrary XML schema and map them to an internal RelaxNG model.

2. [Apache's Velocity Template Engine](http://velocity.apache.org/)
Used as template framework, e.g. to provide scripting within templates.

Within schema2template three common use cases covers with default templates as examples.
For a given XML schema (e.g. RelaxNG, DTD, W3C schema) the following can be created:

1. a XML node reference for the given format as HTML file
2. Java sources for a typed DOM tree of the format

## Design

Read in the Java documentation more about the [generator design](./docs/api/schema2template/index.html) build upon the Multi Schema Validator (MSV).

## License

Apache License, Version 2.0. Please see file  [LICENSE](LICENSE.txt).
