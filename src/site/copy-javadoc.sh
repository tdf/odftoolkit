#!/bin/bash

set -euo pipefail

echo "Copy generated JavaDoc API from projects into /docs - must been built earlier with 'mvn install'..."
rm -rf ../../docs/api/schema2template
mv ../../generator/schema2template/target/apidocs ../../docs/api/schema2template

rm -rf ../../docs/api/odfdom
mv ../../odfdom/target/apidocs ../../docs/api/odfdom

rm -rf ../../docs/api/taglets
mv ../../taglets/target/apidocs ../../docs/api/taglets
