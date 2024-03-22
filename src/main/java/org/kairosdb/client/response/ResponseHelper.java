package org.kairosdb.client.response;

import org.apache.commons.lang.NotImplementedException;
import org.apache.http.Header;
import org.apache.http.HttpResponse;

import java.io.IOException;
import java.io.InputStream;

public class ResponseHelper
{
	private final HttpResponse response;

	public ResponseHelper(HttpResponse response)
	{
		this.response = response;
	}

	public int getStatusCode()
	{
		return response.getStatusLine().getStatusCode();
	}

	public String getStatusMessage()
	{
		return response.getStatusLine().getReasonPhrase();
	}

	public HttpResponse getResponse()
	{
		return response;
	}

	public String getFirstHeader(String key)
	{
		Header header = response.getFirstHeader(key);
		if (header != null)
			return header.getValue();
		else
			return null;
	}

	/*public ListMultimap<HeaderName, String> getHeaders()
	{
		ImmutableListMultimap.Builder<HeaderName, String> builder = ImmutableListMultimap.builder();
		for (Header header : response.getAllHeaders())
		{
			builder.put(HeaderName.of(header.getName()), header.getValue());
		}
		return builder.build();
	}*/

	public long getBytesRead()
	{
		throw new NotImplementedException();
	}

	public InputStream getInputStream() throws IOException
	{
		if (response.getEntity() != null)
			return response.getEntity().getContent();
		return null;
	}
}
