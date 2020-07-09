# ODFXSLTRunner

## About ODF XSLT Runner

ODF XSLT Runner is a Java command line application that allows you to apply
XSLT stylesheets to XML streams included in ODF packages without extracting
them from the package.

## Documentation

In general, our online site is a great place to start when looking for documentation and other information about [ODFXSLTRunner](https://tdf.github.io/odftoolkit/docs/xsltrunner/ODFXSLTRunner.html).

## Usage

Easiest way is to use the latest [Maven release](https://oss.sonatype.org/content/groups/public/org/odftoolkit/xslt-runner) by adding the Maven dependency:

            <dependency>
                <groupId>org.odftoolkit</groupId>
                <artifactId>xslt-runner</artifactId>
            </dependency>

Or just call from command line: "java -jar xslt-runner-&lt;VERSION&gt;-jar-with-dependencies.jar". 
Detailed documentation can be found [online](https://tdf.github.io/odftoolkit/docs/xsltrunner/ODFXSLTRunner.html).

You will have [ODFDOM](https://tdf.github.io/odftoolkit/docs/odfdom/index.html) as a dependency.

## License
Apache License, Version 2.0. Please see file [LICENSE.txt](LICENSE.txt).