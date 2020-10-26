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

// !!! GENERATED SOURCE CODE !!!
package org.odftoolkit.odfdom.dom.style.props;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.pkg.OdfName;

public interface OdfTableProperties {
  public static final OdfStyleProperty BackgroundColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "background-color"));
  public static final OdfStyleProperty BreakAfter =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "break-after"));
  public static final OdfStyleProperty BreakBefore =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "break-before"));
  public static final OdfStyleProperty KeepWithNext =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "keep-with-next"));
  public static final OdfStyleProperty Margin =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin"));
  public static final OdfStyleProperty MarginBottom =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-bottom"));
  public static final OdfStyleProperty MarginLeft =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-left"));
  public static final OdfStyleProperty MarginRight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-right"));
  public static final OdfStyleProperty MarginTop =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-top"));
  public static final OdfStyleProperty MayBreakBetweenRows =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "may-break-between-rows"));
  public static final OdfStyleProperty PageNumber =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "page-number"));
  public static final OdfStyleProperty RelWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "rel-width"));
  public static final OdfStyleProperty Shadow =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "shadow"));
  public static final OdfStyleProperty Width =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "width"));
  public static final OdfStyleProperty WritingMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "writing-mode"));
  public static final OdfStyleProperty Align =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.TABLE, "align"));
  public static final OdfStyleProperty BorderModel =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.TABLE, "border-model"));
  public static final OdfStyleProperty Display =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TableProperties,
          OdfName.newName(OdfDocumentNamespace.TABLE, "display"));
}
