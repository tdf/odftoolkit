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
package org.odftoolkit.odfdom.changes;

import java.util.ArrayList;
import java.util.List;

/** The comment component. */
class CommentComponent extends CachedComponent {

  private static final long serialVersionUID = 1L;
  private String mCommentName;
  private String mAuthor;
  private String mDate;
  private List<Integer> mCommentPosition;
  private boolean isInHeaderFooter = false;

  public boolean isInHeaderFooter() {
    return isInHeaderFooter;
  }

  public void setInHeaderFooter() {
    isInHeaderFooter = true;
  }

  public CommentComponent(List<Integer> start, String commentName) {
    mCommentPosition = new ArrayList<>(start);
    mCommentName = commentName;
  }

  public List<Integer> getComponentPosition() {
    return mCommentPosition;
  }

  public String getCommentName() {
    return mCommentName;
  }

  public void setAuthor(String a) {
    mAuthor = a;
  }

  public void setDate(String a) {
    mDate = a;
  }

  public String getAuthor() {
    return mAuthor;
  }

  public String getDate() {
    return mDate;
  }
}
