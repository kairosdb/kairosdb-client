package org.kairosdb.client.response;

import com.proofpoint.http.client.ResponseHandler;

public interface JsonResponseHandler<T> extends ResponseHandler<T, RuntimeException>
{
}
