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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.net.MalformedURLException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * HTTP implementation of a client.
 */
public class HttpClient extends AbstractClient
{
	private CloseableHttpClient client;
	private int retries = 3;

	/**
	 * Creates a client to talk to the host on the specified port.
	 *
	 * @param url url to KairosDB server
	 */
	public HttpClient(String url) throws MalformedURLException
	{
		super(url);
		HttpClientBuilder builder = HttpClientBuilder.create();
		client = builder.build();
	}

	@Override
	protected ClientResponse postData(String json, String url) throws IOException
	{
		StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost postMethod = new HttpPost(url);
		postMethod.setEntity(requestEntity);

		return execute(postMethod);
	}

	@Override
	protected ClientResponse queryData(String url) throws IOException
	{
		HttpGet getMethod = new HttpGet(url);
		getMethod.addHeader("accept", "application/json");

		return execute(getMethod);
	}

	@Override
	protected ClientResponse delete(String url) throws IOException
	{
		HttpDelete deleteMethod = new HttpDelete(url);
		deleteMethod.addHeader("accept", "application/json");

		return execute(deleteMethod);
	}

	private ClientResponse execute(HttpUriRequest request) throws IOException
	{
		HttpResponse response;

		int tries = retries + 1;
		while (true)
		{
			tries--;
			try
			{
				response = client.execute(request);
				break;
			}
			catch (IOException e)
			{
				if (tries < 1)
					throw e;
			}
		}

		return new HttpClientResponse(response);
	}

	@Override
	public void shutdown() throws IOException
	{
		client.close();
	}

	@Override
	public int getRetryCount()
	{
		return retries;
	}

	public void setRetryCount(int retries)
	{
		checkArgument(retries >= 0);
		this.retries = retries;
	}

	/**
	 * Used for testing only
	 */
	protected void setClient(CloseableHttpClient client)
	{
		this.client = client;
	}
}