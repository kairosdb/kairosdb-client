package org.kairosdb.client.builder.aggregator;

import org.kairosdb.client.builder.Aggregator;
import org.kairosdb.client.builder.TimeUnit;

import static java.util.Objects.requireNonNull;

public class RateAggregator extends Aggregator
{
	private TimeUnit unit;

	public RateAggregator(TimeUnit unit)
	{
		super("rate");
		this.unit = requireNonNull(unit);
	}

	public TimeUnit getUnit()
	{
		return unit;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		RateAggregator that = (RateAggregator) o;
		return unit == that.unit;
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + unit.hashCode();
		return result;
	}
}
