/*
 * Copyright 2026 The Document Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.odftoolkit.odfdom.consumer;

import org.odftoolkit.odfdom.doc.OdfTextDocument;

public class Smoke {

  public static void main(String[] args) throws Exception {
    OdfTextDocument document = OdfTextDocument.newTextDocument();
    try {
      if (document.getContentDom() == null) {
        throw new IllegalStateException("content DOM must be available");
      }
    } finally {
      document.close();
    }
    System.out.println("JPMS_CONSUMER_OK");
  }
}
