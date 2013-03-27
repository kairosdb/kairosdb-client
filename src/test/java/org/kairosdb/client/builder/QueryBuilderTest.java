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
import org.kairosdb.client.builder.grouper.TagGrouper;
import org.kairosdb.client.builder.grouper.TimeGrouper;
import org.kairosdb.client.builder.grouper.ValueGrouper;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class QueryBuilderTest
{
	@Test(expected = NullPointerException.class)
	public void test_MetricNameNull_Invalid()
	{
		QueryBuilder.getInstance().addMetric(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_MetricNameEmpty_Invalid()
	{
		QueryBuilder.getInstance().addMetric("");
	}

	@Test(expected = NullPointerException.class)
	public void test_AbsoluteStartNull_Invalid()
	{
		QueryBuilder.getInstance().setStart(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_AbsoluteStartAndRelativeStartSet_Invalid()
	{
		QueryBuilder.getInstance().setStart(new Date()).setStart(3, TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeStartAndAbsoluteStartSet_Invalid()
	{
		QueryBuilder.getInstance().setStart(3, TimeUnit.DAYS).setStart(new Date());
	}

	@Test(expected = NullPointerException.class)
	public void test_RelativeStartUnitNull_Invalid() throws IOException
	{
		QueryBuilder.getInstance().setStart(3, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeStartValueZero_Invalid() throws IOException
	{
		QueryBuilder.getInstance().setStart(0, TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeStartValueNegative_Invalid() throws IOException
	{
		QueryBuilder.getInstance().setStart(-1, TimeUnit.DAYS);
	}

	@Test(expected = NullPointerException.class)
	public void test_RelativeEndUnitNull_Invalid() throws IOException
	{
		QueryBuilder.getInstance().setEnd(3, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeEndValueZero_Invalid() throws IOException
	{
		QueryBuilder.getInstance().setEnd(0, TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeEndValueNegative_Invalid() throws IOException
	{
		QueryBuilder.getInstance().setEnd(-1, TimeUnit.DAYS);
	}

	@Test(expected = IllegalStateException.class)
	public void test_startTimeNotSpecified_Invalid() throws IOException
	{
		QueryBuilder.getInstance().build();
	}

	@Test(expected = IllegalStateException.class)
	public void test_endTimeAbsoluteLaterThanStartTimeAbsolute() throws IOException
	{
		QueryBuilder.getInstance()
				.setStart(new Date())
				.setEnd(new Date(System.currentTimeMillis() - 10000))
				.build();
	}

	@Test(expected = IllegalStateException.class)
	public void test_endTimeRelativeLaterThanStartTimeRelative() throws IOException
	{
		QueryBuilder.getInstance()
				.setStart(2, TimeUnit.DAYS)
				.setEnd(2, TimeUnit.WEEKS)
				.build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_cacheTimeZero_Invalid()
	{
		QueryBuilder.getInstance().setCacheTime(0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_cacheTimeNegative_Invalid()
	{
		QueryBuilder.getInstance().setCacheTime(-1);
	}

	@Test
	public void test_MultipleMetricsWithRelativeTimes() throws IOException
	{
		String json = Resources.toString(Resources.getResource("query_multiple_metrics_relative_times.json"), Charsets.UTF_8);

		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setCacheTime(2000)
				.setStart(3, TimeUnit.WEEKS)
				.setEnd(2, TimeUnit.DAYS);
		builder.addMetric("metric1")
				.addTag("foo", "bar")
				.addTag("larry", "moe")
				.addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.DAYS));
		builder.addMetric("metric2")
				.addTag("curly", "joe");

		assertThat(builder.build(), equalTo(json));
	}

	@Test
	public void test_SingleMetricAbsoluteStartNoEndTimeNoTags() throws IOException
	{
		String json = Resources.toString(Resources.getResource("query_single_metric_absoluteStart_noEndTime_noTags.json"), Charsets.UTF_8);

		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(new Date(1359774127000L))
				.addMetric("metric1")
				.addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.DAYS))
				.addAggregator(AggregatorFactory.createRateAggregator());

		assertThat(builder.build(), equalTo(json));
	}

	@Test
	public void test_WithGroupBy() throws IOException
	{
		String json = Resources.toString(Resources.getResource("query_withGroupBys.json"), Charsets.UTF_8);

		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(2, TimeUnit.MONTHS);
		QueryMetric metric = builder.addMetric("metric1");
		metric.addGrouper(new ValueGrouper(10));
		metric.addGrouper(new TagGrouper("tag1", "tag2"));
		metric.addGrouper(new TimeGrouper(new RelativeTime(2, TimeUnit.HOURS), 3));

		assertThat(builder.build(), equalTo(json));
	}
}