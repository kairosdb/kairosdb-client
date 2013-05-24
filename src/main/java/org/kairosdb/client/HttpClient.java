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

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

/**
 * HTTP implementation of a client.
 */
public class HttpClient extends AbstractClient
{
	private org.apache.http.client.HttpClient client;

	/**
	 * Creates a client to talk to the host on the specified port.
	 *
	 * @param host name of the KairosDB server
	 * @param port KairosDB server port
	 */
	public HttpClient(String host, int port)
	{
		super(host, port);
		client = new DefaultHttpClient();
	}

	@Override
	protected ClientResponse postData(String json, String url) throws IOException
	{
		StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost postMethod = new HttpPost(url);
		postMethod.setEntity(requestEntity);

		return new HttpClientResponse(client.execute(postMethod));
	}

	@Override
	protected ClientResponse queryData(String url) throws IOException
	{
		HttpGet getMethod = new HttpGet(url);
		getMethod.addHeader("accept", "application/json");

		return new HttpClientResponse(client.execute(getMethod));
	}

	@Override
	public void shutdown()
	{
		client.getConnectionManager().shutdown();
	}
}