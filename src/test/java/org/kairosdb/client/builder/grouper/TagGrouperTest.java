//
//  TagGrouperTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder.grouper;

import org.junit.Test;

public class TagGrouperTest
{

	@Test(expected = NullPointerException.class)
	public void test_constructor_nullTagNames_invalid()
	{
		new TagGrouper((String[])null);
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
}