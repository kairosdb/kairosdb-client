package org.kairosdb.client;

import org.kairosdb.eventbus.Subscribe;
import org.kairosdb.events.DataPointEvent;

public class TestDataPointListener
{
	private org.kairosdb.events.DataPointEvent event;

	@Subscribe
	public void dataPoint(org.kairosdb.events.DataPointEvent event)
	{
		this.event = event;
	}

	public DataPointEvent getEvent()
	{
		return event;
	}
}
