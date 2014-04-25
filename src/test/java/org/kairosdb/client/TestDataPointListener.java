package org.kairosdb.client;

import org.kairosdb.core.DataPoint;
import org.kairosdb.core.DataPointListener;

import java.util.SortedMap;

public class TestDataPointListener implements DataPointListener
{
	private DataPointEvent event;

	public void setEvent(DataPointEvent event)
	{
		this.event = event;
	}

	@Override
	public void dataPoint(String metricName, SortedMap<String, String> tags, DataPoint dataPoint)
	{
		if (event != null)
			event.datapoint(metricName);
	}
}
