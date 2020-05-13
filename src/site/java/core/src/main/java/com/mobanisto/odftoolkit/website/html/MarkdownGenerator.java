package com.mobanisto.odftoolkit.website.html;

import java.io.IOException;
import java.nio.file.Path;

import com.mobanisto.odftoolkit.website.markdown.Markdown;

import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.HtmlBuilder;
import de.topobyte.jsoup.bootstrap3.Bootstrap;
import de.topobyte.jsoup.bootstrap3.components.Container;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webpaths.WebPath;

public class MarkdownGenerator extends BaseGenerator
{

	private String markdownSidenav;

	public MarkdownGenerator(String markdownSidenav)
	{
		this.markdownSidenav = markdownSidenav;
	}

	public void create(Path path, WebPath webPath, Path file) throws IOException
	{
		HtmlBuilder htmlBuilder = new HtmlBuilder();
		setupHeader(webPath, htmlBuilder);

		Element body = htmlBuilder.getBody();
		addMenu(webPath, body);

		Container content = body.ac(Bootstrap.container());

		Div row = content.ac(Bootstrap.row());

		Div side = row.ac(HTML.div("col col-xs-12 col-sm-3"));
		Div main = row.ac(HTML.div("col col-xs-12 col-sm-9"));

		Markdown.renderString(side, markdownSidenav);
		Markdown.renderFile(main, file);

		addFooter(webPath, body);

		htmlBuilder.write(path);
	}

}
