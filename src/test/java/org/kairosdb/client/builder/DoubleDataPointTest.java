//
//  DoubleDataPointTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class DoubleDataPointTest
{

	@Test
	public void test_isInteger()
	{
		DoubleDataPoint dataPoint = new DoubleDataPoint(4L, 43.2);

		assertThat(dataPoint.isInteger(), equalTo(false));
	}
}