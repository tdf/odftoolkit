Title: Release Process
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

###Incubator Release Steps

1. Environment setup for releasing artifacts (same for SNAPSHOTs and releases) -
  1. Increase the default Java heap available to Maven (required for Java SE 6)  

        export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m"
  2. Use the latest Sun 1.6.0 JDK
  3. Use Maven 3.0.3 or later
  4. Make sure the [Release Setup](release-setup.html) steps have been performed.

2. Prepare the source for release:     
     1. Cleanup JIRA so the Fix Version in issues resolved since the last release includes this release version correctly. Also, transition any Resolved issues to the Closed state.
     2. Update the text files in a working copy of the project root -
         1. Update the CHANGELOG based on the Text release reports from JIRA.
         2. Review and update README.txt if needed.
         3. Commit any changes back to svn 
     3. Stage any Roadmap or Release landing pages on the site.

3. Checkout a clean copy of the trunk to release using command line svn.   

   *Do not use Eclipse to do the checkout. The extra dot (.) files created by Eclipse throws off the rat:check processing.*

4. Use `mvn -P pedantic verify` to verify the source has the required headers before trying to release.

5. Do a dry run of the release:prepare step:  
       
        $ mvn -Papache-release release:prepare -DdryRun=true

   The dry run will not commit any changes back to SVN and gives you the opportunity to verify that the release process will complete as expected. You will be prompted for the following information :
      * Release version - take the default 
      * SCM release tag - *DO NOT TAKE THE DEFAULT*  -: : 0.1-incubating
      * New development version - take the default
      * GPG Passprhase  

  *If you cancel a release:prepare before it updates the pom.xml versions, then use the release:clean goal to just remove the extra files that were created.*

6. Verify that the release process completed as expected
    1. The release plugin will create pom.xml.tag files which contain the changes that would have been committed to SVN. The only differences between pom.xml.tag and it's corresponding pom.xml file should be the version number.
    2. If other formatting changes have been made you should review the changes and then commit them -   

            $ svn commit -m "fixing formatting for release"
    3. Check release.properties and make sure that the scm properties have the right version. Sometimes the scm location can be the previous version not the next version.
    4. Verify signatures ([Verifying release signatures](http://incubator.apache.org/rave/release-process.html#verify_signatures))

7. Once any failures or required updates have been committed to svn, rollback the release prepare files:  

        $ mvn -Papache-release release:rollback

8. Prepare the release
   1. Run the "release:prepare" step for real this time. You'll be prompted for the same version information.
	
          $ mvn release:prepare -Papache-release -DpreparationGoals="clean install cargo:package"

   2. Backup (zip or tar) your local release candidate directory in case you need to rollback the release after the next step is performed.

9. Perform the release

  This step will create a maven staging repository and site for use in testing and voting. 

        $ mvn release:perform -Papache-release -Duser.name=<your_apache_uid>
   
    *If your local OS userid doesn't match your Apache userid, then you'll have to also override the value provided by the OS to Maven for the site-deploy step to work. This is known to work for Linux, but not for Mac and unknown for Windows.*
    *The maven-release-plugin is configured with goals "deploy site site:deploy" and will deploy the site files to a staging-site directory on people.apache.org.*
10. Verify the release artifacts
   1. Verify the HTML links in site are correct
   2. Verify the staged artifacts in the nexus repo          
           1. https://repository.apache.org/index.html
           2. Enterprise --> Staging
           3. Staging tab --> Name column --> org.apache.rave
           4. Navigate through the artifact tree and make sure that all javadoc, sources, tests, jars, ... have .asc (GPG signature) and .md5 files. See http://people.apache.org/~henkp/repo/faq.html and http://www.apache.org/dev/release-signing.html#openpgp-ascii-detach-sig
   3. Close the nexus staging repo
     1. https://repository.apache.org/index.html
     2. Enterprise --> Staging
     3. Staging tab --> Name column --> org.apache.rave
     4. Right click on the open staging repo (org.apache.rave-XXX) and select Close.
11. Put the release candidate up for a vote
     1. Create a VOTE email thread on rave-dev@ to record votes as replies, like [this](release-vote.txt)
     2. Create a DISCUSS email thread on rave-dev@ for any vote questions, [this](release-discuss.txt)
     3. Perform a review of the release and cast your vote. See the following for more details on Apache releases 

     [http://www.apache.org/dev/release.html](http://www.apache.org/dev/release.html)  
     [http://incubator.apache.org/guides/releasemanagement.html](http://incubator.apache.org/guides/releasemanagement.html)

     4. A -1 vote does not necessarily mean that the vote must be redone, however it is usually a good idea to rollback the release if a -1 vote is received. See - Recovering from a vetoed release
     5. After the vote has been open for at least 72 hours, has at least three +1 PMC votes and no -1 votes, then post the results to the vote thread by -
         1. reply to the initial email and prepend to the original subject -

         [RESULTS]

         2. Include a list of everyone who voted +1, 0 or -1.

12. Put the release candidate up for an Incubator PMC vote
    1. Create a VOTE email thread on general@incubator to record votes as replies, like [this](vote-ipmc.txt)

    2. After the vote has been open for at least 72 hours, has at least three +1 Incubator PMC votes and no -1 votes, then post the results to the vote thread by -
        1. replying to the initial email and prepend to the original subject -

         [RESULTS]

        2. Include a list of everyone who voted +1, 0 or -1.

13. Finalizing a release
   1. Promote the staged nexus artifacts -
       1. https://repository.apache.org/index.html
       2. Enterprise --> Staging
       3. Staging tab --> Name column --> org.apache.rave
       4. Right click on the closed staging repo (org.apache.rave-XXX) and select Promote.

    2. Copy the distribution artifacts over to the distribution area

          $ ssh ${user.name}@people.apache.org  
          $ mkdir /www/www.apache.org/dist/incubator/rave/${project.version}   
          $ cd /www/www.apache.org/dist/incubator/rave/${project.version}   
          $ wget https://repository.apache.org/content/repositories/releases/org/apache/rave/rave-parent/${project.version}/rave-parent-${project.version}-source-release.zip    
          $ wget https://repository.apache.org/content/repositories/releases/org/apache/rave/rave-parent/${project.version}/rave-parent-${project.version}-source-release.zip.asc   
          $ wget https://repository.apache.org/content/repositories/releases/org/apache/rave/rave-parent/${project.version}/rave-parent-${project.version}-source-release.zip.md5   
          $ wget https://repository.apache.org/content/repositories/releases/org/apache/rave/rave-parent/${project.version}/rave-parent-${project.version}-source-release.zip.sha1    

     **Make sure all the copied files have g+rw set and only o+r set**

          $ find . -user ${user.name} -type f | xargs chmod 664
          $ find . -user ${user.name} -type d | xargs chmod 775

     *Note: All of the artifacts are in the maven repos, but we may create an assembly to publish in future releases*
    
    3. Publish the staged website 

14. Update the JIRA versions page to mark the version as "released", and set the date to the date that the release was approved. You may also need to make a new release entry for the next release.
15. Announcing the release
   1. After the mirrors have had time to update (24 hours to be on the safe side) update the wiki with pointers to the new release
   2. Make a news announcement on the Rave homepage.
   3. Make an announcement about the release on the rave-users@incubator.apache.org, rave-dev@incubator.apache.org, general@incubator.apache.org, and announce@apache.org list as per the Apache Announcement Mailing Lists page)


####Recovering from a vetoed release

1. Reply to the initial vote email and prepend to the original subject -

     [CANCELED]

2. Rollback the version upgrades in trunk by either -
    1. Restore the 0.1-rc1.tar.gz and run
    
        $ mvn -Papache-release release:rollback

    2. Manually revert the versions in trunk to the prior version and commit

3. Delete the svn tag created by the release:perform step -

       $ svn del https://svn.apache.org/repos/asf/incubator/rave/tags/0.1-incubating -m "deleting tag from rolled back release"

4. Drop the nexus staging repo
    1. https://repository.apache.org/index.html
    2. Enterprise --> Staging
    3. Staging tab --> Name column --> org.apache.rave
    4. Right click on the closed staging repo (org.apache.rave-XXX) and select Drop.

5. Remote the staged site

6. Make the required updates that caused the vote to be canceled
7. Spin another release candidate!


####Verifying release signatures
On unix platforms the following command can be executed -

      for file in `find . -type f -iname '*.asc'`
      do
          gpg --verify ${file} 
      done

You'll need to look at the output to ensure it contains only good signatures -

gpg: Good signature from ...
gpg: Signature made ...
