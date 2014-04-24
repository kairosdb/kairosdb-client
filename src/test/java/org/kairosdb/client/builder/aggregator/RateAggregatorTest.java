package org.kairosdb.client.builder.aggregator;

import org.junit.Test;
import org.kairosdb.client.builder.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class RateAggregatorTest
{
	@Test(expected = NullPointerException.class)
	public void test_constructor_null_unit_invalid()
	{
		new RateAggregator(null);
	}

	@Test
	public void test_getter()
	{
		RateAggregator aggregator = new RateAggregator(TimeUnit.MINUTES);

		assertThat(aggregator.getUnit(), equalTo(TimeUnit.MINUTES));
	}

}
