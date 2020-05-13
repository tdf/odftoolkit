package com.mobanisto.odftoolkit.website.html;

import com.mobanisto.odftoolkit.website.html.widgets.Footer;
import com.mobanisto.odftoolkit.website.html.widgets.MainMenu;

import de.topobyte.jsoup.ElementUtil;
import de.topobyte.jsoup.HtmlBuilder;
import de.topobyte.jsoup.bootstrap3.Bootstrap3;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webpaths.WebPath;

public class BaseGenerator
{

	protected void setupHeader(WebPath webPath, HtmlBuilder htmlBuilder)
	{
		Element head = htmlBuilder.getHead();
		htmlBuilder.getTitle().appendText("ODF Toolkit");

		Bootstrap3.addCdnHeaders(head);

		ElementUtil.appendFragmentHead(head, "<link rel=\"stylesheet\" href=\""
				+ webPath.relativize(Site.PATH_STYLES).toString() + "\">");
		ElementUtil.appendFragmentHead(head,
				"<link rel=\"stylesheet\" href=\""
						+ webPath.relativize(Site.PATH_CUSTOM_STYLES).toString()
						+ "\">");
	}

	protected void addMenu(WebPath webPath, Element body)
	{
		body.ac(new MainMenu(webPath));
	}

	protected void addFooter(WebPath webPath, Element body)
	{
		body.ac(new Footer(webPath));
	}

}
