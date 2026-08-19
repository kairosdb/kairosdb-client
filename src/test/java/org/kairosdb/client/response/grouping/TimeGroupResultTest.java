package org.kairosdb.client.response.grouping;

import org.junit.jupiter.api.Test;
import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.builder.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TimeGroupResultTest
{

	@Test
	public void test_constructor_Zero_GroupCount_invalid()
	{
		assertThrows(IllegalArgumentException.class, () ->
				new TimeGroupResult(new RelativeTime(1, TimeUnit.MILLISECONDS), 0, new GroupingNumber(2)));
	}

	@Test
	public void test_constructor_null_RelativeTime_invalid()
	{
		assertThrows(NullPointerException.class, () ->
				new TimeGroupResult(null, 2, new GroupingNumber(2)));
	}

	@Test
	public void test_constructor_null_GroupNumber_invalid()
	{
		assertThrows(NullPointerException.class, () ->
				new TimeGroupResult(new RelativeTime(1, TimeUnit.MILLISECONDS), 2, null));
	}
}
