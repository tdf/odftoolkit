/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved. Copyright 2018-2026
 * The Document Foundation. All rights reserved.
 *
 * <p>Use is subject to license terms.
 *
 * <p>Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0. You can also obtain a copy of the License at
 * http://odftoolkit.org/docs/license.txt
 *
 * <p>Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 *
 * <p>See the License for the specific language governing permissions and limitations under the
 * License.
 *
 * <p>**********************************************************************
 */

/**
 * ODFDOM - OpenDocument Format DOM API
 *
 * <p>This module provides the core ODFDOM API for creating, accessing and manipulating Open
 * Document Format (ODF) documents.
 *
 * <p>The main entry points are:
 *
 * <ul>
 *   <li>{@link org.odftoolkit.odfdom.doc OdfDocument classes} - High-level document API
 *   <li>{@link org.odftoolkit.odfdom.pkg OdfPackage} - Package-level access
 *   <li>{@link org.odftoolkit.odfdom.dom OdfSchemaDocument} - DOM-level access
 *   <li>{@link org.odftoolkit.odfdom.changes CollabTextDocument} - Collaboration API
 * </ul>
 *
 * @moduleGraph
 * @since 0.14.0
 */
module org.odftoolkit.odfdom {
  // ============================================================================
  // PUBLIC API EXPORTS - Stable, documented APIs for external use
  // ============================================================================

  // Core API
  exports org.odftoolkit.odfdom;

  // Document Layer - High-level document API (stable)
  exports org.odftoolkit.odfdom.doc;
  exports org.odftoolkit.odfdom.doc.table;
  exports org.odftoolkit.odfdom.doc.presentation;

  // Package Layer - Package-level access (stable)
  exports org.odftoolkit.odfdom.pkg;
  exports org.odftoolkit.odfdom.pkg.manifest;
  exports org.odftoolkit.odfdom.pkg.rdfa;

  // Type System - Data types (stable)
  exports org.odftoolkit.odfdom.type;

  // Changes API - Collaboration API (stable)
  exports org.odftoolkit.odfdom.changes;

  // DOM Layer - Schema-based DOM access (for advanced users)
  exports org.odftoolkit.odfdom.dom;
  exports org.odftoolkit.odfdom.dom.style;
  exports org.odftoolkit.odfdom.dom.element;
  // Note: org.odftoolkit.odfdom.dom.attribute contains only subpackages, not exported directly
  // Subpackages like org.odftoolkit.odfdom.dom.attribute.text are accessible via parent package
  exports org.odftoolkit.odfdom.dom.rdfa;

  // ============================================================================
  // OPENS - For reflection access (frameworks, serialization, etc.)
  // ============================================================================

  // Open for Apache Jena (RDF processing)
  opens org.odftoolkit.odfdom.pkg.rdfa to
      org.apache.jena.core;

  // Open for XML processing frameworks that use reflection
  opens org.odftoolkit.odfdom.pkg to
      java.xml;

  // ============================================================================
  // REQUIRES - Module dependencies
  // ============================================================================

  // Java Platform Modules
  requires java.base;
  requires java.desktop; // for javax.xml.*
  requires java.logging;
  requires java.xml;
  requires java.xml.crypto; // for digital signatures

  // Automatic Modules (JARs without module-info.java)
  // Note: Module names are derived from JAR file names or Automatic-Module-Name manifest attribute
  // Maven compiler plugin automatically places dependencies on module path when module-info.java
  // exists
  requires xercesImpl; // xerces:xercesImpl (xercesImpl-2.12.2.jar -> xercesImpl, derived from
  // filename)
  requires serializer; // xalan:serializer (serializer-2.7.3.jar -> serializer) - confirmed working
  requires org.apache.jena.core; // org.apache.jena:jena-core (has Automatic-Module-Name)
  requires java.rdfa; // net.rootdev:java-rdfa (java-rdfa-1.0.0-BETA1.jar -> java.rdfa) - confirmed
  // working
  requires org.apache
      .commons
      .validator; // commons-validator:commons-validator (has Automatic-Module-Name)
  requires org.apache
      .commons
      .compress; // org.apache.commons:commons-compress (has Automatic-Module-Name)
  requires org.apache.commons.lang3; // org.apache.commons:commons-lang3 (has Automatic-Module-Name)
  requires org.json; // org.json:json (has Automatic-Module-Name)
  requires org.bouncycastle.provider; // org.bouncycastle:bcprov-jdk18on (has Automatic-Module-Name)

  // Optional dependencies (may not be present at runtime)
  requires static org.slf4j; // org.slf4j:slf4j-api (has Automatic-Module-Name: org.slf4j)

// ============================================================================
// SERVICES - Service provider interfaces (if any)
// ============================================================================

// Currently no services defined
}
