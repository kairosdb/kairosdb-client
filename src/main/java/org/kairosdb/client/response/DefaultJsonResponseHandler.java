package org.kairosdb.client.response;


import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import org.apache.http.client.methods.HttpUriRequest;
import org.kairosdb.client.DataPointTypeRegistry;
import org.kairosdb.client.JsonMapper;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static java.util.Objects.requireNonNull;
import static org.kairosdb.client.util.Exceptions.propagate;

public class DefaultJsonResponseHandler<T> implements JsonResponseHandler
{
	private static final MediaType MEDIA_TYPE_JSON = MediaType.create("application", "json");
	private final Set<Integer> successfulResponseCodes;

	private final JsonMapper mapper;
	private final Type type;

	@SuppressWarnings({"unused", "WeakerAccess"})
	public DefaultJsonResponseHandler(Class<T> clazz)
	{
		this(clazz, new DataPointTypeRegistry());
	}

	@SuppressWarnings({"WeakerAccess", "unused"})
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
		requireNonNull(typeRegistry, "typeRegistry must not be null");
		mapper = new JsonMapper(typeRegistry);
		successfulResponseCodes = ImmutableSet.of(200, 204);
		this.type = requireNonNull(type, "type must not be null");
	}

	@Override
	public Object handleException(HttpUriRequest request, Exception exception) throws RuntimeException
	{
		throw propagate(request, exception);
	}

	@Override
	public T handle(HttpUriRequest request, ResponseHelper response) throws RuntimeException
	{
		if (!successfulResponseCodes.contains(response.getStatusCode()) && response.getStatusCode() != 400)
		{
			throw new UnexpectedResponseException(
					String.format("Expected response code to be %s, but was %d: %s", successfulResponseCodes, response.getStatusCode(), response.getStatusMessage()),
					request,
					response);
		}
		if (response.getStatusCode() == 204) {
			// Apparently some proxies/gateways return 204 but with content
			return null;
		}
		String contentType = response.getFirstHeader(CONTENT_TYPE);
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

			reader = new InputStreamReader(response.getInputStream(), StandardCharsets.UTF_8);

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
