/*
 * Copyright 2013 Proofpoint Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.client.builder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class QueryMetricTest
{
	@Test
	public void test_constructor_NullName_Invalid()
	{
		assertThrows(NullPointerException.class, () -> new QueryMetric(null));
	}

	@Test
	public void test_constructor_EmptyName_Invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> new QueryMetric(""));
	}

	@Test
	public void test_addTag_nullName_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(NullPointerException.class, () -> queryMetric.addTag(null, "value"));
	}

	@Test
	public void testAddTags_nullMap_invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(NullPointerException.class, () -> queryMetric.addTags(null));
	}

	@Test
	public void testAddMultiValuedTags_nullMap_invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(NullPointerException.class, () -> queryMetric.addMultiValuedTags(null));
	}

	@Test
	public void test_addTag_emptyName_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(IllegalArgumentException.class, () -> queryMetric.addTag("", "value"));
	}

	@Test
	public void test_addTag_nullValue_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(NullPointerException.class, () -> queryMetric.addTag("tag", (String)null));
	}

	@Test
	public void test_addTag_emptyValue_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(IllegalArgumentException.class, () -> queryMetric.addTag("tag", ""));
	}

	@Test
	public void test_nullAggregator_invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(NullPointerException.class, () -> queryMetric.addAggregator(null));
	}

	@Test
	public void test_nullGrouper_invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(NullPointerException.class, () -> queryMetric.addGrouper(null));
	}

	@Test
	public void test_setLimit_lessThanZero_invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric");
		assertThrows(IllegalArgumentException.class, () -> queryMetric.setLimit(0));
	}
}
