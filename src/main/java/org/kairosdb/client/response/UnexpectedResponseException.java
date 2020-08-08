package org.kairosdb.client.response;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.MoreObjects.toStringHelper;

public class UnexpectedResponseException extends RuntimeException
{
	private final HttpUriRequest request;
	private final int statusCode;
	private final String statusMessage;
	private final ResponseHelper response;

	public UnexpectedResponseException(HttpUriRequest request, ResponseHelper response)
	{
		this(String.format("%d: %s", response.getStatusCode(), response.getStatusMessage()),
				request,
				response.getStatusCode(),
				response.getStatusMessage(),
				response);
	}

	public UnexpectedResponseException(String message, HttpUriRequest request, ResponseHelper response)
	{
		this(message,
				request,
				response.getStatusCode(),
				response.getStatusMessage(),
				response);
	}

	public UnexpectedResponseException(String message, HttpUriRequest request, int statusCode, String statusMessage, ResponseHelper response)
	{
		super(message);
		this.request = request;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.response = response;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public String getStatusMessage()
	{
		return statusMessage;
	}

	@Nullable
	public String getHeader(String name)
	{
		List<String> values = getHeaders(name);
		if (values.isEmpty()) {
			return null;
		}
		return values.get(0);
	}

	public List<String> getHeaders(String name)
	{
		Header[] headers = response.getResponse().getHeaders(name);
		List<String> ret = new ArrayList<>();
		if (headers != null)
		{
			for (Header header : headers)
			{
				ret.add(header.getValue());
			}
		}

		return ret;
	}


	@Override
	public String toString()
	{
		return toStringHelper(this)
				.add("request", request)
				.add("statusCode", statusCode)
				.add("statusMessage", statusMessage)
				.add("headers", response.getResponse().getAllHeaders())
				.toString();
	}
}
