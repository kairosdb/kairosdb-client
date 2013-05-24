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
package org.kairosdb.client;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.Test;
import org.kairosdb.client.builder.LongDataPoint;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.TimeUnit;
import org.kairosdb.client.response.*;
import org.kairosdb.client.response.grouping.TagGroupResults;
import org.kairosdb.client.response.grouping.TimeGroupResults;
import org.kairosdb.client.response.grouping.ValueGroupResults;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

public class ClientTest
{
	@Test(expected = NullPointerException.class)
	public void test_nullBuilder_invalid() throws IOException, URISyntaxException
	{
		FakeClient client = new FakeClient(200, "");

		client.pushMetrics(null);
	}

	@Test
	public void test_ErrorResponse() throws IOException, URISyntaxException
	{
		MetricBuilder builder = MetricBuilder.getInstance();
		FakeClient client = new FakeClient(400, "{\"errors\":[\"Error1\", \"Error2\"]}");

		Response response = client.pushMetrics(builder);

		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().get(0), equalTo("Error1"));
		assertThat(response.getErrors().get(1), equalTo("Error2"));
	}

	@Test
	public void test_validResponse() throws IOException, URISyntaxException
	{
		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(2, TimeUnit.MILLISECONDS);

		String json = Resources.toString(Resources.getResource("response_valid.json"), Charsets.UTF_8);

		FakeClient client = new FakeClient(200, json);

		QueryResponse response = client.query(builder);

		List<Queries> queries = response.getQueries();
		List<Results> results = queries.get(0).getResults();

		assertThat(results.size(), equalTo(1));

		// Name
		assertThat(results.get(0).getName(), equalTo("archive_search"));

		// Tags
		assertThat(results.get(0).getTags().size(), equalTo(2));
		assertThat(results.get(0).getTags().get("partner_guid"), hasItems("messagelabs"));
		assertThat(results.get(0).getTags().get("bucket"), hasItems("0-5s", "10-20s", "5-10s", "20s-"));

		// Data points
		assertThat(results.get(0).getDataPoints().size(), equalTo(3));
		LongDataPoint dataPoint = (LongDataPoint) results.get(0).getDataPoints().get(0);
		assertThat(dataPoint.getTimestamp(), equalTo(1362034800000L));
		assertThat(dataPoint.getValue(), equalTo(1L));
		dataPoint = (LongDataPoint) results.get(0).getDataPoints().get(1);
		assertThat(dataPoint.getTimestamp(), equalTo(1362121200000L));
		assertThat(dataPoint.getValue(), equalTo(2L));
		dataPoint = (LongDataPoint) results.get(0).getDataPoints().get(2);
		assertThat(dataPoint.getTimestamp(), equalTo(1362207600000L));
		assertThat(dataPoint.getValue(), equalTo(3L));

		// Groups
		assertThat(results.get(0).getGroupResults().size(), equalTo(3));

		// Value Group
		ValueGroupResults valueGroupResults = (ValueGroupResults) results.get(0).getGroupResults().get(0);
		assertThat(valueGroupResults.getName(), equalTo("value"));
		assertThat(valueGroupResults.getRangeSize(), equalTo(10));
		assertThat(valueGroupResults.getRangeSize(), equalTo(10));
		assertThat(valueGroupResults.getGroup().getGroupNumber(), equalTo(2));

		// Tag Group
		TagGroupResults tagGroupResults = (TagGroupResults) results.get(0).getGroupResults().get(1);
		assertThat(tagGroupResults.getName(), equalTo("tag"));
		assertThat(tagGroupResults.getTags(), hasItems("bucket", "customer", "datacenter"));
		assertThat(tagGroupResults.getGroup().size(), equalTo(2));
		assertThat(tagGroupResults.getGroup().get("bucket"), equalTo("5-10s"));
		assertThat(tagGroupResults.getGroup().get("customer"), equalTo("Acme"));

		// Time Group
		TimeGroupResults timeGroupResults = (TimeGroupResults) results.get(0).getGroupResults().get(2);
		assertThat(timeGroupResults.getName(), equalTo("time"));
		assertThat(timeGroupResults.getGroupCount(), equalTo(168));
		assertThat(timeGroupResults.getRangeSize().getValue(), equalTo(1));
		assertThat(timeGroupResults.getRangeSize().getUnit(), equalTo(TimeUnit.HOURS.toString()));
		assertThat(timeGroupResults.getGroup().getGroupNumber(), equalTo(3));
	}
}
