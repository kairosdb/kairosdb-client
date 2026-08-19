//
//  ValueGrouperTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder.grouper;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValueGrouperTest
{
	@Test()
	public void test_constructor_rangeSize_lessThan1()
	{
		assertThrows(IllegalArgumentException.class, () -> new ValueGrouper(0));
	}
}