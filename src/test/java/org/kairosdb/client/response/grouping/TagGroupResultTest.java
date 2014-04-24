package org.kairosdb.client.response.grouping;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;

public class TagGroupResultTest
{
	@Test(expected = NullPointerException.class)
	public void test_constructor_null_tags_invalid()
	{
		new TagGroupResult(null, new HashMap<String, String>());
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_null_group_invalid()
	{
		new TagGroupResult(new ArrayList<String>(), null);
	}

}
