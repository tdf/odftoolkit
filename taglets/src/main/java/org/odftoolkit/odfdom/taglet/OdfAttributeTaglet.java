/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2018-2019 The Document Foundation. All rights reserved.
 * Copyright 2009 IBM. All rights reserved.
 * Copyright 2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Use is subject to license terms.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. You can also
 * obtain a copy of the License at http://odftoolkit.org/docs/license.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.odftoolkit.odfdom.taglet;

import com.sun.source.doctree.DocTree;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.lang.model.element.Element;
import jdk.javadoc.doclet.Taglet;

/**
 * This class implements a custom taglet to the map the ODF attribute to the declaration of the ODF
 * attribute in the OpenDocument specification.
 *
 * <p>The position of the OpenDocument specification in HTML can be provided using an environment
 * variable or java system property, while the system property overrides the environment variable.
 * In case nothing is been a default path within the JavaDoc resources directory is being used.
 *
 * <p>For example the taglet <code>{&#64;odf.attribute xml:id}</code> would be resolved without
 * variable settings to <code>
 * JAVA_DOC_BASE/resources/OpenDocument-v1.3-os-part3-schema.html#attribute-xml_id
 * </code> .
 */
public class OdfAttributeTaglet implements Taglet {

  private static final Logger LOG = Logger.getLogger(OdfAttributeTaglet.class.getName());
  private static final String NAME = "odf.attribute";
  private static final String ODF_SPEC_PART1_PATH =
      "../../../../../../resources/OpenDocument-v1.3-os-part3-schema.html";
  private static final String ODF_SPEC_PART3_PATH =
      "../../../../../resources/OpenDocument-v1.3-os-part2-packages.html";
  private static String mOdfSpecPart1Path = null;
  private static String mOdfSpecPart3Path = null;
  private static Set<String> mNS_IN_PART3 = new HashSet<String>();

  // initial attribute set which should be search in part3.
  static {
    mNS_IN_PART3.add("manifest");
    mNS_IN_PART3.add("dsig");
    mNS_IN_PART3.add("ds");
  }

  /*
   * FINDING THE ABSOLUTE PATH TO THE ODF SPEC PART1 and PART3 IN HTML:
   * 1) Try to get the odfSpecPath from the Java System variable (ODF_SPEC_PATH)
   * 2) Try to get the odfSpecPath from the environment variable (ODF_SPEC_PATH)
   * 3) If both not worked, use the default path
   */
  static {
    mOdfSpecPart1Path = System.getProperty("ODF_SPEC_PART1_PATH");
    if (mOdfSpecPart1Path == null) {
      mOdfSpecPart1Path = System.getenv("ODF_SPEC_PATH");
      if (mOdfSpecPart1Path == null) {
        mOdfSpecPart1Path = ODF_SPEC_PART1_PATH;
        LOG.info("OdfSpecPart1Path was set to " + mOdfSpecPart1Path + " by class declaration.");
      } else {
        LOG.info(
            "OdfSpecPart1Path was set to "
                + mOdfSpecPart1Path
                + " by environment property 'ODF_SPEC_PATH'.");
      }
    } else {
      LOG.info(
          "OdfSpecPart1Path was set to "
              + mOdfSpecPart1Path
              + " by Java System property 'ODF_SPEC_PATH'.");
    }

    mOdfSpecPart3Path = System.getProperty("ODF_SPEC_PART3_PATH");
    if (mOdfSpecPart3Path == null) {
      mOdfSpecPart3Path = System.getenv("ODF_SPEC_PATH");
      if (mOdfSpecPart3Path == null) {
        mOdfSpecPart3Path = ODF_SPEC_PART3_PATH;
        LOG.info("OdfSpecPart3Path was set to " + mOdfSpecPart3Path + " by class declaration.");
      } else {
        LOG.info(
            "OdfSpecPart3Path was set to "
                + mOdfSpecPart3Path
                + " by environment property 'ODF_SPEC_PATH'.");
      }
    } else {
      LOG.info(
          "OdfSpecPart3Path was set to "
              + mOdfSpecPart3Path
              + " by Java System property 'ODF_SPEC_PATH'.");
    }
  }

  /** @return the name of this custom tag. */
  public String getName() {
    return NAME;
  }

  /** @return true since this tag can be used in a field doc comment */
  public boolean inField() {
    return true;
  }

  /** @return true since this tag can be used in a constructor doc comment */
  public boolean inConstructor() {
    return true;
  }

  /** @return true since this tag can be used in a method doc comment */
  public boolean inMethod() {
    return true;
  }

  /** @return true since this tag can be used in an overview doc comment */
  public boolean inOverview() {
    return true;
  }

  /** @return true since this tag can be used in a package doc comment */
  public boolean inPackage() {
    return true;
  }

  /** @return true since this */
  public boolean inType() {
    return true;
  }

  /**
   * Will return true since this is an inline tag.
   *
   * @return true since this is an inline tag.
   */
  public boolean isInlineTag() {
    return true;
  }

  /**
   * Register this Taglet.
   *
   * @param tagletMap the map to register this tag to.
   */
  public static void register(Map<String, Taglet> tagletMap) {
    OdfAttributeTaglet tag = new OdfAttributeTaglet();
    Taglet t = tagletMap.get(tag.getName());
    if (t != null) {
      tagletMap.remove(tag.getName());
    }
    tagletMap.put(tag.getName(), tag);
  }

  /**
   * Given the <code>Tag</code> representation of this custom tag, return its string representation.
   *
   * @return the string representation of the custom tag
   */
  @Override
  public String toString(List<? extends DocTree> tags, Element element) {
    // The taglet text like the following for the ODF attribute @draw:transform
    // docText = {odf.attribute draw:transform}
    String docText = tags.get(0).toString();
    int startPos = docText.lastIndexOf(" ");
    int endPos = docText.lastIndexOf(":");
    String namespace = docText.substring(startPos + 1, endPos);
    String name = docText.substring(endPos + 1, docText.length() - 1);
    String mOdfSpecPath = mOdfSpecPart1Path;
    if (mNS_IN_PART3.contains(namespace)) {
      mOdfSpecPath = mOdfSpecPart3Path;
    } else {
    }
    String fragmentIdentifier = "attribute-" + namespace + "_" + name;
    return "<a href=\""
        + mOdfSpecPath
        + "#"
        + fragmentIdentifier
        + "\">@"
        + namespace
        + ":"
        + name
        + "</a>";
  }

  @Override
  public Set<Location> getAllowedLocations() {
    throw new UnsupportedOperationException(
        "Not supported yet."); // To change body of generated methods, choose Tools | Templates.
  }
}
