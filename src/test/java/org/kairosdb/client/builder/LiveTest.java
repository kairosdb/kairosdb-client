//
//  LiveTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder;

import org.junit.Test;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.builder.grouper.ValueGrouper;
import org.kairosdb.client.response.QueryResponse;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class LiveTest
{
	@Test
	public void test() throws IOException, URISyntaxException
	{
		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(1, TimeUnit.MONTHS);
		QueryMetric metric = builder.addMetric("archive_search");
		metric.addGrouper(new ValueGrouper(10));
		metric.addTag("customer_name", "Telx_Group");
		metric.addAggregator(new SamplingAggregator("sum", 1, TimeUnit.MILLISECONDS));

		HttpClient client = new HttpClient("localhost", 9000);
		QueryResponse response = client.query(builder);

		assertThat(response.getStatusCode(), equalTo(200));

		System.out.println(response.getQueries());

	}
}