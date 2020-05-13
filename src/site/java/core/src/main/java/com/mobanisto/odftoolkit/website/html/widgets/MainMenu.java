package com.mobanisto.odftoolkit.website.html.widgets;

import com.mobanisto.odftoolkit.website.html.Site;

import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.components.A;
import de.topobyte.jsoup.components.bootstrap3.Menu;
import de.topobyte.webpaths.WebPath;

public class MainMenu extends Menu
{

	public MainMenu(WebPath path)
	{
		A brand = HTML.a(path.relativize(Site.PATH_INDEX).toString());
		brand.appendText("ODF Toolkit");

		A tags = HTML.a("https://www.documentfoundation.org/",
				"The Document Foundation");

		addBrand(brand);
		addMain(tags, false);
	}

}
