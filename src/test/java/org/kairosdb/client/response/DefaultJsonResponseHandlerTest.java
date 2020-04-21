package org.kairosdb.client.response;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.kairosdb.client.DataPointTypeRegistry;
import org.kairosdb.client.JsonMapper;
import org.kairosdb.client.builder.RollupTask;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static junit.framework.TestCase.assertNull;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.apache.http.entity.ContentType.APPLICATION_JSON;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultJsonResponseHandlerTest
{
	private HttpUriRequest mockRequest;
	private ResponseHelper mockResponse;
	private DefaultJsonResponseHandler<String> handler;

	@Before
	public void setup()
	{
		mockRequest = mock(HttpUriRequest.class);
		mockResponse = mock(ResponseHelper.class);

		when(mockResponse.getFirstHeader(CONTENT_TYPE)).thenReturn(APPLICATION_JSON.toString());

		handler = new DefaultJsonResponseHandler<>(String.class);
	}

	@Test
	public void test_ErrorResponse()
	{
		when(mockResponse.getStatusCode()).thenReturn(500);
		when(mockResponse.getStatusMessage()).thenReturn("The status message");

		try
		{
			handler.handle(mockRequest, mockResponse);
			assertFalse("expected exception", false);
		}
		catch (UnexpectedResponseException e)
		{
			assertThat(e.getStatusCode(), equalTo(500));
			assertThat(e.getStatusMessage(), equalTo("The status message"));
		}
	}

	@Test
	public void test_noContentType()
	{
		when(mockResponse.getStatusCode()).thenReturn(200);
		when(mockResponse.getStatusMessage()).thenReturn("OK");

		try
		{
			handler.handle(mockRequest, mockResponse);
			assertFalse("expected exception", false);
		}
		catch (UnexpectedResponseException e)
		{
			assertThat(e.getMessage(), equalTo("Content-Type is not set for response"));
		}
	}

	@Test
	public void test_invalidMediaType()
	{
		when(mockResponse.getStatusCode()).thenReturn(200);
		when(mockResponse.getStatusMessage()).thenReturn("OK");
		when(mockResponse.getFirstHeader(CONTENT_TYPE)).thenReturn("application/bogus");

		try
		{
			handler.handle(mockRequest, mockResponse);
			assertFalse("expected exception", false);
		}
		catch (UnexpectedResponseException e)
		{
			assertThat(e.getMessage(), equalTo("Expected application/json response from server but got application/bogus"));
		}
	}

	@Test
	public void test_badJsonRequest() throws IOException
	{
		when(mockResponse.getStatusCode()).thenReturn(400);
		when(mockResponse.getStatusMessage()).thenReturn("Bad Request");
		when(mockResponse.getInputStream()).thenReturn(new ByteArrayInputStream("{\"errors\":[\"error message\"]}".getBytes()));

		try
		{
			handler.handle(mockRequest, mockResponse);
			assertFalse("expected exception", false);
		}
		catch (UnexpectedResponseException e)
		{
			assertThat(e.getMessage(), equalTo("Expected response code to be [200, 204], but was 400: Errors: error message\n"));
		}
	}

	@Test
	public void test_JsonSyntaxException() throws IOException
	{
		when(mockResponse.getStatusCode()).thenReturn(200);
		when(mockResponse.getStatusMessage()).thenReturn("OK");
		when(mockResponse.getInputStream()).thenReturn(new ByteArrayInputStream("{bogus}".getBytes()));

		try
		{
			handler.handle(mockRequest, mockResponse);
			assertFalse("expected exception", false);
		}
		catch (IllegalArgumentException e)
		{
			assertThat(e.getMessage(), equalTo("Unable to create parse JSON response:\n"));
		}
	}

	@Test
	public void test_RuntimeException() throws IOException
	{
		when(mockResponse.getStatusCode()).thenReturn(200);
		when(mockResponse.getStatusMessage()).thenReturn("OK");
		when(mockResponse.getInputStream()).thenThrow(new IOException());

		try
		{
			handler.handle(mockRequest, mockResponse);
			assertFalse("expected exception", false);
		}
		catch (RuntimeException e)
		{
			assertThat(e.getMessage(), equalTo("Error reading JSON response from server"));
		}
	}

	@Test
	/*
	 Apparently some proxies/gateways return 204 with content. That's just wrong.
	 */
	public void test_NoContent() throws IOException
	{
		when(mockResponse.getStatusCode()).thenReturn(204);
		when(mockResponse.getStatusMessage()).thenReturn("OK");
		when(mockResponse.getInputStream()).thenReturn(new ByteArrayInputStream("bogus".getBytes()));

		String response = handler.handle(mockRequest, mockResponse);

		assertNull(response);
	}

	@Test
	public void test() throws IOException
	{
		String json = Resources.toString(Resources.getResource("rollup.json"), Charsets.UTF_8);
		DefaultJsonResponseHandler<RollupTask> handler = new DefaultJsonResponseHandler<>(RollupTask.class);
		when(mockResponse.getStatusCode()).thenReturn(200);
		when(mockResponse.getStatusMessage()).thenReturn("OK");
		when(mockResponse.getInputStream()).thenReturn(new ByteArrayInputStream(json.getBytes()));

		RollupTask task = handler.handle(mockRequest, mockResponse);

		JsonMapper mapper = new JsonMapper(new DataPointTypeRegistry());
		assertThat(task, equalTo(mapper.fromJson(json, RollupTask.class)));
	}
}