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

public interface OdfTextProperties {
  public static final OdfStyleProperty BackgroundColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "background-color"));
  public static final OdfStyleProperty Color =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties, OdfName.newName(OdfDocumentNamespace.FO, "color"));
  public static final OdfStyleProperty Country =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "country"));
  public static final OdfStyleProperty FontFamily =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "font-family"));
  public static final OdfStyleProperty FontSize =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "font-size"));
  public static final OdfStyleProperty FontStyle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "font-style"));
  public static final OdfStyleProperty FontVariant =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "font-variant"));
  public static final OdfStyleProperty FontWeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "font-weight"));
  public static final OdfStyleProperty Hyphenate =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "hyphenate"));
  public static final OdfStyleProperty HyphenationPushCharCount =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "hyphenation-push-char-count"));
  public static final OdfStyleProperty HyphenationRemainCharCount =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "hyphenation-remain-char-count"));
  public static final OdfStyleProperty Language =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "language"));
  public static final OdfStyleProperty LetterSpacing =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "letter-spacing"));
  public static final OdfStyleProperty Script =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties, OdfName.newName(OdfDocumentNamespace.FO, "script"));
  public static final OdfStyleProperty TextShadow =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "text-shadow"));
  public static final OdfStyleProperty TextTransform =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "text-transform"));
  public static final OdfStyleProperty CountryAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "country-asian"));
  public static final OdfStyleProperty CountryComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "country-complex"));
  public static final OdfStyleProperty FontCharset =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-charset"));
  public static final OdfStyleProperty FontCharsetAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-charset-asian"));
  public static final OdfStyleProperty FontCharsetComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-charset-complex"));
  public static final OdfStyleProperty FontFamilyAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-family-asian"));
  public static final OdfStyleProperty FontFamilyComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-family-complex"));
  public static final OdfStyleProperty FontFamilyGeneric =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-family-generic"));
  public static final OdfStyleProperty FontFamilyGenericAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-family-generic-asian"));
  public static final OdfStyleProperty FontFamilyGenericComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-family-generic-complex"));
  public static final OdfStyleProperty FontName =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-name"));
  public static final OdfStyleProperty FontNameAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-name-asian"));
  public static final OdfStyleProperty FontNameComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-name-complex"));
  public static final OdfStyleProperty FontPitch =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-pitch"));
  public static final OdfStyleProperty FontPitchAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-pitch-asian"));
  public static final OdfStyleProperty FontPitchComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-pitch-complex"));
  public static final OdfStyleProperty FontRelief =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-relief"));
  public static final OdfStyleProperty FontSizeAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-size-asian"));
  public static final OdfStyleProperty FontSizeComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-size-complex"));
  public static final OdfStyleProperty FontSizeRel =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-size-rel"));
  public static final OdfStyleProperty FontSizeRelAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-size-rel-asian"));
  public static final OdfStyleProperty FontSizeRelComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-size-rel-complex"));
  public static final OdfStyleProperty FontStyleAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-style-asian"));
  public static final OdfStyleProperty FontStyleComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-style-complex"));
  public static final OdfStyleProperty FontStyleName =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-style-name"));
  public static final OdfStyleProperty FontStyleNameAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-style-name-asian"));
  public static final OdfStyleProperty FontStyleNameComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-style-name-complex"));
  public static final OdfStyleProperty FontWeightAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-weight-asian"));
  public static final OdfStyleProperty FontWeightComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "font-weight-complex"));
  public static final OdfStyleProperty LanguageAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "language-asian"));
  public static final OdfStyleProperty LanguageComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "language-complex"));
  public static final OdfStyleProperty LetterKerning =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "letter-kerning"));
  public static final OdfStyleProperty RfcLanguageTag =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "rfc-language-tag"));
  public static final OdfStyleProperty RfcLanguageTagAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "rfc-language-tag-asian"));
  public static final OdfStyleProperty RfcLanguageTagComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "rfc-language-tag-complex"));
  public static final OdfStyleProperty ScriptAsian =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "script-asian"));
  public static final OdfStyleProperty ScriptComplex =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "script-complex"));
  public static final OdfStyleProperty ScriptType =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "script-type"));
  public static final OdfStyleProperty TextBlinking =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-blinking"));
  public static final OdfStyleProperty TextCombine =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-combine"));
  public static final OdfStyleProperty TextCombineEndChar =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-combine-end-char"));
  public static final OdfStyleProperty TextCombineStartChar =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-combine-start-char"));
  public static final OdfStyleProperty TextEmphasize =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-emphasize"));
  public static final OdfStyleProperty TextLineThroughColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-line-through-color"));
  public static final OdfStyleProperty TextLineThroughMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-line-through-mode"));
  public static final OdfStyleProperty TextLineThroughStyle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-line-through-style"));
  public static final OdfStyleProperty TextLineThroughText =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-line-through-text"));
  public static final OdfStyleProperty TextLineThroughTextStyle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-line-through-text-style"));
  public static final OdfStyleProperty TextLineThroughType =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-line-through-type"));
  public static final OdfStyleProperty TextLineThroughWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-line-through-width"));
  public static final OdfStyleProperty TextOutline =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-outline"));
  public static final OdfStyleProperty TextOverlineColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-overline-color"));
  public static final OdfStyleProperty TextOverlineMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-overline-mode"));
  public static final OdfStyleProperty TextOverlineStyle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-overline-style"));
  public static final OdfStyleProperty TextOverlineType =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-overline-type"));
  public static final OdfStyleProperty TextOverlineWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-overline-width"));
  public static final OdfStyleProperty TextPosition =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-position"));
  public static final OdfStyleProperty TextRotationAngle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-rotation-angle"));
  public static final OdfStyleProperty TextRotationScale =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-rotation-scale"));
  public static final OdfStyleProperty TextScale =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-scale"));
  public static final OdfStyleProperty TextUnderlineColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-underline-color"));
  public static final OdfStyleProperty TextUnderlineMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-underline-mode"));
  public static final OdfStyleProperty TextUnderlineStyle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-underline-style"));
  public static final OdfStyleProperty TextUnderlineType =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-underline-type"));
  public static final OdfStyleProperty TextUnderlineWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "text-underline-width"));
  public static final OdfStyleProperty UseWindowFontColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "use-window-font-color"));
  public static final OdfStyleProperty Condition =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "condition"));
  public static final OdfStyleProperty Display =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.TextProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "display"));
}
