package net.digitalbebop;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.LinkedBlockingQueue;

public class JafixManager
{
	private ConcurrentHashMap<String, ArrayList<IOException>> errorLog;
	private ConcurrentHashMap<String, Integer> urlmap;
	private ThreadPoolExecutor executor;
	private URI rootURI;
	private String rootDomain;

	public JafixManager(URI rootURI, int crawlers)
	{
		this.urlmap = new ConcurrentHashMap<String, Integer>();
		this.executor = new ThreadPoolExecutor(crawlers, crawlers, 
						       1000L, TimeUnit.MILLISECONDS,
						       new LinkedBlockingQueue<Runnable>());
		this.errorLog = new ConcurrentHashMap<String, ArrayList<IOException>>();
		this.rootURI = rootURI;
		this.rootDomain = getURIDomain(rootURI);
	}

	/**
	 * Helper method that gets the domain of the URI,
	 * without the leading www. if present.
	 * @return domain Domain of the URI
	 */
	private String getURIDomain(URI uri)
	{
		String domain;

		domain = uri.getHost();
		if(domain.startsWith("www."))
			domain = domain.substring(4);

		return domain;
	}

	/**
	 * Get the root URI for this manager. 
	 * By providing this, we can let our crawlers determine if they give
	 * up on a URL outside of the root domain.
	 * @return uri Root URI for this manager
	 */
	public URI getRootURI()
	{
		return this.rootURI;
	}

	/**
	 * Schedule a URL to be traversed, if the URL has already been
	 * traversed, then drop it and continue on with life.
	 *
	 * Scheduled URL's are put onto the executor service as a
	 * Crawler job, and will be run when a thread is available.
	 * @param parentURI URI we came from
	 * @param nextURI URI to schedule for traverse
	 * @param boolean Whether uri was actually scheduled
	 */
	public boolean schedURI(URI parentURI, URI nextURI)
	{
		String parentDomain;

		/* Check if the parent is outside of our domain */
		parentDomain = getURIDomain(parentURI);
		if(!parentDomain.equals(rootDomain))
			return false;

		/* Drop if already traversed */
		if(urlmap.get(nextURI.toString()) != null)
			return false;

		urlmap.put(nextURI.toString(), 1);
		try {
			executor.execute(new Crawler(nextURI, this));
		}
		catch(URISyntaxException e) {
			System.err.println("Jafix Manager URI Syntax: " + e.getMessage());
		}

		return true;
	}

	/**
	 * Generates a report for the program, giving a breakdown of the
	 * invalid or dead URL links, and where they came from.
	 */
	public void generateReport()
	{
		try {
			this.executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		}
		catch(InterruptedException e) {
		}
	}

	/**
	 * Add a URL that has a known problem, and the parent URL that
	 * the invalid one was reached from.
	 * @param parent Parent URL link was found from
	 * @param err Error that was derived from trying to traverse link
	 */
	public void reportURI(URI parent, IOException err)
	{
		System.out.println("Offender: " + err + " from: " + parent);
		ArrayList<IOException> plist;

		plist = errorLog.putIfAbsent(parent.toString(), new ArrayList<IOException>());
		if(plist == null)
			plist = errorLog.get(parent);

		plist.add(err);
	}

	/**
	 * Wrapper that returns the number of active running threads.
	 */
	public synchronized int getActiveCrawlersCount()
	{
		return this.executor.getActiveCount();
	}

	/**
	 * Tell the manager not to accept anymore crawlers or schedule
	 * any more URL's.
	 */
	public void stopCrawling()
	{
		this.executor.shutdown();
	}
}
