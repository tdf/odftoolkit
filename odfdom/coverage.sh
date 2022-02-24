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
mvn surefire:test -Dtest=FeatureLoadTest -DtextFeatureName=text_italic -DargLine=-javaagent:/home/svante/.m2/repository/org/jacoco/org.jacoco.agent/0.8.7/org.jacoco.agent-0.8.7-runtime.jar=destfile=./target/jacoco.exec
mvn jacoco:report

# ToDo: destination file name should vary on input name
cp ./target/site/jacoco/jacoco.xml src/test/resources/test-input/feature/coverage/jacoco_text_italic.cov

# ToDo: check in cover2cover.py
# ToDo: destination file name should vary on input name
python2 cover2cover.py target/site/jacoco/jacoco.xml src/main/java > src/test/resources/test-input/feature/coverage/cobertura_text_italic.cov

