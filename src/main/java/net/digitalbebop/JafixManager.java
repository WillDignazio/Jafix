package net.digitalbebop;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JafixManager
{
	private ConcurrentHashMap<String, Integer> urlmap;
	private SynchronousQueue<String> workq;
	private ExecutorService executor;

	public JafixManager()
	{
		this.urlmap = new ConcurrentHashMap<String, Integer>();
		this.workq = new SynchronousQueue<String>();
		this.executor = Executors.newCachedThreadPool();
	}

	/**
	 * Schedule a URL to be traversed, if the URL has already been
	 * traversed, then drop it and continue on with life.
	 * @param url Url to schedule for traverse
	 */
	public void schedUrl(String url)
	{
		/* Drop if already traversed */
		if(urlmap.get(url) != null)
			return;

		try {
			urlmap.put(url, 1);
			workq.put(url);
		}
		catch(InterruptedException e) {
		}
	}
}
