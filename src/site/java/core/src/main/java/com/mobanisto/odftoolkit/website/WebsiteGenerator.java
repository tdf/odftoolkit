package com.mobanisto.odftoolkit.website;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;

import com.google.common.base.Joiner;
import com.mobanisto.odftoolkit.website.html.MarkdownGenerator;

import de.topobyte.melon.paths.PathUtil;
import de.topobyte.webpaths.NioPaths;
import de.topobyte.webpaths.WebPath;

public class WebsiteGenerator
{

	private Path repo;

	public WebsiteGenerator(Path repo)
	{
		this.repo = repo;
	}

	private Path dirOutput;
	private Path dirSite;
	private Path dirTemplates;
	private Path dirContent;

	private String markdownSidenav;

	private void init() throws IOException
	{
		dirOutput = repo.resolve("docs");
		System.out.println("output directory: " + dirOutput);

		dirSite = repo.resolve("src/site/site");
		dirTemplates = dirSite.resolve("templates");
		dirContent = dirSite.resolve("content/odftoolkit_website");

		Path dirResources = repo.resolve("src/site/java/resources");
		copy(dirResources.resolve("custom.css"), dirOutput);

		Path fileSideNav = dirTemplates.resolve("sidenav.mdtext");
		markdownSidenav = loadText(fileSideNav);
	}

	public void generate(List<String> paths) throws IOException
	{
		init();
		for (String path : paths) {
			generate(dirContent.resolve(path));
		}
	}

	public void generate() throws IOException
	{
		init();
		List<Path> markdownFiles = PathUtil.findRecursive(dirContent,
				"*.mdtext");
		for (Path markdownFile : markdownFiles) {
			generate(markdownFile);
		}
	}

	private void copy(Path file, Path dir) throws IOException
	{
		Files.copy(file, dir.resolve(file.getFileName()),
				StandardCopyOption.REPLACE_EXISTING);
	}

	private String loadText(Path file) throws IOException
	{
		InputStream input = Files.newInputStream(file);
		return IOUtils.toString(input, StandardCharsets.UTF_8);
	}

	private void generate(Path input) throws IOException
	{
		Path relativeInput = dirContent.relativize(input);
		String filenameHtml = relativeInput.getFileName().toString()
				.replaceFirst(".mdtext$", ".html");
		System.out.println("input: " + relativeInput);
		Path relativeHtml = relativeInput.resolveSibling(filenameHtml);

		WebPath webPath = NioPaths.convert(relativeHtml, false);

		int n = relativeInput.getNameCount() - 1;
		String ups = n == 0 ? ""
				: Joiner.on("/").join(Collections.nCopies(n, "..")) + "/";

		String modifiedMarkdownSidenav = markdownSidenav
				.replaceAll("/odftoolkit_website/", ups);

		MarkdownGenerator markdownGenerator = new MarkdownGenerator(
				modifiedMarkdownSidenav);
		Path pathOutput = dirOutput.resolve(relativeHtml);
		System.out.println("output: " + pathOutput);
		markdownGenerator.create(pathOutput, webPath, input);
	}

}
