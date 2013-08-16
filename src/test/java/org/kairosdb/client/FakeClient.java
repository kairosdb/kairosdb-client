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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FakeClient extends AbstractClient
{
	private int responseCode;
	private String responseJson;

	protected FakeClient(int responseCode, String responseJson)
	{
		super("fake", 80);
		this.responseCode = responseCode;
		this.responseJson = responseJson;
	}

	@Override
	protected ClientResponse postData(String json, String url) throws IOException
	{
		return new FakeClientResponse(responseCode, responseJson);
	}

	@Override
	protected ClientResponse queryData(String url) throws IOException
	{
		return new FakeClientResponse(responseCode, responseJson);
	}

	private class FakeClientResponse implements ClientResponse
	{
		private int statusCode;
		private String responseJson;

		private FakeClientResponse(int statusCode, String responseJson)
		{
			this.statusCode = statusCode;
			this.responseJson = responseJson;
		}

		@Override
		public int getStatusCode()
		{
			return statusCode;
		}

		@Override
		public InputStream getContentStream() throws IOException
		{
			if (responseJson != null)
				return new ByteArrayInputStream(responseJson.getBytes());
			return null;
		}
	}

	@Override
	public boolean isSSLConnection()
	{
		return false;
	}

	@Override
	public int getRetryCount()
	{
		return 0;
	}

	@Override
	public void shutdown()
	{
	}
}