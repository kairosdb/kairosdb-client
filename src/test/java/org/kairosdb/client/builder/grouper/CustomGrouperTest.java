package org.kairosdb.client.builder.grouper;

import org.junit.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class CustomGrouperTest
{
	@Test(expected = NullPointerException.class)
	public void test_constructor_null_json_invalid()
	{
		new CustomGrouper("name", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_empty_json_invalid()
	{
		new CustomGrouper("name", "");
	}

	@Test
	public void test_toJson()
	{
		CustomGrouper grouper = new CustomGrouper("group1", "{\"foo\": 120}");

		assertThat(grouper.toJson(), equalTo("\"name\": \"group1\", {\"foo\": 120}"));
	}
}
