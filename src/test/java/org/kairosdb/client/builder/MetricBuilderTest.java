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

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class MetricBuilderTest
{
	@Test
	public void test() throws IOException
	{
		String json = Resources.toString(Resources.getResource("multiple_metrics.json"), Charsets.UTF_8);

		MetricBuilder builder = MetricBuilder.getInstance();

		builder.addMetric("metric1")
				.addDataPoint(1, 10)
				.addDataPoint(2, 30L)
				.addTag("tag1", "tab1value")
				.addTag("tag2", "tab2value");

		builder.addMetric("metric2")
				.addDataPoint(2, 30L)
				.addDataPoint(3, 2.3)
				.addTag("tag3", "tab3value");

		assertThat(builder.build(), equalTo(json));
	}

	@Test(expected = IllegalStateException.class)
	public void test_metricContainsTags() throws IOException
	{
		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("metric1");
		builder.addMetric("metric2").addTag("tag", "value");

		builder.build();
	}

	public static class DataPointTest
	{

		@Test(expected = IllegalArgumentException.class)
		public void test_timestampNegative_invalid()
		{
			MetricBuilder.getInstance().addMetric("metric").addDataPoint(-1, 3);
		}

		@Test(expected = IllegalArgumentException.class)
		public void test_timestampZero_invalid()
		{
			MetricBuilder.getInstance().addMetric("metric").addDataPoint(0, 3);
		}
	}

	public static class MetricTest
	{
		@Test(expected = NullPointerException.class)
		public void test_nullMetricName_invalid()
		{
			MetricBuilder builder = MetricBuilder.getInstance();

			builder.addMetric(null);
		}

		@Test(expected = IllegalArgumentException.class)
		public void test_emptyMetricName_invalid()
		{
			MetricBuilder builder = MetricBuilder.getInstance();

			builder.addMetric("");
		}

		@Test(expected = NullPointerException.class)
		public void test_nullTagName_invalid()
		{
			MetricBuilder builder = MetricBuilder.getInstance();

			builder.addMetric("metric1").addTag(null, "value");
		}

		@Test(expected = IllegalArgumentException.class)
		public void test_emptyTagName_invalid()
		{
			MetricBuilder builder = MetricBuilder.getInstance();

			builder.addMetric("metric1").addTag("", "value");
		}

		@Test(expected = NullPointerException.class)
		public void test_nullTagValue_invalid()
		{
			MetricBuilder builder = MetricBuilder.getInstance();

			builder.addMetric("metric1").addTag("tag", null);
		}

		@Test(expected = IllegalArgumentException.class)
		public void test_emptyTagValue_invalid()
		{
			MetricBuilder builder = MetricBuilder.getInstance();

			builder.addMetric("metric1").addTag("tag", "");
		}
	}
}