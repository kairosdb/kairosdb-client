//
//  ValueGrouperTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder.grouper;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ValueGrouperTest
{
	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_rangeSize_lessThan1()
	{
		new ValueGrouper(0);
	}

	@Test
	public void test_toJson()
	{
		ValueGrouper grouper = new ValueGrouper(10);

		assertThat(grouper.toJson(), equalTo("{\"name\":\"value\",\"range_size\":10}"));
	}
}