package org.kairosdb.client.builder.aggregator;

import org.junit.jupiter.api.Test;
import org.kairosdb.client.builder.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class RateAggregatorTest
{
	@Test
	public void test_constructor_null_unit_invalid()
	{
		assertThrows(NullPointerException.class, () -> new RateAggregator(null));
	}

	@Test
	public void test_getter()
	{
		RateAggregator aggregator = new RateAggregator(TimeUnit.MINUTES);

		assertThat(aggregator.getUnit(), equalTo(TimeUnit.MINUTES));
	}

}
