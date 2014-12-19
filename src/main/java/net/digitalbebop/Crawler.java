package net.digitalbebop;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.jsoup.Jsoup;

public class Crawler
	extends Thread
{
	private String baseURL;
	private JafixManager manager;

	private Document doc;
	private Elements tags;

	/**
	 * Build a new crawling job, when run will traverse the given
	 * URL. The manager is for making sure that found URL's are not 
	 * already explored, and that new url's are schedule for traversal.
	 * @param baseURL URL to crawl
	 * @param manager Jafix Crawler Manager
	 */
	public Crawler(String baseURL, 
		       JafixManager manager)
	{
		this.baseURL = baseURL;
		this.manager = manager;
	}
	
	/**
	 * Start the crawler, uses the baseURL to find the links
	 * in the site, then from there schedules them for traversal.
	 */
	@Override
	public void run()
	{
		try {
			doc = Jsoup.connect(this.baseURL).get();
			tags = doc.getElementsByTag("a");
			
			for(Element element : tags) {
				System.out.println("<a>: " + element.absUrl("href"));
				manager.schedUrl(element.absUrl("href"));
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
}
