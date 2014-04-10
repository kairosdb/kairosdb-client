package org.kairosdb.client.builder;

import org.junit.Test;

public class AggregatorFactoryTest
{
	@Test(expected = IllegalArgumentException.class)
	public void test_createDivAggregator_zero_divisor_invalid()
	{
		AggregatorFactory.createDivAggregator(0);
	}
}
