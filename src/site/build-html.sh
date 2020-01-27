#!/bin/bash

echo "Creating HTML from MarkDown as described"
echo "  in https://tdf.github.io/odftoolkit/website-development.html"
# Start the Python Markdown daemon. (tested with Python 2.7.16)
export MARKDOWN_SOCKET=`pwd`/markdown.socket PYTHONPATH=`pwd`
python cms/build/markdownd.py
echo 
echo 1. Built the site..
#  Build the site
cms/build/build_site.pl --source-base site --target-base www

echo 2. Exchanging the absolute HTML reference with relative ones..
# 1) find all files (even with space in name) ending with ''.html' of a certain directory level
# 2) exchange the fixed prefix '/odftoolkit_website' with the adequate relative one
find www -mindepth 3 -maxdepth 3 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+.+g'
find www -mindepth 4 -maxdepth 4 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+..+g'
find www -mindepth 5 -maxdepth 5 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../..+g'
find www -mindepth 6 -maxdepth 6 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../..+g' 2>/dev/null
find www -mindepth 7 -maxdepth 7 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../../..+g' 2>/dev/null
find www -mindepth 8 -maxdepth 8 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../../../..+g' 2>/dev/null
find www -mindepth 9 -maxdepth 9 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../../../../..+g' 2>/dev/null

echo 3. Copied the HTML to GitHub /docs
echo 4. Saved the generated JAVADOC API
mv  ../../docs/api  ../..

echo 5. Removed all prior website content
rm -rf ../../docs/*

echo 6. Copied all generated HTML as new website content
mv ./www/content/odftoolkit_website/* ../../docs/

echo 7. Restored saved generated javadoc
mv ../../api ../../docs

echo 8. Removed temporary files and directories
rm markdown.socket
rm -rf www
rm -rf cms/build/*.pyc

echo 
echo Now you may review the generated website in the '"<ODF_TOOLKIT>/docs/" directory'!