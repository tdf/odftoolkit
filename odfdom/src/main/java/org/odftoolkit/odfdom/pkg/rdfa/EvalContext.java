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
package org.odftoolkit.odfdom.pkg.rdfa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.xml.namespace.NamespaceContext;

/** EvalContext modified from net.rootdev.javardfa.EvalContext */
final class EvalContext implements NamespaceContext {

  EvalContext parent;
  String base;
  String parentSubject;
  String parentObject;
  String language;
  String vocab;
  List<String> forwardProperties;
  List<String> backwardProperties;
  Map<String, String> xmlnsMap = Collections.emptyMap();
  Map<String, String> prefixMap = Collections.emptyMap();

  protected EvalContext(String base) {
    super();
    this.base = base;
    this.parentSubject = base;
    this.forwardProperties = new ArrayList<>();
    this.backwardProperties = new ArrayList<>();
  }

  public EvalContext(EvalContext toCopy) {
    super();
    this.base = toCopy.base;
    this.parentSubject = toCopy.parentSubject;
    this.parentObject = toCopy.parentObject;
    this.language = toCopy.language;
    this.forwardProperties = new ArrayList<>(toCopy.forwardProperties);
    this.backwardProperties = new ArrayList<>(toCopy.backwardProperties);
    this.parent = toCopy;
    this.vocab = toCopy.vocab;
  }

  public void setBase(String abase) {
    // This is very dodgy. We want to check if ps and po have been changed
    // from their typical values (base).
    // Base changing happens very late in the day when we're streaming, and
    // it is very fiddly to handle
    boolean setPS = Objects.equals(parentSubject, base);
    boolean setPO = Objects.equals(parentObject, base);

    if (abase.contains("#")) {
      this.base = abase.substring(0, abase.indexOf("#"));
    } else {
      this.base = abase;
    }

    if (setPS) this.parentSubject = base;
    if (setPO) this.parentObject = base;

    if (parent != null) {
      parent.setBase(base);
    }
  }

  @Override
  public String toString() {
    return String.format(
        "[\n\tBase: %s\n\tPS: %s\n\tPO: %s\n\tlang: %s\n\tIncomplete: -> %s <- %s\n]",
        base,
        parentSubject,
        parentObject,
        language,
        forwardProperties.size(),
        backwardProperties.size());
  }

  /**
   * RDFa 1.1 prefix support
   *
   * @param prefix Prefix
   * @param uri URI
   */
  public void setPrefix(String prefix, String uri) {
    if (uri.length() == 0) {
      uri = base;
    }
    if (prefixMap == Collections.EMPTY_MAP) prefixMap = new HashMap<>();
    prefixMap.put(prefix, uri);
  }

  /**
   * RDFa 1.1 prefix support.
   *
   * @param prefix
   * @return
   */
  public String getURIForPrefix(String prefix) {
    if (prefixMap.containsKey(prefix)) {
      return prefixMap.get(prefix);
    } else if (xmlnsMap.containsKey(prefix)) {
      return xmlnsMap.get(prefix);
    } else if (parent != null) {
      return parent.getURIForPrefix(prefix);
    } else {
      return null;
    }
  }

  // Namespace methods
  public void setNamespaceURI(String prefix, String uri) {
    if (uri.length() == 0) {
      uri = base;
    }
    if (xmlnsMap == Collections.EMPTY_MAP) xmlnsMap = new HashMap<>();
    xmlnsMap.put(prefix, uri);
  }

  public String getNamespaceURI(String prefix) {
    if (xmlnsMap.containsKey(prefix)) {
      return xmlnsMap.get(prefix);
    } else if (parent != null) {
      return parent.getNamespaceURI(prefix);
    } else {
      return null;
    }
  }

  public String getPrefix(String uri) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public Iterator<String> getPrefixes(String uri) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  // I'm not sure about this 1.1 term business. Reuse prefix map
  public void setTerm(String term, String uri) {
    setPrefix(term + ":", uri);
  }

  public String getURIForTerm(String term) {
    return getURIForPrefix(term + ":");
  }

  public String getBase() {
    return base;
  }

  public String getVocab() {
    return vocab;
  }
}

/*
 * (c) Copyright 2009 University of Bristol All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
