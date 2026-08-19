//
//  TimeGrouperTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//
package org.kairosdb.client.builder.grouper;

import org.junit.jupiter.api.Test;
import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.builder.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimeGrouperTest
{

	@Test
	public void test_constructor_nullRangeSize_invalid()
	{
		assertThrows(NullPointerException.class, () -> new TimeGrouper(null, 4));
	}

	@Test
	public void test_constructor_CountLessThanOne_invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> new TimeGrouper(new RelativeTime(1, TimeUnit.DAYS), 0));
	}
}
