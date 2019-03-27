Title:     How to edit the ODF Toolkit website
Notice:    Licensed to the Apache Software Foundation (ASF) under one
           or more contributor license agreements.  See the NOTICE file
           distributed with this work for additional information
           regarding copyright ownership.  The ASF licenses this file
           to you under the Apache License, Version 2.0 (the
           "License"); you may not use this file except in compliance
           with the License.  You may obtain a copy of the License at
           .
             http://www.apache.org/licenses/LICENSE-2.0
           .
           Unless required by applicable law or agreed to in writing,
           software distributed under the License is distributed on an
           "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
           KIND, either express or implied.  See the License for the
           specific language governing permissions and limitations
           under the License.

This documentation shows how to edit the Apache ODF Toolkit
website. This includes creation of new pages, modification, and deletion - for files
and sub-directories.

## How it works in general

The website is hosted in a Content Management System (CMS). When you edit a web page
there are potentially four different versions of it to think about:

 1. There is the latest version of the page source stored in subversion.
 1. There is your working copy of that page source, which you are editing.
 1. There is the generated HTML from that source, in the staging directory.
 1. There is the production version of the HTML, which is what the public sees.


The general flow for updating the website is:

 1. You check out the latest version of the web page's source.
 1. You edit the page source using [Markdown text syntax][1] (mdtext).
 1. You commit the source into the repository.  (You will see the SVN commit mail with
    your log message.)
 1. The commit automatically triggers a build that converts the markdown files 
    into HTML files in the staging directory.  (You will see a commit mail with "Staging
    update by buildbot" as log message.)
 1. You verify that the staged webpage is correct and then tell the CMS to publish 
    the changes to the production directory.  (You will see a commit mail with "Publishing
    merge to odftoolkit site by <your Apache ID>" as log message.)

The intent of this workflow is to allow committers flexibility in changing 
webpages and testing changes on a staging website, before moving these changes to
the public web site.

## Command line editing workflow

It is assumed that you have already checked out the code from the SVN repository
(https://svn.apache.org/repos/asf/incubator/odf).

Edit the file via your favorite editor like vi and finally commit via SVN:

     vi myfile.mdtext
     svn ci -m"My log message" myfile.mdtext

Now wait a few seconds so that the entire website can be rebuilt.
Verify the staged webpage in a web browser at:

     http://odftoolkit.staging.apache.org/odftoolkit/myfile.html

 Then publish the site:

    curl -sL http://s.apache.org/cms-cli | perl

If this does not work on your local machine just do it on "people.apache.org" by
executing this:

    ssh -t <user>@people.apache.org publish.pl odftoolkit <your Apache ID>

## Browser-based editing workflow

### Prerequisites

#### Browser bookmark

Normally you see the webpage in your browser. To update this page with an
inline-editor and little preview you use the [Apache CMS] [3] JavaScript bookmarklet.  Drag that 
link to your browser's toolbar.  For more information see [here][2].

#### Apache ID

Furthermore, you need an Apache ID to authenticate to the system.

### Starting

Browse to the webpage or directory you would like to edit and click on
the bookmarklet. Now click on the [Edit this directory] link on the top. The following
is displayed as content from the CMS.

### Edit an existing webpage

Click on the actions link [Edit] in the appropriate table cell for editing the
current file. If the file is not in this directory but in a subdirectory, just click on
the directory name (e.g., "docs/") to enter this directory.

Now you should see a new webpage with four fields:

 1. Upper left is the inline editor for text in markdown syntax.
 1. Upper right shows the written text as a permanent preview or as HTML source code.
 1. Below the editor you can enter a log message.
 1. Enter a general header text, e.g., the license of the entered text.

When you have finished writing your text, check the "Quick Commit" box, enter a
commit message and click on [Submit].  This commits your changes to the Subversion 
repository.

### Verifying the staged web site

As noted above, committing your changes triggers a build of the markdown files
into HTML in the staging directory.  Wait a few
seconds and then click the [Staged] link to view your web page as staged.

At this point you should test your web page.  Does it look right?  Any spelling 
errors? Do the links work?  If you've made substantial changes, perhaps test in
more than one browser.

Once you are satisfied that the page is correct, you are ready to publish it
to the production site.

### Publishing to the production directory

After you have edited your files and/or directories, you need now to publish your
modifications into production. For this please click on the link
[Publish site] on the top of the webpage. Enter a commit message on click 
on [Submit].  After a few seconds you can check the [Production] link to see the
live version of your changes.  


### Create a new webpage

When in the correct directory, at the top enter a name for the new text file (e.g.,
"my-file.mdtext"). Now you enter your text (see topic above).

### Create new subdirectory

When in the correct directory, at the top enter a name for the new directory (e.g.,
"my-dir/"). Make sure you entered a trailing "/"!

### Delete a webpage or subdirectory

When in the correct directory, click on the actions link [Delete] in the appropriate
table cell and enter a commit message. Finally click on [Submit].  Generally speaking
it is always a good idea to use the CMS to delete files and directories over using
the command-line svn interface, as the CMS will ensure everything gets deleted from
the staging (and eventually production) repository on commit.  If you use the command-
line interface instead, you will also need to manually delete the corresponding entities
in the staging repository in order for those changes to propagate to the production site.


## Links

[CMS Documentation Reference][4]

  [1]:  http://daringfireball.net/projects/markdown/syntax
  [2]:  https://cms.apache.org/#bookmark
  [3]:  javascript:void(location.href='https://cms.apache.org/redirect?uri='+escape(location.href))
  [4]:  http://www.apache.org/dev/cmsref.html
