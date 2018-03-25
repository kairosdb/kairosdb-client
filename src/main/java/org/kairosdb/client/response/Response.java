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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Response returned by the KairosDB server.
 */
public class Response
{
	private int statusCode;
	private List<String> errors = new ArrayList<String>();

	public Response()
	{
	}

	public Response(int statusCode)
	{
		this.statusCode = statusCode;
	}

	public void addErrors(List<String> errors)
	{
		this.errors = new ArrayList<String>(errors);
	}

	public List<String> getErrors()
	{
		return Collections.unmodifiableList(errors);
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public void setStatusCode(int statusCode)
	{
		this.statusCode = statusCode;
	}


	public static String getBody(InputStream stream) throws IOException
	{
		if (stream == null)
			return "";

		StringBuilder builder = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
		{
			String line;
			while ((line = reader.readLine()) != null)
			{
				builder.append(line);
			}
		}
		return builder.toString();
	}
}