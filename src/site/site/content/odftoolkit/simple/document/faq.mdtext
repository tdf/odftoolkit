Title: Frequently Asked Questions

##For Users

**How to download and install**    

You can get Simple Java API for ODF as binary distribution from the [download][1] area. There you'll find Javadocs as well. To obtain source code, please refer to development section. 

**How to start development**    

I suggest you to start from an overview of this project. [This page][2] will give you an overall introduction of the package structures. After that, you can go to the [Cookbook][3] and [Demos][4] to read some code samples. And then, you can start your own program to manipulate ODF document.

If you are familar with ODFDOM, [Here][5] are the API changes from ODFDOM.

**Prerequisites**   

In addition to the Simple Java API JAR file you will need to download and install the following runtime prerequisite:

* JDK version 1.6
* [ODFDOM 0.8.7][6]
* The Apache Xerces 2.9.1 or higher version. (download from [Apache Xerces web site][7])

**Code examples**    
You can find sample codes from our [Cookbook][8] and [Demos][9].    

**How to report defects**     
The Simple API uses Bugzilla to track the defects. You can report defects [here][10].

##For Developers

**How to download source**    

First, install [latest Mercurial][11]   
Second, setup Mercurial (optional):   
     Config file &lt;Hg Install Dir&gt;\Mercurial.ini on Windows or &lt;Hg Install Dir&gt;/.hgrc on Unix.    
     Enhance the default configuration, using GIT diff and enable [default plugin][12] avoid different line breaks in the source.    


    [ui]
    username = your Name <yourLoginName@odftoolkit.org>
    ;merge = your-merge-program (or internal:merge)
    
    [diff]
    git = 1
    
    [defaults]
    diff=-p -U 8
    
    [extensions]
    # Enables mercurial EOL extension for line break handling
    # See http://mercurial.selenic.com/wiki/EolExtension (bundled since 1.5.4)
    eol =
    
    # It'll remove unknown files and empty directories by default. 
    # Usually you call 'hg update -C' and 'hg purge' in sequence
    # See http://mercurial.selenic.com/wiki/PurgeExtension  (bundled)
    hgext.purge=
    
    [eol]
    # Converts mixed line ending within a file to LF (Unix) format 
    # before adding the file to the source repository
    # See http://mercurial.selenic.com/wiki/EolExtension
    only-consistent = False


Now, get the source code from the Simple project [Mercurial][13] repository. Please see below for a short introduction to using Mercurial on odftoolkit.org. There's also a more general help on how to use source control systems on odftoolkit.org. The command     

`hg clone https://hg.odftoolkit.org/hg/simple~code-base`

will download the repository into a new directory. 

Please note that by this you'll get the latest changeset. If you for example want stable release 0.2, you may want to use<br /><pre>hg clone https://odftoolkit.org/hg/simple~code-base -r v0.2</pre>Developers however always work on the latest changeset.

**How to build and run the unit tests**     
After the source code is checked out, now get and install [Apache Maven][14]. On command line test your installation with `"mvn -v"`.

If Maven is correctly installed, change into the project directory and build with command `"mvn"`.

##Communications
You can subscribe to the project's mailing lists from [this page][15].  The "users" list is for discussions about using the toolkit, and the "dev" list is for discussing the development of the toolkit.  Active developers should also subscribe to the "commit" mailing list so they will receive those automatic notifications.


##Easy entry level tasks
TBD

##Design of the API
The design principle is "make it easy for users to locate the functions they want".     

org.odftoolkit.simple.Document is the abstract base class which the specific document classes are derived from:  TextDocument, SpreadsheetDocument, PresentationDocument, GraphicsDocument and ChartDocument.     

For each document type  we have a subpackage that contain additional classes related to that kind of documents.  So org.odftoolkit.simple.chart has classes related to chart, org.odftoolkit.simple.text has classes related to text content,  and org.odftoolkit.simple.presentation has classes related to presentation documents.    

Besides the packages for different document types, there are additional packages defined for important common ODF features which are available across all kinds of documents, such as table, meta and style.      

There is another package named org.odftoolkit.simple.common, which contains functions that are not related with a specific document type nor a specific feature. For example, the text extractor functions are put in this package.     


##How to contribute patches
If you want to contribute a patch to this project, following below steps:

  - Step 1. Create an issue in [bugzilla][16], or write a comment to a related open issue, to describe what functions you want to provide.      
  - Step 2. Go through "Design of the API" to make sure your contribution will follow it.    
  - Step 3. Check out the source code following the steps in development selection, and add your    
    contribution codes, including the source code and the unit tests.    
    Every public method should have a corresponding unit test method.     
    The [code guidelines][17] are same as ODFDOM.    
  - Step 4. Test your contribution with Mecurial command to make sure all the unit tests pass.  `mvn`    
  - Step 5. Generate a patch with hg commands.       
 
    `hg commit -A -u "Developer:xxx" -m "#bug XY# Description of changes"`      
    `hg export -a -g -o ../myChanges.patch`

  - Step 6. Upload your patch to bugzilla.
  - Step 7. Refactor your patch if you get comments from reviewers.

##How to review patches

As a reviewer, you need to carefully read the source code, run the unit tests, to make sure the new contributed code won't hurt the quality of this project.  
      
 - Step 1. Write a comment to [bugzilla][18], saying you would like to review this patch.         
 - Step 2. Check out the source code following the steps in development selection, and download the patch.      
 - Step 3. Merge the patch to the source code with Mecurial command:   
 

    `hg import --no-commit ../someFile.patch`

 - Step 4. Carefully read the code to make sure the code follows the Design of the API, the code can reach the goal and the code won't bring other problems.    
 - Step 5. Carefully check the unit test code to make sure every public method has been enough tested.    
 - Step 6. Run the unit tests with Maven command to see if all the unit tests pass:   

    `mvn`

 - Step 7. Write comment to bugzilla if you think anything needs to be improved.   
 - Step 8. Repeat step 2 to 7 after the contributor improves the patch.    
 - Step 9. Push the patch to repository with Mecurial command if you think the patch is good enough:   
 

    `hg commit -A -u "Developer:xxx Reviewer:yyy" -m "#bug XY# Description of changes"`   
    `hg push https://<your user id>:<your password>@odftoolkit.org/hg/simple~code-base`

##Reference material
**ODF**    

The current draft of the OASIS ODF 1.2 standard can be downloaded [here][19].

**Commands line tools**   

 - [Here][20] is a list of the most frequently used commands for Mercurial.  
 - [Here][21] is a list of the most frequently used commands for Maven.  


  [1]: ../download.html
  [2]: PackageLayer.html
  [3]: cookbook/index.html
  [4]: ../demo/index.html
  [5]: PackageLayer.html
  [6]: http://odftoolkit-extra.apache-extras.org.codespot.com/files/odfdom-0.8.7.jar
  [7]: http://xerces.apache.org/mirrors.cgi
  [8]: cookbook/index.html
  [9]: ../demo/index.html
  [10]: http://odftoolkit.org/bugzilla/buglist.cgi?product=simple&order=bugs.bug_id
  [11]: http://mercurial.selenic.com/wiki/
  [12]: http://mercurial.selenic.com/wiki/Win32TextExtension
  [13]: http://www.selenic.com/mercurial/wiki/
  [14]: http://maven.apache.org/
  [15]: http://incubator.apache.org/odftoolkit/mailing-lists.html
  [16]: http://odftoolkit.org/bugzilla/buglist.cgi?product=simple&order=bugs.bug_id
  [17]: http://incubator.apache.org/odftoolkit/odfdom/Development.html
  [18]: http://odftoolkit.org/bugzilla/buglist.cgi?product=simple&order=bugs.bug_id
  [19]: http://www.oasis-open.org/committees/tc_home.php?wg_abbrev=office#odf12
  [20]: http://incubator.apache.org/odftoolkit/odfdom/Development.html
  [21]: http://incubator.apache.org/odftoolkit/odfdom/Development.html