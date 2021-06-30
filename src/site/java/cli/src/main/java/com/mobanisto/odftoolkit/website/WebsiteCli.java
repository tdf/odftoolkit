package com.mobanisto.odftoolkit.website;

import de.topobyte.utilities.apache.commons.cli.commands.ArgumentParser;
import de.topobyte.utilities.apache.commons.cli.commands.ExeRunner;
import de.topobyte.utilities.apache.commons.cli.commands.ExecutionData;
import de.topobyte.utilities.apache.commons.cli.commands.RunnerException;
import de.topobyte.utilities.apache.commons.cli.commands.options.DelegateExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptions;
import de.topobyte.utilities.apache.commons.cli.commands.options.ExeOptionsFactory;

public class WebsiteCli
{

	public static ExeOptionsFactory OPTIONS_FACTORY = new ExeOptionsFactory() {

		@Override
		public ExeOptions createOptions()
		{
			DelegateExeOptions options = new DelegateExeOptions();
			options.addCommand("generate", RunGenerate.OPTIONS_FACTORY,
					RunGenerate.class);
			return options;
		}

	};

	public static void main(String[] args) throws RunnerException
	{
		String name = "website-cli";

		ExeOptions options = OPTIONS_FACTORY.createOptions();
		ArgumentParser parser = new ArgumentParser(name, options);

		if (args.length == 0) {
			System.out.println("Website Generator Command Line Interface");
			System.out.println();
			options.usage(name);
			System.exit(1);
		}

		ExecutionData data = parser.parse(args);
		if (data != null) {
			ExeRunner.run(data);
		}
	}

}
