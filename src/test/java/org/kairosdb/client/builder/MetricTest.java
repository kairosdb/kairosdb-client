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

public class MetricTest
{
	@Test
	public void test_nullMetricName_invalid()
	{
		assertThrows(NullPointerException.class, () ->
				MetricBuilder.getInstance().addMetric(null));
	}

	@Test
	public void test_emptyMetricName_invalid()
	{
		assertThrows(IllegalArgumentException.class, () ->
				MetricBuilder.getInstance().addMetric(""));
	}

	@Test
	public void test_nullTagName_invalid()
	{
		assertThrows(NullPointerException.class, () ->
				MetricBuilder.getInstance().addMetric("metric1").addTag(null, "value"));
	}

	@Test
	public void test_emptyTagName_invalid()
	{
		assertThrows(IllegalArgumentException.class, () ->
				MetricBuilder.getInstance().addMetric("metric1").addTag("", "value"));
	}

	@Test
	public void test_nullTagValue_invalid()
	{
		assertThrows(NullPointerException.class, () ->
				MetricBuilder.getInstance().addMetric("metric1").addTag("tag", null));
	}

	@Test
	public void test_emptyTagValue_invalid()
	{
		assertThrows(IllegalArgumentException.class, () ->
				MetricBuilder.getInstance().addMetric("metric1").addTag("tag", ""));
	}

	@Test
	public void test_ttl_less_than_zero_invalid()
	{
		assertThrows(IllegalArgumentException.class, () ->
				MetricBuilder.getInstance().addMetric("metric1").addTtl(-1));
	}
}
