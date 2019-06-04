/************************************************************************
 *
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER
 *
 * Copyright 2008, 2010 Oracle and/or its affiliates. All rights reserved.
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
 ************************************************************************/
package org.odftoolkit.odfdom.pkg.rdfa;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.rootdev.javardfa.StatementSink;

import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.pkg.OdfFileSaxHandler;
import org.w3c.dom.Node;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shared.PrefixMapping.IllegalPrefixException;

/**
 * To cache the Jena RDF triples parsed from RDFaParser
 *
 */
public class JenaSink implements StatementSink {

//	private OdfFileSaxHandler odf;
	private Node contextNode;
	private OdfFileDom mFileDom;
    private Map<String, Resource> bnodeLookup;
    private URIExtractor extractor;
    private EvalContext context;

    public JenaSink(OdfFileDom mFileDom) {
		this.mFileDom = mFileDom;
		this.bnodeLookup = new HashMap<String, Resource>();
    }

    //@Override
    public void start() {
        bnodeLookup = new HashMap<String, Resource>();
    }

    //@Override
    public void end() {
        bnodeLookup = null;
    }

    //@Override
    public void addObject(String subject, String predicate, String object) {
    	Model model =getContextModel();
        Resource s = getResource(model, subject.trim());
        Property p = model.createProperty(predicate.trim());
        Resource o = getResource(model, object.trim());
        model.add(s, p, o);
    }

    //@Override
    public void addLiteral(String subject, String predicate, String lex, String lang, String datatype) {
    	if (lex.isEmpty()){
    		return;
    	}
    	Model model =getContextModel();
    	Resource s = getResource(model, subject.trim());
        Property p = model.createProperty(predicate.trim());
        Literal o;
        if (lang == null && datatype == null) {
            o = model.createLiteral(lex.trim());
        } else if (lang != null) {
            o = model.createLiteral(lex.trim(), lang.trim());
        } else {
            o = model.createTypedLiteral(lex.trim(), datatype.trim());
        }
        model.add(s, p, o);
    }

    private Resource getResource(Model model, String res) {
        if (res.startsWith("_:")) {
            if (bnodeLookup.containsKey(res)) {
                return bnodeLookup.get(res);
            }
            Resource bnode = model.createResource();
            bnodeLookup.put(res, bnode);
            return bnode;
        } else {
            return model.createResource(res);
        }
    }

    public void addPrefix(String prefix, String uri) {
//    	Model model =getContextModel();
//        try {
//            model.setNsPrefix(prefix.trim(), uri.trim());
//        } catch (IllegalPrefixException e) {
//        }
    }

    public void setBase(String base) {}

	private Model getContextModel() {
		Map<Node, Model> cache = this.mFileDom.getInContentMetadataCache();
		Model model = cache.get(contextNode);
		if (model == null) {
			model = ModelFactory.createDefaultModel();
			this.mFileDom.getInContentMetadataCache().put(
					contextNode, model);
		}
		return model;
	}

	public Node getContextNode() {
		return contextNode;
	}

	public void setContextNode(Node contextNode) {
		this.contextNode = contextNode;
	}

	public URIExtractor getExtractor() {
		return extractor;
	}

	public void setExtractor(URIExtractor extractor) {
		this.extractor = extractor;
	}

	public EvalContext getContext() {
		return context;
	}

	public void setContext(EvalContext context) {
		this.context = context;
	}


//    // Namespace methods
//    public void setNamespaceURI(String prefix, String uri) {
//        if (uri.length() == 0) {
//            uri = base;
//        }
//        if (xmlnsMap == Collections.EMPTY_MAP) xmlnsMap = new HashMap<String, String>();
//        xmlnsMap.put(prefix, uri);
//    }

}
