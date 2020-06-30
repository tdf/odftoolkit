package com.mobanisto.odftoolkit.website.html;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.mobanisto.odftoolkit.website.Resources;
import com.mobanisto.odftoolkit.website.markdown.Markdown;

import de.topobyte.jsoup.ElementUtil;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.HtmlBuilder;
import de.topobyte.jsoup.components.A;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.Img;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.webpaths.WebPath;
import de.topobyte.webpaths.WebPaths;

public class MarkdownGenerator extends BaseGenerator
{

	private String markdownSidenav;
	private WebPath webPath;

	public MarkdownGenerator(String markdownSidenav)
	{
		this.markdownSidenav = markdownSidenav;
	}

	public void create(Path path, WebPath webPath, Path file) throws IOException
	{
		this.webPath = webPath;

		HtmlBuilder htmlBuilder = new HtmlBuilder();
		setupHeader(webPath, htmlBuilder);

		Element body = htmlBuilder.getBody();

		banner(body);
		clear(body);

		Div sidenav = body.ac(HTML.div());
		sidenav.attr("id", "sidenav");

		Div contentA = body.ac(HTML.div());
		contentA.attr("id", "contenta");

		Markdown.renderString(sidenav, markdownSidenav);

		for (org.jsoup.nodes.Element element : sidenav.select("ul")) {
			element.addClass("list-group");
		}
		for (org.jsoup.nodes.Element element : sidenav.select("li")) {
			element.addClass("list-group-item");
		}

		Markdown.renderFile(contentA, file);

		addFooter(body);

		Files.createDirectories(path.getParent());
		htmlBuilder.write(path);
	}

	private void addFooter(Element element) throws IOException
	{
		String text = Resources.load("snippets/footer.html");
		ElementUtil.appendFragment(element, text);
	}

	private void clear(Element element)
	{
		element.ac(HTML.div()).attr("id", "clear");
	}

	private void banner(Element content)
	{
		Div divBanner = content.ac(HTML.div());
		divBanner.attr("id", "banner");
		Div divBannerRight = divBanner.ac(HTML.div());
		divBannerRight.attr("id", "bannerright");
		A linkLogo = divBannerRight
				.ac(HTML.a("https://www.documentfoundation.org/"));
		linkLogo.attr("alt", "The Document Foundation");

		WebPath pathLogo = WebPaths.get(
				"images/LibreOffice-Initial-Artwork-Logo-ColorLogoBasic-500px.png");

		Img image = linkLogo
				.ac(HTML.img(webPath.relativize(pathLogo).toString()));
		image.attr("id", "tdf-logo");
		image.attr("class", "w350");
		image.attr("alt", "The Document Foundation");
	}

}
