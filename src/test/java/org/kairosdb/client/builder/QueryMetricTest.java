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

import org.junit.Test;

public class QueryMetricTest
{
	@Test(expected = NullPointerException.class)
	public void test_constructor_NullName_Invalid()
	{
		new QueryMetric(null, "sum");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_EmptyName_Invalid()
	{
		new QueryMetric("", "sum");
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_NullAggregator_Invalid()
	{
		new QueryMetric("metric", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_EmptyAggregator_Invalid()
	{
		new QueryMetric("metric", "");
	}

	@Test(expected = NullPointerException.class)
	public void test_setTags_null_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric", "sum");

		queryMetric.setTags(null);
	}

	@Test(expected = NullPointerException.class)
	public void test_addTag_nullName_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric", "sum");

		queryMetric.addTag(null, "value");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_addTag_emptyName_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric", "sum");

		queryMetric.addTag("", "value");
	}

	@Test(expected = NullPointerException.class)
	public void test_addTag_nullValue_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric", "sum");

		queryMetric.addTag("tag", null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_addTag_emptyValue_Invalid()
	{
		QueryMetric queryMetric = new QueryMetric("metric", "sum");

		queryMetric.addTag("tag", "");
	}


}