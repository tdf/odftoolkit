#!/bin/bash

echo "Creating HTML from MarkDown as described"
echo "  in ./website-development.html"

echo 1. Built the site..
#  Build the site
cd java
./gradlew clean createRuntime
./scripts/website-cli generate ../../../docs/
cd ..

echo
echo Now you may review the generated website in the '"<ODF_TOOLKIT>/docs/" directory'!
echo Alternative use your GitHub Pages on your GitHub fork using your Git branch and /docs as root!
echo Use a broken link checker e.g. https://www.drlinkcheck.com/
