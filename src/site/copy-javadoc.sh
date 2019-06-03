#!/bin/bash

echo "Copy JavaDoc API from projects - had to been built with 'mvn install' previously..."
rm -rf ../docs/api/schema2template
mv ../../generator/schema2template/target/ ../docs/api/schema2template

rm -rf ../docs/api/odfdom
mv ../../odfdom/target/ ../docs/api/odfdom

rm -rf ../docs/api/simple
mv ../../simple/target/ ../docs/api/simple

rm -rf ../docs/api/taglets
mv ../../taglets/target/ ../docs/api/taglets
