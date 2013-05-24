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

import com.google.gson.stream.JsonReader;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.response.ErrorResponse;
import org.kairosdb.client.response.GetResponse;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.Response;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Base code used to send metrics to Kairos or query Kairos.
 */
public abstract class AbstractClient implements Client
{
	private String host;
	private int port;
	private ObjectMapper mapper;

	/**
	 * Creates a client
	 *
	 * @param host name of the Kairos server
	 * @param port Kairos port
	 */
	protected AbstractClient(String host, int port)
	{
		this.host = host;
		this.port = port;
		mapper = new ObjectMapper();
	}

	@Override
	public GetResponse getMetricNames() throws IOException
	{
		return get("http://" + host + ":" + port + "/api/v1/metricnames");
	}

	@Override
	public GetResponse getTagNames() throws IOException
	{
		return get("http://" + host + ":" + port + "/api/v1/tagnames");
	}

	@Override
	public GetResponse getTagValues() throws IOException
	{
		return get("http://" + host + ":" + port + "/api/v1/tagvalues");
	}

	@Override
	public QueryResponse query(QueryBuilder builder) throws URISyntaxException, IOException
	{
		ClientResponse clientResponse = postData(builder.build(), "http://" + host + ":" + port + "/api/v1/datapoints/query");
		int responseCode = clientResponse.getStatusCode();

		InputStream stream = clientResponse.getContentStream();
		try
		{
			if (stream != null)
			{
				StringWriter resultWriter = new StringWriter();
				IOUtils.copy(stream, resultWriter);

				JsonParser jsonParser = mapper.getJsonFactory().createJsonParser(resultWriter.toString());
				if (responseCode >= 400)
				{
					QueryResponse response = new QueryResponse();
					response.setStatusCode(responseCode);
					ErrorResponse errorResponse = mapper.readValue(jsonParser, ErrorResponse.class);
					response.addErrors(errorResponse.getErrors());
					return response;
				}
				else
				{
					QueryResponse response = mapper.readValue(jsonParser, QueryResponse.class);
					response.setStatusCode(responseCode);
					return response;
				}
			}
		}
		finally
		{
			if (stream != null)
				stream.close();
		}

		QueryResponse response = new QueryResponse();
		response.setStatusCode(responseCode);
		return response;
	}

	@Override
	public Response pushMetrics(MetricBuilder builder) throws URISyntaxException, IOException
	{
		checkNotNull(builder);
		return post(builder.build(), "http://" + host + ":" + port + "/api/v1/datapoints");  // todo what if we want https?
	}

	private Response post(String json, String url) throws URISyntaxException, IOException
	{
		ClientResponse clientResponse = postData(json, url);

		Response response = new Response(clientResponse.getStatusCode());
		InputStream stream = clientResponse.getContentStream();
		try
		{
			if (stream != null)
			{
				StringWriter resultWriter = new StringWriter();
				IOUtils.copy(stream, resultWriter);

				JsonParser jsonParser = mapper.getJsonFactory().createJsonParser(resultWriter.toString());
				ErrorResponse errorResponse = mapper.readValue(jsonParser, ErrorResponse.class);
				response.addErrors(errorResponse.getErrors());
			}
		}
		finally
		{
			if (stream != null)
				stream.close();
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
