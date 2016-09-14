package org.kairosdb.client.response.grouping;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;

public class BinGroupResultTest
{
	@Test(expected = NullPointerException.class)
	public void test_constructor_null_bins_invalid()
	{
		new BinGroupResult(null, new HashMap<String, Integer>());
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_null_group_invalid()
	{
		new BinGroupResult(new ArrayList<Double>(), null);
	}

	@Test
	public void test_getBins()
	{
		BinGroupResult result = new BinGroupResult(Arrays.asList(1.0, 2.0, 3.0), new HashMap<String, Integer>());

		assertThat(result.getBins(), hasItems(1.0, 2.0, 3.0));
	}

	@Test
	public void test_getBinNumber()
	{
		Map<String, Integer> groups = new HashMap<String, Integer>();
		groups.put("bin_number", 2);

		BinGroupResult result = new BinGroupResult(Arrays.asList(1.0, 2.0, 3.0), groups);

		assertThat(result.getBinNumber(), equalTo(2));
	}
}
