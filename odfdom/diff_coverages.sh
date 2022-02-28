#!/bin/bash
@echo on
# -e causes the shell to exit if any subcommand or pipeline returns a non-zero status
# -v causes the shell to view each command
set -e -v


# !!! PLEASE UPDATE VARIABLES BELOW!!!

# ODT feature minuend document trunc name, e.g src/test/resources/test-input/feature/
ODF_FEATURE_MINUENT__FILE_NAME__TRUNC="coverage_loadBoldTextODT"
# ODT feature subtrahend document trunc name, e.g src/test/resources/test-input/feature/
ODF_FEATURE_SUBTRAHEND__FILE_NAME__TRUNC="coverage_loadPlainODT"
#  Version number of the Saxon release currently used to add feature branch before rebase!
JACOCO_AGENT_VERION="0.8.7"
# Maven .m2 repository parent directory
MAVEN_REPO_PATH=$HOME/.m2
# Version nmber of the ODFDOM being used
ODFDOM_VERSION=0.10.0-SNAPSHOT


# DO NOT CHANGE BELOW THE FOLLOWING LINE
# --------------------------------------------------------

cp ${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}.cov /target/test-classes/test-reference/feature/coverage
cp ${ODF_FEATURE_SUBTRAHEND__FILE_NAME__TRUNC}.cov /target/test-classes/test-reference/feature/coverage

# Strip the uncovered lines from the Cobertura Coverage XML
# java  org.odftoolkit.odfdom.changes.CoberturaXMLHandler -cp ./target/odfdom-java-${ODFDOM_VERSION}-jar-with-dependencies.jar  ./src/test/resources/test-input/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}.cov
java -cp ./target/odfdom-java-${ODFDOM_VERSION}-jar-with-dependencies.jar org.odftoolkit.odfdom.changes.CoberturaXMLHandler ./${ODF_FEATURE_MINUENT__FILE_NAME}.cov ./${ODF_FEATURE_SUBTRAHEND__FILE_NAME__TRUNC}.cov

cp target/test-classes/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}--stripped.cov src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}--stripped.cov
cp target/test-classes/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}----diff.cov src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}--diff.cov

# XML pretty-printing/indentation - xmllint comes with libxml2-utils: https://gitlab.gnome.org/GNOME/libxml2
xmllint --format src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}--stripped.cov > src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}--stripped-indent.cov
xmllint --format src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}--stripped.cov > src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FEATURE_MINUENT__FILE_NAME__TRUNC}--diff-indent.cov
