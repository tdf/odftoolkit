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
 * <p>*********************************************************************
 */
package org.odftoolkit.odfdom.dom;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.doc.OdfDocument;
import org.odftoolkit.odfdom.utils.ResourceUtilities;

public class DefaultNamespaceTest {

  public DefaultNamespaceTest() {}

  /**
   * There should be no other test file earlier be loaded otherwise a class cache would precedence
   * the namespace fix
   */
  private static final String SOURCE_DEFAULT_NAMESPACE = "default_namespace.ods";

  private static final String TARGET_DEFAULT_NAMESPACE = "default_namespace__out.ods";

  @Test
  public void testDefaultNamespace() {
    OdfDocument odfDocument;
    try {
      odfDocument =
          OdfDocument.loadDocument(
              ResourceUtilities.getAbsoluteInputPath(SOURCE_DEFAULT_NAMESPACE));
      OdfContentDom content = odfDocument.getContentDom();
      Assert.assertTrue(content.getRootElement().getLocalName().equals("document-content"));
      odfDocument.save(ResourceUtilities.getAbsoluteOutputPath(TARGET_DEFAULT_NAMESPACE));
    } catch (Exception e) {
      Logger.getLogger(DefaultNamespaceTest.class.getName())
          .log(Level.SEVERE, e.getMessage() + ExceptionUtils.getStackTrace(e), e);
      Assert.fail(e.getMessage());
    }
  }
}
