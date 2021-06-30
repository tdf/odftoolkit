#!/bin/bash

echo "Creating HTML from MarkDown as described"
echo "  in ./website-development.html"
# Start the Python Markdown daemon. (tested with Python 2.7.16)

echo 1. Backup none-site related content
mv "../../docs/api" ../..

echo 2. Remove previous HTML
rm ../../docs/*.html
rm ../../docs/odfdom/*.html
rm ../../docs/simple/*.html
rm ../../docs/xsltrunner/*.html

echo 3. Built the site..
#  Build the site
cd java
./gradlew clean createRuntime
./scripts/website-cli generate ../../../docs/
cd ..

echo 4. Restore none-site related content
mv "../../api" ../../docs

echo
echo Now you may review the generated website in the '"<ODF_TOOLKIT>/docs/" directory'!
