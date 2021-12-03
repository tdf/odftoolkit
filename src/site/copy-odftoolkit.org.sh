#!/bin/bash

# prepare website for deploying on odftoolkit.org - the main thing is
# that 0.9 javadocs are added in "api-0.9/"

set -euo pipefail

if ! test -f "docs/website-development.html"; then
    echo must be run from the root of the git repo
    exit 1
fi

dir="$(mktemp -d)"

echo using "${dir}"

git archive --format=tar origin/master | tar -x -C "${dir}" docs/
git archive --format=tar --prefix=0.9/ origin/0.9 | tar -x -C "${dir}" 0.9/docs/api
mv "${dir}/0.9/docs/api" "${dir}/docs/api-0.9"
rm -r "${dir}/0.9"
chmod -R u=rwX,g=rX,o=rX "${dir}"

echo ... done, now rsync it to the server

