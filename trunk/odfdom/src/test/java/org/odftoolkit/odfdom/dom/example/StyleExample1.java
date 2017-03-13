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
package org.odftoolkit.odfdom.dom.example;


class StyleExample1 {
/* todo: refactor            
    public static void main(String[] args) {

        try {
            OdfDocument odfdoc = OdfDocument.OdfDocument("test/resources/test1.odt");
            LOG.info("parsed document.");
            
            OdfElement e = (OdfElement) odfdoc.getContentDom().getDocumentElement();
            NodeAction dumpStyles = new NodeAction() {
                protected void apply(Node node, Object arg, int depth) {
                    String indent = new String();
                    for (int i=0; i<depth; i++) indent += "  ";
                    LOG.info(indent + node.getNodeName());
                    if (node.getNodeType() == Node.TEXT_NODE) {
                        LOG.info(": " + node.getNodeValue());
                    }
                    LOG.info();
                    if (node instanceof OdfStylableElement) {
                        try {
                            LOG.info(indent + "-style info...");
                            OdfStylableElement se = (OdfStylableElement) node;
                            OdfStyle ds = se.getDocumentStyle();
                            OdfStyle ls = se.getAutomaticStyle();
                            if (ls != null) {
                                LOG.info(indent + "-OdfLocalStyle: " + ls);
                            }
                            if (ds != null) {
                                LOG.info(indent + "-OdfDocumentStyle: " + ds);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(StyleExample1.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            };       
            dumpStyles.performAction(e, null);                
            // serializeXml(e, System.out);                                    
        } catch (Exception e) {
            e.printStackTrace();
        }

    } */
}
