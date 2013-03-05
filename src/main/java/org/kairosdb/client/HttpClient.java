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

import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.IOException;
import java.io.InputStream;

/**
 * HTTP implementation of a client.
 */
public class HttpClient extends Client
{
	private org.apache.commons.httpclient.HttpClient client;
	private PostMethod postMethod;
	private GetMethod getMethod;

	/**
	 * Creates a client to talk to the host on the specified port.
	 *
	 * @param host name of the Kairos server
	 * @param port Kairos server port
	 */
	public HttpClient(String host, int port)
	{
		super(host, port);
		client = new org.apache.commons.httpclient.HttpClient();
	}

	@Override
	protected int postData(String json, String url) throws IOException
	{
		StringRequestEntity requestEntity = new StringRequestEntity(json, "application/json", "UTF-8");
		postMethod = new PostMethod(url);
		postMethod.setRequestEntity(requestEntity);

		return client.executeMethod(postMethod);
	}

	@Override
	protected int queryData(String url) throws IOException
	{
		getMethod = new GetMethod(url);
		getMethod.addRequestHeader("accept", "application/json");
		return client.executeMethod(getMethod);
	}

	@Override
	protected InputStream getGetResponseStream() throws IOException
	{
		return getMethod.getResponseBodyAsStream();
	}

	@Override
	protected InputStream getPostResponseStream() throws IOException
	{
		return postMethod.getResponseBodyAsStream();
	}
}