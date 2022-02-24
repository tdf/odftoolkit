#!/bin/bash
@echo on
# -e causes the shell to exit if any subcommand or pipeline returns a non-zero status
# -v causes the shell to view each command
set -e -v


# !!! PLEASE UPDATE VARIABLES BELOW!!!

# ODT feature document trunc name, e.g src/test/resources/test-input/feature/
ODF_TRUNC="table_merged-cells"
#  Version number of the Saxon release currently used to add feature branch before rebase!
JACOCO_AGENT_VERION="0.8.7"
# Maven .m2 repository parent directory
MAVEN_REPO_PATH=$HOME/.m2



# DO NOT CHANGE BELOW THE FOLLOWING LINE
# --------------------------------------------------------

# -f gives no error if no jacoco.exec exists
rm -f ./target/jacoco.exec
rm -rf ./target/site

# Triggers a single test 'FeatureLoadTest' with the system property 'textFeatureName' being the trunc name of an ODT test file
# being loaded from src/test/resources/test-input/feature/
mvn surefire:test -Dtest=FeatureLoadTest -DtextFeatureName=${ODF_TRUNC} -DargLine=-javaagent:$MAVEN_REPO_PATH/repository/org/jacoco/org.jacoco.agent/${JACOCO_AGENT_VERION}/org.jacoco.agent-${JACOCO_AGENT_VERION}-runtime.jar=destfile=./target/jacoco.exec

# Creates the cobertura XML file and HTML coverage report from the binary target/jacoco.exec file
mvn jacoco:report

# Save the jacoco XML file using the cov suffix to avoid whitespace lint on XML
cp ./target/site/jacoco/jacoco.xml src/test/resources/test-input/feature/coverage/jacoco_${ODF_TRUNC}.cov

# Transform the jacoco XML to cobertura xml file and keeping it using the cov suffix to avoid whitespace lint on XML
python2 cover2cover.py target/site/jacoco/jacoco.xml src/main/java > src/test/resources/test-input/feature/coverage/cobertura_${ODF_TRUNC}.cov

