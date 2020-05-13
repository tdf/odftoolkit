package com.mobanisto.odftoolkit.website;

import java.nio.file.Path;

import org.apache.commons.cli.Options;

import de.topobyte.utilities.apache.commons.cli.commands.args.CommonsCliArguments;
import de.topobyte.utilities.apache.commons.cli.commands.options.CommonsCliExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class RunGenerate
{

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			Options options = new Options();
			return new CommonsCliExeOptions(options, "[options]");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		System.out.println("Generating website...");

		CliEnvironment environment = new CliEnvironment();
		Path repo = environment.getPathRepo();

		WebsiteGenerator websiteGenerator = new WebsiteGenerator(repo);
		websiteGenerator.generate();
	}

}
