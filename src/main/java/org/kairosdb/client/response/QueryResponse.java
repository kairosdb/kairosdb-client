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
 */
public class QueryResponse extends Response
{
	private final int responseCode;
	private final JsonMapper mapper;

	private List<Queries> queries;
	private String json;

	public QueryResponse(JsonMapper mapper, int responseCode, InputStream stream) throws IOException
	{
		super(responseCode);
		this.mapper = checkNotNull(mapper);
		this.responseCode = responseCode;
		this.json = getJson(stream);
		this.queries = getQueries();
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
		if (queries != null)
			return queries;

		if (getJson() != null)
		{
			if (responseCode >= 400)
			{
				ErrorResponse errorResponse = mapper.fromJson(json, ErrorResponse.class);
				addErrors(errorResponse.getErrors());
				return Collections.emptyList();
			}
			else
			{
				KairosQueryResponse response = mapper.fromJson(json, KairosQueryResponse.class);
				return response.getQueries();
			}
		}

		return Collections.emptyList();
	}

	/**
	 * Returns the json response as a string.
	 *
	 * @return json as a string or empty string.
	 */
	public String getJson()
	{
		return json;
	}

	public String getJson(InputStream stream) throws IOException
	{
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