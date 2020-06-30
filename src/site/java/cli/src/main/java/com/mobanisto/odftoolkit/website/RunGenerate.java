package com.mobanisto.odftoolkit.website;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.cli.CommandLine;
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
			return new CommonsCliExeOptions(options,
					"[options] <output directory>");
		}

	};

	public static void main(String name, CommonsCliArguments arguments)
			throws Exception
	{
		System.out.println("Generating website...");

		CliEnvironment environment = new CliEnvironment();
		Path repo = environment.getPathRepo();

		CommandLine line = arguments.getLine();
		List<String> args = line.getArgList();
		if (args.size() != 1) {
			System.out.println("Please specify the output directory");
			arguments.getOptions().usage("generate");
			System.exit(1);
		}

		Path dirOutput = Paths.get(args.get(0));
		Files.createDirectories(dirOutput);

		WebsiteGenerator websiteGenerator = new WebsiteGenerator(repo,
				dirOutput);
		websiteGenerator.generate();
	}

}
