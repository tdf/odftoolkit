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

public interface OdfGraphicProperties {
  public static final OdfStyleProperty AmbientColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "ambient-color"));
  public static final OdfStyleProperty BackScale =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "back-scale"));
  public static final OdfStyleProperty BackfaceCulling =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "backface-culling"));
  public static final OdfStyleProperty CloseBack =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "close-back"));
  public static final OdfStyleProperty CloseFront =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "close-front"));
  public static final OdfStyleProperty Depth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "depth"));
  public static final OdfStyleProperty DiffuseColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "diffuse-color"));
  public static final OdfStyleProperty EdgeRounding =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "edge-rounding"));
  public static final OdfStyleProperty EdgeRoundingMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "edge-rounding-mode"));
  public static final OdfStyleProperty EmissiveColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "emissive-color"));
  public static final OdfStyleProperty EndAngle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "end-angle"));
  public static final OdfStyleProperty HorizontalSegments =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "horizontal-segments"));
  public static final OdfStyleProperty LightingMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "lighting-mode"));
  public static final OdfStyleProperty NormalsDirection =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "normals-direction"));
  public static final OdfStyleProperty NormalsKind =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "normals-kind"));
  public static final OdfStyleProperty Dr3dShadow =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "shadow"));
  public static final OdfStyleProperty Shininess =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "shininess"));
  public static final OdfStyleProperty SpecularColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "specular-color"));
  public static final OdfStyleProperty TextureFilter =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "texture-filter"));
  public static final OdfStyleProperty TextureGenerationModeX =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "texture-generation-mode-x"));
  public static final OdfStyleProperty TextureGenerationModeY =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "texture-generation-mode-y"));
  public static final OdfStyleProperty TextureKind =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "texture-kind"));
  public static final OdfStyleProperty TextureMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "texture-mode"));
  public static final OdfStyleProperty VerticalSegments =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DR3D, "vertical-segments"));
  public static final OdfStyleProperty AutoGrowHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "auto-grow-height"));
  public static final OdfStyleProperty AutoGrowWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "auto-grow-width"));
  public static final OdfStyleProperty Blue =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "blue"));
  public static final OdfStyleProperty CaptionAngle =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-angle"));
  public static final OdfStyleProperty CaptionAngleType =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-angle-type"));
  public static final OdfStyleProperty CaptionEscape =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-escape"));
  public static final OdfStyleProperty CaptionEscapeDirection =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-escape-direction"));
  public static final OdfStyleProperty CaptionFitLineLength =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-fit-line-length"));
  public static final OdfStyleProperty CaptionGap =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-gap"));
  public static final OdfStyleProperty CaptionLineLength =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-line-length"));
  public static final OdfStyleProperty CaptionType =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "caption-type"));
  public static final OdfStyleProperty ColorInversion =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "color-inversion"));
  public static final OdfStyleProperty ColorMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "color-mode"));
  public static final OdfStyleProperty Contrast =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "contrast"));
  public static final OdfStyleProperty DecimalPlaces =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "decimal-places"));
  public static final OdfStyleProperty DrawAspect =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "draw-aspect"));
  public static final OdfStyleProperty EndGuide =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "end-guide"));
  public static final OdfStyleProperty EndLineSpacingHorizontal =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "end-line-spacing-horizontal"));
  public static final OdfStyleProperty EndLineSpacingVertical =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "end-line-spacing-vertical"));
  public static final OdfStyleProperty Fill =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill"));
  public static final OdfStyleProperty FillColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-color"));
  public static final OdfStyleProperty FillGradientName =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-gradient-name"));
  public static final OdfStyleProperty FillHatchName =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-hatch-name"));
  public static final OdfStyleProperty FillHatchSolid =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-hatch-solid"));
  public static final OdfStyleProperty FillImageHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-image-height"));
  public static final OdfStyleProperty FillImageName =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-image-name"));
  public static final OdfStyleProperty FillImageRefPoint =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-image-ref-point"));
  public static final OdfStyleProperty FillImageRefPointX =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-image-ref-point-x"));
  public static final OdfStyleProperty FillImageRefPointY =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-image-ref-point-y"));
  public static final OdfStyleProperty FillImageWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fill-image-width"));
  public static final OdfStyleProperty FitToContour =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fit-to-contour"));
  public static final OdfStyleProperty FitToSize =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "fit-to-size"));
  public static final OdfStyleProperty FrameDisplayBorder =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "frame-display-border"));
  public static final OdfStyleProperty FrameDisplayScrollbar =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "frame-display-scrollbar"));
  public static final OdfStyleProperty FrameMarginHorizontal =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "frame-margin-horizontal"));
  public static final OdfStyleProperty FrameMarginVertical =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "frame-margin-vertical"));
  public static final OdfStyleProperty Gamma =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "gamma"));
  public static final OdfStyleProperty GradientStepCount =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "gradient-step-count"));
  public static final OdfStyleProperty Green =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "green"));
  public static final OdfStyleProperty GuideDistance =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "guide-distance"));
  public static final OdfStyleProperty GuideOverhang =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "guide-overhang"));
  public static final OdfStyleProperty ImageOpacity =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "image-opacity"));
  public static final OdfStyleProperty LineDistance =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "line-distance"));
  public static final OdfStyleProperty Luminance =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "luminance"));
  public static final OdfStyleProperty MarkerEnd =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "marker-end"));
  public static final OdfStyleProperty MarkerEndCenter =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "marker-end-center"));
  public static final OdfStyleProperty MarkerEndWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "marker-end-width"));
  public static final OdfStyleProperty MarkerStart =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "marker-start"));
  public static final OdfStyleProperty MarkerStartCenter =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "marker-start-center"));
  public static final OdfStyleProperty MarkerStartWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "marker-start-width"));
  public static final OdfStyleProperty MeasureAlign =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "measure-align"));
  public static final OdfStyleProperty MeasureVerticalAlign =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "measure-vertical-align"));
  public static final OdfStyleProperty OleDrawAspect =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "ole-draw-aspect"));
  public static final OdfStyleProperty Opacity =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "opacity"));
  public static final OdfStyleProperty OpacityName =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "opacity-name"));
  public static final OdfStyleProperty Parallel =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "parallel"));
  public static final OdfStyleProperty Placing =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "placing"));
  public static final OdfStyleProperty Red =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "red"));
  public static final OdfStyleProperty SecondaryFillColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "secondary-fill-color"));
  public static final OdfStyleProperty DrawShadow =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "shadow"));
  public static final OdfStyleProperty ShadowColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "shadow-color"));
  public static final OdfStyleProperty ShadowOffsetX =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "shadow-offset-x"));
  public static final OdfStyleProperty ShadowOffsetY =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "shadow-offset-y"));
  public static final OdfStyleProperty ShadowOpacity =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "shadow-opacity"));
  public static final OdfStyleProperty ShowUnit =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "show-unit"));
  public static final OdfStyleProperty StartGuide =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "start-guide"));
  public static final OdfStyleProperty StartLineSpacingHorizontal =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "start-line-spacing-horizontal"));
  public static final OdfStyleProperty StartLineSpacingVertical =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "start-line-spacing-vertical"));
  public static final OdfStyleProperty Stroke =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "stroke"));
  public static final OdfStyleProperty StrokeDash =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "stroke-dash"));
  public static final OdfStyleProperty StrokeDashNames =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "stroke-dash-names"));
  public static final OdfStyleProperty StrokeLinejoin =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "stroke-linejoin"));
  public static final OdfStyleProperty SymbolColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "symbol-color"));
  public static final OdfStyleProperty TextareaHorizontalAlign =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "textarea-horizontal-align"));
  public static final OdfStyleProperty TextareaVerticalAlign =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "textarea-vertical-align"));
  public static final OdfStyleProperty TileRepeatOffset =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "tile-repeat-offset"));
  public static final OdfStyleProperty Unit =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "unit"));
  public static final OdfStyleProperty VisibleAreaHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "visible-area-height"));
  public static final OdfStyleProperty VisibleAreaLeft =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "visible-area-left"));
  public static final OdfStyleProperty VisibleAreaTop =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "visible-area-top"));
  public static final OdfStyleProperty VisibleAreaWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "visible-area-width"));
  public static final OdfStyleProperty WrapInfluenceOnPosition =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.DRAW, "wrap-influence-on-position"));
  public static final OdfStyleProperty BackgroundColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "background-color"));
  public static final OdfStyleProperty Border =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "border"));
  public static final OdfStyleProperty BorderBottom =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "border-bottom"));
  public static final OdfStyleProperty BorderLeft =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "border-left"));
  public static final OdfStyleProperty BorderRight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "border-right"));
  public static final OdfStyleProperty BorderTop =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "border-top"));
  public static final OdfStyleProperty Clip =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "clip"));
  public static final OdfStyleProperty Margin =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin"));
  public static final OdfStyleProperty MarginBottom =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-bottom"));
  public static final OdfStyleProperty MarginLeft =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-left"));
  public static final OdfStyleProperty MarginRight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-right"));
  public static final OdfStyleProperty MarginTop =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "margin-top"));
  public static final OdfStyleProperty MaxHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "max-height"));
  public static final OdfStyleProperty MaxWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "max-width"));
  public static final OdfStyleProperty MinHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "min-height"));
  public static final OdfStyleProperty MinWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "min-width"));
  public static final OdfStyleProperty Padding =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "padding"));
  public static final OdfStyleProperty PaddingBottom =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "padding-bottom"));
  public static final OdfStyleProperty PaddingLeft =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "padding-left"));
  public static final OdfStyleProperty PaddingRight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "padding-right"));
  public static final OdfStyleProperty PaddingTop =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "padding-top"));
  public static final OdfStyleProperty WrapOption =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.FO, "wrap-option"));
  public static final OdfStyleProperty BackgroundTransparency =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "background-transparency"));
  public static final OdfStyleProperty BorderLineWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "border-line-width"));
  public static final OdfStyleProperty BorderLineWidthBottom =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "border-line-width-bottom"));
  public static final OdfStyleProperty BorderLineWidthLeft =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "border-line-width-left"));
  public static final OdfStyleProperty BorderLineWidthRight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "border-line-width-right"));
  public static final OdfStyleProperty BorderLineWidthTop =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "border-line-width-top"));
  public static final OdfStyleProperty Editable =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "editable"));
  public static final OdfStyleProperty FlowWithText =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "flow-with-text"));
  public static final OdfStyleProperty HorizontalPos =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "horizontal-pos"));
  public static final OdfStyleProperty HorizontalRel =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "horizontal-rel"));
  public static final OdfStyleProperty Mirror =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "mirror"));
  public static final OdfStyleProperty NumberWrappedParagraphs =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "number-wrapped-paragraphs"));
  public static final OdfStyleProperty OverflowBehavior =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "overflow-behavior"));
  public static final OdfStyleProperty PrintContent =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "print-content"));
  public static final OdfStyleProperty Protect =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "protect"));
  public static final OdfStyleProperty RelHeight =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "rel-height"));
  public static final OdfStyleProperty RelWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "rel-width"));
  public static final OdfStyleProperty Repeat =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "repeat"));
  public static final OdfStyleProperty RunThrough =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "run-through"));
  public static final OdfStyleProperty StyleShadow =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "shadow"));
  public static final OdfStyleProperty ShrinkToFit =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "shrink-to-fit"));
  public static final OdfStyleProperty VerticalPos =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "vertical-pos"));
  public static final OdfStyleProperty VerticalRel =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "vertical-rel"));
  public static final OdfStyleProperty Wrap =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "wrap"));
  public static final OdfStyleProperty WrapContour =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "wrap-contour"));
  public static final OdfStyleProperty WrapContourMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "wrap-contour-mode"));
  public static final OdfStyleProperty WrapDynamicThreshold =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "wrap-dynamic-threshold"));
  public static final OdfStyleProperty WritingMode =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.STYLE, "writing-mode"));
  public static final OdfStyleProperty FillRule =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "fill-rule"));
  public static final OdfStyleProperty Height =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "height"));
  public static final OdfStyleProperty StrokeColor =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "stroke-color"));
  public static final OdfStyleProperty StrokeLinecap =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "stroke-linecap"));
  public static final OdfStyleProperty StrokeOpacity =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "stroke-opacity"));
  public static final OdfStyleProperty StrokeWidth =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "stroke-width"));
  public static final OdfStyleProperty Width =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.SVG, "width"));
  public static final OdfStyleProperty X =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties, OdfName.newName(OdfDocumentNamespace.SVG, "x"));
  public static final OdfStyleProperty Y =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties, OdfName.newName(OdfDocumentNamespace.SVG, "y"));
  public static final OdfStyleProperty AnchorPageNumber =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "anchor-page-number"));
  public static final OdfStyleProperty AnchorType =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "anchor-type"));
  public static final OdfStyleProperty Animation =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "animation"));
  public static final OdfStyleProperty AnimationDelay =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "animation-delay"));
  public static final OdfStyleProperty AnimationDirection =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "animation-direction"));
  public static final OdfStyleProperty AnimationRepeat =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "animation-repeat"));
  public static final OdfStyleProperty AnimationStartInside =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "animation-start-inside"));
  public static final OdfStyleProperty AnimationSteps =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "animation-steps"));
  public static final OdfStyleProperty AnimationStopInside =
      OdfStyleProperty.get(
          OdfStylePropertiesSet.GraphicProperties,
          OdfName.newName(OdfDocumentNamespace.TEXT, "animation-stop-inside"));
}
