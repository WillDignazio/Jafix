package net.digitalbebop;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Jafix
{
	public static final int DEFAULT_CRAWLERS = Runtime.getRuntime().availableProcessors();

	public static void main(String[] args)
	{
		URI uri;
		JafixManager manager;
		CommandLineParser parser;
		CommandLine cmd;
		Options opts;

		opts = new Options();
		opts.addOption("url", true, "Origin URL for broken link scan");

		try {
			parser = new BasicParser();
			cmd = parser.parse(opts, args);

			try {
				uri = new URI(args[0]);

				manager = new JafixManager(uri, DEFAULT_CRAWLERS);
				manager.schedURI(uri, uri);
				manager.generateReport();
			}
			catch(URISyntaxException e) {
				System.err.println(e.getMessage());
				System.exit(1);
			}
		}
		catch(ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
