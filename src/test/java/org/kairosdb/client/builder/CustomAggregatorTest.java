//
//  CustomAggregatorTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class CustomAggregatorTest
{
	@Test(expected = NullPointerException.class)
	public void test_NullName_invalid()
	{
		new CustomAggregator(null, "json");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_EmptyName_invalid()
	{
		new CustomAggregator("", "json");
	}

	@Test(expected = NullPointerException.class)
	public void test_NullJSON_invalid()
	{
		new CustomAggregator("name", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_EmptyJSON_invalid()
	{
		new CustomAggregator("name", "");
	}

	@Test
	public void test()
	{
		Aggregator aggregator = new CustomAggregator("testAggregator", "{\"property1\":\"value1\", \"property2\": \"value2\"}");

		assertThat(aggregator.toJson(), equalTo("\"name\":\"testAggregator\",{\"property1\":\"value1\", \"property2\": \"value2\"}"));
	}
}