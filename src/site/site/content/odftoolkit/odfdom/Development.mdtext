Title: Development

**Get and Build the Source Code**

1. Install latest Mercurial
1. Setup Mercurial (optional):    
Config file $Hg Install Dir\Mercurial.ini on Windows or <Hg Install Dir>/.hgrc on Unix.    
Enhance the default configuration, using GIT diff and enable [default plugin][1] avoid different line breaks in the source.  

    [ui]
    username = your Name <yourLoginName@odftoolkit.org>
    ;merge = your-merge-program (or internal:merge)
    
    [diff]
    git = 1
    
    [defaults]
    diff=-p -U 8
    
    [extensions]  
    \# Enables mercurial EOL extension for line break handling   
    \# See http://mercurial.selenic.com/wiki/EolExtension (bundled since 1.5.4)  
    eol =
    
    \# It'll remove unknown files and empty directories by default.    
    \# Usually you call 'hg update -C' and 'hg purge' in sequence   
    \# See http://mercurial.selenic.com/wiki/PurgeExtension  (bundled)  
    hgext.purge=
    
    [eol]
    \# Converts mixed line ending within a file to LF (Unix) format     
    \# before adding the file to the source repository    
    \# See http://mercurial.selenic.com/wiki/EolExtension    
    only-consistent = False

1. Get the [ODFDOM source code][2] from the odfdom~developer [Mercurial][3] repository. Please see below for a short introduction to using Mercurial on odftoolkit.org. There's also a more general [help][4] on how to use source control systems on odftoolkit.org. The command   
`hg clone https://odftoolkit.org/hg/odfdom~developer` will download the repository into a new directory.    
Please note that by this you'll get the latest changeset. If you for example want stable release 0.8.6, you may want to use  
`hg clone https://odftoolkit.org/hg/odfdom~developer -r v0.8.6`

ODFDOM Developers however always work on the latest changeset.   
Now get and install [Apache Maven][5]. On command line test your installation with "mvn -v".

If Maven is correctly installed, change into the project directory and build with command "mvn".   


**Set up ODFDOM Development Environment**

***Using Eclipse IDE***

You can also set up your own ODFDOM development environment in Eclipse.

1. Install [Java / JDK 5][6] (you might use [JDK 6][7] if you do not recontribute)
1. Install [Eclipse][8].
1. Open Eclipse, choose "Window->Preferences->Java->Installed JREs", add JDK 5 as a installed JRE, and check it to add to the build path of a java project by default.
1. Install Maven Plugin for Eclipse, see http://m2eclipse.sonatype.org/installing-m2eclipse.html
1. Install Mercurial Plugin for Eclipse, see http://javaforge.com/project/HGE#download
1. Get the source code with Mercurial as described above.  
1. Import ODFDOM Maven project: Choose "File->Import->Maven->Existing Maven project"

***Using Netbeans IDE***     

To establish your own ODFDOM development environment:

1. Install [Java / JDK 5][9] (you might use [JDK 6][10] if you do not recontribute)
1. Install [NetBeans 6.x][11]. In case you are new to Netbeans, there are several nice [tutorials][12] available.
1. Get the source code with Mercurial as described above.
1. Start Netbeans,  choose "File->Open Project.." from the Netbeans menu and select the ODFDOM directory.
As the ODFDOM source bundle comes together with Netbeans project files, ODFDOM opens as a pre-configured project.
You still have the opportunity to work solely with [Maven][13] directly on the command line instead having the IDE GUI comfort provided by Netbeans.

Since Netbeans 6.1 the [Mercurial plugin][14] is part of the IDE, which help you to track the changes being made and ease providing patches. Select in the menu among 'Versioning' the desired Mercurial commands.

After the commitment of your changed files, you need to pull the latest updates from the server, perhaps merge them with your changes and finally push your changes to the repository using in the menu Versioning->Mercurial->Share->Push Other...
and adding `https://myUserName:myPassword@odftoolkit.org/hg/odfdom~developer`
      
   
**Command Line Tools**    

***Mercurial***       
    
[Mercurial][15] is the version control system used for ODFDOM development.

Here a list of the most frequently used commands:
<pre>
  // Get source code into a new local repository
  hg clone https://odftoolkit.org/hg/odfdom~developer [new folder name]
  // See if there are updates
  hg in
  // See if you have local changes
  hg status
  // If there are no local changes: Update
  hg pull
  hg update
  // Display the latest 3 entries of the revision history
  hg log -l 3
  // Display only the latest entry of the revision history
  hg tip
  // Register all locally created or deleted files. 
  // Please carefully check the output of "hg status" first
  hg addremove
  // Safe alternatives
  hg add <myNewFile>
  hg remove <myUnnecessaryFile>
  hg remove -A <myAlreadyDeletedFile>  
  // Commit your changes locally and display their revision number
  hg commit -A -u "your Name" -m "#bug XY# Description of changes"
  hg tip
  // Export your locally committed changes as patch
  hg export -a -g -o ../myChanges.patch <RevisionNumbersOfYourCommit>
  // Import changes into your local repository
  hg import --no-commit ../someFile.patch
  // Get help
  hg help
  // Get help about a special command
  hg help <command>
  hg help export
  hg help addremove
  ...
</pre>
In case you do changes for a bug or feature request, please export them as a patch and attach them to the bug's entry on the [issue list][16]. If there's no such entry, please create one first.

That way others will review your patch for you. If everything is ok, the reviewer will push your changes to the global repository. This procedure is the same for all developers and is meant to keep up code quality.

***Maven***  
[Maven][17] is the build manager used for ODFDOM development.
<pre>
  // Build the project and create target/odfdom.jar
  mvn
  // Workaround: Ignore failed test when building
  mvn -Dmaven.test.failure.ignore=true clean install
  // Create javadoc
  mvn javadoc:javadoc
  // Generate DOM layer elements and attributes from RelaxNG
  mvn clean test -P codegen
  // Generate code coverage documentation in
  // <PROJECT_DIR>/target/site/cobertura/index.html
  // see http://mojo.codehaus.org/cobertura-maven-plugin/
  mvn cobertura:cobertura
</pre>

**Coding Guidelines**
ODFDOM take advantage of the existing [Java Coding Guidelines][18]. 

***Naming Convention***    
Aside of [the Naming Convention of Java Coding Guideline][19], we use an 'm' as prefix for member object variables, e.g. "mParentDocument". 
Note: There should be NO datatype prefix being used as 'i', 's', etc.

***Source Code Format***   
Although Maven may support source code formatting it has not yet been enabled.
Instead the automated formatting via the IDE is used (e.g. by Netbeans via the context menu - Format).  

***Spaces***   
To allow a customized indentation within the IDE based on the user's taste, the indent shall be done using TABs.
For instance, Svante uses a 4 whitespace indent by TABs configured within Netbeans.

***Line Feed***   
There have been recently problems with merging sources using Mercurial when working with different platforms (windows/unix).
For this reason, some additional Mercurial configuration  shall be used to unify the interal line feed handling.


**Current and Future Work**

Especially the convenient layer will grow on demand. As ODFDOM should be the base of many future ODF projects, a high quality is desired. Therefore automatic tests are obligatory for all new sources of the Java reference implementation.

The development is being discussed on the [dev mailing list][20].

While we still plan to deliver quarterly results, we created some large feature groups/headlines for the upcoming versions

In general when building a layered API, it seemed reasonable to start from the
button to base higher APIs on the stable lower layers:


***Version 0.9***

  - Complete ODF 1.2 PKG functionality (package), e.g.   
   - Add PKG validation feature
  - Complete ODF 1.2 DOM functionality, e.g.
   - Style handling refactoring
   - Add DOM validation feature
   - RDF metadata feature
   - Some create child element methods (ie. office:body and style:style element) dependent on attribute value
  - ODF 1.2 DOC API 
   - Create a lean convenient API working on the complete DOM layer, but hiding XML design details of ODF 1.2
  - Complete ODF 1.2 TEST API (largly dependent on DOC API, but should be developed in parallel)

***Version 1.0***   

- PKG performance tweaks
- DOM performance tweaks
- important DOC functionality
- Test coverage

Please see also for [open tasks][21].

**ODFDOM Code Generator**  

The ODFDOM Code Generator is used to generate the core Classes for ODFDOM which are a typed mapping of the ODF elements on real Java Classes. For the future we also plan to generate ODFDOM e.g. C# ( .NET ) for other programming languages with this generator. Take a look at this page [ODFDOM Code Generator][22] to see how the generator works in general (will be updated soon). We are moving toward treating the code generator as a separate component. The repository [http://odftoolkit.org/hg/odfdom~relaxng2template][23] is the new home of the code generator. It may move to its own project on this site at some point.


**Other Useful Pages**     


  - [Class Structure in ODFDOM][24]  
  - [Convenience Layer Design Ideas][25]


  [1]: http://mercurial.selenic.com/wiki/Win32TextExtension
  [2]: http://odftoolkit.org/projects/odfdom/sources
  [3]: http://www.selenic.com/mercurial/wiki/
  [4]: SourceControl.html
  [5]: http://maven.apache.org/
  [6]: http://java.sun.com/javase/downloads/index_jdk5.jsp
  [7]: http://java.sun.com/javase/downloads/index.jsp
  [8]: http://www.eclipse.org
  [9]: http://java.sun.com/javase/downloads/index_jdk5.jsp
  [10]: http://java.sun.com/javase/downloads/index.jsp
  [11]: http://netbeans.org
  [12]: http://www.netbeans.org/kb/trails/platform.html
  [13]: http://maven.apache.org/
  [14]: http://wiki.netbeans.org/MercurialVersionControl
  [15]: http://mercurial.selenic.com/wiki/
  [16]: http://odftoolkit.org/bugzilla/buglist.cgi?product=odfdom&order=bugs.bug_id
  [17]: http://maven.apache.org/
  [18]: http://java.sun.com/docs/codeconv/html/CodeConvTOC.doc.html
  [19]: http://java.sun.com/docs/codeconv/html/CodeConventions.doc8.html#367
  [20]: mailto:odf-dev@incubator.apache.org
  [21]: https://odftoolkit.org/bugzilla/buglist.cgi?query_format=advanced&short_desc_type=allwordssubstr&short_desc=&product=odfdom&long_desc_type=allwordssubstr&long_desc=&bug_file_loc_type=allwordssubstr&bug_file_loc=&status_whiteboard_type=allwordssubstr&status_whiteboard=&bug_status=UNCONFIRMED&bug_status=NEW&bug_status=ASSIGNED&bug_status=REOPENED&emailtype1=substring&email1=&emailtype2=substring&email2=&bugidtype=include&bug_id=&votes=&chfieldfrom=&chfieldto=Now&chfieldvalue=&cmdtype=doit&order=Reuse+same+sort+as+last+time&field0-0-0=noop&type0-0-0=noop&value0-0-0=
  [22]: ODFDOM-Code-Generator.html
  [23]: http://odftoolkit.org/hg/odfdom~relaxng2template
  [24]: ODFDOM-Class-Structure.html
  [25]: ConvenienceLayerDesignIdeas.html