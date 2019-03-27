#!/bin/bash

echo "Creating HTML from MarkDown as described in https://tdf.github.io/odftoolkit/website-local.html"
export MARKDOWN_SOCKET=`pwd`/markdown.socket PYTHONPATH=`pwd`
python cms/build/markdownd.py

echo Build the site..
cms/build/build_site.pl --source-base site --target-base www

echo Copying the HTML to GitHub Docs
#mv -v ../../docs/api  ../..
#rm -rfv ../../docs/*
#mv -v ./www/content/odftoolkit/* ../../docs/
#mv -v ../../api ../../docs