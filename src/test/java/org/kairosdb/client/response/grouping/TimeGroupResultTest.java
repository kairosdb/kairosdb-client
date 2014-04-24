package org.kairosdb.client.response.grouping;

import org.junit.Test;
import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.builder.TimeUnit;

public class TimeGroupResultTest
{

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_Zero_GroupCount_invalid()
	{
		new TimeGroupResult(new RelativeTime(1, TimeUnit.MILLISECONDS), 0, new GroupingNumber(2));
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_null_RelativeTime_invalid()
	{
		new TimeGroupResult(null, 2, new GroupingNumber(2));
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_null_GroupNumber_invalid()
	{
		new TimeGroupResult(new RelativeTime(1, TimeUnit.MILLISECONDS), 2, null);
	}
}
