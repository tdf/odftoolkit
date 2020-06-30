#!/bin/bash

echo "Creating HTML from MarkDown as described"
echo "  in ./website-development.html"
# Start the Python Markdown daemon. (tested with Python 2.7.16)
export MARKDOWN_SOCKET=`pwd`/markdown.socket PYTHONPATH=`pwd`
python cms/build/markdownd.py
echo 1. Copying actual README.md \& related files to site
# root README.md and related fiels becomding index.mdtext
#cp ../../README.md ./site/content/odftoolkit_website/introduction.mdtext #2DO rename to index.mdtext
cp ../../LICENSE ./site/content/odftoolkit_website/
cp ../../NOTICE ./site/content/odftoolkit_website/
cp ../../KEYS ./site/content/odftoolkit_website/

echo 2. Built the site..
#  Build the site
cms/build/build_site.pl --source-base site --target-base www

echo 3. Exchanging the absolute HTML reference with relative ones..
# 1) find all files (even with space in name) ending with ''.html' of a certain directory level
# 2) exchange the fixed prefix '/odftoolkit_website' with the adequate relative one
find www -mindepth 3 -maxdepth 3 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+.+g'
find www -mindepth 4 -maxdepth 4 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+..+g'
find www -mindepth 5 -maxdepth 5 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../..+g'
find www -mindepth 6 -maxdepth 6 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../..+g' 2>/dev/null
find www -mindepth 7 -maxdepth 7 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../../..+g' 2>/dev/null
find www -mindepth 8 -maxdepth 8 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../../../..+g' 2>/dev/null
find www -mindepth 9 -maxdepth 9 -type f -print0 -name *.html | xargs -0 sed -i -e 's+/odftoolkit_website+../../../../../..+g' 2>/dev/null

echo 4. Backup none-site related content
mv ../../docs/api ../..
mv ../../docs/docs/presentations ../..
mv ../../docs/odf1.2 ../..
mv ../../docs/odf1.3 ../..

echo 5. Remove all existing content
rm -rf ../../docs/*

echo 6. Move all new generated HTML into /docs folder
mv ./www/content/odftoolkit_website/* ../../docs/

echo 7. Restore none-site related content
mv ../../api ../../docs
mv ../../presentations ../../docs/docs/
mv ../../odf1.2 ../../docs
mv ../../odf1.3 ../../docs

echo 8. Remove temporary files and directories
rm markdown.socket
rm -rf www
rm -rf cms/build/*.pyc
# root README.md and related fiels becomding index.mdtext
rm ./site/content/odftoolkit_website/introduction.mdtext #2DO rename
rm ./site/content/odftoolkit_website/LICENSE
rm ./site/content/odftoolkit_website/NOTICE
rm ./site/content/odftoolkit_website/KEYS

echo 
echo Now you may review the generated website in the '"<ODF_TOOLKIT>/docs/" directory'!
# There is a HTML generation bug, sometimes the created files are empty
find ../../docs/ -name "*.html" -print0 | while read -d $'\0' file
do
      DIFF=`diff "$file" site/empty-template.html`
      if [ "$DIFF" == "" ] 
      then
            echo WARNING: Empty generated HTML file found: $file
      fi
done
