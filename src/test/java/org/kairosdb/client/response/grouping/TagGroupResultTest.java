package org.kairosdb.client.response.grouping;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagGroupResultTest
{
	@Test
	public void test_constructor_null_tags_invalid()
	{
		assertThrows(NullPointerException.class, () -> new TagGroupResult(null, new HashMap<String, String>()));
	}

	@Test
	public void test_constructor_null_group_invalid()
	{
		assertThrows(NullPointerException.class, () -> new TagGroupResult(new ArrayList<String>(), null));
	}

}
