#!/bin/bash

echo "Creating HTML from MarkDown as described in https://tdf.github.io/odftoolkit/website-local.html"
export MARKDOWN_SOCKET=`pwd`/markdown.socket PYTHONPATH=`pwd`
python cms/build/markdownd.py

echo Build the site..
cms/build/build_site.pl --source-base site --target-base www

echo Copy the HTML to GitHub Docs
echo Save the generated JAVADOC API
#mv  ../../docs/api  ../..
#echo Remove all prior website content
#rm -rf ../../docs/*

: ' gathering basics for exchanging "odftoolkit_website" against  some stuff
sed -i -e "s/odftoolkit/odftoolkit_website/g" .
./conformance
./xsltrunner
./simple
./simple/document
./simple/document/cookbook
./simple/demo
./simple/demo/image
./odfdom
./images
./css
./docs
./docs/governance
'

# echo Copy all generated HTML as new website content
#mv -v ./www/content/odftoolkit_website/* ../../docs/
# restore the generated javadoc
#mv -v ../../api ../../docs
# remove temporary files and directories
#rm markdown.socket
#rm -rf www