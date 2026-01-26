# Java Module System (JPMS) for ODF Toolkit

## Overview

The ODF Toolkit has been extended with Java Module System (JPMS) support. All artifacts (JARs and WARs) are now configured as Java modules.

## Modules

### 1. `org.odftoolkit.odfdom`

The main module for ODFDOM - the core API for ODF documents.

**Exported Packages:**
- `org.odftoolkit.odfdom` - Core API
- `org.odftoolkit.odfdom.doc.*` - Document Layer (high-level API)
- `org.odftoolkit.odfdom.pkg.*` - Package Layer
- `org.odftoolkit.odfdom.type` - Data types
- `org.odftoolkit.odfdom.changes` - Collaboration API
- `org.odftoolkit.odfdom.dom.*` - DOM Layer (for advanced users)

**Not Exported:**
- `org.odftoolkit.odfdom.incubator.*` - Experimental APIs (explicitly marked as "Incubator Status")

### 2. `org.odftoolkit.odfvalidator`

The Validator module for ODF validation.

**Exported Packages:**
- `org.odftoolkit.odfvalidator` - Validator API

**Dependencies:**
- `org.odftoolkit.odfdom` - Requires ODFDOM

### 3. `org.odftoolkit.odfxsltrunner`

The XSLT Runner module for XSLT transformations in ODF documents.

**Exported Packages:**
- `org.odftoolkit.odfxsltrunner` - XSLT Runner API

**Dependencies:**
- `org.odftoolkit.odfdom` - Requires ODFDOM

## Usage

### As Modules (Java 9+)

```java
module com.example.myapp {
    requires org.odftoolkit.odfdom;
    requires org.odftoolkit.odfvalidator;  // optional
    requires org.odftoolkit.odfxsltrunner;  // optional
}
```

### On the Module Path

```bash
java --module-path lib/odfdom-java-0.14.0.jar \
     --module org.odftoolkit.odfdom
```

### On the Classpath (Backward Compatibility)

The modules continue to work on the classpath for Java 8+ applications:

```bash
java -cp lib/odfdom-java-0.14.0.jar:lib/other.jar MyApp
```

## Validation

### Automatic Validation

Run the validation script:

**Linux/macOS:**
```bash
chmod +x src/site/validate-modules.sh
./src/site/validate-modules.sh
```

**Windows:**
```cmd
src\site\validate-modules.bat
```

### Manual Validation

#### 1. Check Compilation
```bash
mvn clean compile
```

#### 2. Analyze Module Dependencies
```bash
jdeps --module-path odfdom/target/classes \
      --add-modules org.odftoolkit.odfdom \
      --list-deps \
      odfdom/target/odfdom-java-*.jar
```

#### 3. Generate Module Graph
```bash
jdeps --module-path odfdom/target/classes \
      --dot-output module-graph.dot \
      odfdom/target/odfdom-java-*.jar

# Visualize with Graphviz
dot -Tpng module-graph.dot -o module-graph.png
```

#### 4. Check JAR Contents
```bash
jar tf odfdom/target/odfdom-java-*.jar | grep module-info
```

## Known Issues and Solutions

### Automatic Modules

Many dependencies do not yet have a `module-info.java` and are treated as "automatic modules". Module names are derived from JAR file names:

- `xercesImpl-2.12.2.jar` → `xerces.impl`
- `commons-validator-1.10.1.jar` → `commons.validator`
- `jena-core-5.6.0.jar` → `org.apache.jena.core`

### Reflection Access

If frameworks need to access internal classes via reflection, corresponding `opens` directives have been added:

```java
opens org.odftoolkit.odfdom.pkg.rdfa to org.apache.jena.core;
```

### WAR Deployment

The Validator is provided as a WAR file. Modern servlet containers (Tomcat 10+, Jetty 11+) support modules in WARs. For older containers, classpath mode continues to work.

## Migration from Existing Code

### Before (Classpath)
```java
import org.odftoolkit.odfdom.doc.OdfTextDocument;
// ...
```

### After (Module Path)
```java
module myapp {
    requires org.odftoolkit.odfdom;
}

import org.odftoolkit.odfdom.doc.OdfTextDocument;
// ... (code remains the same!)
```

**Important:** The code itself does not need to be changed! Only the `module-info.java` needs to be added.

## Additional Information

- [Java Platform Module System (JEP 261)](https://openjdk.java.net/projects/jigsaw/spec/)
- [The State of the Module System](https://openjdk.java.net/projects/jigsaw/spec/sotms/)
- [Maven Guide to Java Modules](https://maven.apache.org/guides/mini/guide-multiple-modules.html)

## Support

For questions or issues:
- GitHub Issues: https://github.com/tdf/odftoolkit/issues
- Mailing List: dev@odftoolkit.org
