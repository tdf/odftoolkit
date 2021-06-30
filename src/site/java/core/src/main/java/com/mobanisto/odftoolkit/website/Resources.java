package com.mobanisto.odftoolkit.website;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;

public class Resources
{

	public static String load(String path) throws IOException
	{
		try (InputStream input = Thread.currentThread().getContextClassLoader()
				.getResourceAsStream(path)) {
			return IOUtils.toString(input);
		}
	}

}
