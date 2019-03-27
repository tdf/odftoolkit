#!/bin/bash

echo "Creating HTML from MarkDown as described in https://tdf.github.io/odftoolkit/website-local.html"
export MARKDOWN_SOCKET=`pwd`/markdown.socket PYTHONPATH=`pwd`
python cms/build/markdownd.py

echo Build the site..
cms/build/build_site.pl --source-base site --target-base www

echo Copying the HTML to GitHub Docs
# saving the generated JAVADOC API
mv -v ../../docs/api  ../..
# removing all prior website content
rm -rfv ../../docs/*
# copying all generated HTML as new website content
mv -v ./www/content/odftoolkit_website/* ../../docs/
# restore the generated javadoc
mv -v ../../api ../../docs
# remove temporary files and directories
rm markdown.socket
rm -rf www