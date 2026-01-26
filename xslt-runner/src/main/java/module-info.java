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
 * ODF XSLT Runner - XSLT Transformation Tool for ODF Documents
 *
 * <p>This module provides functionality to apply XSLT stylesheets to XML streams included in ODF
 * packages without extracting them from the package.
 *
 * <p>Main entry point: {@link org.odftoolkit.odfxsltrunner.Main}
 *
 * @moduleGraph
 * @since 0.14.0
 */
module org.odftoolkit.odfxsltrunner {
  // ============================================================================
  // PUBLIC API EXPORTS
  // ============================================================================

  exports org.odftoolkit.odfxsltrunner;

  // ============================================================================
  // REQUIRES - Module dependencies
  // ============================================================================

  // Java Platform Modules
  requires java.base;
  requires java.logging;
  requires java.xml;

  // ODFDOM module
  requires org.odftoolkit.odfdom;

  // Automatic Modules (JARs without module-info.java)
  requires Saxon.HE; // net.sf.saxon:Saxon-HE (Saxon-HE-*.jar -> Saxon.HE)
}
