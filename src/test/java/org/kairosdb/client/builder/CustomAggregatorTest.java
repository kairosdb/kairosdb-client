//
//  CustomAggregatorTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//
package org.kairosdb.client.builder;

import org.junit.jupiter.api.Test;
import org.kairosdb.client.builder.aggregator.CustomAggregator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomAggregatorTest
{
	@Test
	public void test_NullName_invalid()
	{
		assertThrows(NullPointerException.class, () -> new CustomAggregator(null, "json"));
	}

	@Test
	public void test_EmptyName_invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> new CustomAggregator("", "json"));
	}

	@Test
	public void test_NullJSON_invalid()
	{
		assertThrows(NullPointerException.class, () -> new CustomAggregator("name", null));
	}

	@Test
	public void test_EmptyJSON_invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> new CustomAggregator("name", ""));
	}

	@Test
	public void test()
	{
		CustomAggregator aggregator = new CustomAggregator("testAggregator", "{\"property1\":\"value1\", \"property2\": \"value2\"}");

		assertThat(aggregator.toJson(), equalTo("{\"name\":\"testAggregator\",{\"property1\":\"value1\", \"property2\": \"value2\"}}"));
	}
}
