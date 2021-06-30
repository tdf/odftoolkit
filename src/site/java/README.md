# Website generator project

**NOTE: The website generator project is only tested on Linux.**
The project is meant as an interim solution to overcome the prior buggy Apache CMS solution for website generation.
In the Apache CMS solution often the middle content page is generated empty.
The idea is to switch to an exisiting well used feature rich framework like Sphinx and not reinvent the wheel with an own Java solution.

The following commands should be used via Linux commandline from the directory of this README as base!

## Build the project

    ./gradlew clean createRuntime

## Generate the website

The generator use the content defined by --content:
Generate the current markdown documentation into the GitHub Pages 'docs' directory via commandline:

    ./scripts/website-cli generate --content ../../../ ../../../docs/

## Known Bugs

* index.html has multiple html, body and head tags (for footer)
