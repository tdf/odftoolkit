package com.mobanisto.odftoolkit.website;

import java.nio.file.Path;
import java.nio.file.Paths;

public class CliEnvironment
{

	private Path pathRepo;

	public CliEnvironment()
	{
		String repo = System.getProperty("repo");
		pathRepo = Paths.get(repo);
	}

	public Path getPathRepo()
	{
		return pathRepo;
	}

}
