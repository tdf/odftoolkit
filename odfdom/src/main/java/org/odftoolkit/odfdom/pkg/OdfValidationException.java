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
package org.odftoolkit.odfdom.pkg;

import org.xml.sax.SAXParseException;

/**
 * This ODF specific validation exception facilitates the usage of a SAXParseException for non-XML
 * validation, used by an <code>ErrorHandler</code>.
 */
public class OdfValidationException extends SAXParseException {

  private ValidationConstraint mConstraint;
  private static final String NO_SOURCE = "";
  private static final String SOURCE_PREFIX = " '";
  private static final String SOURCE_SUFFIX = "'";

  /**
   * An OdfValidationException should be used for any validation result related to an ODF package.
   * This constructor should only be used for SaxParseExceptions unrelated to XML. This constructor
   * sets the public and system ID for XML and line and column number of the super class will be set
   * to be invalid.
   *
   * @param constraint the predefined constraint message
   */
  public OdfValidationException(ValidationConstraint constraint) {
    // The constraint convention is the first parameter to be the sourcePath of the ODF
    // document/package causing the exception
    super(String.format(constraint.getMessage(), NO_SOURCE), null, null, -1, -1);
    mConstraint = constraint;
  }

  /**
   * An OdfValidationException should be used for any validation result related to an ODF package.
   * This constructor should only be used for SaxParseExceptions unrelated to XML. This constructor
   * sets the public and system ID for XML and line and column number of the super class will be set
   * to be invalid.
   *
   * @param constraint the predefined constraint message
   * @param sourcePath the source path of the exception. For instance, it might be an ODF package or
   *     ODF document.
   * @param messageParameters allow the customization of a constraint message with parameters.
   */
  public OdfValidationException(
      ValidationConstraint constraint, String sourcePath, Object... messageParameters) {
    // The constraint convention is the first parameter to be the sourcePath of the ODF
    // document/package causing the exception
    super(formatMessage(constraint, sourcePath, messageParameters), null, null, -1, -1);
    mConstraint = constraint;
  }

  /**
   * An OdfValidationException should be used for any validation result related to an ODF package.
   * This constructor should only be used for SaxParseExceptions unrelated to XML. This constructor
   * sets the public and system ID for XML and line and column number of the super class will be set
   * to be invalid.
   *
   * @param e root exception to be embeddded
   * @param constraint the predefined constraint message
   * @param sourcePath the source path of the exception. For instance, it might be an ODF package or
   *     ODF document.
   * @param messageParameters allow the customization of a constraint message with parameters.
   */
  public OdfValidationException(
      ValidationConstraint constraint,
      String sourcePath,
      Exception e,
      Object... messageParameters) {
    // The constraint convention is the first parameter to be the sourcePath of the ODF
    // document/package causing the exception
    super(formatMessage(constraint, sourcePath, messageParameters), null, null, -1, -1, e);
    mConstraint = constraint;
  }

  /** @return constraint belonging to this exception */
  public ValidationConstraint getConstraint() {
    return mConstraint;
  }

  private static String formatMessage(
      ValidationConstraint constraint, String sourcePath, Object... messageParameters) {
    String formattedString = NO_SOURCE;
    int varCount = 0;
    if (messageParameters != null) {
      varCount = messageParameters.length;
    }
    switch (varCount) {
      case 0:
        formattedString =
            String.format(
                constraint.getMessage(),
                sourcePath == null || sourcePath.equals(NO_SOURCE)
                    ? NO_SOURCE
                    : SOURCE_PREFIX + sourcePath + SOURCE_SUFFIX);
        break;
      case 1:
        formattedString =
            String.format(
                constraint.getMessage(),
                sourcePath == null || sourcePath.equals(NO_SOURCE)
                    ? NO_SOURCE
                    : SOURCE_PREFIX + sourcePath + SOURCE_SUFFIX,
                messageParameters[0]);
        break;
      case 2:
        formattedString =
            String.format(
                constraint.getMessage(),
                sourcePath == null || sourcePath.equals(NO_SOURCE)
                    ? NO_SOURCE
                    : SOURCE_PREFIX + sourcePath + SOURCE_SUFFIX,
                messageParameters[0],
                messageParameters[1]);
        break;
      case 3:
        formattedString =
            String.format(
                constraint.getMessage(),
                sourcePath == null || sourcePath.equals(NO_SOURCE)
                    ? NO_SOURCE
                    : SOURCE_PREFIX + sourcePath + SOURCE_SUFFIX,
                messageParameters[0],
                messageParameters[1],
                messageParameters[2]);
        break;
      case 4:
        formattedString =
            String.format(
                constraint.getMessage(),
                sourcePath == null || sourcePath.equals(NO_SOURCE)
                    ? NO_SOURCE
                    : SOURCE_PREFIX + sourcePath + SOURCE_SUFFIX,
                messageParameters[0],
                messageParameters[1],
                messageParameters[2],
                messageParameters[3]);
        break;
      case 5:
        formattedString =
            String.format(
                constraint.getMessage(),
                sourcePath == null || sourcePath.equals(NO_SOURCE)
                    ? NO_SOURCE
                    : SOURCE_PREFIX + sourcePath + SOURCE_SUFFIX,
                messageParameters[0],
                messageParameters[1],
                messageParameters[2],
                messageParameters[3],
                messageParameters[4]);
        break;
    }
    return formattedString;
  }
}
