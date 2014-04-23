//
//  ValueGrouperTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder.grouper;

import org.junit.Test;

public class ValueGrouperTest
{
	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_rangeSize_lessThan1()
	{
		new ValueGrouper(0);
	}
}