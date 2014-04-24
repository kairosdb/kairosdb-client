package org.kairosdb.client.response.grouping;

import org.junit.Test;

public class DefaultGroupResultTest
{
	@Test(expected = NullPointerException.class)
	public void test_constructor_type_null_invalid()
	{
		new DefaultGroupResult("name", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_type_empty_invalid()
	{
		new DefaultGroupResult("name", "");
	}

}
