# Website generator project

Build the project:

    ./gradlew clean createRuntime

Generate the website:

    ./scripts/website-cli generate <output directory>

The generator will use the content in this repository, but it is also possible
to specifiy the content repository separately:

    ./scripts/website-cli generate --content /tmp/tdf-odftoolkit-fork <output directory>

## Known Bugs

* The table header in people.html does not work
