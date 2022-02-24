#!/bin/bash
@echo on
# -e causes the shell to exit if any subcommand or pipeline returns a non-zero status
# -v causes the shell to view each command
set -e -v

# -f gives no error if no jacoco.exec exists
rm -f ./target/jacoco.exec
rm -rf ./target/site

# ToDo: textFeatureName should vary on input name
# ToDo: Add variable for MavenRepo Path
# ToDo: Add variable for JAR version used from pom.xm
# Triggers a single test 'FeatureLoadTest' with the system property 'textFeatureName' being the trunc name of an ODT test file
# being loaded from src/test/resources/test-input/feature/text_italic.odt
mvn surefire:test -Dtest=FeatureLoadTest -DtextFeatureName=text_italic -DargLine=-javaagent:/home/svante/.m2/repository/org/jacoco/org.jacoco.agent/0.8.7/org.jacoco.agent-0.8.7-runtime.jar=destfile=./target/jacoco.exec

# Creates the cobertura XML file and HTML coverage report from the binary target/jacoco.exec file
mvn jacoco:report

# ToDo: destination file name should vary on input name
# Save the jacoco XML file using the cov suffix to avoid whitespace lint on XML
cp ./target/site/jacoco/jacoco.xml src/test/resources/test-input/feature/coverage/jacoco_text_italic.cov

# ToDo: check in cover2cover.py
# ToDo: destination file name should vary on input name
# Transform the jacoco XML to cobertura xml file and keeping it using the cov suffix to avoid whitespace lint on XML
python2 cover2cover.py target/site/jacoco/jacoco.xml src/main/java > src/test/resources/test-input/feature/coverage/cobertura_text_italic.cov

