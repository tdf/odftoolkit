package com.mobanisto.odftoolkit.website;

import java.io.IOException;

import de.topobyte.system.utils.SystemPaths;

public class TestGenerateWebsite
{

	public static void main(String[] args) throws IOException
	{
		WebsiteGenerator websiteGenerator = new WebsiteGenerator(
				SystemPaths.HOME.resolve("github/sebkur/odftoolkit"));
		websiteGenerator.generate();
	}

}
