package net.digitalbebop;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JafixManager
{
	private ConcurrentHashMap<String, Integer> urlmap;
	private ExecutorService executor;

	public JafixManager(int crawlers)
	{
		this.urlmap = new ConcurrentHashMap<String, Integer>();
		this.executor = Executors.newFixedThreadPool(crawlers);
	}

	/**
	 * Schedule a URL to be traversed, if the URL has already been
	 * traversed, then drop it and continue on with life.
	 *
	 * Scheduled URL's are put onto the executor service as a
	 * Crawler job, and will be run when a thread is available. 
	 * @param url Url to schedule for traverse
	 */
	public void schedUrl(String url)
	{
		/* Drop if already traversed */
		if(urlmap.get(url) != null)
			return;

		urlmap.put(url, 1);
		executor.execute(new Crawler(url, this));
	}
}
