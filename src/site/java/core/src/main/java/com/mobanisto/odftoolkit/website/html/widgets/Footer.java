package com.mobanisto.odftoolkit.website.html.widgets;

import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.bootstrap3.Bootstrap;
import de.topobyte.jsoup.components.UnorderedList;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webpaths.WebPath;

public class Footer extends Element
{

	private WebPath path;

	public Footer(WebPath path)
	{
		super("footer");
		this.path = path;

		attr("class", "footer");

		Element container = ac(Bootstrap.container());

		addLinks(container);
	}

	private void addLinks(Element container)
	{
		UnorderedList links = container.ac(HTML.ul());

		links.addItem(
				HTML.a("https://www.libreoffice.org/imprint", "Impressum"));
		links.addItem(HTML.a("https://www.libreoffice.org/privacy",
				"Privacy policy"));
	}

}
