package org.kairosdb.client.builder.grouper;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomGrouperTest
{
	@Test
	public void test_constructor_null_json_invalid()
	{
		assertThrows(NullPointerException.class, () -> new CustomGrouper("name", null));
	}

	@Test
	public void test_constructor_empty_json_invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> new CustomGrouper("name", ""));
	}

	@Test
	public void test_toJson()
	{
		CustomGrouper grouper = new CustomGrouper("group1", "{\"foo\": 120}");

		assertThat(grouper.toJson(), equalTo("{\"name\": \"group1\", {\"foo\": 120}}"));
	}
}
