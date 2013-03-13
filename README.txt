====================================================================================

                            Apache ODF Toolkit  
            <http://incubator.apache.org/odftoolkit/index.html>
        
====================================================================================

The Apache ODF Toolkit (incuating) is a set of Java modules that allow programmatic 
creation, scanning and manipulation of Open Document Format (ISO/IEC 26300 == ODF) 
documents. Unlike other approaches which rely on runtime manipulation of heavy-weight 
editors via an automation interface, the ODF Toolkit is lightweight and ideal for 
server use.
It's an incubator project of the Apache Software Foundation <http://www.apache.org/>.

The ODF Toolkit consists of four subcomponents:

1. ODFDOM (odfdom-java-*.jar) 
    This is an Open Document Format (ODF) framework. Its purpose is to provide 
    an easy, common way to create, access and manipulate ODF files, without 
    requiring detailed knowledge of the ODF specification. It is designed to 
    provide the ODF developer community with an easy, lightweight programming API 
    portable to any object-oriented language.
    
2. Simple API (simple-odf-*.jar)
    The Simple Java API for ODF is an easy-to-use, high-level Java API 
    for creating, modifying and extracting data from ODF 1.2 documents.
    It is written in pure Java and does not require that you install any
    document editor on your system. The Simple Java API for ODF is a high
    level abstraction of the lower-level ODFDOM API

3. ODF Validator (odfvalidator-*.war)
    This is a tool that validates Open Document Format (ODF) files and checks them
    for conformance according to the ODF Standard. ODF Validator is available as an 
    online service and as a command line tool. This page primarily describes the 
    command line tool. Please visit web page:
       http://incubator.apache.org/odftoolkit/conformance/ODFValidator.html
    for details regarding the online tool.

4. ODF XSLT Runner(xslt-runner-*.jar, xslt-runner-task-*.jar)
    ODF XSLT Runner is a small Java application that allows you to apply XSLT 
    stylesheets to XML streams included in ODF packages without extracting them 
    from the package. It can be used from the command line. A driver to use it 
    within an Ant build file, ODF XSLT Runner Task, is also available.


Getting Started
===============

The ODF Toolkit is based on Java 5 and uses the Maven 2 <http://maven.apache.org/>
build system. To build ODF Toolkit, use the following command in this directory:

    mvn clean install

The simplest way to use these modules are just put the jars files in your classpath
directly. If you are not using maven you can see the versions of the major components for
your release in CHANGES.txt.

Documentation
=============

The Home Page for the ODF Toolkit:
    http://incubator.apache.org/odftoolkit/index.html
    
ODFDOM Getting Start Guide:    
    http://incubator.apache.org/odftoolkit/odfdom/index.html   
     
Simple API Getting Start Guide:    
    http://incubator.apache.org/odftoolkit/simple/gettingstartguide.html    
       
Simple API Cookbook:
    http://incubator.apache.org/odftoolkit/simple/document/cookbook/index.html
    
Simple API Demos:
    http://incubator.apache.org/odftoolkit/simple/demo/index.html
    
Simple API Online JavaDoc:
    http://incubator.apache.org/odftoolkit/simple/document/javadoc/index.html
    
ODF Validator Getting Start Guide:   
    http://incubator.apache.org/odftoolkit/conformance/ODFValidator.html
    
ODF XSLT Runner Getting Start Guide:   
    http://incubator.apache.org/odftoolkit/xsltrunner/ODFXSLTRunner.html     
    
    
License (see also LICENSE.txt)
==============================

Collective work: Copyright 2011 The Apache Software Foundation.

Licensed to the Apache Software Foundation (ASF) under one or more
contributor license agreements.  See the NOTICE file distributed with
this work for additional information regarding copyright ownership.
The ASF licenses this file to You under the Apache License, Version 2.0
(the "License"); you may not use this file except in compliance with
the License.  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Apache ODF Toolkit includes a number of subcomponents with separate copyright
notices and license terms. Your use of these subcomponents is subject to
the terms and conditions of the licenses listed in the LICENSE.txt file.
    
    
Mailing Lists
=============

Discussion about ODF Toolkit takes place on the following mailing lists:

Development Mailing List
    Subscribe: odf-dev-subscribe@incubator.apache.org
    Post (after subscription): odf-dev@incubator.apache.org
    Unsubscribe: odf-dev-unsubscribe@incubator.apache.org
    Archives
    (1) Markmail - http://markmail.org/search/+list:org.apache.incubator.odf-dev/
    (2) Apache - http://mail-archives.apache.org/mod_mbox/incubator-odf-dev/

Users Mailing List
    Subscribe: odf-users-subscribe@incubator.apache.org
    Post (after subscription): odf-users@incubator.apache.org
    Unsubscribe: odf-users-unsubscribe@incubator.apache.org
    Archives: http://mail-archives.apache.org/mod_mbox/incubator-odf-users/

Notification on all code changes are sent to the following mailing list:

    odf-commits@incubator.apache.org

The mailing lists are open to anyone and publicly archived.


Issue Tracker
=============

If you encounter errors in ODF Toolkit or want to suggest an improvement or
a new feature, please visit the ODF Toolkit issue tracker at
https://issues.apache.org/jira/browse/ODFTOOLKIT. There you can also find the
latest information on known issues and recent bug fixes and enhancements.    
