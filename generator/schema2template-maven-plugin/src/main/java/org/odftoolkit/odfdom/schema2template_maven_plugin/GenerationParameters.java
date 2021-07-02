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
 * <p>********************************************************************
 */
package org.odftoolkit.odfdom.schema2template_maven_plugin;

public class GenerationParameters {

  /** @Parameter @required */
  private String mTemplateBaseDir;

  /** @Parameter @required */
  private String mTemplateFileName;

  /** @Parameter @required */
  private String mXmlGrammarFilePath;

  /** @Parameter @optional */
  private String mContextInfo;

  /** @Parameter @required */
  private String mTargetBaseDir;

  /** @Parameter @required */
  private String mTargetFileName;

  public GenerationParameters() {
    System.err.println("YEIIHAAA!!");
  }

  /**
   * Capsulates a set of parameter required to do a schema2template transformation and generate
   * output from given templates and XML grammar.
   *
   * @param templateBaseDir
   * @param templateFileName
   * @param xmlGrammarFilePath
   * @param contextInfo
   * @param targetBaseDir
   * @param targetFileName
   */
  public GenerationParameters(
      String templateBaseDir,
      String templateFileName,
      String xmlGrammarFilePath,
      String contextInfo,
      String targetBaseDir,
      String targetFileName) {
    mTemplateBaseDir = templateBaseDir;
    mTemplateFileName = templateFileName;
    mXmlGrammarFilePath = xmlGrammarFilePath;
    mContextInfo = contextInfo;
    mTargetBaseDir = targetBaseDir;
    mTargetFileName = targetFileName;
  }

  public String getTemplateBaseDir() {
    return mTemplateBaseDir;
  }

  public void setTemplateBaseDir(String templateBaseDir) {
    mTemplateBaseDir = templateBaseDir;
  }

  public String getTemplateFileName() {
    return mTemplateFileName;
  }

  public void setTemplateFileName(String templateFileName) {
    mTemplateFileName = templateFileName;
  }

  public String getXmlGrammarFilePath() {
    return mXmlGrammarFilePath;
  }

  public void setXmlGrammarFilePath(String xmlGrammarFilePath) {
    mXmlGrammarFilePath = xmlGrammarFilePath;
  }

  public String getContextInfo() {
    return mContextInfo;
  }

  public void getContextInfo(String contextInfo) {
    mContextInfo = contextInfo;
  }

  public String getTargetBaseDir() {
    return mTargetBaseDir;
  }

  public void setTargetBaseDir(String targetBaseDir) {
    mTargetBaseDir = targetBaseDir;
  }

  public String getTargetFileName() {
    return mTargetFileName;
  }

  public void setTargetFileName(String targetFileName) {
    mTargetFileName = targetFileName;
  }
}
