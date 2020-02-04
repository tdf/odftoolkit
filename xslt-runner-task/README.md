# ODFXSLTRunner

## About ODF XSLT Runner Task

ODF XSLT Runner Task is a task definition for [Ant](https://ant.apache.org/) which allows to apply
XSLT stylesheets to ODF documents similar to Ant's build-in &lt;xslt&gt; task.
It is based on [ODFXSLTRunner](https://tdf.github.io/odftoolkit/docs/xsltrunner/ODFXSLTRunner.html).

## Documentation

In general, our online page is a great place to start when looking for [documentation and other information about ODFXSLTRunnerTask](https://tdf.github.io/odftoolkit/docs/xsltrunner/ODFXSLTRunnerTask.html).

## Usage    

Easiest way is to use the latest [Maven release](https://oss.sonatype.org/content/groups/public/org/odftoolkit/xslt-runner-task) by adding the Maven dependency:

            <dependency>
                <groupId>org.odftoolkit</groupId>
                <artifactId>xslt-runner-task</artifactId>
            </dependency>

Or just call from command line: "java -jar xslt-runner-&lt;VERSION&gt;-jar-with-dependencies.jar". 
Detailed documentation can be found [online](https://tdf.github.io/odftoolkit/docs/xsltrunner/ODFXSLTRunner.html).

You will need [ODFXSLTRunner](https://tdf.github.io/odftoolkit/docs/xsltrunner/ODFXSLTRunner.html) and [ODFDOM](https://tdf.github.io/odftoolkit/docs/odfdom/index.html).

## License
Apache License, Version 2.0. Please see file [LICENSE.txt](LICENSE.txt).