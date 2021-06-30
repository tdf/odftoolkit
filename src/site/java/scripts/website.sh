#!/bin/bash

DIR=$(dirname $0)
LIBS="$DIR/../cli/build/lib-run"
REPO=$(readlink -f "$DIR/../../../../")

if [ ! -d "$LIBS" ]; then
	echo "Please run './gradlew createRuntime'"
	exit 1
fi

CLASSPATH="$LIBS/*"

exec java -Drepo="$REPO" -cp "$CLASSPATH" "$@"
