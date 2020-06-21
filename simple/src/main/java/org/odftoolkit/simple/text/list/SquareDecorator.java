/*
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
*/

package org.odftoolkit.simple.text.list;

import org.odftoolkit.simple.Document;

/**
 * SquareDecorator is an implementation of the ListDecorator interface, decorates a given List as
 * square list. User can extend this class and realize their own list and list item style. For
 * example, set a specifies list item with red color.
 *
 * <p>A SquareDecorator can be reused in the same Document.
 *
 * @since 0.4
 */
public class SquareDecorator extends BulletDecoratorBase {
  private static String DEFAULT_NAME = "Simple_Default_Square_List";

  /**
   * Constructor with Document.
   *
   * @param doc the Document which this BulletDecorator will be used on.
   */
  public SquareDecorator(Document doc) {
    super(doc, DEFAULT_NAME, "Bullet_20_Symbols", DEFAULT_SQUARE_CHAR);
  }
}
