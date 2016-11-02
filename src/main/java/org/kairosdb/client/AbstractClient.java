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

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.QueryTagBuilder;
import org.kairosdb.client.response.ErrorResponse;
import org.kairosdb.client.response.GetResponse;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.QueryTagResponse;
import org.kairosdb.client.response.Response;

import com.google.gson.stream.JsonReader;

/**
 * Base code used to send metrics to Kairos or query Kairos.
 */
public abstract class AbstractClient implements Client
{
	private String url;
	private JsonMapper mapper;
	private DataPointTypeRegistry typeRegistry;

	/**
	 * Creates a client
	 *
	 * @param url url to the KairosDB server
	 * @throws MalformedURLException if url is malformed
	 */
	protected AbstractClient(String url) throws MalformedURLException
	{
		this.url = checkNotNullOrEmpty(url);
		new URL(url); // validate url

		typeRegistry = new DataPointTypeRegistry();
		mapper = new JsonMapper(typeRegistry);
	}

	@Override
	public GetResponse getMetricNames() throws IOException
	{
		return get(url + "/api/v1/metricnames");
	}

	@Override
	public GetResponse getTagNames() throws IOException
	{
		return get(url + "/api/v1/tagnames");
	}

	@Override
	public GetResponse getTagValues() throws IOException
	{
		return get(url + "/api/v1/tagvalues");
	}

	@Override
	public QueryResponse query(QueryBuilder builder) throws URISyntaxException, IOException
	{
		ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints/query");
		int responseCode = clientResponse.getStatusCode();

		InputStream stream = clientResponse.getContentStream();
		return new QueryResponse(mapper, responseCode, stream);
	}
	
	@Override
	public QueryTagResponse queryTag(QueryTagBuilder builder) throws URISyntaxException, IOException
	{
		ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints/query/tags");
		int responseCode = clientResponse.getStatusCode();

		InputStream stream = clientResponse.getContentStream();
		return new QueryTagResponse(mapper, responseCode, stream);
	}

	@Override
	public Response pushMetrics(MetricBuilder builder) throws URISyntaxException, IOException
	{
		checkNotNull(builder);
		ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints");

		return getResponse(clientResponse);
	}

	@Override
	public Response deleteMetric(String name) throws IOException
	{
		checkNotNullOrEmpty(name);

		ClientResponse response = delete(url + "/api/v1/metric/" + name);
		return getResponse(response);
	}

	@Override
	public Response delete(QueryBuilder builder) throws URISyntaxException, IOException
	{
		checkNotNull(builder);
		ClientResponse clientResponse = postData(builder.build(), url + "/api/v1/datapoints/delete");

		return getResponse(clientResponse);
	}

	@Override
	public void registerCustomDataType(String groupType, Class dataPointClass)
	{
		typeRegistry.registerCustomDataType(groupType, dataPointClass);
	}

	@Override
	public Class getDataPointValueClass(String groupType)
	{
		return typeRegistry.getDataPointValueClass(groupType);
	}

	private Response getResponse(ClientResponse clientResponse) throws IOException
	{
		Response response = new Response(clientResponse.getStatusCode());
		InputStream stream = clientResponse.getContentStream();
		if (stream != null)
		{
			InputStreamReader reader = new InputStreamReader(stream);
			try
			{
				ErrorResponse errorResponse = mapper.fromJson(reader, ErrorResponse.class);
				response.addErrors(errorResponse.getErrors());
			}
			finally
			{
				reader.close();
			}
		}
		return response;
	}

	private GetResponse get(String url) throws IOException
	{
		ClientResponse clientResponse = queryData(url);
		int responseCode = clientResponse.getStatusCode();

		if (responseCode >= 400)
		{
			return new GetResponse(responseCode);
		}
		else
		{
			InputStream stream = clientResponse.getContentStream();
			if (stream == null)
			{
				throw new IOException("Could not get content stream.");
			}

			return new GetResponse(responseCode, readNameQueryResponse(stream));
		}
	}

	private List<String> readNameQueryResponse(InputStream stream) throws IOException
	{
		List<String> list = new ArrayList<String>();
		JsonReader reader = new JsonReader(new InputStreamReader(stream, "UTF-8"));

		try
		{
			reader.beginObject();
			String container = reader.nextName();
			if (container.equals("results"))
			{
				reader.beginArray();
				while (reader.hasNext())
				{
					list.add(reader.nextString());
				}
				reader.endArray();
				reader.endObject();
			}
		}
		finally
		{
			reader.close();
		}

		return list;
	}

	protected abstract ClientResponse postData(String json, String url) throws IOException;

	protected abstract ClientResponse queryData(String url) throws IOException;

	protected abstract ClientResponse delete(String url) throws IOException;
}
