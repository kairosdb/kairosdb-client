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

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.TimeUnit;

import java.io.IOException;
import java.net.MalformedURLException;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HttpClientTest
{
	private CloseableHttpClient apacheMockClient;

	@Before
	public void setup()
	{
		apacheMockClient = mock(CloseableHttpClient.class);
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_null_url_invalid() throws MalformedURLException
	{
		new HttpClient(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_empty_url_invalid() throws MalformedURLException
	{
		new HttpClient("");
	}

	@Test(expected = MalformedURLException.class)
	public void test_constructor_invalid_url() throws MalformedURLException
	{
		new HttpClient("foo");
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_negativeRetries_invalid() throws MalformedURLException
	{
		HttpClient client = new HttpClient("http://bogus");
		client.setRetryCount(-1);
	}

	@Test
	public void test_pushMetrics_DefaultRetries() throws IOException
	{
		when(apacheMockClient.execute(any())).thenThrow(new IOException("Fake Exception"));

		HttpClient client = new HttpClient("http://bogus");
		client.setClient(apacheMockClient);

		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("newMetric").addDataPoint(10, 10).addTag("host", "server1");

		try
		{
			client.pushMetrics(builder);
			fail("IOException should have been thrown");
		}
		catch (IOException e)
		{
			// ignore
		}
		verify(apacheMockClient, times(4)).execute(any()); // 1 try and 3 retries
	}

	@Test
	public void test_pushMetrics_setRetries() throws IOException
	{
		when(apacheMockClient.execute(any())).thenThrow(new IOException("Fake Exception"));

		HttpClient client = new HttpClient("http://bogus");
		client.setClient(apacheMockClient);
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
			// ignore
		}
		verify(apacheMockClient, times(11)).execute(any()); // 1 try and 10 retries
	}

	@Test
	public void test_pushMetrics_setRetries_zero() throws IOException
	{
		when(apacheMockClient.execute(any())).thenThrow(new IOException("Fake Exception"));

		HttpClient client = new HttpClient("http://bogus");
		client.setClient(apacheMockClient);
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
			// ignore
		}
		verify(apacheMockClient, times(1)).execute(any()); // 1 try and 0 retries
	}

	@Test
	public void test_query_DefaultRetries() throws IOException
	{
		when(apacheMockClient.execute(any())).thenThrow(new IOException("Fake Exception"));

		HttpClient client = new HttpClient("http://bogus");
		client.setClient(apacheMockClient);

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
			// ignore
		}
		verify(apacheMockClient, times(4)).execute(any()); // 1 try and 3 retries
	}

	@Test
	public void test_query_setRetries() throws IOException
	{
		when(apacheMockClient.execute(any())).thenThrow(new IOException("Fake Exception"));

		HttpClient client = new HttpClient("http://bogus");
		client.setClient(apacheMockClient);
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
			// ignore
		}
		verify(apacheMockClient, times(11)).execute(any()); // 1 try and 10 retries
	}

	@Test
	public void test_query_setRetries_zero() throws IOException
	{
		when(apacheMockClient.execute(any())).thenThrow(new IOException("Fake Exception"));

		HttpClient client = new HttpClient("http://bogus");
		client.setClient(apacheMockClient);
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
			// ignore
		}
		verify(apacheMockClient, times(1)).execute(any()); // 1 try and zero retries
	}
}