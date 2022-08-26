/**
 * *********************************************************************
 *
 * <p>DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
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
 * <p>*****************************************************************
 */
package schema2template.example.odf;

import static schema2template.example.odf.BuildEnvConstants.ODF_GRAMMAR_BASE_DIR;

/** Contains all test relevant constants related to ODF */
class OdfConstants {

  /**
   * The GrammarID defines the part across specifciations to make it comparable (e.g. for API
   * history)
   */
  public enum GrammarID {
    ODF_MANIFEST("odf-package-manifest"),
    ODF_SIGNATURE("odf-package-digital-signature"),
    ODF_SCHEMA("odf-schema");

    /** defines the part across specifciations to make it comparable (e.g. for API history) */
    public final String ID;

    GrammarID(String grammarID) {
      this.ID = grammarID;
    }
  }

  /**
   * This enum contains all information for code generations for each part of an existing
   * OpenDocument specification.
   */
  public enum OdfSpecificationPart {
    ODF_1_3_PACKAGE_MANIFEST(
        "1.3",
        GrammarID.ODF_MANIFEST,
        "OpenDocument-v1.3-manifest-schema.rng",
        ODF13_MANIFEST_ELEMENT_NUMBER,
        ODF13_MANIFEST_ATTRIBUTE_NUMBER,
        ODF13_MANIFEST_ELEMENTS_WITH_DUPLICATES,
        ODF13_MANIFEST_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_3_PACKAGE_SIGNATURE(
        "1.3",
        GrammarID.ODF_SIGNATURE,
        "OpenDocument-v1.3-dsig-schema.rng",
        ODF13_SIGNATURE_ELEMENT_NUMBER,
        ODF13_SIGNATURE_ATTRIBUTE_NUMBER,
        ODF13_SIGNATURE_ELEMENTS_WITH_DUPLICATES,
        ODF13_SIGNATURE_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_3_SCHEMA(
        "1.3",
        GrammarID.ODF_SCHEMA,
        "OpenDocument-v1.3-schema.rng",
        ODF13_ELEMENT_NUMBER,
        ODF13_ATTRIBUTE_NUMBER,
        ODF13_ELEMENTS_WITH_DUPLICATES,
        ODF13_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_2_PACKAGE_MANIFEST(
        "1.2",
        GrammarID.ODF_MANIFEST,
        "OpenDocument-v1.2-os-manifest-schema.rng",
        ODF12_MANIFEST_ELEMENT_NUMBER,
        ODF12_MANIFEST_ATTRIBUTE_NUMBER,
        ODF12_MANIFEST_ELEMENTS_WITH_DUPLICATES,
        ODF12_MANIFEST_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_2_PACKAGE_SIGNATURE(
        "1.2",
        GrammarID.ODF_SIGNATURE,
        "OpenDocument-v1.2-os-dsig-schema.rng",
        ODF12_SIGNATURE_ELEMENT_NUMBER,
        ODF12_SIGNATURE_ATTRIBUTE_NUMBER,
        ODF12_SIGNATURE_ELEMENTS_WITH_DUPLICATES,
        ODF12_SIGNATURE_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_2_SCHEMA(
        "1.2",
        GrammarID.ODF_SCHEMA,
        "OpenDocument-v1.2-os-schema.rng",
        ODF12_ELEMENT_NUMBER,
        ODF12_ATTRIBUTE_NUMBER,
        ODF12_ELEMENTS_WITH_DUPLICATES,
        ODF12_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_1_PACKAGE_MANIFEST(
        "1.1",
        GrammarID.ODF_MANIFEST,
        "OpenDocument-manifest-schema-v1.1.rng",
        ODF11_MANIFEST_ELEMENT_NUMBER,
        ODF11_MANIFEST_ATTRIBUTE_NUMBER,
        ODF11_MANIFEST_ELEMENTS_WITH_DUPLICATES,
        ODF11_MANIFEST_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_1_SCHEMA(
        "1.1",
        GrammarID.ODF_SCHEMA,
        "OpenDocument-schema-v1.1.rng",
        ODF11_ELEMENT_NUMBER,
        ODF11_ATTRIBUTE_NUMBER,
        ODF11_ELEMENTS_WITH_DUPLICATES,
        ODF11_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_0_PACKAGE_MANIFEST(
        "1.0",
        GrammarID.ODF_MANIFEST,
        "OpenDocument-manifest-schema-v1.0-os.rng",
        ODF10_MANIFEST_ELEMENT_NUMBER,
        ODF10_MANIFEST_ATTRIBUTE_NUMBER,
        ODF10_MANIFEST_ELEMENTS_WITH_DUPLICATES,
        ODF10_MANIFEST_ATTRIBUTES_WITH_DUPLICATES),
    ODF_1_0_SCHEMA(
        "1.0",
        GrammarID.ODF_SCHEMA,
        "OpenDocument-schema-v1.0-os.rng",
        ODF10_ELEMENT_NUMBER,
        ODF10_ATTRIBUTE_NUMBER,
        ODF10_ELEMENTS_WITH_DUPLICATES,
        ODF10_ATTRIBUTES_WITH_DUPLICATES);

    /** Sortable ODF version number, for instance: 1.3, 1.2 */
    public final String grammarVersion;
    /**
     * grammarID defines the part across specifciations to make it comparable (e.g. for API history)
     */
    public final String grammarID;
    /** the absolute path to the ODF grammar */
    public final String grammarPath;
    /**
     * amount of XML elements defined by a certain ODF grammar given by MSV (MultiSchemaValidator)
     */
    public final int elementNo;
    /**
     * amount of XML attributes defined by a certain ODF grammar given by MSV (MultiSchemaValidator)
     */
    public final int attributeNo;
    /**
     * amount of all distinct definitions of XML elements of a certain ODF grammar given by MSV
     * (MultiSchemaValidator)
     */
    public final int elementNoWithDuplicates;
    /**
     * amount of all distinct definitions of XML attributes of a certain ODF grammar given by MSV
     * (MultiSchemaValidator)
     */
    public final int attributeNoWithDuplicates;

    /**
     * @param grammarVersion Sortable ODF version number, for instance: 1.3, 1.2
     * @param grammarID defines the part across specifciations to make it comparable (e.g. for API
     *     history)
     * @param grammarPath the absolute path to the ODF grammar
     * @param elementNo amount of XML elements defined by a certain ODF grammar given by MSV
     *     (MultiSchemaValidator)
     * @param attributeNo amount of XML attributes defined by a certain ODF grammar given by MSV
     *     (MultiSchemaValidator)
     * @param elementNoWithDuplicates amount of all distinct definitions of XML elements of a
     *     certain ODF grammar given by MSV (MultiSchemaValidator)
     * @param attributeNoWithDuplicates amount of all distinct definitions of XML attributes of a
     *     certain ODF grammar given by MSV (MultiSchemaValidator)
     */
    OdfSpecificationPart(
        String grammarVersion,
        GrammarID grammarID,
        String grammarPath,
        int elementNo,
        int attributeNo,
        int elementNoWithDuplicates,
        int attributeNoWithDuplicates) {
      this.grammarVersion = grammarVersion;
      this.grammarID = grammarID.ID;
      this.grammarPath = ODF_GRAMMAR_BASE_DIR + grammarPath;
      this.elementNo = elementNo;
      this.attributeNo = attributeNo;
      this.elementNoWithDuplicates = elementNoWithDuplicates;
      this.attributeNoWithDuplicates = attributeNoWithDuplicates;
    }
  }

  /**
   * Expresses the amount of elements in ODF 1.1. There are some issues in the schema that have to
   * be fixed before the full number can be returned by MSV: Reference table-table-template is never
   * used, therefore several elements are not taking into account:: "table:body"
   * "table:even-columns" "table:even-rows" "table:first-column" "table:first-row"
   * "table:last-column" "table:last-row" "table:odd-columns" "table:odd-rows"
   * "table:table-template" NOTE: Ignoring the '*' there can be 525 elements parsed, but with fixed
   * schema it should be 535.
   */
  // ToDo: 535 - by search/Replace using RNGSchema and tools, prior exchange <name> to element or
  // attribute declaration
  private static final int ODF10_ELEMENT_NUMBER = 498;

  private static final int ODF11_ELEMENT_NUMBER = 500;
  private static final int ODF12_ELEMENT_NUMBER = 599;
  private static final int ODF13_ELEMENT_NUMBER = 606;

  private static final int ODF10_ELEMENTS_WITH_DUPLICATES = 505;
  private static final int ODF11_ELEMENTS_WITH_DUPLICATES = 507;
  private static final int ODF12_ELEMENTS_WITH_DUPLICATES = 606;
  private static final int ODF13_ELEMENTS_WITH_DUPLICATES = 613;

  private static final int ODF13_MANIFEST_ELEMENT_NUMBER = 15;
  private static final int ODF13_MANIFEST_ELEMENTS_WITH_DUPLICATES = 15;
  private static final int ODF13_MANIFEST_ATTRIBUTE_NUMBER = 16;
  private static final int ODF13_MANIFEST_ATTRIBUTES_WITH_DUPLICATES = 18;

  private static final int ODF13_SIGNATURE_ELEMENT_NUMBER = 3;
  private static final int ODF13_SIGNATURE_ELEMENTS_WITH_DUPLICATES = 3;
  private static final int ODF13_SIGNATURE_ATTRIBUTE_NUMBER = 2;
  private static final int ODF13_SIGNATURE_ATTRIBUTES_WITH_DUPLICATES = 2;

  private static final int ODF12_MANIFEST_ELEMENT_NUMBER = 7;
  private static final int ODF12_MANIFEST_ELEMENTS_WITH_DUPLICATES = 7;
  private static final int ODF12_MANIFEST_ATTRIBUTE_NUMBER = 15;
  private static final int ODF12_MANIFEST_ATTRIBUTES_WITH_DUPLICATES = 16;

  private static final int ODF12_SIGNATURE_ELEMENT_NUMBER = 3;
  private static final int ODF12_SIGNATURE_ELEMENTS_WITH_DUPLICATES = 3;
  private static final int ODF12_SIGNATURE_ATTRIBUTE_NUMBER = 2;
  private static final int ODF12_SIGNATURE_ATTRIBUTES_WITH_DUPLICATES = 2;

  private static final int ODF11_MANIFEST_ELEMENT_NUMBER = 5;
  private static final int ODF11_MANIFEST_ELEMENTS_WITH_DUPLICATES = 5;
  private static final int ODF11_MANIFEST_ATTRIBUTE_NUMBER = 10;
  private static final int ODF11_MANIFEST_ATTRIBUTES_WITH_DUPLICATES = 10;

  private static final int ODF10_MANIFEST_ELEMENT_NUMBER = 5;
  private static final int ODF10_MANIFEST_ELEMENTS_WITH_DUPLICATES = 5;
  private static final int ODF10_MANIFEST_ATTRIBUTE_NUMBER = 10;
  private static final int ODF10_MANIFEST_ATTRIBUTES_WITH_DUPLICATES = 10;

  /**
   * /** /** Expresses the amount of attributes in ODF 1.1. There are some issues in the schema that
   * have to be fixed before the full number can be returned by MSV: Following references are never
   * used, therefore its attribute is not taking into account:: draw-glue-points-attlist with
   * "draw:escape-direction" office-process-content with "office:process-content" (DEPRECATED in
   * ODF1.2 only on foreign elements)
   *
   * <p>Following attributes are member of the not referenced element "table:table-template":
   * "text:first-row-end-column" "text:first-row-start-column" "text:last-row-end-column"
   * "text:last-row-start-column" "text:paragraph-style-name"
   *
   * <p>NOTE: Ignoring the '*' there can be 1162 elements parsed, but with fixed schema it should be
   * 1169.
   */
  private static final int ODF10_ATTRIBUTE_NUMBER = 752;

  private static final int ODF10_ATTRIBUTES_WITH_DUPLICATES = 835;

  // ToDo: 1169 - by search/Replace using RNGSchema and tools, prior exchange <name> to element or
  // attribute declaration
  private static final int ODF11_ATTRIBUTE_NUMBER = 757; // 1163;
  private static final int ODF11_ATTRIBUTES_WITH_DUPLICATES = 840;

  // in RNG 1301 as there is one deprecated attribute on foreign elements not referenced (ie.
  // @office:process-content)
  private static final int ODF12_ATTRIBUTE_NUMBER = 1301;
  private static final int ODF12_ATTRIBUTES_WITH_DUPLICATES = 1417;

  // in RNG 1301 as there is one deprecated attribute on foreign elements not referenced (ie.
  // @office:process-content)
  private static final int ODF13_ATTRIBUTE_NUMBER = 1317;
  private static final int ODF13_ATTRIBUTES_WITH_DUPLICATES = 1434;
}
