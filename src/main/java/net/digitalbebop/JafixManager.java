package net.digitalbebop;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

public class JafixManager
{
	private ConcurrentHashMap<String, String> errorLog;
	private ConcurrentHashMap<String, Integer> urlmap;
	private ThreadPoolExecutor executor;

	public JafixManager(int crawlers)
	{
		this.urlmap = new ConcurrentHashMap<String, Integer>();
		this.executor = new ThreadPoolExecutor(crawlers, crawlers, 
						       1000L, TimeUnit.MILLISECONDS,
						       new LinkedBlockingQueue<Runnable>());
	}

	/**
	 * Schedule a URL to be traversed, if the URL has already been
	 * traversed, then drop it and continue on with life.
	 *
	 * Scheduled URL's are put onto the executor service as a
	 * Crawler job, and will be run when a thread is available. 
	 * @param uri Uri to schedule for traverse
	 * @param boolean Whether uri was actually scheduled
	 */
	public boolean schedURI(URI uri)
	{
		/* Drop if already traversed */
		if(urlmap.get(uri.toString()) != null)
			return false;

		urlmap.put(uri.toString(), 1);
		try {
			executor.execute(new Crawler(uri, this));
		}
		catch(URISyntaxException e) {
			System.err.println(e.getMessage());
		}

		return true;
	}

	/**
	 * Generates a report for the program, giving a breakdown of the
	 * invalid or dead URL links, and where they came from.
	 */
	public void generateReport()
	{
		
	}

	/**
	 * Wrapper that returns the number of active running threads.
	 */
	public synchronized int getActiveCrawlersCount()
	{
		return this.executor.getActiveCount();
	}
}
