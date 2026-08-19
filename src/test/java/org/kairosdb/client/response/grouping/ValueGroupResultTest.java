package org.kairosdb.client.response.grouping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class ValueGroupResultTest
{

	@Test
	public void test_constructor_null_GroupingNumber_invalid()
	{
		assertThrows(NullPointerException.class, () -> new ValueGroupResult(1, null));
	}

	@Test
	public void test_constructor_rangeSize_Zero_invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> new ValueGroupResult(0, new GroupingNumber(1)));
	}
}
