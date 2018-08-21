package org.kairosdb.client.response;


import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.proofpoint.http.client.Request;
import com.proofpoint.http.client.Response;
import com.proofpoint.http.client.UnexpectedResponseException;
import org.kairosdb.client.DataPointTypeRegistry;
import org.kairosdb.client.JsonMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Set;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.proofpoint.http.client.ResponseHandlerUtils.propagate;
import static org.weakref.jmx.internal.guava.base.Preconditions.checkNotNull;

public class DefaultJsonResponseHandler<T> implements JsonResponseHandler
{
	private static final MediaType MEDIA_TYPE_JSON = MediaType.create("application", "json");
	private final Set<Integer> successfulResponseCodes;

	private final JsonMapper mapper;
	private final Type type;

	@SuppressWarnings("unused")
	public DefaultJsonResponseHandler(Class<T> clazz)
	{
		this(clazz, new DataPointTypeRegistry());
	}

	@SuppressWarnings("WeakerAccess")
	public DefaultJsonResponseHandler(Type type)
	{
		this(type, new DataPointTypeRegistry());
	}

	public DefaultJsonResponseHandler(Class<T> clazz, DataPointTypeRegistry typeRegistry)
	{
		this(TypeToken.of(clazz).getType(), typeRegistry);
	}

	public DefaultJsonResponseHandler(Type type, DataPointTypeRegistry typeRegistry)
	{
		checkNotNull(typeRegistry, "typeRegistry must not be null");
		mapper = new JsonMapper(typeRegistry);
		successfulResponseCodes = ImmutableSet.of(200, 204);
		this.type = checkNotNull(type, "type must not be null");
	}

	@Override
	public T handleException(Request request, Exception e)
	{
		throw propagate(request, e);
	}

	@Override
	public T handle(Request request, Response response)
	{
		if (!successfulResponseCodes.contains(response.getStatusCode()) && response.getStatusCode() != 400)
		{
			throw new UnexpectedResponseException(
					String.format("Expected response code to be %s, but was %d: %s", successfulResponseCodes, response.getStatusCode(), response.getStatusMessage()),
					request,
					response);
		}
		if (response.getStatusCode() == 204) {
			return null;
		}
		String contentType = response.getHeader(CONTENT_TYPE);
		if (contentType == null)
		{
			throw new UnexpectedResponseException("Content-Type is not set for response", request, response);
		}
		if (!MediaType.parse(contentType).is(MEDIA_TYPE_JSON))
		{
			throw new UnexpectedResponseException("Expected application/json response from server but got " + contentType, request, response);
		}

		Reader reader = null;
		try{
			if (response.getInputStream() == null)
			{
				return null;
			}

			reader = new InputStreamReader(response.getInputStream());

			if (response.getStatusCode() == 400)
			{
				ErrorResponse error = mapper.fromJson(reader, ErrorResponse.class);
				throw new UnexpectedResponseException(
						String.format("Expected response code to be %s, but was %d: %s", successfulResponseCodes, response.getStatusCode(), error.toString()),
						request,
						response);
			}
			else
			{
				return mapper.fromJson(reader, type);
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException("Error reading JSON response from server", e);
		}
		catch (JsonIOException | JsonSyntaxException e)
		{
			throw new IllegalArgumentException("Unable to create parse JSON response:\n", e);
		}
		finally
		{
			if (reader != null)
			{
				try
				{
					reader.close();
				}
				catch (IOException e)
				{
					//noinspection ThrowFromFinallyBlock
					throw new RuntimeException("Error closing reader ", e);
				}
			}
		}
	}
}
