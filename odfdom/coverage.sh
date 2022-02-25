#!/bin/bash
@echo on
# -e causes the shell to exit if any subcommand or pipeline returns a non-zero status
# -v causes the shell to view each command
set -e -v


# !!! PLEASE UPDATE VARIABLES BELOW!!!

# ODT feature document trunc name, e.g src/test/resources/test-input/feature/
ODF_FILE_NAME__TRUNC="table_merged-cells"
#  Version number of the Saxon release currently used to add feature branch before rebase!
JACOCO_AGENT_VERION="0.8.7"
# Maven .m2 repository parent directory
MAVEN_REPO_PATH=$HOME/.m2
# Version nmber of the ODFDOM being used
ODFDOM_VERSION=0.10.0-SNAPSHOT


# DO NOT CHANGE BELOW THE FOLLOWING LINE
# --------------------------------------------------------

# -f gives no error if no jacoco.exec exists
rm -f ./target/jacoco.exec
rm -rf ./target/site

# Triggers a single test 'FeatureLoadTest' with the system property 'textFeatureName' being the trunc name of an ODT test file
# being loaded from src/test/resources/test-input/feature/
mvn surefire:test -Dtest=FeatureLoadTest -DtextFeatureName=${ODF_FILE_NAME__TRUNC} -DargLine=-javaagent:$MAVEN_REPO_PATH/repository/org/jacoco/org.jacoco.agent/${JACOCO_AGENT_VERION}/org.jacoco.agent-${JACOCO_AGENT_VERION}-runtime.jar=destfile=./target/jacoco.exec

# Creates the cobertura XML file and HTML coverage report from the binary target/jacoco.exec file
mvn jacoco:report

# Save the jacoco XML file using the cov suffix to avoid whitespace lint on XML
cp ./target/site/jacoco/jacoco.xml src/test/resources/test-input/feature/coverage/jacoco_${ODF_FILE_NAME__TRUNC}.cov

# XML pretty-printing/indentation - xmllint comes with libxml2-utils: https://gitlab.gnome.org/GNOME/libxml2
xmllint --format src/test/resources/test-input/feature/coverage/jacoco_${ODF_FILE_NAME__TRUNC}.cov > src/test/resources/test-reference/feature/coverage/jacoco_${ODF_FILE_NAME__TRUNC}_indent.cov

# Transform the jacoco XML to cobertura xml file and keeping it using the cov suffix to avoid whitespace lint on XML
python2 cover2cover.py target/site/jacoco/jacoco.xml src/main/java > src/test/resources/test-input/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}.cov

# XML pretty-printing/indentation - xmllint comes with libxml2-utils: https://gitlab.gnome.org/GNOME/libxml2
xmllint --format src/test/resources/test-input/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}.cov > src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}_indent.cov

# Strip the uncovered lines from the Cobertura Coverage XML
# java  org.odftoolkit.odfdom.changes.CoberturaXMLHandler -cp ./target/odfdom-java-${ODFDOM_VERSION}-jar-with-dependencies.jar  ./src/test/resources/test-input/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}.cov
java -cp ./target/odfdom-java-${ODFDOM_VERSION}-jar-with-dependencies.jar org.odftoolkit.odfdom.changes.CoberturaXMLHandler ./cobertura_${ODF_FILE_NAME__TRUNC}.cov

cp target/test-classes/test-reference/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}--stripped.cov src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}--stripped.cov

# XML pretty-printing/indentation - xmllint comes with libxml2-utils: https://gitlab.gnome.org/GNOME/libxml2
xmllint --format src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}--stripped.cov > src/test/resources/test-reference/feature/coverage/cobertura_${ODF_FILE_NAME__TRUNC}--stripped-indent.cov
