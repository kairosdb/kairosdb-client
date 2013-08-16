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

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.TimeUnit;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class HttpClientTest
{
	@Test(expected = IllegalArgumentException.class)
	public void test_negativeRetries_invalid()
	{
		HttpClient client = new HttpClient("bogus", 80);
		client.setRetryCount(-1);
	}

	@Test
	public void test_pushMetrics_DefaultRetries() throws IOException, URISyntaxException
	{
		HttpClient client = new HttpClient("bogus", 80);
		FakeClient fakeClient = new FakeClient();
		client.setClient(fakeClient);

		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("newMetric").addDataPoint(10, 10).addTag("host", "server1");

		try
		{
			client.pushMetrics(builder);
			fail("IOException should have been thrown");
		}
		catch (IOException e)
		{
			assertThat(fakeClient.getExecutionCount(), equalTo(4));  // 1 try and 3 retries
		}
	}

	@Test
	public void test_pushMetrics_setRetries() throws URISyntaxException
	{
		HttpClient client = new HttpClient("bogus", 80);
		FakeClient fakeClient = new FakeClient();
		client.setClient(fakeClient);
		client.setRetryCount(10);

		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("newMetric").addDataPoint(10, 10).addTag("host", "server1");

		try
		{
			client.pushMetrics(builder);
			fail("IOException should have been thrown");
		}
		catch (IOException e)
		{
			assertThat(fakeClient.getExecutionCount(), equalTo(11));  // 1 try and 10 retries
		}
	}

	@Test
	public void test_pushMetrics_setRetries_zero() throws URISyntaxException
	{
		HttpClient client = new HttpClient("bogus", 80);
		FakeClient fakeClient = new FakeClient();
		client.setClient(fakeClient);
		client.setRetryCount(0);

		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("newMetric").addDataPoint(10, 10).addTag("host", "server1");

		try
		{
			client.pushMetrics(builder);
			fail("IOException should have been thrown");
		}
		catch (IOException e)
		{
			assertThat(fakeClient.getExecutionCount(), equalTo(1));  // 1 try and 0 retries
		}
	}

	@Test
	public void test_query_DefaultRetries() throws IOException, URISyntaxException
	{
		HttpClient client = new HttpClient("bogus", 80);
		FakeClient fakeClient = new FakeClient();
		client.setClient(fakeClient);

		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(1, TimeUnit.DAYS);
		builder.addMetric("newMetric");

		try
		{
			client.query(builder);
			fail("IOException should have been thrown");
		}
		catch (IOException e)
		{
			assertThat(fakeClient.getExecutionCount(), equalTo(4));  // 1 try and 3 retries
		}
	}

	@Test
	public void test_query_setRetries() throws URISyntaxException
	{
		HttpClient client = new HttpClient("bogus", 80);
		FakeClient fakeClient = new FakeClient();
		client.setClient(fakeClient);
		client.setRetryCount(10);

		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(1, TimeUnit.DAYS);
		builder.addMetric("newMetric");

		try
		{
			client.query(builder);
			fail("IOException should have been thrown");
		}
		catch (IOException e)
		{
			assertThat(fakeClient.getExecutionCount(), equalTo(11));  // 1 try and 10 retries
		}
	}

	@Test
	public void test_query_setRetries_zero() throws URISyntaxException
	{
		HttpClient client = new HttpClient("bogus", 80);
		FakeClient fakeClient = new FakeClient();
		client.setClient(fakeClient);
		client.setRetryCount(0);

		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(1, TimeUnit.DAYS);
		builder.addMetric("newMetric");

		try
		{
			client.query(builder);
			fail("IOException should have been thrown");
		}
		catch (IOException e)
		{
			assertThat(fakeClient.getExecutionCount(), equalTo(1));  // 1 try and 0 retries
		}
	}

	private static class FakeClient implements org.apache.http.client.HttpClient
	{
		private int executionCount;

		private int getExecutionCount()
		{
			return executionCount;
		}

		@Override
		public HttpParams getParams()
		{
			return null;
		}

		@Override
		public ClientConnectionManager getConnectionManager()
		{
			return null;
		}

		@Override
		public HttpResponse execute(HttpUriRequest httpUriRequest) throws IOException
		{
			executionCount++;
			throw new IOException("Fake Exception");
		}

		@Override
		public HttpResponse execute(HttpUriRequest httpUriRequest, HttpContext httpContext) throws IOException
		{
			executionCount++;
			throw new IOException("Fake Exception");
		}

		@Override
		public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest) throws IOException
		{
			return null;
		}

		@Override
		public HttpResponse execute(HttpHost httpHost, HttpRequest httpRequest, HttpContext httpContext) throws IOException
		{
			return null;
		}

		@Override
		public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler) throws IOException
		{
			return null;
		}

		@Override
		public <T> T execute(HttpUriRequest httpUriRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException
		{
			return null;
		}

		@Override
		public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler) throws IOException
		{
			return null;
		}

		@Override
		public <T> T execute(HttpHost httpHost, HttpRequest httpRequest, ResponseHandler<? extends T> responseHandler, HttpContext httpContext) throws IOException
		{
			return null;
		}
	}
}