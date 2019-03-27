Title: ODFXSLTRunnerTask
## Apply XSLT stylesheets to ODF documents with Ant

**ODFXSLTRunnerTask** is a task definition for [Apache Ant][1] which allows 
to apply XSLT stylesheets to ODF documents similar to Ant's build-in 
<tt>&lt;xslt&gt;</tt> [task][2]. It is based on ODFXSLTRunner. Please see 
ODFXSLTRunner for further details of its operation.

##Getting and building ODFXSLTRunnerTask

**odfxsltrunnertask** is build by a NetBeans project. After you have checked 
out the odf-xslt-runner-task-src/show, you can open the project in NetBeans 
and build **odfxsltrunnertask**.

A binary release of **odfxsltrunnertask.jar** is available in the download 
section.

Building and running **odfxsltrunner.jar** requires additional jar files. 
Please see Requirements for details.

##Usage

To use **odfxsltrunner.jar** with Ant, you have to include the following [task definition][3] into 
your buildfile, where ''<tt>&lt;path&gt;</tt>'' has to be replaced with the path where you have stored the **odfxsltrunnertask.jar** and **odfxsltrunner.jar** files:

     
     <taskdef name="odfxslt" classname="odfxsltrunnertask.ODFXSLTRunnerTask" 
         classpath="<path>/odfxsltrunnertak.jar:<path>/odfxsltrunner.jar"/>

 

You can use this task definition on the top level (that is as a child element 
of the <tt>&lt;project&gt;</tt> element or locally within a single 
<tt>&lt;target&gt;</tt> element. It defines a new task <tt>&lt;odfxslt&gt;</tt> which allows to process ODF documents with XSLT stylesheets.

The new task supports the use of nested <tt>&lt;param&gt;</tt> and 
<tt>&lt;factory&gt;</tt>elements which have the same meaning as the 
<tt>&lt;param&gt;</tt> and <tt>&lt;factory&gt;</tt> child elements of the 
<tt>&lt;xslt&gt;</tt> [task][2].

##Parameters

<table border="border">
<tr><th>Attribute</th><th>Description</th><th>Required</th></tr>
<tr><td valign="top">in</td><td valign="top">specifies an ODF document to which the stylesheet is applied.</td><td valign="top">Yes, unless infile has been specified</td></tr>
<tr><td valign="top">infile</td><td valign="top">specifies a plain XML document to which the stylesheet is applied.</td><td valign="top">Yes, unless in has been specified</td></tr>
<tr><td valign="top">out</td><td valign="top">specifies an ODF document to which the result of the transformation is stored.<br/>
Unless template has been specified, the package specified by in is copied to out, and the stream specified by path is replaced with the result of the transformation.</td><td valign="top">Yes, unless outfile has been specified</td></tr>
<tr><td valign="top">outfile</td><td valign="top">specifies a plain XML document to which the result of the transformation is stored.</td><td valign="top">Yes, unless out has been specified</td></tr>
<tr><td valign="top">path</td><td valign="top">Specifies the stream within the ODF packages specified by in and out, which is the source or target of the transformation.</td><td valign="top">No</td></tr>
<tr><td valign="top">template</td><td valign="top">Specifies that the specified stream within the out ODF package is replaced with the result of the transformation, without previously copying the package specified by in. The specified out package must exist.</td><td valign="top">No:  Default is "content.xml"</td></tr>
<tr><td valign="top">force</td><td valign="top">Specifies that the target file shall be recreated, even if it is newer than the source file or the stylesheet.</td><td valign="top">No: Default is false</td></tr>
</table>

##Parameters specified as nested elements

###Param

Param specifies a parameter that is passed as [XSLT parameter][4] to the 
XSL stylesheet.

###Parameters

<table border="border">
<tr><th>Attribute</th><th>Description</th><th>Required</th></tr>
<tr><td valign="top">name</td><td valign="top">name of the parameter.</td><td valign="top">Yes</td></tr>
<tr><td valign="top">expression</td><td>value of the parameter.
Note: All parameter values are passed as string values to the XSLT parameters specified by <tt>&lt;xslt:param&gt;</tt> elements within the stylesheet.
</td><td valign="top">Yes</td></tr>
</table>

**Note:** The parameters <tt>if</tt> and <tt>unless</tt> which are supported by the <tt>&lt;xslt&gt;</tt> task are (not yet) supported.

###Factory

Factory specifies Java TransformerFactory class to use.

###Parameters

<table border="border">
<tr><th>Attribute</th><th>Description</th><th>Required</th></tr>
<tr><td valign="top">name</td><td valign="top">full qualified TransformerFactory class name.</td><td valign="top">Yes</td></tr>
</table>

##Requirements

**odfxsltrunnertask** requires [J2RE 5][5], or a later version of Java. 

It further requires ODFXSLTRunner and the ODFDOM component (at least version  v0.6.1). 

**Note:** When building **odfxsltrunertask**, the **odfxsltrunner** project has be checked out, too. It is build automatically.

In the Ant task definition, the **classpth** attribute must include the **odfxsltrunnertask.jar** and **odfxsltrunner.jar** files. The  ODFDOM jar file is found automatically if it is located in a folder called **lib** next to the **odfxsltrunner.jar** file. 

**Note:** <a href="{{project odfdom page home}}">ODFDOM</a> v0.6.1  requires [Apache Xerces][6]. The jar file **xercesImpl.jar** also must exist in a folder **lib** next to **odfxsltrunner.jar** file.


[1]: http://ant.apache.org/
[2]: http://ant.apache.org/manual/CoreTasks/style.html
[3]: http://ant.apache.org/manual/CoreTasks/taskdef.html
[4]: http://www.w3.org/TR/1999/REC-xslt-19991116#top-level-variables
[5]: http://java.sun.com/javase/downloads/index.jsp
[6]: http://xml.apache.org/dist/xerces-j/