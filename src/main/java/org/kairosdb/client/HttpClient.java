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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * HTTP implementation of a client.
 */
public class HttpClient extends AbstractClient
{
	private org.apache.http.client.HttpClient client;
	private boolean isSSL;
	private int retries = 3;

	/**
	 * Creates a client to talk to the host on the specified port.
	 *
	 * @param host name of the KairosDB server
	 * @param port KairosDB server port
	 */
	public HttpClient(String host, int port)
	{
		this(host, port, false);
	}

	/**
	 * Creates a client to talk to the host on the specified port.
	 *
	 * @param host   name of the KairosDB server
	 * @param port   KairosDB server port
	 * @param useSSL if true, SSL is used for the connection
	 */
	public HttpClient(String host, int port, boolean useSSL)
	{
		super(host, port);
		client = new DefaultHttpClient();
		this.isSSL = useSSL;

		if (useSSL)
		{
			//NOTE: We have seen problems using getSocketFactory() with later versions ofJava 7.
			// This uses the Javax SSLContextImpl$TLS10Context and we get connection
			// failures about 2% of the time.
			//SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();

			SSLSocketFactory socketFactory = SSLSocketFactory.getSystemSocketFactory();
			Scheme sch = new Scheme("https", port, socketFactory);
			client.getConnectionManager().getSchemeRegistry().register(sch);
		}
	}

	@Override
	protected ClientResponse postData(String json, String url) throws IOException
	{
		StringEntity requestEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
		HttpPost postMethod = new HttpPost(url);
		postMethod.setEntity(requestEntity);

		HttpResponse response;

		int tries = ++retries;
		while (true)
		{
			tries--;
			try
			{
				response = client.execute(postMethod);
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
	protected ClientResponse queryData(String url) throws IOException
	{
		HttpGet getMethod = new HttpGet(url);
		getMethod.addHeader("accept", "application/json");

		HttpResponse response;

		int tries = ++retries;
		while (true)
		{
			tries--;
			try
			{
				response = client.execute(getMethod);
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
	public boolean isSSLConnection()
	{
		return isSSL;
	}

	@Override
	public void shutdown()
	{
		client.getConnectionManager().shutdown();
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
	protected void setClient(org.apache.http.client.HttpClient client)
	{
		this.client = client;
	}
}