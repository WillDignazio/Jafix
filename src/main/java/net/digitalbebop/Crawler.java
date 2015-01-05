package net.digitalbebop;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;
import org.jsoup.HttpStatusException;

public class Crawler
	implements Runnable
{
	private String baseDomain;
	private URI baseURI;
	private JafixManager manager;

	private Document doc;
	private Elements tags;

	/**
	 * Build a new crawling job, when run will traverse the given
	 * URL. The manager is for making sure that found URL's are not 
	 * already explored, and that new url's are schedule for traversal.
	 * @param url URL to crawl
	 * @param manager Jafix Crawler Manager
	 */
	public Crawler(URI uri, 
		       JafixManager manager)
		throws URISyntaxException
	{
		this.baseURI = uri;
		this.baseDomain = this.baseURI.getHost();
		if(this.baseDomain.startsWith("www."))
			this.baseDomain = this.baseDomain.substring(4);

		this.manager = manager;
	}
	
	/**
	 * Start the crawler, uses the baseURL to find the links
	 * in the site, then from there schedules them for traversal.
	 */
	@Override
	public void run()
	{
		int scheduled;

		scheduled = 0;
		
		try {
			doc = Jsoup.connect(this.baseURI.toString()).get();
			tags = doc.getElementsByTag("a");

			for(Element element : tags) {
				URI luri;
				String domain;

				try {
					luri = new URI(element.absUrl("href"));

					domain = luri.getHost();
					if(domain.startsWith("www."))
						domain = domain.substring(4);

					if(domain.equals(this.baseDomain))
					{	
						/* Schedule for next traversal */
						if(manager.schedURI(luri))
							++scheduled;
					}
				}
				catch(URISyntaxException e) {
					System.err.println(e.getMessage());
					continue;
				}
			}
		}
		catch(HttpStatusException e) {
			/*
			 * We've found a bad URL that needs to be included in the
			 * report, from here we hand it off to the report log.
			 * In the end, this will be included in report file.
			 */
			manager.reportUrl(baseURI.toString(), e);
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		int count = manager.getActiveCrawlersCount();
		if(manager.getActiveCrawlersCount() == 1 &&
		   scheduled == 0) {
			/* We're done */
			manager.stopCrawling();
		}
	}
}
