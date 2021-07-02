/**
 * **********************************************************************
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
 * <p>*********************************************************************
 */
package org.odftoolkit.odfdom.schema2template_maven_plugin;

import static org.codehaus.plexus.PlexusTestCase.getBasedir;

import java.io.File;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class CodeGenTest extends AbstractMojoTestCase {

  private static final String CONFIG_PATH =
      "src"
          + File.separator
          + "test"
          + File.separator
          + "resources"
          + File.separator
          + "unit"
          + File.separator
          + "basic-test-plugin-config.xml";

  /**
   * @throws java.lang.Exception
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  protected void setUp() throws Exception {
    // required for mojo lookups to work
    super.setUp();
  }

  /** @throws Exception */
  public void testMojoGoal() throws Exception {
    System.err.println("getBasedir" + getBasedir());
    File testPom = new File(getBasedir(), CONFIG_PATH);

    CodeGenMojo mojo = (CodeGenMojo) lookupMojo("codegen", testPom);

    assertNotNull(mojo);
  }
}
