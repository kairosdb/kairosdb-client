//
//  HttpClientResponse.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client;

import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

class HttpClientResponse implements ClientResponse
{
	private HttpResponse httpResponse;

	HttpClientResponse(HttpResponse httpResponse)
	{
		this.httpResponse = httpResponse;
	}

	@Override
	public int getStatusCode()
	{
		return httpResponse.getStatusLine().getStatusCode();
	}

	@Override
	public InputStream getContentStream() throws IOException
	{
		if (httpResponse.getEntity() != null)
			return httpResponse.getEntity().getContent();
		return null;
	}
}