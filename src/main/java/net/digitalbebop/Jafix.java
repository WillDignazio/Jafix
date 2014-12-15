package net.digitalbebop;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

public class Jafix
{
	public static void main(String[] args)
	{
		CommandLineParser parser;
		CommandLine cmd;
		Document doc;
		Elements tags;
		Options opts;

		opts = new Options();
		opts.addOption("url", true, "Origin URL for broken link scan");

		try {
			parser = new BasicParser();
			cmd = parser.parse(opts, args);
		}
		catch(ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		try {
			doc = Jsoup.connect(args[0]).get();
			tags = doc.getElementsByTag("a");
			
			for(Element element : tags)
				System.out.println("<a>: " + element.absUrl("href"));
		}
		catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
}
