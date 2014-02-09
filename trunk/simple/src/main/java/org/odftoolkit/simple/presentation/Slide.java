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
package org.odftoolkit.simple.presentation;

import java.awt.Rectangle;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.odftoolkit.odfdom.dom.OdfDocumentNamespace;
import org.odftoolkit.odfdom.dom.attribute.presentation.PresentationClassAttribute;
import org.odftoolkit.odfdom.dom.attribute.text.TextAnchorTypeAttribute;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StylePresentationPageLayoutElement;
import org.odftoolkit.odfdom.incubator.doc.office.OdfOfficeStyles;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.odftoolkit.odfdom.pkg.OdfFileDom;
import org.odftoolkit.odfdom.type.CellRangeAddressList;
import org.odftoolkit.simple.Component;
import org.odftoolkit.simple.Document;
import org.odftoolkit.simple.PresentationDocument;
import org.odftoolkit.simple.SpreadsheetDocument;
import org.odftoolkit.simple.chart.AbstractChartContainer;
import org.odftoolkit.simple.chart.Chart;
import org.odftoolkit.simple.chart.ChartContainer;
import org.odftoolkit.simple.chart.DataSet;
import org.odftoolkit.simple.draw.AbstractTextboxContainer;
import org.odftoolkit.simple.draw.FrameRectangle;
import org.odftoolkit.simple.draw.Textbox;
import org.odftoolkit.simple.draw.TextboxContainer;
import org.odftoolkit.simple.table.AbstractTableContainer;
import org.odftoolkit.simple.table.Table;
import org.odftoolkit.simple.table.TableContainer;
import org.odftoolkit.simple.table.Table.TableBuilder;
import org.odftoolkit.simple.text.list.AbstractListContainer;
import org.odftoolkit.simple.text.list.List;
import org.odftoolkit.simple.text.list.ListContainer;
import org.odftoolkit.simple.text.list.ListDecorator;
import org.w3c.dom.NodeList;

/**
 * <code>Slide</code> represents the presentation slide feature of the ODF
 * document. <code>Slide</code> provides methods to get the slide index,get the
 * content of the current slide, etc.
 */
public class Slide extends Component implements ListContainer, TableContainer, TextboxContainer, ChartContainer {

	DrawPageElement maSlideElement;
	private ListContainerImpl listContainerImpl;
	private TableContainerImpl tableContainerImpl;
	private TextboxContainerImpl mTextboxContainerImpl;
	private ChartContainerImpl chartContainerImpl;
	
	/**
	 * This is a tool class which supplies all of the slide creation detail.
	 * <p>
	 * The end user isn't allowed to create it directly, otherwise an
	 * <code>IllegalStateException</code> will be thrown.
	 * 
	 *@since 0.3.5
	 */
	public static class SlideBuilder {

		private final IdentityHashMap<DrawPageElement, Slide> maSlideRepository = new IdentityHashMap<DrawPageElement, Slide>();

		/**
		 * SlideBuilder constructor. This constructor should only be use in
		 * owner {@link org.odftoolkit.simple.PresentationDocument
		 * PresentationDocument} constructor. The end user isn't allowed to call
		 * it directly, otherwise an <code>IllegalStateException</code> will be
		 * thrown.
		 * 
		 * @param doc
		 *            the owner <code>PresentationDocument</code>.
		 * @throws IllegalStateException
		 *             if new SlideBuilder out of owner PresentationDocument
		 *             constructor, this exception will be thrown.
		 */
		public SlideBuilder(PresentationDocument doc) {
			if (doc.getSlideBuilder() != null) {
				throw new IllegalStateException(
						"SlideBuilder only can be created in owner PresentationDocument constructor.");
			}
		}

		/**
		 * Get a presentation slide instance by an instance of
		 * <code>DrawPageElement</code>.
		 * 
		 * @param pageElement
		 *            an instance of <code>DrawPageElement</code>
		 * @return an instance of <code>Slide</code> that can represent
		 *         <code>pageElement</code>
		 */
		public synchronized Slide getSlideInstance(DrawPageElement pageElement) {
			if (maSlideRepository.containsKey(pageElement)) {
				return maSlideRepository.get(pageElement);
			} else {
				Slide newSlide = new Slide(pageElement);
				maSlideRepository.put(pageElement, newSlide);
				return newSlide;
			}
		}
	}

	private Slide(DrawPageElement pageElement) {
		maSlideElement = pageElement;
	}

	/**
	 * Get a presentation slide instance by an instance of
	 * <code>DrawPageElement</code>.
	 * 
	 * @param pageElement
	 *            an instance of <code>DrawPageElement</code>
	 * @return an instance of <code>Slide</code> that can represent
	 *         <code>pageElement</code>
	 */
	public static Slide getInstance(DrawPageElement pageElement) {
		PresentationDocument ownerDocument = (PresentationDocument) ((OdfFileDom) (pageElement.getOwnerDocument()))
				.getDocument();
		return ownerDocument.getSlideBuilder().getSlideInstance(pageElement);
	}

	/**
	 * Return an instance of <code>DrawPageElement</code> which represents
	 * presentation slide feature.
	 * 
	 * @return an instance of <code>DrawPageElement</code>
	 */
	public DrawPageElement getOdfElement() {
		return maSlideElement;
	}

	/**
	 * Get the current slide index in the owner document.
	 * 
	 * @return the slide index in the owner document
	 *         <p>
	 *         -1, if the odf element which can represent this slide is not in
	 *         the document DOM tree
	 */
	public int getSlideIndex() {
		OdfFileDom contentDom = (OdfFileDom) maSlideElement.getOwnerDocument();
		NodeList slideNodes = contentDom.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideEle = (DrawPageElement) slideNodes.item(i);
			if (slideEle == maSlideElement)// should not equals here, see
			// OdfElement.equals(Object obj)
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * Get the current slide name.
	 * <p>
	 * If the "draw:name" attribute is not present there, create an unique name
	 * for this slide
	 * 
	 * @return the name of the current slide
	 */
	public String getSlideName() {
		String slideName = maSlideElement.getDrawNameAttribute();
		if (slideName == null) {
			slideName = makeUniqueSlideName();
			maSlideElement.setDrawNameAttribute(slideName);
		}
		return slideName;
	}

	/**
	 * Set the current slide name.
	 * <p>
	 * It must be unique slide name in the current presentation. If not, an
	 * IllegalArgumentException will be thrown. If the given name is null, an
	 * IllegalArgumentException will also be thrown.
	 * 
	 * @param name
	 *            the new name of the current slide
	 * @throws IllegalArgumentException
	 *             if the given name is null or it is not unique in the current
	 *             presentation.
	 */
	public void setSlideName(String name) {
		if (name == null) {
			throw new IllegalArgumentException("slide name is null is not accepted in the presentation document");
		}
		// check if name is unique in this presentation
		OdfFileDom contentDom = (OdfFileDom) maSlideElement.getOwnerDocument();
		NodeList slideNodes = contentDom.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "page");
		for (int i = 0; i < slideNodes.getLength(); i++) {
			DrawPageElement slideEle = (DrawPageElement) slideNodes.item(i);
			Slide slide = Slide.getInstance(slideEle);
			String slideName = slide.getSlideName();
			if (slideName.equals(name)) {
				throw new IllegalArgumentException(
						"the given slide name is already exist in the current presentation document");
			}
		}
		maSlideElement.setDrawNameAttribute(name);
	}

	/**
	 * Get the Notes page of this slide
	 * 
	 * @return the instance of <code>Notes</code> which represent the notes page
	 *         of the current slide
	 */
	public Notes getNotesPage() {
		NodeList notesList = maSlideElement.getElementsByTagNameNS(OdfDocumentNamespace.PRESENTATION.getUri(), "notes");
		if (notesList.getLength() > 0) {
			PresentationNotesElement noteEle = (PresentationNotesElement) notesList.item(0);
			return Notes.getInstance(noteEle);

		}
		return null;
	}

	private String makeUniqueSlideName() {
		int index = getSlideIndex();
		String slideName = "page" + (index + 1) + "-" + String.format("a%06x", (int) (Math.random() * 0xffffff));
		return slideName;
	}

	/**
	 * A slide layout is a slide with some predefine place holder.
	 * 
	 * we define some template layout as below:
	 * <ul>
	 * <li>"BLANK" template is a slide without any filled element.</li>
	 * <li>"TITLE_ONLY" template is a slide with a title only.</li>
	 * <li>"TITLE_SUBTITLE" template is a slide with a title and a subtitle.</li>
	 * <li>"TITLE_OUTLINE" template is a slide with a title and an outline
	 * block.</li>
	 * <li>"TITLE_PLUS_TEXT" template is a slide with a title and a text block.</li>
	 * <li>"TITLE_PLUS_CHART" template is a slide with a title and a chart
	 * block.</li>
	 * <li>"TITLE_PLUS_2_TEXT_BLOCK" template is a slide with a title and two
	 * text blocks.</li>
	 * <li>"TITLE_PLUS_2_CHART" template is a slide with a title and two chart
	 * blocks.</li>
	 * <li>"TITLE_LEFT_CHART_RIGHT_OUTLINE" template is a slide with a title, a
	 * chart block on the left and an outline block on the right.</li>
	 * <li>"TITLE_PLUS_3_OBJECT" template is a slide with a title, an outline
	 * block and two chart blocks.</li>
	 * <li>"TITLE_PLUS_4_OBJECT" template is a slide with a title, an outline
	 * block and three chart blocks.</li>
	 * <ul>
	 */
	public enum SlideLayout {

		/**
		 * Blank, a blank presentation
		 */
		BLANK("blank") {
			public void apply(DrawPageElement page) {
				//do nothing.			
			}
		},
		/**
		 * Title_only, the presentation with title only
		 */
		TITLE_ONLY("title_only") {
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = "AL1T" + makeUniqueName();
				try {
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					layout.newPresentationPlaceholderElement("title", "2.058cm", "1.743cm", "23.91cm", "3.507cm");
				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);

				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setProperty(StyleGraphicPropertiesElement.StyleShadow, "true");
				frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
				frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());

				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.006cm");
				frame1.setSvgWidthAttribute("24.299cm");
				frame1.setSvgXAttribute("1.35cm");
				frame1.setSvgYAttribute("0.717cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
			}
		},
		/**
		 * title_subtitle, the presentation with title and subtitle.
		 */
		TITLE_SUBTITLE("title_subtitle") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName ="AL1T" + makeUniqueName();
				try {
					styles = doc.getStylesDom().getOfficeStyles();
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					//String presentationObjectValue, String svgHeightValue, String svgWidthValue, String svgXValue, String svgYValue
					layout.newPresentationPlaceholderElement("title", "3.507cm", "23.912cm", "2.058cm", "1.743cm");
					layout.newPresentationPlaceholderElement("subtitle", "13.23cm", "23.912cm", "2.058cm", "5.838cm");
					
				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);
				
				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());
				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("4.244cm");
				frame1.setSvgWidthAttribute("23.848cm");
				frame1.setSvgXAttribute("2.075cm");
				frame1.setSvgYAttribute("6.621cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setPresentationStyleNameAttribute(frame2.getStyleName());
				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("5.097cm");
				frame2.setSvgWidthAttribute("19.631cm");
				frame2.setSvgXAttribute("4.183cm");
				frame2.setSvgYAttribute("12.003cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.SUBTITLE.toString());
				frame2.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.newDrawTextBoxElement();
			}
		},
		/**
		 * Title_outline, the presentation with outline
		 */
		TITLE_OUTLINE("title_outline") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = makeUniqueName();
				try {
					if (styles == null) {
						styles = doc.getStylesDom().getOfficeStyles();
					}
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					layout.newPresentationPlaceholderElement("title", "2.058cm", "1.743cm", "23.91cm", "3.507cm");
					layout.newPresentationPlaceholderElement("outline", "2.058cm", "1.743cm", "23.91cm", "3.507cm");

				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);

				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setProperty(StyleGraphicPropertiesElement.StyleShadow, "true");
				frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
				frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());

				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.006cm");
				frame1.setSvgWidthAttribute("24.299cm");
				frame1.setSvgXAttribute("1.35cm");
				frame1.setSvgYAttribute("0.717cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				DrawFrameElement frame2 = page.newDrawFrameElement();

				frame2.setProperty(StyleGraphicPropertiesElement.FillColor, "#ffffff");
				frame2.setProperty(StyleGraphicPropertiesElement.MinHeight, "13.114");
				frame2.setPresentationStyleNameAttribute(frame2.getStyleName());

				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("11.629cm");
				frame2.setSvgWidthAttribute("24.199cm");
				frame2.setSvgXAttribute("1.35cm");
				frame2.setSvgYAttribute("4.337cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.newDrawTextBoxElement();
			}
		},
		/**
		 * Title_text, the presentation with title and one text block
		 */
		TITLE_PLUS_TEXT("title_text") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = makeUniqueName();
				try {
					if (styles == null) {
						styles = doc.getStylesDom().getOfficeStyles();
					}
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					layout.newPresentationPlaceholderElement("title", "2.058cm", "1.743cm", "23.91cm", "1.743cm");
					layout.newPresentationPlaceholderElement("subtitle", "2.058cm", "5.838cm", "23.91cm", "13.23cm");

				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);

				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
				frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());

				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.006cm");
				frame1.setSvgWidthAttribute("24.299cm");
				frame1.setSvgXAttribute("1.35cm");
				frame1.setSvgYAttribute("0.717cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
				frame2.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
				frame2.setPresentationStyleNameAttribute(frame2.getStyleName());

				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("11.88cm");
				frame2.setSvgWidthAttribute("24.299cm");
				frame2.setSvgXAttribute("1.35cm");
				frame2.setSvgYAttribute("4.712cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.SUBTITLE.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.newDrawTextBoxElement();
			}
		},
		/**
		 * title_two_text_block, the presentation with title and two text blocks
		 */
		TITLE_PLUS_2_TEXT_BLOCK("title_two_text_block") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = makeUniqueName();
				try {
					if (styles == null) {
						styles = doc.getStylesDom().getOfficeStyles();
					}
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					layout.newPresentationPlaceholderElement("outline", "2.058cm", "1.743cm", "23.91cm", "1.743cm");
					layout.newPresentationPlaceholderElement("outline", "1.35cm", "4.212cm", "11.857cm", "11.629cm");
					layout.newPresentationPlaceholderElement("outline", "4.212cm", "13.8cm", "11.857cm", "11.629cm");

				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);
				
				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
				frame1.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());
				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.006cm");
				frame1.setSvgWidthAttribute("24.299cm");
				frame1.setSvgXAttribute("1.35cm");
				frame1.setSvgYAttribute("0.717cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
				frame2.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
				frame2.setPresentationStyleNameAttribute(frame2.getStyleName());
				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("11.629cm");
				frame2.setSvgWidthAttribute("11.857cm");
				frame2.setSvgXAttribute("1.35cm");
				frame2.setSvgYAttribute("4.212cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.newDrawTextBoxElement();
				
				DrawFrameElement frame3 = page.newDrawFrameElement();
				frame3.setProperty(StyleGraphicPropertiesElement.AutoGrowHeight, "true");
				frame3.setProperty(StyleGraphicPropertiesElement.MinHeight, "3.507");
				frame3.setPresentationStyleNameAttribute(frame3.getStyleName());
				frame3.setDrawLayerAttribute("layout");
				frame3.setSvgHeightAttribute("11.62cm");
				frame3.setSvgWidthAttribute("11.857cm");
				frame3.setSvgXAttribute("13.8cm");
				frame3.setSvgYAttribute("4.212cm");
				frame3.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
				frame3.setPresentationPlaceholderAttribute(true);
				frame3.newDrawTextBoxElement();
			}
		},
		/**
		 * title_three_objects, the presentation with title, chart and outline blocks.
		 */
		TITLE_LEFT_CHART_RIGHT_OUTLINE("title_left_chart_right_outline") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = makeUniqueName();
				try {
					styles = doc.getStylesDom().getOfficeStyles();
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					//String presentationObjectValue, String svgHeightValue, String svgWidthValue, String svgXValue, String svgYValue
					layout.newPresentationPlaceholderElement("title", "3.507cm", "25.199cm", "1.4cm", "0.837cm");
					layout.newPresentationPlaceholderElement("chart", "13.86cm", "12.296cm", "1.4cm", "4.914cm");
					layout.newPresentationPlaceholderElement("outline", "13.86cm", "12.296cm", "14.311cm", "4.914cm");
				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);

				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());
				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.507cm");
				frame1.setSvgWidthAttribute("25.199cm");
				frame1.setSvgXAttribute("1.4cm");
				frame1.setSvgYAttribute("0.837cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setDrawStyleNameAttribute(frame2.getStyleName());
				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("13.86cm");
				frame2.setSvgWidthAttribute("12.296cm");
				frame2.setSvgXAttribute("1.4cm");
				frame2.setSvgYAttribute("4.914cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame2.newDrawObjectElement();

				DrawFrameElement frame3 = page.newDrawFrameElement();
				frame3.setPresentationStyleNameAttribute(frame3.getStyleName());
				frame3.setDrawLayerAttribute("layout");
				frame3.setSvgHeightAttribute("13.86cm");
				frame3.setSvgWidthAttribute("12.296cm");
				frame3.setSvgXAttribute("14.311cm");
				frame3.setSvgYAttribute("4.914cm");
				frame3.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
				frame3.setPresentationPlaceholderAttribute(true);
				frame3.newDrawTextBoxElement();
			}
		},
		/**
		 * title_plus_chart, the presentation with title and chart.
		 */
		TITLE_PLUS_CHART("title_plus_chart") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = "AL1T" + makeUniqueName();
				try {
					styles = doc.getStylesDom().getOfficeStyles();
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					//String presentationObjectValue, String svgHeightValue, String svgWidthValue, String svgXValue, String svgYValue
					layout.newPresentationPlaceholderElement("title", "3.507cm", "25.199cm", "1.4cm", "0.837cm");
					layout.newPresentationPlaceholderElement("chart", "13.86cm", "25.199cm", "1.4cm", "4.914cm");
				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);
				
				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());
				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.507cm");
				frame1.setSvgWidthAttribute("25.199cm");
				frame1.setSvgXAttribute("1.4cm");
				frame1.setSvgYAttribute("0.837cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setDrawStyleNameAttribute(frame2.getStyleName());
				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("13.86cm");
				frame2.setSvgWidthAttribute("25.199cm");
				frame2.setSvgXAttribute("1.4cm");
				frame2.setSvgYAttribute("4.914cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame2.newDrawObjectElement();
			}
		},
		/**
		 * title_plus_two_chart, the presentation with title and two charts.
		 */
		TITLE_PLUS_2_CHART("title_plus_2_chart") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = makeUniqueName();
				try {
					styles = doc.getStylesDom().getOfficeStyles();
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					//String presentationObjectValue, String svgHeightValue, String svgWidthValue, String svgXValue, String svgYValue
					layout.newPresentationPlaceholderElement("title", "3.507cm", "25.199cm", "1.4cm", "0.837cm");
					layout.newPresentationPlaceholderElement("chart", "6.61cm", "25.199cm", "1.4cm", "4.914cm");
					layout.newPresentationPlaceholderElement("chart", "6.61cm", "25.199cm", "1.4cm", "12.153cm");
				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);
				
				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());
				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.507cm");
				frame1.setSvgWidthAttribute("25.199cm");
				frame1.setSvgXAttribute("1.4cm");
				frame1.setSvgYAttribute("0.837cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setDrawStyleNameAttribute(frame2.getStyleName());
				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("6.61cm");
				frame2.setSvgWidthAttribute("25.199cm");
				frame2.setSvgXAttribute("1.4cm");
				frame2.setSvgYAttribute("4.914cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame2.newDrawObjectElement();

				DrawFrameElement frame3 = page.newDrawFrameElement();
				frame3.setDrawStyleNameAttribute(frame3.getStyleName());
				frame3.setDrawLayerAttribute("layout");
				frame3.setSvgHeightAttribute("6.61cm");
				frame3.setSvgWidthAttribute("25.199cm");
				frame3.setSvgXAttribute("1.4cm");
				frame3.setSvgYAttribute("12.153cm");
				frame3.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame3.setPresentationPlaceholderAttribute(true);
				frame2.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame3.newDrawObjectElement();
			}
		},
		/**
		 * title_three_object, the presentation with title and three object blocks.
		 */
		TITLE_PLUS_3_OBJECT("title_plus_three_object") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = makeUniqueName();
				try {
					styles = doc.getStylesDom().getOfficeStyles();
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					//String presentationObjectValue, String svgHeightValue, String svgWidthValue, String svgXValue, String svgYValue
					layout.newPresentationPlaceholderElement("title", "3.507cm", "25.199cm", "1.4cm", "0.837cm");
					layout.newPresentationPlaceholderElement("chart", "6.61cm", "12.296cm", "1.4cm", "4.914cm");
					layout.newPresentationPlaceholderElement("outline", "6.61cm", "12.296cm", "14.311cm", "4.914cm");
					layout.newPresentationPlaceholderElement("chart", "6.61cm", "25.199cm", "1.4cm", "12.153cm");
				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);
				
				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());
				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.507cm");
				frame1.setSvgWidthAttribute("25.199cm");
				frame1.setSvgXAttribute("1.4cm");
				frame1.setSvgYAttribute("0.837cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setDrawStyleNameAttribute(frame2.getStyleName());
				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("6.61cm");
				frame2.setSvgWidthAttribute("12.296cm");
				frame2.setSvgXAttribute("1.4cm");
				frame2.setSvgYAttribute("4.914cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame2.newDrawObjectElement();

				DrawFrameElement frame3 = page.newDrawFrameElement();
				frame3.setPresentationStyleNameAttribute(frame3.getStyleName());
				frame3.setDrawLayerAttribute("layout");
				frame3.setSvgHeightAttribute("6.61cm");
				frame3.setSvgWidthAttribute("12.296cm");
				frame3.setSvgXAttribute("14.311cm");
				frame3.setSvgYAttribute("4.914cm");
				frame3.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
				frame3.setPresentationPlaceholderAttribute(true);
				frame3.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame3.newDrawTextBoxElement();
				
				DrawFrameElement frame4 = page.newDrawFrameElement();
				frame4.setDrawStyleNameAttribute(frame4.getStyleName());
				frame4.setDrawLayerAttribute("layout");
				frame4.setSvgHeightAttribute("6.61cm");
				frame4.setSvgWidthAttribute("25.199cm");
				frame4.setSvgXAttribute("1.4cm");
				frame4.setSvgYAttribute("12.153cm");
				frame4.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame4.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame4.setPresentationPlaceholderAttribute(true);
				frame4.newDrawObjectElement();
			}
		},
		/**
		 * title_four_object, the presentation with title and four object blocks.
		 */
		TITLE_PLUS_4_OBJECT("title_four_object") {
			
			public void apply(DrawPageElement page) {
				Document doc = (Document) ((OdfFileDom) page.getOwnerDocument()).getDocument();
				OdfOfficeStyles styles = doc.getOrCreateDocumentStyles();
				String layoutName;
				layoutName = makeUniqueName();
				try {
					styles = doc.getStylesDom().getOfficeStyles();
					if (styles == null) {
						styles = doc.getStylesDom().newOdfElement(OdfOfficeStyles.class);
					}
					StylePresentationPageLayoutElement layout = styles.newStylePresentationPageLayoutElement(layoutName);
					//String presentationObjectValue, String svgHeightValue, String svgWidthValue, String svgXValue, String svgYValue
					layout.newPresentationPlaceholderElement("title", "3.507cm", "25.199cm", "1.4cm", "0.837cm");
					layout.newPresentationPlaceholderElement("chart", "6.61cm", "12.296cm", "1.4cm", "4.914cm");
					layout.newPresentationPlaceholderElement("outline", "6.61cm", "12.296cm", "14.311cm", "4.914cm");
					layout.newPresentationPlaceholderElement("chart", "6.61cm", "12.296cm", "1.4cm", "12.153cm");
					layout.newPresentationPlaceholderElement("chart", "6.61cm", "12.296cm", "14.311cm", "12.153cm");
				} catch (Exception e1) {
					Logger.getLogger(SlideLayout.class.getName()).log(Level.SEVERE, null, e1);
				}
				page.setPresentationPresentationPageLayoutNameAttribute(layoutName);
				
				DrawFrameElement frame1 = page.newDrawFrameElement();
				frame1.setPresentationStyleNameAttribute(frame1.getStyleName());
				frame1.setDrawLayerAttribute("layout");
				frame1.setSvgHeightAttribute("3.507cm");
				frame1.setSvgWidthAttribute("25.199cm");
				frame1.setSvgXAttribute("1.4cm");
				frame1.setSvgYAttribute("0.837cm");
				frame1.setPresentationClassAttribute(PresentationClassAttribute.Value.TITLE.toString());
				frame1.setPresentationPlaceholderAttribute(true);
				frame1.newDrawTextBoxElement();
				
				DrawFrameElement frame2 = page.newDrawFrameElement();
				frame2.setDrawStyleNameAttribute(frame2.getStyleName());
				frame2.setDrawLayerAttribute("layout");
				frame2.setSvgHeightAttribute("6.61cm");
				frame2.setSvgWidthAttribute("12.296cm");
				frame2.setSvgXAttribute("1.4cm");
				frame2.setSvgYAttribute("4.914cm");
				frame2.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame2.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame2.setPresentationPlaceholderAttribute(true);
				frame2.newDrawObjectElement();

				DrawFrameElement frame3 = page.newDrawFrameElement();
				frame3.setPresentationStyleNameAttribute(frame3.getStyleName());
				frame3.setDrawLayerAttribute("layout");
				frame3.setSvgHeightAttribute("6.61cm");
				frame3.setSvgWidthAttribute("12.296cm");
				frame3.setSvgXAttribute("14.311cm");
				frame3.setSvgYAttribute("4.914cm");
				frame3.setPresentationClassAttribute(PresentationClassAttribute.Value.OUTLINE.toString());
				frame3.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame3.setPresentationUserTransformedAttribute(true);
				frame3.setPresentationPlaceholderAttribute(true);
				frame3.newDrawTextBoxElement();
				
				DrawFrameElement frame4 = page.newDrawFrameElement();
				frame4.setDrawStyleNameAttribute(frame4.getStyleName());
				frame4.setDrawLayerAttribute("layout");
				frame4.setSvgHeightAttribute("6.61cm");
				frame4.setSvgWidthAttribute("12.296cm");
				frame4.setSvgXAttribute("1.4cm");
				frame4.setSvgYAttribute("12.153cm");
				frame4.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame4.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame4.setPresentationPlaceholderAttribute(true);
				frame4.newDrawObjectElement();
				
				DrawFrameElement frame5 = page.newDrawFrameElement();
				frame5.setDrawStyleNameAttribute(frame5.getStyleName());
				frame5.setDrawLayerAttribute("layout");
				frame5.setSvgHeightAttribute("6.61cm");
				frame5.setSvgWidthAttribute("12.296cm");
				frame5.setSvgXAttribute("14.311cm");
				frame5.setSvgYAttribute("12.153cm");
				frame5.setPresentationClassAttribute(PresentationClassAttribute.Value.CHART.toString());
				frame5.setTextAnchorTypeAttribute(TextAnchorTypeAttribute.Value.PAGE.toString());
				frame5.setPresentationPlaceholderAttribute(true);
				frame5.newDrawObjectElement();
			}
		};
		
		private String mValue;

		SlideLayout(String aValue) {
			mValue = aValue;
		}

		/**
		 * Applies this layout on a slide page.
		 * 
		 * @param page
		 *            the slide element which this layout is applied on.
		 * @since 0.6
		 */
		public abstract void apply(DrawPageElement page);
		
		/**
		 * Return the slide template type value.
		 * 
		 * @return the template type value
		 */
		@Override
		public String toString() {
			return mValue;
		}

		/**
		 * Return the name of the template slide type.
		 * 
		 * @param aEnum
		 *            a <code>SlideLayout</code>
		 * @return the name of slide template type
		 */
		public static String toString(SlideLayout aEnum) {
			return aEnum.toString();
		}

		/**
		 * Return a template slide type.
		 * 
		 * @param aString
		 *            the name of the slide template type
		 * @return a <code>SlideLayout</code>
		 */
		public static SlideLayout enumValueOf(String aString) {
			for (SlideLayout aIter : values()) {
				if (aString.equals(aIter.toString())) {
					return aIter;
				}
			}
			return null;
		}
		
		//return an unique name.
		private static String makeUniqueName() {
			return String.format("a%06x", (int) (Math.random() * 0xffffff));
		}
	}

	public OdfElement getListContainerElement() {
		return getListContainerImpl().getListContainerElement();
	}

	public List addList() {
		return getListContainerImpl().addList();
	}

	public List addList(ListDecorator decorator) {
		return getListContainerImpl().addList(decorator);
	}

	public void clearList() {
		getListContainerImpl().clearList();
	}

	public Iterator<List> getListIterator() {
		return getListContainerImpl().getListIterator();
	}

	public boolean removeList(List list) {
		return getListContainerImpl().removeList(list);
	}

	private ListContainerImpl getListContainerImpl() {
		if (listContainerImpl == null) {
			listContainerImpl = new ListContainerImpl();
		}
		return listContainerImpl;
	}

	private class ListContainerImpl extends AbstractListContainer {

		public OdfElement getListContainerElement() {
			DrawFrameElement frame = null;
			DrawTextBoxElement textBox = null;
			NodeList frameList = maSlideElement.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "frame");
			if (frameList.getLength() > 0) {
				int index = frameList.getLength() - 1;
				while (index >= 0) {
					frame = (DrawFrameElement) frameList.item(index);
					String presentationClass = frame.getPresentationClassAttribute();
					if (presentationClass == null || "outline".equals(presentationClass)
							|| "text".equals(presentationClass) || "subtitle".equals(presentationClass)) {
						break;
					} else {
						index--;
					}
					frame = null;
				}
			}
			if (frame == null) {
				throw new UnsupportedOperationException(
						"There is no list container in this slide, please chose a proper slide layout.");
			}
			NodeList textBoxList = frame.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "text-box");
			if (textBoxList.getLength() <= 0) {
				textBox = frame.newDrawTextBoxElement();
			} else {
				textBox = (DrawTextBoxElement) textBoxList.item(textBoxList.getLength() - 1);
			}
			return textBox;
		}
	}

	public Table addTable() {
		return getTableContainerImpl().addTable();
	}

	public Table addTable(int numRows, int numCols) {
		return getTableContainerImpl().addTable(numRows, numCols);
	}

	public Table getTableByName(String name) {
		return getTableContainerImpl().getTableByName(name);
	}

	public java.util.List<Table> getTableList() {
		return getTableContainerImpl().getTableList();
	}

	public TableBuilder getTableBuilder() {
		return getTableContainerImpl().getTableBuilder();
	}

	public OdfElement getTableContainerElement() {
		return getTableContainerImpl().getTableContainerElement();
	}

	protected TableContainer getTableContainerImpl() {
		if (tableContainerImpl == null) {
			tableContainerImpl = new TableContainerImpl();
		}
		return tableContainerImpl;
	}

	private class TableContainerImpl extends AbstractTableContainer {

		public OdfElement getTableContainerElement() {
			DrawFrameElement frame = null;
			NodeList frameList = maSlideElement.getElementsByTagNameNS(OdfDocumentNamespace.DRAW.getUri(), "frame");
			if (frameList.getLength() > 0) {
				int index = frameList.getLength() - 1;
				while (index >= 0) {
					frame = (DrawFrameElement) frameList.item(index);
					String presentationClass = frame.getPresentationClassAttribute();
					if (presentationClass == null || "table".equals(presentationClass)) {
						break;
					} else {
						index--;
					}
					frame = null;
				}
			}
			if (frame == null) {
				frame = maSlideElement.newDrawFrameElement();
				frame.setPresentationClassAttribute("table");
				frame.setDrawLayerAttribute("layout");
				frame.setStyleName("standard");
				frame.setSvgHeightAttribute("1.945cm");
				frame.setSvgWidthAttribute("14.098cm");
				frame.setSvgXAttribute("6.922cm");
				frame.setSvgYAttribute("10.386cm");
			}
			return frame;
		}
	}

	// *********Text box support **********//
	public Textbox addTextbox() {
		return getTextboxContainerImpl().addTextbox();
	}

	public Iterator<Textbox> getTextboxIterator() {
		return getTextboxContainerImpl().getTextboxIterator();
	}

	public boolean removeTextbox(Textbox box) {
		return getTextboxContainerImpl().removeTextbox(box);
	}

	public OdfElement getFrameContainerElement() {
		return getTextboxContainerImpl().getFrameContainerElement();
	}

	public Textbox addTextbox(FrameRectangle position) {
		return getTextboxContainerImpl().addTextbox(position);
	}

	public Textbox getTextboxByName(String name) {
		return getTextboxContainerImpl().getTextboxByName(name);
	}

	private class TextboxContainerImpl extends AbstractTextboxContainer {
		public OdfElement getFrameContainerElement() {
			return maSlideElement;
		}
	}

	private TextboxContainerImpl getTextboxContainerImpl() {
		if (mTextboxContainerImpl == null)
			mTextboxContainerImpl = new TextboxContainerImpl();
		return mTextboxContainerImpl;
	}

	public java.util.List<Textbox> getTextboxByUsage(PresentationDocument.PresentationClass usage) {
		return getTextboxContainerImpl().getTextboxByUsage(usage);
	}
	public Chart createChart(String title, DataSet dataset, Rectangle rect) {
		return getChartContainerImpl().createChart(title, dataset, rect);
	}

	public Chart createChart(String title, SpreadsheetDocument document, CellRangeAddressList cellRangeAddr, boolean firstRowAsLabel,
			boolean firstColumnAsLabel, boolean rowAsDataSeries, Rectangle rect) {
		return getChartContainerImpl().createChart(title, document, cellRangeAddr, firstRowAsLabel, firstColumnAsLabel,
				rowAsDataSeries, rect);
	}

	public Chart createChart(String title, String[] labels, String[] legends, double[][] data, Rectangle rect) {
		return getChartContainerImpl().createChart(title, labels, legends, data, rect);
	}

	public void deleteChartById(String chartId) {
		getChartContainerImpl().deleteChartById(chartId);
	}

	public void deleteChartByTitle(String title) {
		getChartContainerImpl().deleteChartByTitle(title);
	}

	public Chart getChartById(String chartId) {
		return getChartContainerImpl().getChartById(chartId);
	}

	public java.util.List<Chart> getChartByTitle(String title) {
		return getChartContainerImpl().getChartByTitle(title);
	}

	public int getChartCount() {
		return getChartContainerImpl().getChartCount();
	}
	
	private ChartContainerImpl getChartContainerImpl() {
		if (chartContainerImpl == null) {
			chartContainerImpl = new ChartContainerImpl(getOwnerDocument(), this);
		}
		return chartContainerImpl;
	}
	
	private class ChartContainerImpl extends AbstractChartContainer {
		DrawPageElement slide;

		protected ChartContainerImpl(Document doc, Slide slide) {
			super(doc);
			this.slide = slide.getOdfElement();
		}

		protected DrawFrameElement getChartFrame() throws Exception {
			DrawFrameElement element = OdfElement.findFirstChildNode(DrawFrameElement.class, slide);
			while (element != null) {
				if ("chart".equals(element.getPresentationClassAttribute())) {
					DrawObjectElement chartEle= OdfElement.findFirstChildNode(DrawObjectElement.class, element);
					if(chartEle == null){
						return element;
					}else{
						String href = chartEle.getXlinkHrefAttribute();
						if("".equals(href) || href ==null){
							return element;
						}
					}
				}
				element = OdfElement.findNextChildNode(DrawFrameElement.class, element);
			}
			return null;
		}
	}
}
