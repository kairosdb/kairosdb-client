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
package org.kairosdb.client.response;

import org.kairosdb.client.JsonMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;

/**
 * Response returned by KairosDB.
 * <p/>
 * You can call getQueries() or getJson() but not both because the input stream
 * from the server is read. Once read, it cannot be read again.
 */
public class QueryResponse extends Response
{
	private final int responseCode;
	private final JsonMapper mapper;

	private InputStream stream;
	private KairosQueryResponse response;
	private String json;

	public QueryResponse(JsonMapper mapper, int responseCode, InputStream stream)
	{
		super(responseCode);
		this.mapper = checkNotNull(mapper);
		this.responseCode = responseCode;
		this.stream = stream;
	}

	/**
	 * Returns a list of query results returned by KairosDB. If status code is not
	 * successful, call getErrors to get errors returned.
	 *
	 * @return list of query results or empty list of no data or if an error is returned.
	 * @throws IOException if could not map response to Queries object
	 */
	public List<Queries> getQueries() throws IOException
	{
		if (response != null)
			return response.getQueries();

		if (stream != null)
		{
			InputStreamReader reader = new InputStreamReader(stream);
			try
			{
				if (responseCode >= 400)
				{
					ErrorResponse errorResponse = mapper.fromJson(reader, ErrorResponse.class);
					addErrors(errorResponse.getErrors());
					return Collections.emptyList();
				}
				else
				{
					response = mapper.fromJson(reader, KairosQueryResponse.class);
					return response.getQueries();
				}
			}
			finally
			{
				reader.close();
				stream = null;
			}
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the json response as a string.
	 *
	 * @return json as a string or empty string.
	 * @throws IOException
	 */
	public String getJson() throws IOException
	{
		if (json != null)
			return json;

		if (stream == null)
			return "";

		StringBuilder builder = new StringBuilder();
		BufferedReader reader = null;
		try
		{
			reader = new BufferedReader(new InputStreamReader(stream));
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
		}
		finally
		{
			if (reader != null)
				reader.close();
		}

		json = builder.toString();
		stream = null;
		return json;
	}

	private class KairosQueryResponse
	{
		private List<Queries> queries = new ArrayList<Queries>();

		public List<Queries> getQueries()
		{
			return queries;
		}
	}
}