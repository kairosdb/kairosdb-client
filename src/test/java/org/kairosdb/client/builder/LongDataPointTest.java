//
//  LongDataPointTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LongDataPointTest
{

	@Test
	public void test_isInteger()
	{
		LongDataPoint dataPoint = new LongDataPoint(4L, 43);

		assertThat(dataPoint.isInteger(), equalTo(true));
	}
}