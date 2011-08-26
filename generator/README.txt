*************************************************************
* Schema2template Java Library                              *
*                                                           *
*************************************************************


About Schema2template
----------------------

The schema2template library reads arbitrary XML schemata and 
provides functionality to map their XML model to arbitrary user 
templates.

The library is build upon two powerful open source tools:

1) Sun's Multi Schema Validator (MSV)
Used to read arbitrary XML schema and map them to an internal RelaxNG 
model.

2) Apache's Velocity Template Engine
Used as template framework, e.g. to provide scripting within templates.

Within schema2template three common use cases covers with default templates 
as examples. 
For a given XML schema (e.g. RelaxNG, DTD, W3C schema) the following can be 
created:

a) a XML node reference for the given format as HTML file

b) Java sources for a typed DOM tree of the format



License
--------

Apache License, Version 2.0. Please see file LICENSE.txt.


Installation
-------------

Just put the file odfdom.jar in your classpath. You will
need Apache Velocity Engine and Sun's Multi Schema Validator (MSV) as well. Get it from
   http://
   http://


Documentation
--------------

Javadoc can be downloaded from the project's download area:
   http://

For online documentation please start by reading the project's
Wiki page
   http://
