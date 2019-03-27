Title: Release Guide
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

I. Prerequisites
-------------------
 1. You should read the document:[A Guide To Release Management During Incubation (DRAFT)][1].
 2. You must have shell access to people.apache.org, such as [PuTTY][2].
 3. Release manager must have his public key appended to the KEYS file checked in to SVN. Make sure that a current version is available in the dist folder. The key should be published on one of the public key servers. Also, it should be added to the profile page via id.apache.org.
    ODF Toolkit project KEYS file: [https://svn.apache.org/repos/asf/incubator/odf/trunk/KEYS][3].
    The sign tool, such as [GnuPG][4] should be installed. More info can be found here: [http://www.apache.org/dev/release-signing.html][5].
 4. You must have [Oracle JDK 1.8][6] or [Open JDK][7].
 5. [Subversion client][8] is installed. We suggest you install version 1.9.3 or later.
 6. The ODF Toolkit build system requires [Maven][9] to perform a build. We suggest you install version 3.2.5 or later. Make sure that you have set up your Maven installation according to the guide on [Publishing Maven Artifacts][15]
 7. All mail adresses the release manager is using should be added to the profile via id.apache.org

II. Legal Audit
-------------------
 Legal policy and interpretation changes from time to time so it is worth investing a little time reading again the legal release material.

 1. Check that dependencies (and in particular those dependencies that ship in the packages) comply with Apache policy. Apache projects may distribute artifacts and documents as part of a release which are not Apache Licensed. All such artifacts must comply with [Apache's 3rd party licensing policy][10].
    All the licenses on all the files to be included within a package should be included in the LICENSE document.
 2. The NOTICE document is for additional copyright and attribution statements those licenses may require. A typical NOTICE document at a minimum includes a copyright and attribution statement for The Apache Software Foundation. Nothing else belongs in the NOTICE document.
 3. It is good practice to check the provenance of any source documents which do not have license headers. With the help of RAT, you can run Maven command: 'mvn verify -Ppedantic' on the "trunk" to get a report about the license header.
 4. Make sure every committer have a signed [Contributor License Agreement (CLA)][11] on file. It's a contract between a contributor and Apache granting Apache rights to code contributed.
 5. If any dependencies are cryptographic libraries then it may be necessary to fill in some [paperwork][12] to make sure they are compliance with US export regulations.

III. Release Document
-------------------------
Documents a particular release of a product rather than the product itself. For a release manager, the first thing he should do is to consult and correct the project's release documentation. For ODF Toolkit, we should make sure the following items are ready.

 1. Ensure the CHANGES.txt is up to date.
 2. The Cookbook was updated to support the new release, add new content and modify the old parts.
 3. Make sure all of the demo codes work well under the new release. If necessary, write new demo to introduce the key new features in the new version.
 4. Remember to update the online Javadoc to the newest version, but don't publish until the vote pass.
 5. Prepare other website update content, save them as staged, but don't publish until the vote pass.

IV. Release Candidate
--------------------------------
A release candidate is a set of artifacts upon which a vote is held for a release. The actual nature of the release candidate depends on the release system adopted by a the project. For ODF Toolkit, we supply 3 type of artifacts, binary package, source package and document package. The release candidate jars are deployed to a Maven staging repository. Making release artefacts should follow the following steps:

 1. Make sure that your working copy is clean

    Preferrably check out a clean version from https://svn.apache.org/repos/asf/incubator/odf/trunk

 2. Prepare the release

        mvn release:prepare

    If you haven't set up your environment for automatic svn access you need to pass in username and password:

        mvn release:prepare -Dusername=APACHE_ID -Dpassword=PASSWORD

    This will start the interactive release preparation. You need to supply Maven with the versions of the release. "release version" should be the version of the intended release (recommended value should be fine). "SCM release tag or label" should include the RC version (e.g. odftoolkit-0.6.1-incubating-RC3). "new development version" should be the current SNAPSHOT version (don't use the recommended version, e.g. when building 0.6.1-incubating this should be 0.6.1-incubating-SNAPSHOT, not 0.6.2-incubating-SNAPSHOT).

    This will update the versions of all modules and create an svn tag. The version should always include incubating (or incubator) to ensure that the artifacts created comply with [Incubator release policy][13].

 3. Perform the release

        mvn release:perform

    This will checkout the tag to the target directory, build and package everything and upload the artifacts to the staging repository.

 4. Close the staging repo.

    Log in to [the Nexus repository manager][20], click on Staging Repositories, select the current ODFToolkit staging repo and click on close.

    You can test the staging repository by adding a snippet like this to your Maven settings (the url to the staged repo can be obtained from the mail you got when closing the repo):

            <profile>
                <id>odfstage</id>
                <activation>
                    <activeByDefault>true</activeByDefault>
                </activation>
                <repositories>
                    <repository>
                        <id>staged-releases</id>
                        <url>https://repository.apache.org/content/repositories/orgapacheodftoolkit-127/</url>
                    </repository>
                </repositories>
            </profile>

 5. Package download artifacts

    Switch to the checkout directory that has been created by the release: cd target/checkout. Use the following command to package (replace &lt;YOUR APACHEID&gt; with your apache id, e.g. devinhan):

        mvn install -Prelease-distribution -pl=. -Duser.name=&lt;YOUR APACHEID&gt;

    After package there should be a folder release/$TAG in the "target\"

    vote.txt is a draft of the dev vote mail. Each package has its own .asc, .md5 and .sha files.

 6. Upload artifacts for vote.
    Login people.apache.org using your Apache LDAP account. Copy the folder release/$TAG to directory "/public_html/odftoolkit-release/$TAG" using [scp][14]. If the directory does't exist, please create. It is best to scp into the home directory and then copy into position from there.
    Make sure all of the files are owned by the incubator group, group readable and writable, and read only for the world. In short -rw-rw-r--. To do this from the top level:

          > find . -type f -exec chmod 664 {} \;
          > find . -type d -exec chmod 775 {} \;
          > chgrp -R incubator *

    Then the artifacts can be accessed from Web browser. The link looks like:

      http://people.apache.org/~$USER/odftoolkit-release/$TAG

    Replace $USER with your Apache ID, such as "devinhan".


V. Incubator Release Vote
-----------------------------
After release candidate is uploaded, the release manager can start vote process. All releases by podlings must be approved by the Incubator PMC. The conventional process is for the podling to follow the usual Apache process release vote and then call for a Incubator PMC VOTE on the general incubator list.

1. Vote in ODF Toolkit Development List
A formal vote on candidate artifacts must be conducted by the release manager on the ODF Toolkit development list. The vote mail can be drafted based on the vote.txt, which is generated with artifacts. A sample looks like this:

       Subject: [VOTE] Release Apache ODF Toolkit 0.5-incubating(RC6)
            To: odf-dev@incubator.apache.org
       Hi all,
       Please vote on releasing the following candidate as Apache ODF Toolkit (incubating) version 0.5.
       This will be the first incubator release for ODF Toolkit in Apache.

       This release candidate fixes the following issues found in RC5:
       (1) Reomve JUnit declaration from NOTICE & LICENSE files.
       (2) Fix the "mvn clean" failed issue.

       The candidate for the ODF Toolkit 0.5-incubating release is available at:
       http://people.apache.org/~devinhan/odftoolkit-release/odftoolkit-0.5-incubating-rc6/

       The release candidate is a zip archive of the sources in:
       https://svn.apache.org/repos/asf/incubator/odf/tags/odftoolkit-0.5-incubating/

       The SHA1 checksum of the archive is 44e16d8ee39fc0426f96aa7da9cd8eefccaf78e5.
       The MD5 checksum of the archive is c12a87f0ca441f23d945a423e1d4127c.

       Besides source code, binary packages and javadoc packages are also listed in:
       http://people.apache.org/~devinhan/odftoolkit-release/odftoolkit-0.5-incubating-rc6/

       All of the artifacts supply three package formats, tar.gz, tar.bz2 and zip.

       Keys:
         http://www.apache.org/dist/incubator/odftoolkit/KEYS

       Please vote on releasing this package as Apache ODF Toolkit 0.5-incubating.
       The vote is open for the next full week, until next Saturday, Dec 31rd 6pm, because of the Christmas holiday, and passes
       if a majority of at least 3 +1 IPMC votes are cast.
       [ ] +1 Release this package as Apache ODF Toolkit 0.5-incubating
       [ ] -1 Do not release this package because...
       To learn more about Apache ODF Toolkit, please access: http://incubator.apache.org/odftoolkit/.

All votes are welcome, but only those votes by the project's PPMC members (or IPMC members) count towards the final tally. The voting period should be no less than 72 hours, and at the end of the voting the release manager should post a final tally to the list. eg.

      Subject: [RESULT][VOTE] Release Apache ODF Toolkit 0.5-incubating-rc7
          To: odf-dev@incubator.apache.org

      Hi All,
      The RC7 ballot has ended (due to New Year Holiday, we extended the vote to this Monday ) with the following results:

      From PPMC members:
      +1 daisyguo
      +1 devinhan
      +1 robweir
      +1 svanteschubert

      From Mentors:
      +1 yegor(IPMC)

      Other votes:
       (abstain non-binding [;<) orcmid

      I will send a vote mail to incubator-general mail list and collect another 2 IPMC +1's votes.
      Thanks all of the voters.

      The vote passes if there are at least 3 +1's from the PPMC/IPMC members and more +1's than -1's. This is what is meant by majority consensus. If you've gotten this far, congratulations, you're nearly ready to upload and announce the release.
      If the vote is failed, the release manager should improve the artifacts based on the comments and prepare new candidate. That means step "III. Release Candidate" need to rework. This process maybe repeat several times until the vote passes.
      **NOTE:** Please remember to delete old release candidates from Apache home space after new candidates are uploaded.

 2. Vote in General Incubator List
In the case of the incubator, the IPMC must approve all releases. That means there is an additional bit of voting that the release manager must now oversee on general@incubator in order to gain that approval. The release manager must inform general@incubator that the vote has passed on the project's development list, and should indicate any IPMC votes gained during that process. A new vote on the release candidate artifacts must now be held on general@incubator to seek majority consensus from the IPMC. Previous IPMC votes issued on the project's development list count towards that goal. The sample mail is:

      Subject: [VOTE] Release Apache ODF Toolkit 0.5-incubating(RC7)
           To: general@incubator.apache.org
           Cc: odf-dev@incubator.apache.org
          Hi all,

          The ODF Toolkit 0.5 is ready for release.  This will be our first incubator release.
          We had a preliminary vote in the PPMC, which had great results, including a +1 from our mentor, Yegor.

          The PPMC vote result thread is here:
          http://markmail.org/message/tw3juzkak6kdiod2
          The vote thread is here:
          http://markmail.org/message/h6qfmhl4vulyjyhw

          We need two more IPMC votes to pass.

          Please vote on releasing the following candidate RC7 as Apache ODF Toolkit (incubating) version 0.5.

          This release candidate fixes the pom.xml file inconsistant issue found in RC6. Thanks Yegor!

          The candidate for the ODF Toolkit 0.5-incubating release is available at:
          http://people.apache.org/~devinhan/odftoolkit-release/odftoolkit-0.5-incubating-rc7/


          The release candidate is a zip archive of the sources in:
          https://svn.apache.org/repos/asf/incubator/odf/tags/odftoolkit-0.5-incubating/


          The SHA1 checksum of the zip archive is 4e97a1a79291035d590b5578caf79478dc3f6de8.
          The MD5 checksum of the zip archive is 8883f036ee34282077d3c175329f6257.

          Besides source code, binary packages and javadoc packages are also listed in:
          http://people.apache.org/~devinhan/odftoolkit-release/odftoolkit-0.5-incubating-rc7/

          All of the artifacts supply three package formats, tar.gz, tar.bz2 and zip.

          Keys:
          http://www.apache.org/dist/incubator/odftoolkit/KEYS

          The vote is open for 72 hours, or until we get the needed number of votes (3 +1).

            [ ] +1 Release this package as Apache ODF Toolkit 0.5-incubating
            [ ] -1 Do not release this package because...

          To learn more about Apache ODF Toolkit, please access http://incubator.apache.org/odftoolkit/.

   Once the 72-hour minimum voting period has ended on general@incubator, the release manager should tally the votes and declare a result. If majority consensus has been achieved with respect to IPMC votes, the release manager may proceed with the release. Otherwise, the release manager need to rework step(III) and step(IV) based on the received comments.
   The vote result mail looks like:

          Subject: [VOTE] Release Apache ODF Toolkit 0.5-incubating(RC7)
               To: general@incubator.apache.org, odf-dev@incubator.apache.org
          Hi all,

          The Apache ODF Toolkit 0.5-incubating RC7 ballot has ended. We have received 4 IPMC +1 votes (plus an additional 4 PPMC +1   votes)
          during the release voting on dev and general. The vote passed!

          Results:

          From IPMC members:
                   name                     apache id
                +1 Yegor Kozlov(mentor)       yegor
                +1 Nick Burch(mentor)         nick
                +1 Chris Mattmann             mattmann
                +1 Christian Grobmeier        grobmeier

          From PPMC members:
                   name                      apache id
                +1  Ying Chung Guo              daisyguo
                +1  Biao Han                    devinhan
                +1  Rob Weir                    robweir
                +1  Svante Schubert             svanteschubert

          Other votes:
                                            name                       apache id
                (abstain non-binding [;<)   Dennis E. Hamilton        orcmid

           We will work on releasing ODF Toolkit 0.5. Thank you everyone who worked in this release!

VI. After the Vote
------------------------------
1. Distributing Releases
   The distribution upload location (www.apache.org/dist) for all Apache projects is the /www/www.apache.org/dist directory on people.apache.org. Each project (including the Incubator) owns a directory within dist.
   The directory of ODF Toolkit is http://www.apache.org/dist/incubator/odftoolkit/. The release manager should move the release artifacts from /public_html/odftoolkit-release/$TAG to this directory.

2. Mirroring
   To avoid excessive use of bandwidth and to increase download speeds, official releases are made available through a global network of volunteer mirrors. Using these mirrors has some notable differences from unmirrored downloads. In particular, a <a href="http://www.apache.org/dev/release-download-pages.html" script</a  must be used to direct the download to an appropriate URL. The mdtext format sample is:

     \*\*Current Version (0.5-incubating)**
     \* Source:
     \* \[odftoolkit-0.5-incubating-src.tar.gz\](http://www.apache.org/dyn/closer.cgi/incubator/odftoolkit/sources/odftoolkit-0.5-incubating-src.tar.gz)
       \[\[asc\](http://www.apache.org/dist/incubator/odftoolkit/sources/odftoolkit-0.5-incubating-src.tar.gz.asc)\]
       \[\[md5\](http://www.apache.org/dist/incubator/odftoolkit/sources/odftoolkit-0.5-incubating-src.tar.gz.md5)\]
       \[\[sha\](http://www.apache.org/dist/incubator/odftoolkit/sources/odftoolkit-0.5-incubating-src.tar.gz.sha)\]

   Users will download the mirrored release artifacts from machines outside Apache control. Users need to verify that the copy downloaded is identical to the original. Mirrored copies of checksums, KEYS and signature files (.asc and .md5 files) will be present on the mirrors but must never be used for verification. So, all links from the podling website to signatures, sums and KEYS need to refer to the original documents on www.apache.org. See release signing guide for more information.

3. Archiving
   All Apache releases form an important part of the history of a project. They are therefore archived with the aim of preserving them indefinitely for future reference. All artifacts within www.apache.org/dist will be automatically archived to http://archive.apache.org/dist. When a new artifact is uploaded, it will be sync'd to the archive. The sync'ing is scheduled to operate several times a day. So it may be some hours before an added artifact is archived. When an (archived) artifact is deleted from the live distribution, it will remain in the archives.
   Please remember that these archives are served from Apache bandwidth. Anyone who wants to obtain a large quantity of data from the archives should contact the Infrastructure Team.

4. Publishing Maven Artifacts
   Log in to [the Nexus repository manager][20] and release the staging repository.

5. Copy the SVN tag to the release version

        svn cp https://svn.apache.org/repos/asf/incubator/odf/tags/odftoolkit-0.XX-incubating-RCYY https://svn.apache.org/repos/asf/incubator/odf/tags/odftoolkit-0.XX-incubating/

6. Publish Document
   Publish all of the prepared document on the website and update the download page.

7. Send Announcements
   The release manager need to send announcements to odf-user and odf-dev lists as well as announce@apache.org, general@incubator.apache.org, dev@openoffice.apache.org. Note, announcements should be sent from your @apache.org e-mail address. The sample looks like:

   Subject:[ANNOUNCEMENT] Apache ODF Toolkit(Incubating) 0.5-incubating Release
   To: odf-users@incubator.apache.org
   Hi all,

   The Apache ODF Toolkit(Incubating) team is pleased to announce the release of 0.5-incubating. This is our first Apache release.

   The Apache ODF Toolkit is a set of Java modules that allow programmatic creation, scanning and manipulation of Open Document Format (ISO/IEC 26300 == ODF) documents. Unlike other approaches which rely on runtime manipulation of heavy-weight editors via an automation interface, the ODF Toolkit is lightweight and ideal for server use.

   A full list of changes is available in the change log[1]. People interested should also follow the mail list[2] to track progress.

   The ODF Toolkit source release as well as the pre-built binary deployment packages are listed in the downloads page[3]. Pre-built versions of all ODF Toolkit components are available in the central Maven repository under Group ID "org.apache.odftoolkit" and Version "0.5-incubating".

   \[1] http://www.apache.org/dist/incubator/odftoolkit/CHANGES-0.5-incubating.txt.
   \[2] http://incubator.apache.org/odftoolkit/mailing-lists.html.
   \[3] http://incubator.apache.org/odftoolkit/downloads.html


References
-------------------------
1. [A Guide To Release Management During Incubation (DRAFT)][16]
2. [Apache Release Management][17]
3. [POI Release Guide][18]
4. [POI Release Checklist][19]



  [1]: http://incubator.apache.org/guides/releasemanagement.html
  [2]: http://www.putty.org/
  [3]: https://svn.apache.org/repos/asf/incubator/odf/trunk/KEYS
  [4]: www.gnupg.org
  [5]: http://www.apache.org/dev/release-signing.html
  [6]: http://www.oracle.com/technetwork/java/javase/downloads/index.html
  [7]: http://openjdk.java.net/
  [8]: http://subversion.apache.org/
  [9]: http://maven.apache.org/
  [10]: http://www.apache.org/legal/resolved.html
  [11]: http://www.apache.org/licenses/#clas
  [12]: http://www.apache.org/dev/crypto.html
  [13]: http://incubator.apache.org/incubation/Incubation_Policy.html#Releases
  [14]: http://www.apache.org/dev/user-ssh.html
  [15]: http://www.apache.org/dev/publishing-maven-artifacts.html
  [16]: http://incubator.apache.org/guides/releasemanagement.html
  [17]: http://www.apache.org/dev/#releases
  [18]: https://svn.apache.org/repos/asf/poi/branches/ooxml/src/documentation/release-guide.txt
  [19]: https://svn.apache.org/repos/asf/poi/branches/ooxml/src/documentation/Release-Checklist.txt
  [20]: https://repository.apache.org/index.html
