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
package org.odftoolkit.odfdom.utils;

import org.w3c.dom.Node;

/**
 * Recursive function traversing nodes of the XML tree depth first. Providing <code>apply</code>
 * function for the user to apply actions on every node with knowledge of depths.
 */
public abstract class NodeAction<T> {
  protected abstract void apply(Node node, T arg, int depth) throws Exception;

  private int depth = 0;

  public void performAction(Node node, T arg) throws Exception {
    while (node != null) {
      apply(node, arg, depth);
      depth++;
      performAction(node.getFirstChild(), arg);
      depth--;
      node = node.getNextSibling();
    }
  }
}
