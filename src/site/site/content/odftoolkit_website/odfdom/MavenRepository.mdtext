Title: Maven Repository
We have an interim maven repository. So far, all that is deployed in it are the artifacts for the code generator.

To use this, you'll need something like the following:

     <repositories>
        <repository>
          <releases>
            <enabled>false</enabled>
            <updatePolicy>always</updatePolicy>
            <checksumPolicy>fail</checksumPolicy>
          </releases>
          <snapshots>
            <enabled>true</enabled>
            <updatePolicy>never</updatePolicy>
            <checksumPolicy>fail</checksumPolicy>
          </snapshots>
          <id>odfdom-snapshots</id>
          <name>ODFDOM Snapshots</name>
          <url>http://odftoolkit.org/svn/odfdom~maven2/snapshot/</url>
          <layout>default</layout>
        </repository>
      </repositories>

You'll need the same thing in pluginRepositories to use the codegen-maven-plugin.



