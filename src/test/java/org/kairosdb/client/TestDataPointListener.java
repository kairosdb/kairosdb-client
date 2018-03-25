package org.kairosdb.client;

import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import org.kairosdb.core.DataPoint;

import java.util.SortedMap;

public class TestDataPointListener
{
	private DataPointEvent event;

	@Inject
	public TestDataPointListener()
	{
	}

	public void setEvent(DataPointEvent event)
	{
		this.event = event;
	}

	@Subscribe
	public void dataPoint(String metricName, SortedMap<String, String> tags, DataPoint dataPoint)
	{
		if (event != null)
			event.datapoint(metricName);
	}
}
