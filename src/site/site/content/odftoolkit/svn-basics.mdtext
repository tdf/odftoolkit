Title: Subversion Basics
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

We use [Apache Subversion][1] for version control. For a complete reference on Subversion see the [Subversion Book][2]. You can [browse our repository][3] in your web browser.

This page gives instructions on performing basic development tasks using the Subversion Command-Line Client. This instruction assumes you have Apache Subversion installed.

* [Overview](#overview) 
* [Sub-commands and Abbreviations](#sub-commands_and_abbreviations) 
* [Client Configuration](#client_configuration)
* [Repository Layout](#repository_layout)
* [Getting the source code](#getting_the_source_code)
* [Basic Work Cycle](#basic_work_cycle)
* [Committing Changes By Others](#committing_changes_by_others)
* [Creating and Submitting Patches](#creating_and_submitting_patches)
* [Further Information](#further_information)

## Overview

You begin using Subversion by copying a directory from a remote repository to a local directory on your file system. This is known as a checkout of a working copy.

Subversion uses a copy-modify-merge model meaning that you can add and edit files and directories in your working copy like any other files on your system, but you should use subversion commands for everything else such as `svn copy` and `svn move` instead of the operating system commands.

## Sub-commands and Abbreviations

Subversion commands can be run from a command shell such as Bash on Linux. The subversion client command is `svn` followed by optional sub-commands, options, and arguments.

Show the program version and modules

    $ svn --version

Run a sub-command

    $ svn <subcommand> [options] [args]

Most sub-commands take file and/or directory arguments, recursing on the directories. If no arguments are supplied to such a command, it recurses on the current directory (inclusive) by default. (from `svn help`)

The following is only a partial list of sub-commands relating to this instruction. For a complete list, see the [Subversion Book][2], or use `svn help`.

* `add` - Schedule a new file or directory (including contained files) for inclusion in the repository
* `checkout`, `co` - Create a local working copy of a remote repository
* `commit`, `ci` - Commit (check in) local changes to the repository
* `copy`, `cp` - Copy one or more files in a working copy or in the repository
* `delete`, `del`, `remove`, `rm` - Items specified are scheduled for deletion upon the next commit. Working copy files not yet committed are deleted immediately.
* `diff`, `di` - Displays differences in files from the directory
* `help`, `?`, `h` - Subversion help and help on sub-commands
* `move`, `mv`, `rename`, `ren` - Moves files or directories in your working copy or repository
* `resolve` - Resolve conflicts on working copy files or directories
* `revert` - Undo all local edits or optionally a file or directory
* `status` - Print the status of working copy files and directories
* `update` - Bring changes from the repository into your working copy

## Client Configuration

Committers need to [configure their Subversion client][6] to handle the differences in line endings of text files on different operating systems.

There are instances where Subversion may need to open an editor. You need to have the environment variable EDITOR set to the editor you would like to use. To set it for the current terminal session in Bash (your path may differ):

    $ export EDITOR=/usr/bin/vim

## Repository Layout

The Odf repository layout uses the following top-level directories `branches`, `site`, `tags`, and `trunk`.

* `branches` - Contains branches used for continued development of a specific version, experimental versions, or for  developing features to be merged into the trunk or a branch later. (needs examples)
* `site` - Contains the web site source code. Also contains it's own trunk directory.
* `tags` - Contains specific versions of the project. These tags are not to be revised. (needs examples)
* `trunk` - Contains the current source code.
For more information see the [Contributors Tech Guide][7].

## Getting the source code

From the parent directory of where you want the working copy. In this example the `odf-trunk` directory will be created if it does not exist.

    $ svn co https://svn.apache.org/repos/asf/incubator/odf/trunk odf-trunk
    A    odf-trunk/tools
    A    odf-trunk/tools/dev
    A    odf-trunk/tools/dev/fetch-all-cws.sh
    A    odf-trunk/tools/dev/cws-list.txt
    A    odf-trunk/tools/dev/fetch-all-web.sh
    A    odf-trunk/tools/dev/web-list.txt
    A    odf-trunk/tools/dev/single-hg.sh
    Checked out revision 1145818.

"A" indicates file or directory is "Added" to working copy

## Basic Work Cycle

* Update your working copy - For this you use the `svn update` command
*  Make changes - For this you may edit files in an editor, or use the `svn add`, `svn delete`, `svn copy`, `svn-move` commands
* Review Changes - For this you use the `svn status` and `svn diff`
* Fix Mistakes - Make additional edits to files or you can use the `svn revert` to restore files or directories to an unmodified state
* Resolve Conflicts - There is a chance others have committed changes while you have been changing your working copy. You should run the `svn update` command to bring your copy up to date. This may create a local conflict where someone may have added a file with a name that you also want to add, or may have made changes to the same line of a file as you. For this use the `svn resolve` command.
* Publish Changes - For this you use the `svn commit` command

### Adding a File

After creating the file "test-file.txt" in the working copy.

    $ svn status
    ?       test-file.txt

? indicates test-file.txt is not under version control

### Scheduling a file for addition to repository

    $ svn add test-file.txt
    A         test-file.txt

    $ svn status
    A       test-file.txt

"A" indicates file is scheduled for addition

### Running a diff

    $ svn diff
    Index: test-file.txt
    ===================================================================
    --- test-file.txt	(revision 0)
    +++ test-file.txt	(revision 0)
    @@ -0,0 +1 @@
    +This is a test file for svn-basics.

    Property changes on: test-file.txt
    ___________________________________________________________________
    Added: svn:eol-style
       + native

### Committing a file

    $ svn commit test-file.txt -m "added test-file.txt"
    Adding         test-file.txt
    Transmitting file data .
    Committed revision 2.

### Update the working copy

    $ svn update
    U    test-file.txt
    Updated to revision 3.

"U" indicates an "Update" to a file or directory

Modify the file (this example uses the vim editor)

    $ vim test-file.txt

### Check the Status

    $ svn status
    M       test-file.txt

"M" indicates the file has been "Modified"

    $ svn diff
    Index: test-file.txt
    ===================================================================
    --- test-file.txt	(revision 3)
    +++ test-file.txt	(working copy)
    @@ -1,2 +1,3 @@
     This is a test file for svn-basics.
     This is a new line added by someone else.
    +This line added by me.

### Resolving Conflicts

Suppose someone edits the same line as you before you commit

    $ svn update
    Conflict discovered in 'test-file.txt'.
    Select: (p) postpone, (df) diff-full, (e) edit,
            (mc) mine-conflict, (tc) theirs-conflict,
            (s) show all options: 

This is just like if you had ran the `svn resolve` command

Selecting `df` displays this:

    --- .svn/text-base/test-file.txt.svn-base	Sun Jul 17 17:38:52 2011
    +++ .svn/tmp/test-file.txt.tmp	Sun Jul 17 21:35:09 2011
    @@ -1,2 +1,7 @@
     This is a test file for svn-basics.
     This is a new line added by someone else.
    +<<<<<<< .mine
    +This line added by me.
    +=======
    +This line is added by someone else also.
    +>>>>>>> .r4
    Select: (p) postpone, (df) diff-full, (e) edit, (r) resolved,
            (mc) mine-conflict, (tc) theirs-conflict,
            (s) show all options:

If you choose `e`, Subversion will launch an editor with both sets of changes included for you to edit. You can save your changes in the editor and then select `r` (for resolved).

    G    test-file.txt
    Updated to revision 4.

"G" indicates "merGed"

### Committing the Changes

Only Committers can commit directly to the repository. The following example shows using your Apache ID and password.

    $ svn commit test-file.txt --username your-name --password your-password \
      -m "added new line"
    Sending        test-file.txt
    Transmitting file data .
    Committed revision 5.

For further information see the [Basic Work Cycle][8] page from [Subversion Book][2].

## Committing Changes By Others

See the [Applying Patches][9] section of the Committer FAQ page.

Example similar to one on Committer FAQ:

    Issue #43835:
    Added some cool new feature.
    Submitted by: John Doe <john.doe.at.null.org>

Using the `-m (--message)` option only allows a single line log message. To commit a multi-line message use the `-F (--file)` option (with a previously created file) or use neither -m or -F and an editor will be started.

## Creating and Submitting Patches

See the [Sending in Patches][10] section on the Contributors Tech Guide page.

Create the patch file from `svn diff` where `your-patch-name.patch` is the full path to the patch file to create.

    svn diff > your-patch-name.patch

## Further Information

For more information see: 

* [Apache Subversion Project][1]
* [Subversion Book][2]
* [Apache Developer Information][11]


[1]: http://subversion.apache.org
[2]: http://svnbook.red-bean.com
[3]: http://svn.apache.org/viewvc/incubator/odf/trunk
[4]: https://svn.apache.org/repos/asf/incubator/odf
[5]: http://www.apache.org/dev/version-control.html
[6]: http://www.apache.org/dev/version-control.html#https-svn-config
[7]: http://www.apache.org/dev/contributors.html#svnbasics
[8]: http://svnbook.red-bean.com/nightly/en/svn.tour.cycle.html
[9]: http://www.apache.org/dev/committers.html#applying-patches
[10]: http://www.apache.org/dev/contributors.html#patches
[11]: http://www.apache.org/dev/
