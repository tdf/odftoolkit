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
package org.odftoolkit.odfvalidator;

/** abstract Base class for OpenDocument filetype classes */
public abstract class ODFMediaTypes {

  public static final String TEXT_MEDIA_TYPE = "application/vnd.oasis.opendocument.text";
  public static final String TEXT_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.text-template";
  public static final String GRAPHICS_MEDIA_TYPE = "application/vnd.oasis.opendocument.graphics";
  public static final String GRAPHICS_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.graphics-template";
  public static final String PRESENTATION_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.presentation";
  public static final String PRESENTATION_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.presentation-template";
  public static final String SPREADSHEET_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.spreadsheet";
  public static final String SPREADSHEET_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.spreadsheet-template";
  public static final String CHART_MEDIA_TYPE = "application/vnd.oasis.opendocument.chart";
  public static final String CHART_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.chart-template";
  public static final String IMAGE_MEDIA_TYPE = "application/vnd.oasis.opendocument.image";
  public static final String IMAGE_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.image-template";
  public static final String FORMULA_MEDIA_TYPE = "application/vnd.oasis.opendocument.formula";
  public static final String FORMULA_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.formula-template";
  public static final String TEXT_MASTER_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.text-master";
  // Added in ODF 1.3 / OFFICE-2580
  public static final String TEXT_MASTER_TEMPLATE_MEDIA_TYPE =
      "application/vnd.oasis.opendocument.text-master-template";
  public static final String TEXT_WEB_MEDIA_TYPE = "application/vnd.oasis.opendocument.text-web";
  public static final String DATABASE_MEDIA_TYPE = "application/vnd.oasis.opendocument.base";
}
