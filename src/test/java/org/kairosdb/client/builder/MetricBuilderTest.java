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
import org.junit.jupiter.api.Test;
import org.kairosdb.client.testUtils.MetricParser;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MetricBuilderTest
{
	@Test
	public void testBuild() throws IOException
	{
		MetricParser parser = new MetricParser();
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

		assertThat(parser.parse(builder.build()), equalTo(parser.parse(json)));
	}

	@Test
	public void test_metricContainsTags() throws IOException
	{
		assertThrows(IllegalStateException.class, () -> {
			MetricBuilder builder = MetricBuilder.getInstance();
			builder.addMetric("metric1");
			builder.addMetric("metric2").addTag("tag", "value");
			builder.build();
		});
	}

	@Test
	public void test_timestampNegative_valid()
	{
		MetricBuilder.getInstance().addMetric("metric").addDataPoint(-1, 3);
	}

	@Test
	public void test_timestampZero_valid()
	{
		MetricBuilder.getInstance().addMetric("metric").addDataPoint(0, 3);
	}

	@Test
	public void test_nullMetricName_invalid()
	{
		assertThrows(NullPointerException.class, () -> MetricBuilder.getInstance().addMetric(null));
	}

	@Test
	public void test_emptyMetricName_invalid()
	{
		assertThrows(IllegalArgumentException.class, () -> MetricBuilder.getInstance().addMetric(""));
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
	public void test_compression_flag_set()
	{
		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("sample");
		builder.setCompression(true);
		assert (builder.isCompressionEnabled());
	}

	@Test
	public void test_compression_default()
	{
		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("sample");
		assert (!builder.isCompressionEnabled());
	}

}
