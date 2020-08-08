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

import com.google.common.reflect.TypeToken;
import org.apache.http.HttpResponse;
import org.apache.http.client.entity.EntityBuilder;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.StandardHttpRequestRetryHandler;
import org.kairosdb.client.builder.*;
import org.kairosdb.client.response.DefaultJsonResponseHandler;
import org.kairosdb.client.response.JsonResponseHandler;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.QueryTagResponse;
import org.kairosdb.client.response.ResponseHelper;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.apache.http.HttpHeaders.*;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.apache.http.entity.ContentType.TEXT_PLAIN;
import static org.kairosdb.client.util.Exceptions.propagate;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * HTTP implementation of a client.
 */
public class HttpClient implements Client
{
	private static final String GZIP = "gzip";

	private static final String PATH_ROLLUPS = "/api/v1/rollups/";
	private static final String PATH_METRIC_NAMES = "/api/v1/metricnames";
	private static final String PATH_STATUS = "/api/v1/health/status";
	private static final String PATH_CHECK = "/api/v1/health/check";
	private static final String PATH_VERSION = "/api/v1/version";
	private static final String PATH_QUERY = "/api/v1/datapoints/query";
	private static final String PATH_QUERY_TAGS = "/api/v1/datapoints/query/tags";
	private static final String PATH_DELETE = "/api/v1/datapoints/delete";
	private static final String PATH_DATAPOINTS = "/api/v1/datapoints";
	private static final String PATH_METRIC = "/api/v1/metric/";

	private final CloseableHttpClient client;
	private final String url;
	private final DataPointTypeRegistry typeRegistry;

	/**
	 * Creates a client to talk to the host on the specified port.
	 *
	 * @param url url to the KairosDB server
	 * @throws MalformedURLException if url is malformed
	 */
	public HttpClient(String url) throws MalformedURLException
	{
		this(HttpClientBuilder.create()
						.setRetryHandler(new StandardHttpRequestRetryHandler()),
				url);
	}

	/**
	 * Creates a client to talk to the host on the specified port. This version
	 * of the constructor exposes the HttpClientBuilder that can be used to set
	 * various properties on the client.
	 *
	 * @param builder client builder.
	 * @param url     url to the KairosDB server
	 * @throws MalformedURLException if the url is malformed
	 */
	public HttpClient(HttpClientBuilder builder, String url) throws MalformedURLException
	{
		checkNotNullOrEmpty(url, "url cannot be null");
		requireNonNull(builder, "builder must not be null");
		this.url = url;
		new URL(url); // validate url
		client = builder.build();
		typeRegistry = new DataPointTypeRegistry();
	}

	public HttpClient(CloseableHttpClient client, String url) throws MalformedURLException
	{
		checkNotNullOrEmpty(url, "url cannot be null");
		requireNonNull(client, "client must not be null");
		this.url = url;
		new URL(url); // validate url
		this.client = client;
		typeRegistry = new DataPointTypeRegistry();
	}

	public void registerCustomDataType(String groupType, Class dataPointClass)
	{
		checkNotNullOrEmpty(groupType, "groupType may not be null or empty");
		requireNonNull(dataPointClass, "dataPointClass may not be null");
		typeRegistry.registerCustomDataType(groupType, dataPointClass);
	}

	@Override
	public Class getDataPointValueClass(String groupType)
	{
		return typeRegistry.getDataPointValueClass(groupType);
	}

	@SuppressWarnings("unused")
	public DataPointTypeRegistry getTypeRegistry()
	{
		return typeRegistry;
	}

	@SuppressWarnings("unchecked")
	@Override
	public RollupTask createRollupTask(RollupBuilder builder)
	{
		DefaultJsonResponseHandler<RollupTaskResponse> responseHandler = new DefaultJsonResponseHandler<>(RollupTaskResponse.class, typeRegistry);
		RollupTaskResponse rollupTaskResponse = (RollupTaskResponse) postData(PATH_ROLLUPS, builder.build(), responseHandler);

		checkNotNullOrEmpty(rollupTaskResponse.id, "No task id was returned in the rollup-task");
		return getRollupTask(rollupTaskResponse.id);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<RollupTask> getRollupTasks()
	{
		Type type = new TypeToken<List<RollupTask>>(){}.getType();
		return (List<RollupTask>) queryData(PATH_ROLLUPS, new DefaultJsonResponseHandler<List<RollupTask>>(type, typeRegistry));
	}

	@SuppressWarnings("unchecked")
	@Override
	public RollupTask getRollupTask(String id)
	{
		return (RollupTask)queryData(PATH_ROLLUPS + id, new DefaultJsonResponseHandler<RollupTask>(RollupTask.class, typeRegistry));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteRollupTask(String id)
	{
		delete(PATH_ROLLUPS + id,  new DefaultJsonResponseHandler<Void>(Void.class, typeRegistry));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getMetricNames()
	{
		Results results = (Results) queryData(PATH_METRIC_NAMES, new DefaultJsonResponseHandler<Results>(Results.class, typeRegistry));
		return results.results;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getStatus()
	{
		Type type = new TypeToken<List<String>>(){}.getType();
		return (List<String>) queryData(PATH_STATUS, new DefaultJsonResponseHandler<List<String>>(type, typeRegistry));
	}

	@Override
	public int getStatusCheck()
	{
		return queryData(PATH_CHECK, new JsonResponseHandler<Integer>()
		{
			@Override
			public Integer handleException(HttpUriRequest request, Exception exception) throws RuntimeException
			{
				throw propagate(request, exception);
			}

			@Override
			public Integer handle(HttpUriRequest request, ResponseHelper response) throws RuntimeException
			{
				return response.getStatusCode();
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getVersion()
	{
		Version version = (Version) queryData(PATH_VERSION, new DefaultJsonResponseHandler<Version>(Version.class, typeRegistry));
		return version.version;
	}

	@Override
	public <T> T query(QueryBuilder builder, JsonResponseHandler<T> handler)
	{
		return postData(PATH_QUERY, builder.build(), handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public QueryResponse query(QueryBuilder builder)
	{
		DefaultJsonResponseHandler<QueryResponse> responseHandler = new DefaultJsonResponseHandler<>(QueryResponse.class, typeRegistry);
		return (QueryResponse)postData(PATH_QUERY, builder.build(), responseHandler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public QueryTagResponse queryTags(QueryTagBuilder builder)
	{
		DefaultJsonResponseHandler<QueryTagResponse> responseHandler = new DefaultJsonResponseHandler<>(QueryTagResponse.class, typeRegistry);
		return (QueryTagResponse)postData(PATH_QUERY_TAGS, builder.build(), responseHandler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T queryTags(QueryTagBuilder builder, JsonResponseHandler<T> handler)
	{
		return postData(PATH_QUERY_TAGS, builder.build(), handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void pushMetrics(MetricBuilder builder)
	{
		postData(PATH_DATAPOINTS, builder.build(), new DefaultJsonResponseHandler<Void>(Void.class, typeRegistry), builder.isCompressionEnabled());
	}

	@SuppressWarnings("unchecked")
	@Override
	public void deleteMetric(String name)
	{
		delete(PATH_METRIC + name,  new DefaultJsonResponseHandler<Void>(Void.class, typeRegistry));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void delete(QueryBuilder builder)
	{
		postData(PATH_DELETE, builder.build(), new DefaultJsonResponseHandler<Void>(Void.class, typeRegistry));
	}

	private <T> T postData(String path, String json, JsonResponseHandler<T> responseHandler)
	{
		return this.postData(path, json, responseHandler, false);
	}

	private <T> T postData(String path, String json, JsonResponseHandler<T> responseHandler, boolean compressed)
	{
		HttpPost post = new HttpPost(createURI(path));

		EntityBuilder entityBuilder = EntityBuilder.create()
				.setContentType(APPLICATION_JSON)
				.setText(json);

		if (compressed)
		{
			entityBuilder.gzipCompress();
			post.addHeader(CONTENT_ENCODING, GZIP);
		}
		else
		{
			post.addHeader(CONTENT_TYPE, APPLICATION_JSON.toString());
		}
		post.setEntity(entityBuilder.build());

		return execute(post, responseHandler);
	}

	@SuppressWarnings("unchecked")
	private <T> T queryData(String path, JsonResponseHandler<T> responseHandler)
	{
		HttpGet get = new HttpGet(createURI(path));
		get.addHeader(ACCEPT, APPLICATION_JSON.toString());
		get.addHeader(ACCEPT_ENCODING, "gzip");

		return execute(get, responseHandler);
	}

	@SuppressWarnings("UnusedReturnValue")
	private <T> T delete(String path, JsonResponseHandler<T> responseHandler)
	{
		HttpDelete delete = new HttpDelete(createURI(path));
		delete.addHeader(ACCEPT, APPLICATION_JSON.toString());

		return execute(delete, responseHandler);
	}

	private URI createURI(String path)
	{
		try
		{
			return new URI(url + path);
		}
		catch (URISyntaxException e)
		{
			throw new IllegalArgumentException("Invalid URI", e);
		}
	}

	private <T> T execute(HttpUriRequest request, JsonResponseHandler<T> responseHandler)
	{
		try
		{
			HttpResponse response = client.execute(request);
			return responseHandler.handle(request, new ResponseHelper(response));
		}
		catch (IOException e)
		{
			return responseHandler.handleException(request, e);
		}
	}

	@Override
	public void close() throws IOException
	{
		client.close();
	}


	@SuppressWarnings("unused")
	private class Results
	{
		private List<String> results;
	}

	@SuppressWarnings("unused")
	class RollupTaskResponse
	{
		private String id;
		private String name;
		private Attributes attributes;

		String getId()
		{
			return id;
		}
	}

	@SuppressWarnings("unused")
	private class Attributes
	{
		private String url;
	}

	@SuppressWarnings("unused")
	private class Version
	{
		private String version;
	}
}