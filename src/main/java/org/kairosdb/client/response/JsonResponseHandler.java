package org.kairosdb.client.response;

import org.apache.http.client.methods.HttpUriRequest;

public interface JsonResponseHandler<T>
{
	T handleException(HttpUriRequest request, Exception exception)
			throws RuntimeException;

	T handle(HttpUriRequest request, ResponseHelper response)
			throws RuntimeException;
}
