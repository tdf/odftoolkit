package com.mobanisto.odftoolkit.website;

import java.io.IOException;
import java.util.Arrays;

import de.topobyte.system.utils.SystemPaths;

public class TestGenerateWebsitePartial
{

	public static void main(String[] args) throws IOException
	{
		WebsiteGenerator websiteGenerator = new WebsiteGenerator(
				SystemPaths.HOME.resolve("github/sebkur/odftoolkit"));
		websiteGenerator
				.generate(Arrays.asList("index.mdtext", "odfdom/index.mdtext"));
	}

}
