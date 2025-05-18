/**
 * **********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * <p>Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
package org.odftoolkit.odfdom.dom;

import org.odftoolkit.odfdom.pkg.ValidationConstraint;

/**
 * This class is used for validation of the ODF Document. It contains the constraint messages are
 * taken from the OASIS ODF 1.2 part 1, the XML Schema specification. These messages are used by the
 * <code>ValidationException</code> for ODF validation. The validation is enabled, when an <code>
 * ErrorHandler</code> was provided to the <code>OdfPackage</code>.
 */
public enum OdfSchemaConstraint implements ValidationConstraint {

  // NOTE FOLLOWING CODE CONVENTION:
  // The first parameter is ALWAYS the source path, followed by a missing space. The
  // OdfValidationException will take care of adjusting.
  /** A ODF mimetype is invalid for the ODF XML Schema document. */
  DOCUMENT_WITHOUT_ODF_MIMETYPE(
      "The ODF mimetype '%2$s' is invalid for the ODF XML Schema document%1$s!"),
  /** At least \'content.xml' or 'styles.xml' have to be contained in the ODF XML Schema package. */
  DOCUMENT_WITHOUT_CONTENT_NOR_STYLES_XML(
      "At least 'content.xml' or 'styles.xml' have to be contained in the ODF XML Schema package%1$s!"),
  /**
   * The files \'content.xml' or 'styles.xml' must be parsed without error when existent in the ODF
   * XML Schema package.
   */
  DOCUMENT_WITH_EXISTENT_BUT_UNREADABLE_CONTENT_OR_STYLES_XML(
      "In an ODF document, the '%2$s' have to be readable, when existent!"),
  /** The 'mimetype' file have to be contained in the ODF XML Schema package. */
  PACKAGE_SHALL_CONTAIN_MIMETYPE(
      "The 'mimetype' file have to be contained in the ODF XML Schema package%1$s!"),
  /** The XML is not valid according to the ODF RelaxNG schema. */
  DOCUMENT_XML_INVALID_ATTRIBUTE_VALUE(
      "The XML is not valid according to the ODF RelaxNG schema. The value %1$s for the ODF attribute %2$s is invalid!");
  private final String mMessage;

  /**
   * Creates a ODF Document constraint
   *
   * @param message of the constraint
   */
  OdfSchemaConstraint(String message) {
    mMessage = message;
  }

  /**
   * Creates a localized description of a Constraint. Subclasses may override this method in order
   * to produce a locale-specific message. For subclasses that do not override this method, the
   * default implementation returns the same result as <code>getMessage()</code>.
   *
   * @return The localized description of this constraint.
   */
  public String getLocalizedMessage() {
    //    FUTURE FEATUE: LOCALIZATION:
    //    =============================
    //    There should be a property files e.g. OdfPackageConstraint_de_DE.properties Sourcecode:
    //    PACKAGE_SHALL_BE_ZIP=%s muss eine ZIP Datei sein, wie in [ZIP] definiert. Alle Dateien
    // innerhalbe des ZIPS muessen entwder unkomprimiert (STORED) oder komprimiert sein (DEFLATED)
    // und den DEFLATE aloritmus verwenden.
    //    PACKAGE_SHALL_CONTAIN_MANIFEST=.....
    //    ResourceBundle bundle = ResourceBundle.getBundle(OdfPackageConstraint.class.getName());
    //    return bundle.getString(this.name());
    return mMessage;
  }

  /**
   * Returns the detail message string of this Constraint.
   *
   * @return the detail message string of this <code>Constraint</code> instance (which may be
   *     <code>null</code>).
   */
  public String getMessage() {
    return mMessage;
  }
}
