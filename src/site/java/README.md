# Website generator project

The website generator project is only tested on Linux.
The project is meant as an interim solution to overcome the prior buggy Apache CMS solution for website generation.
In the Apache CMS solution often the middle content page is generated empty.
The idea is to switch to an exisiting well used feature rich framework like Sphinx and not reinvent the wheel with an own Java solution.

## Build the project

    ./gradlew clean createRuntime

## Generate the website

    ./scripts/website-cli generate <output directory>

The generator will use the content in this repository, but it is also possible
to specifiy the content repository separately:

    ./scripts/website-cli generate --content /tmp/tdf-odftoolkit-fork <output directory>

For instance, in our case generating the current documentation:

    ./scripts/website-cli generate --content ../../../ ../../../docs/

## Known Bugs

* index.html has multiple html, body and head tags (for footer)
