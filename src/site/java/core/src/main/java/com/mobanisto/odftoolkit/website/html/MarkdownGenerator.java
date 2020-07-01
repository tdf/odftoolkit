package com.mobanisto.odftoolkit.website.html;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.mobanisto.odftoolkit.website.Resources;
import com.mobanisto.odftoolkit.website.markdown.Markdown;

import de.topobyte.jsoup.ElementUtil;
import de.topobyte.jsoup.HTML;
import de.topobyte.jsoup.HtmlBuilder;
import de.topobyte.jsoup.components.A;
import de.topobyte.jsoup.components.Div;
import de.topobyte.jsoup.components.Img;
import de.topobyte.jsoup.nodes.Element;
import de.topobyte.jsoup.toc.TablesOfContent;
import de.topobyte.jsoup.toc.table.TableOfContent;
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

		htmlBuilder.getHtml().attr("lang", "en");

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

		addHeadingIds(contentA);
		addCodeHilite(contentA);

		TableOfContent toc = TablesOfContent.create(contentA);
		TablesOfContent.replaceMarker(contentA, "[TOC]", toc);

		addFooter(body);

		Files.createDirectories(path.getParent());

		String text = htmlBuilder.getDocument().toString();
		OutputStream os = Files.newOutputStream(path);
		os.write("<!DOCTYPE html>".getBytes());
		os.write(text.getBytes(Charset.forName("UTF-8")));
		os.close();
	}

	private void addHeadingIds(Div element)
	{
		for (String hx : Arrays.asList("h1", "h2", "h3", "h4", "h5", "h6")) {
			for (org.jsoup.nodes.Element child : element.select(hx)) {
				String text = child.text();
				String id = headingId(text);
				child.attr("id", id);
			}
		}
	}

	private String headingId(String text)
	{
		return text.toLowerCase().replaceAll(" ", "-").replaceAll("---", "-");
	}

	private void addCodeHilite(Element element)
	{
		for (org.jsoup.nodes.Element child : element.select("pre")) {
			Div codeContainer = HTML.div("codehilite");
			child.before(codeContainer);
			codeContainer.ac(child);
		}
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
