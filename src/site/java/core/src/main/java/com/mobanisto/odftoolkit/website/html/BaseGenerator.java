package com.mobanisto.odftoolkit.website.html;

import de.topobyte.jsoup.ElementBuilder;
import de.topobyte.jsoup.ElementUtil;
import de.topobyte.jsoup.HtmlBuilder;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webpaths.WebPath;

public class BaseGenerator
{

	protected void setupHeader(WebPath webPath, HtmlBuilder htmlBuilder)
	{
		Element head = htmlBuilder.getHead();
		htmlBuilder.getTitle().appendText("ODF Toolkit");

		ElementUtil.appendFragmentHead(head, "<link rel=\"stylesheet\" href=\""
				+ webPath.relativize(Site.PATH_STYLES).toString() + "\">");
		ElementUtil.appendFragmentHead(head,
				"<link rel=\"stylesheet\" href=\""
						+ webPath.relativize(Site.PATH_CUSTOM_STYLES).toString()
						+ "\">");

		head.ac(ElementBuilder.styleSheet(
				webPath.relativize(Site.PATH_ODF_STYLES).toString()));
	}

}
