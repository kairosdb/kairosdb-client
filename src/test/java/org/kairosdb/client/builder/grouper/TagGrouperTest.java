//
//  TagGrouperTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder.grouper;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class TagGrouperTest
{

	@Test(expected = NullPointerException.class)
	public void test_constructor_nullTagNames_invalid()
	{
		new TagGrouper((String[]) null);
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_nullTagName_invalid()
	{
		new TagGrouper("tag1", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_emptyTagNameList_invalid()
	{
		new TagGrouper();
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_null_list_invalid()
	{
		new TagGrouper((List<String>) null);
	}

	@Test
	public void test_constructor_name()
	{
		TagGrouper grouper = new TagGrouper("tag1", "tag2");

		assertThat(grouper.getName(), equalTo("tag"));
	}

	@Test
	public void test_constructor_list_name()
	{
		TagGrouper grouper = new TagGrouper(Arrays.asList("tag1", "tag2"));

		assertThat(grouper.getName(), equalTo("tag"));
	}

	@Test
	public void test_constructor_tagNames()
	{
		TagGrouper grouper = new TagGrouper("tag1", "tag2");

		assertThat(grouper.getTagNames(), hasItems("tag1", "tag2"));
	}

	@Test
	public void test_constructor_tagNames_from_list()
	{
		TagGrouper grouper = new TagGrouper(Arrays.asList("tag1", "tag2"));

		assertThat(grouper.getTagNames(), hasItems("tag1", "tag2"));
	}
}