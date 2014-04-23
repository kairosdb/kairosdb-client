package org.kairosdb.client.builder.aggregator;

import org.kairosdb.client.builder.Aggregator;
import org.kairosdb.client.builder.TimeUnit;

import static com.google.common.base.Preconditions.checkNotNull;

public class RateAggregator extends Aggregator
{
	private TimeUnit unit;

	public RateAggregator(TimeUnit unit)
	{
		super("rate");
		this.unit = checkNotNull(unit);
	}

	public TimeUnit getUnit()
	{
		return unit;
	}
}
