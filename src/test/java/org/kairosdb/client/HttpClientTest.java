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
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import com.google.common.reflect.TypeToken;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.client.HttpClient.RollupTaskResponse;
import org.kairosdb.client.builder.*;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.QueryTagResponse;
import org.kairosdb.client.response.UnexpectedResponseException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.List;

import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpClientTest
{
	@Rule
	public ExpectedException thrown= ExpectedException.none();

	private CloseableHttpClient mockClient;
	private HttpClient client;
	private JsonMapper mapper;


	@Before
	public void setup() throws MalformedURLException
	{
		mapper = new JsonMapper(new DataPointTypeRegistry());
		HttpClientBuilder mockClientBuilder = mock(HttpClientBuilder.class);
		mockClient = mock(CloseableHttpClient.class);
		when(mockClientBuilder.build()).thenReturn(mockClient);

		client = new HttpClient(mockClientBuilder, "http://localhost");
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

	@Test
	public void test_getMetricNames() throws IOException
	{
		String[] expectedNames = {"metric1", "metric2", "metric3", "metric4"};
		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toMetricNameStream(expectedNames));
		CloseableHttpResponse mockResponse = mockResponse(200, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		List<String> metricNames = client.getMetricNames();

		assertThat(metricNames, hasItems(expectedNames));
	}

	@Test
	public void test_getMetricNames_withIOException() throws IOException
	{
		thrown.expectMessage("Error reading JSON response from server");
		thrown.expect(RuntimeException.class);

		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenThrow(new IOException("Expected Exception"));
		CloseableHttpResponse mockResponse = mockResponse(400, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		client.getMetricNames();
	}

	@Test
	public void test_getMetricNames_returns_400() throws IOException
	{
		thrown.expectMessage("Errors: This is an expected error");
		thrown.expect(UnexpectedResponseException.class);

		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toErrorStream("This is an expected error"));
		CloseableHttpResponse mockResponse = mockResponse(400, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		client.getMetricNames();
	}

	@Test
	public void test_createRollup() throws IOException
	{
		// Create response from create call
		String responseJson = Resources.toString(Resources.getResource("rollupResponse.json"), Charsets.UTF_8);
		RollupTaskResponse taskResponse = mapper.fromJson(responseJson, RollupTaskResponse.class);

		// create rollup builder
		RollupBuilder builder = createRollupBuilder();
		String json = appendId(taskResponse.getId(), builder.build());
		RollupTask expectedTask = mapper.fromJson(json, RollupTask.class);

		// Setup both responses. 1) from create call, 2) query for rollup
		HttpEntity mockEntity1 = mock(HttpEntity.class);
		when(mockEntity1.getContent()).thenReturn(toJsonStream(responseJson));
		HttpEntity mockEntity2 = mock(HttpEntity.class);
		when(mockEntity2.getContent()).thenReturn(toJsonStream(json));
		CloseableHttpResponse mockResponse1 = mockResponse(200, mockEntity1);
		CloseableHttpResponse mockResponse2 = mockResponse(200, mockEntity2);
		when(mockClient.execute(any())).thenReturn(mockResponse1).thenReturn(mockResponse2);

		RollupTask task = client.createRollupTask(builder);

		assertThat(task, equalTo(expectedTask));
	}

	@Test
	public void test_getRollupTasks() throws IOException
	{
		String rollupsJson = Resources.toString(Resources.getResource("rollups.json"), Charsets.UTF_8);
		Type type = new TypeToken<List<RollupTask>>(){}.getType();
		List<RollupTask> expectedTasks = mapper.fromJson(rollupsJson, type);

		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toJsonStream(rollupsJson));
		CloseableHttpResponse mockResponse = mockResponse(200, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		List<RollupTask> rollupTasks = client.getRollupTasks();

		assertThat(rollupTasks, equalTo(expectedTasks));
	}

	@Test
	public void test_getRollupTask() throws IOException
	{
		String rollupJson = Resources.toString(Resources.getResource("rollup.json"), Charsets.UTF_8);
		RollupTask expectedTask = mapper.fromJson(rollupJson, RollupTask.class);

		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toJsonStream(rollupJson));
		CloseableHttpResponse mockResponse = mockResponse(200, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		RollupTask rollupTask = client.getRollupTask("id");

		assertThat(rollupTask, equalTo(expectedTask));
	}

	@Test
	public void test_getStatus() throws IOException
	{
		List<String> statusItems = ImmutableList.of("JVM-Thread-Deadlock: OK", "Datastore-Query: OK");
		String expectedStatus = "[\"" + statusItems.get(0) + "\",\"" + statusItems.get(1) + "\"]";
		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toJsonStream(expectedStatus));
		CloseableHttpResponse mockResponse = mockResponse(200, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		List<String> status = client.getStatus();

		assertThat(status, hasItems(statusItems.get(0), statusItems.get(1)));
	}

	@Test
	public void test_statusCheck() throws IOException
	{
		CloseableHttpResponse mockResponse = mockResponse(204);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		int status = client.getStatusCheck();

		assertThat(status, equalTo(204));
	}

	@Test
	public void test_getVersion() throws IOException
	{
		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toJsonStream("{\"version\": \"KairosDB 0.9.4\"}"));
		CloseableHttpResponse mockResponse = mockResponse(200, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		String version = client.getVersion();

		assertThat(version, equalTo("KairosDB 0.9.4"));
	}

	@Test
	public void test_query() throws IOException
	{
		String expectedResponseJson = Resources.toString(Resources.getResource("response_valid.json"), Charsets.UTF_8);
		QueryResponse expectedResponse = mapper.fromJson(expectedResponseJson, QueryResponse.class);

		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(1, TimeUnit.HOURS);
		builder.addMetric("archive_search").addAggregator(AggregatorFactory.createAverageAggregator(1, TimeUnit.MINUTES));

		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toJsonStream(expectedResponseJson));
		CloseableHttpResponse mockResponse = mockResponse(200, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		QueryResponse queryResponse = client.query(builder);

		assertThat(queryResponse, equalTo(expectedResponse));
	}

	@Test
	public void test_queryTags() throws IOException
	{
		String expectedResponseJson = Resources.toString(Resources.getResource("query_tag_response_valid.json"), Charsets.UTF_8);
		QueryTagResponse expectedResponse = mapper.fromJson(expectedResponseJson, QueryTagResponse.class);

		QueryTagBuilder builder = QueryTagBuilder.getInstance();
		builder.setStart(1, TimeUnit.HOURS);
		builder.addMetric("kairosdb.datastore.query_time");

		HttpEntity mockEntity = mock(HttpEntity.class);
		when(mockEntity.getContent()).thenReturn(toJsonStream(expectedResponseJson));
		CloseableHttpResponse mockResponse = mockResponse(200, mockEntity);
		when(mockClient.execute(any())).thenReturn(mockResponse);

		QueryTagResponse queryResponse = client.queryTags(builder);

		assertThat(queryResponse, equalTo(expectedResponse));
	}

	private String appendId(String id, String json)
	{
		return json.replace("\"name\"", "\"id\":\"" + id + "\", \"name\"");
	}

	@SuppressWarnings("SameParameterValue")
	private CloseableHttpResponse mockResponse(int statusCode)
	{
		return this.mockResponse(statusCode, null);
	}

	private CloseableHttpResponse mockResponse(int statusCode, HttpEntity entity)
	{
		CloseableHttpResponse mockResponse = mock(CloseableHttpResponse.class);
		Header header = mock(Header.class);
		when(header.getValue()).thenReturn(APPLICATION_JSON.toString());
		when(mockResponse.getFirstHeader(CONTENT_TYPE)).thenReturn(header);
		when(mockResponse.getAllHeaders()).thenReturn(new Header[]{new BasicHeader(CONTENT_TYPE, APPLICATION_JSON.toString())});
		when(mockResponse.getStatusLine()).thenReturn(toResponseCode(statusCode));

		if (entity != null)
		{
			when(mockResponse.getEntity()).thenReturn(entity);
		}

		return mockResponse;
	}

	private InputStream toJsonStream(String json)
	{
		return new ByteArrayInputStream(json.getBytes());
	}

	private InputStream toMetricNameStream(String... metricNames)
	{
		StringBuilder builder = new StringBuilder("{\"results\":[");
		int count = 0;
		for (String metricName : metricNames)
		{
			if (count > 0)
			{
				builder.append(",");
			}
			builder.append("\"").append(metricName).append("\"");
			count++;
		}
		builder.append("]}");
		return new ByteArrayInputStream(builder.toString().getBytes());
	}

	@SuppressWarnings("SameParameterValue")
	private InputStream toErrorStream(String errorMessage)
	{
		String error = "{\"errors\": [\"" + errorMessage + "\"]}";
		return new ByteArrayInputStream(error.getBytes());
	}

	private RollupBuilder createRollupBuilder()
	{
		RollupBuilder builder = RollupBuilder.getInstance("test", new RelativeTime(1, TimeUnit.DAYS));
		Rollup rollup = builder.addRollup("foo");
		QueryBuilder queryBuilder = rollup.addQuery();
		queryBuilder.addMetric("testMetric");
		queryBuilder.setStart(1, TimeUnit.DAYS);
		return builder;
	}

	private StatusLine toResponseCode(int statusCode)
	{
		switch (statusCode)
		{
			case 200:
			case 204:
				return new TestStatusLine(statusCode, "OK");
			case 400:
				return new TestStatusLine(statusCode, "Bad Request");
			default:
				throw new IllegalArgumentException("Invalid status code");
		}
	}

	private class TestStatusLine implements StatusLine{
		private final int code;
		private final String reason;

		private TestStatusLine(int code, String reason)
		{
			this.code = code;
			this.reason = reason;
		}

		@Override
		public ProtocolVersion getProtocolVersion()
		{
			return null;
		}

		@Override
		public int getStatusCode()
		{
			return code;
		}

		@Override
		public String getReasonPhrase()
		{
			return reason;
		}
	}
}