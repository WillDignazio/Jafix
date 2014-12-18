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
	private int maxDepth;
	private Document doc;
	private Elements tags;

	public Crawler()
	{
		this.baseURL = null;
		this.maxDepth = Jafix.DEFAULT_MAXDEPTH;
	}
	
	/**
	 * Set URL to start crawl from.
	 * @param url URL to start crawl from.
	 */
	public void setBaseURL(String url)
	{
		this.baseURL = url;
	}

	/**
	 * Set maximum depth to crawl.
	 * @param depth Maximum depth to crawl.
	 */
	public void setMaxDepth(int depth)
	{
		this.maxDepth = depth;
	}

	@Override
	public void run()
	{
		if(this.baseURL == null)
			return;

		try {
			doc = Jsoup.connect(this.baseURL).get();
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
