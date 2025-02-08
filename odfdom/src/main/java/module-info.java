/*
 * Copyright 2022 The Document Foundation.
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

module org.odftoolkit.odfdom {
  exports org.odftoolkit.odfdom;
  exports org.odftoolkit.odfdom.changes;
  exports org.odftoolkit.odfdom.doc.presentation;
  exports org.odftoolkit.odfdom.doc.table;
  exports org.odftoolkit.odfdom.doc;
  exports org.odftoolkit.odfdom.dom.attribute.anim;
  exports org.odftoolkit.odfdom.dom.attribute.chart;
  exports org.odftoolkit.odfdom.dom.attribute.config;
  exports org.odftoolkit.odfdom.dom.attribute.db;
  exports org.odftoolkit.odfdom.dom.attribute.dr3d;
  exports org.odftoolkit.odfdom.dom.attribute.draw;
  exports org.odftoolkit.odfdom.dom.attribute.fo;
  exports org.odftoolkit.odfdom.dom.attribute.form;
  exports org.odftoolkit.odfdom.dom.attribute.grddl;
  exports org.odftoolkit.odfdom.dom.attribute.meta;
  exports org.odftoolkit.odfdom.dom.attribute.number;
  exports org.odftoolkit.odfdom.dom.attribute.office;
  exports org.odftoolkit.odfdom.dom.attribute.presentation;
  exports org.odftoolkit.odfdom.dom.attribute.script;
  exports org.odftoolkit.odfdom.dom.attribute.smil;
  exports org.odftoolkit.odfdom.dom.attribute.style;
  exports org.odftoolkit.odfdom.dom.attribute.svg;
  exports org.odftoolkit.odfdom.dom.attribute.table;
  exports org.odftoolkit.odfdom.dom.attribute.text;
  exports org.odftoolkit.odfdom.dom.attribute.xforms;
  exports org.odftoolkit.odfdom.dom.attribute.xhtml;
  exports org.odftoolkit.odfdom.dom.attribute.xlink;
  exports org.odftoolkit.odfdom.dom.attribute.xml;
  exports org.odftoolkit.odfdom.dom.element;
  exports org.odftoolkit.odfdom.dom.element.anim;
  exports org.odftoolkit.odfdom.dom.element.chart;
  exports org.odftoolkit.odfdom.dom.element.config;
  exports org.odftoolkit.odfdom.dom.element.db;
  exports org.odftoolkit.odfdom.dom.element.dc;
  exports org.odftoolkit.odfdom.dom.element.dr3d;
  exports org.odftoolkit.odfdom.dom.element.draw;
  exports org.odftoolkit.odfdom.dom.element.form;
  exports org.odftoolkit.odfdom.dom.element.math;
  exports org.odftoolkit.odfdom.dom.element.meta;
  exports org.odftoolkit.odfdom.dom.element.number;
  exports org.odftoolkit.odfdom.dom.element.office;
  exports org.odftoolkit.odfdom.dom.element.presentation;
  exports org.odftoolkit.odfdom.dom.element.script;
  exports org.odftoolkit.odfdom.dom.element.style;
  exports org.odftoolkit.odfdom.dom.element.svg;
  exports org.odftoolkit.odfdom.dom.element.table;
  exports org.odftoolkit.odfdom.dom.element.text;
  exports org.odftoolkit.odfdom.dom.element.xforms;
  exports org.odftoolkit.odfdom.dom;
  exports org.odftoolkit.odfdom.dom.style;
  exports org.odftoolkit.odfdom.dom.style.props;
  exports org.odftoolkit.odfdom.incubator.doc.draw;
  exports org.odftoolkit.odfdom.incubator.doc.number;
  exports org.odftoolkit.odfdom.incubator.doc.office;
  exports org.odftoolkit.odfdom.incubator.doc.style;
  exports org.odftoolkit.odfdom.incubator.doc.text;
  exports org.odftoolkit.odfdom.incubator.meta;
  exports org.odftoolkit.odfdom.incubator.search;
  exports org.odftoolkit.odfdom.pkg;
  exports org.odftoolkit.odfdom.pkg.dsig;
  exports org.odftoolkit.odfdom.pkg.manifest;
  exports org.odftoolkit.odfdom.pkg.rdfa;
  exports org.odftoolkit.odfdom.type;
  exports org.odftoolkit.odfdom.dom.rdfa;

  requires commons.validator;
  requires java.desktop;
  requires java.logging;
  requires java.rdfa;
  requires java.xml;
  requires org.apache.commons.compress;
  requires org.apache.commons.lang3;
  requires org.apache.jena.core;
  requires org.json;
  requires org.slf4j;
  requires serializer;
  requires xercesImpl;
}
