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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.deserializer.DataPointDeserializer;
import org.kairosdb.client.deserializer.GroupByDeserializer;
import org.kairosdb.client.response.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Base code used to send metrics to Kairos or query Kairos.
 */
public abstract class AbstractClient implements Client
{
	private String url;
	private Gson mapper;

	/**
	 * Creates a client
	 *
	 * @param url url to the KairosDB server
	 */
	protected AbstractClient(String url) throws MalformedURLException
	{
		this.url = checkNotNullOrEmpty(url);
		new URL(url); // validate url

		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(GroupResult.class, new GroupByDeserializer());
		builder.registerTypeAdapter(DataPoint.class, new DataPointDeserializer());
		mapper = builder.create();
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
		if (stream != null)
		{
			InputStreamReader reader = new InputStreamReader(stream);
			try
			{
				if (responseCode >= 400)
				{
					QueryResponse response = new QueryResponse();
					response.setStatusCode(responseCode);
					ErrorResponse errorResponse = mapper.fromJson(reader, ErrorResponse.class);
					response.addErrors(errorResponse.getErrors());
					return response;
				}
				else
				{
					QueryResponse response = mapper.fromJson(reader, QueryResponse.class);
					response.setStatusCode(responseCode);
					return response;
				}
			}
			finally
			{
				reader.close();
			}
		}

		QueryResponse response = new QueryResponse();
		response.setStatusCode(responseCode);
		return response;
	}

	@Override
	public Response pushMetrics(MetricBuilder builder) throws URISyntaxException, IOException
	{
		checkNotNull(builder);
		return post(builder.build(), url + "/api/v1/datapoints");
	}

	private Response post(String json, String url) throws URISyntaxException, IOException
	{
		ClientResponse clientResponse = postData(json, url);

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
				throw new IOException("Could not get content stream.");

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
}
