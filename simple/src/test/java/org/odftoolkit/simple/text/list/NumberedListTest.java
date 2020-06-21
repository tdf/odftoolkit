/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.odftoolkit.simple.text.list;

import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import org.odftoolkit.odfdom.incubator.doc.text.OdfTextListStyle;
import org.odftoolkit.simple.TextDocument;
import org.w3c.dom.Element;

public class NumberedListTest {

  private static final char lowerGreekAlfa = '\u03B1';

  @Test
  public void testSetExtendedNumberedGreekListDecorator() {
    try {
      TextDocument doc = TextDocument.newTextDocument();
      List greekList = doc.addList(new NumberedGreekLowerDecorator(doc));
      NumberedGreekLowerDecorator greekListdecorator =
          (NumberedGreekLowerDecorator) greekList.decorator;
      OdfTextListStyle listStyle = greekListdecorator.getListStyle();
      Element odfTextListLevelStyleNumberEle = listStyle.getFirstElementChild();
      String styleNumFormat = odfTextListLevelStyleNumberEle.getAttribute("style:num-format");
      // Test if the number style starts with lower greek alfa:
      Assert.assertTrue(styleNumFormat.indexOf(lowerGreekAlfa) == 0);
    } catch (Exception e) {
      Logger.getLogger(NumberedListTest.class.getName()).log(Level.SEVERE, null, e);
      Assert.fail(e.getMessage());
    }
  }
}
