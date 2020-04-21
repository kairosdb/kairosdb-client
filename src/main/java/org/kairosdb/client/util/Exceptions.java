package org.kairosdb.client.util;

import com.google.common.base.Throwables;
import org.apache.http.client.methods.HttpUriRequest;

import java.io.IOException;
import java.net.ConnectException;

public class Exceptions
{
	public static RuntimeException propagate(HttpUriRequest request, Throwable exception)
	{
		if (exception instanceof ConnectException) {
			throw new RuntimeException("Server refused connection: " + request.getURI().toASCIIString(), (ConnectException) exception);
		}
		if (exception instanceof IOException) {
			throw new RuntimeException((IOException) exception);
		}
		throw Throwables.propagate(exception);
	}
}
