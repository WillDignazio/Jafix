package net.digitalbebop;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Jafix
{
	public static final int DEFAULT_MAXDEPTH = 4;

	public static void main(String[] args)
	{
		CommandLineParser parser;
		CommandLine cmd;
		Options opts;

		opts = new Options();
		opts.addOption("url", true, "Origin URL for broken link scan");

		try {
			parser = new BasicParser();
			cmd = parser.parse(opts, args);

			Crawler crawler = new Crawler();
			crawler.setBaseURL(args[0]);
			crawler.start();
		}
		catch(ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
