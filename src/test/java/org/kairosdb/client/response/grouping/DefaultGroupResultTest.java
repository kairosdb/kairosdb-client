package org.kairosdb.client.response.grouping;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultGroupResultTest
{
	@Test
	public void test_constructor_type_null_invalid()
	{
		assertThrows(NullPointerException.class, () -> new DefaultGroupResult("name", null));
	}

	@Test
	public void test_constructor_type_empty_invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> new DefaultGroupResult("name", ""));
	}

}
