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

public interface OdfListLevelProperties {
  public static final OdfStyleProperty Height =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "height"));
  public static final OdfStyleProperty TextAlign =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "text-align"));
  public static final OdfStyleProperty Width =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "width"));
  public static final OdfStyleProperty FontName =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-name"));
  public static final OdfStyleProperty VerticalPos =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "vertical-pos"));
  public static final OdfStyleProperty VerticalRel =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "vertical-rel"));
  public static final OdfStyleProperty Y =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "y"));
  public static final OdfStyleProperty ListLevelPositionAndSpaceMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "list-level-position-and-space-mode"));
  public static final OdfStyleProperty MinLabelDistance =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "min-label-distance"));
  public static final OdfStyleProperty MinLabelWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "min-label-width"));
  public static final OdfStyleProperty SpaceBefore =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.ListLevelProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "space-before"));
}
