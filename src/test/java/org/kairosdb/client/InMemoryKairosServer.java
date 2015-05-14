package org.kairosdb.client;

import org.apache.commons.io.FileUtils;
import org.kairosdb.core.Main;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.core.exception.KairosDBException;

import java.io.File;
import java.io.IOException;

public class InMemoryKairosServer extends Thread
{
	private Main kairos;
	private boolean started;
	private File properties;

	public InMemoryKairosServer()
	{
	}

	public InMemoryKairosServer(File properties)
	{
		this.properties = properties;
	}

	public void run()
	{
		try
		{
			// delete H2DB cache
			File h2db = new File("build/h2db");
			if (h2db.exists())
			{
				FileUtils.deleteDirectory(h2db);
			}

			kairos = new Main(properties);
			kairos.startServices();
			setStarted();
		}
		catch (KairosDBException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public TestDataPointListener getDataPointListener()
	{
		return ListenerModule.listener;
	}

	public synchronized boolean isStarted()
	{
		return started;
	}

	private synchronized void setStarted()
	{
		started = true;
	}

	public void shutdown() throws InterruptedException, DatastoreException
	{
		kairos.stopServices();
	}
}
