package org.kairosdb.client.response.grouping;

import org.junit.Test;

public class ValueGroupResultTest
{

	@Test(expected = NullPointerException.class)
	public void test_constructor_null_GroupingNumber_invalid()
	{
		new ValueGroupResult(1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_rangeSize_Zero_invalid()
	{
		new ValueGroupResult(0, new GroupingNumber(1));
	}
}
