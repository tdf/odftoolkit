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
 * ODF Validator - OpenDocument Format Validation Tool
 *
 * <p>This module provides validation functionality for Open Document Format (ODF) files. It can be
 * used as a command-line tool or deployed as a WAR file in a servlet container.
 *
 * <p>Main entry point: {@link org.odftoolkit.odfvalidator.Main}
 *
 * @moduleGraph
 * @since 0.14.0
 */
module org.odftoolkit.odfvalidator {
  // ============================================================================
  // PUBLIC API EXPORTS
  // ============================================================================

  exports org.odftoolkit.odfvalidator;

  // ============================================================================
  // OPENS - For servlet containers and reflection-based frameworks
  // ============================================================================

  // Open for servlet containers and XML processing frameworks (WAR deployment)
  // Servlet APIs are provided by the container at runtime
  opens org.odftoolkit.odfvalidator to
      java.xml;

  // ============================================================================
  // REQUIRES - Module dependencies
  // ============================================================================

  // Java Platform Modules
  requires java.base;
  requires java.desktop; // for javax.xml.*
  requires java.logging;
  requires java.xml;

  // ODFDOM module
  requires org.odftoolkit.odfdom;

  // Automatic Modules (JARs without module-info.java)
  requires org.apache
      .commons
      .fileupload; // commons-fileupload:commons-fileupload (has Automatic-Module-Name)
  requires msv.core; // net.java.dev.msv:msv-core (msv-core-*.jar -> msv.core)
  requires isorelax
      .jaxp
      .bridge
      .ILM; // org.jopendocument:isorelax-jaxp-bridge-ILM (isorelax-jaxp-bridge-ILM-*.jar ->
  // isorelax.jaxp.bridge.ILM)
  requires xercesImpl; // xerces:xercesImpl (xercesImpl-*.jar -> xercesImpl)

// Note: Servlet APIs are provided by the servlet container at runtime (WAR deployment)
// No compile-time dependency needed - servlet classes are available from the container
}
