//
//  TimeGrouperTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder.grouper;

import org.junit.Test;
import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.builder.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TimeGrouperTest
{

	@Test(expected = NullPointerException.class)
	public void test_constructor_nullRangeSize_invalid()
	{
		new TimeGrouper(null, 4);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_CountLessThanOne_invalid()
	{
		new TimeGrouper(new RelativeTime(1, TimeUnit.DAYS), 0);
	}

	@Test
	public void test_toJson()
	{
		TimeGrouper grouper = new TimeGrouper(new RelativeTime(2, TimeUnit.MINUTES), 5);

		assertThat(grouper.toJson(), equalTo("{\"name\":\"time\",\"range_size\":{\"value\":2,\"unit\":\"MINUTES\"},\"group_count\":5}"));
	}
}