package org.kairosdb.client.response;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonSyntaxException;
import org.junit.Before;
import org.junit.Test;
import org.kairosdb.client.DataPointTypeRegistry;
import org.kairosdb.client.JsonMapper;
import org.kairosdb.client.builder.DataFormatException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class QueryResponseTest
{

	private JsonMapper mapper;

	@Before
	public void setup()
	{
		mapper = new JsonMapper(new DataPointTypeRegistry());
	}

	@Test(expected = NullPointerException.class)
	public void testConstructorNullMapperInvalid() throws IOException
	{
		new QueryResponse(null, 200, new ByteArrayInputStream("bogus".getBytes()));
	}

	@Test
	public void getJson() throws IOException
	{
		String json = Resources.toString(Resources.getResource("response_valid.json"), Charsets.UTF_8);
		json = json.replaceAll(System.getProperty("line.separator"), ""); // remove newlines so strings can compare

		QueryResponse response = new QueryResponse(mapper, 200, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getErrors().size(), equalTo(0));
	}

	@Test
	public void getJsonWithErrors() throws IOException
	{
		String json = "{\"errors\":[\"query.start_time relative or absolute time must be set\"]}";

		QueryResponse response = new QueryResponse(mapper, 400, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().size(), equalTo(1));
		assertThat(response.getErrors().get(0), equalTo("query.start_time relative or absolute time must be set"));
	}

	@Test
	public void getQueriesWithErrors() throws IOException
	{
		String json = "{\"errors\":[\"query.start_time relative or absolute time must be set\"]}";

		QueryResponse response = new QueryResponse(mapper, 400, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getQueries(), equalTo(Collections.<Query>emptyList()));
		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().size(), equalTo(1));
		assertThat(response.getErrors().get(0), equalTo("query.start_time relative or absolute time must be set"));
	}

	@Test
	public void getQueries() throws IOException, DataFormatException
	{
		String json = Resources.toString(Resources.getResource("response_valid.json"), Charsets.UTF_8);
		json = json.replaceAll(System.getProperty("line.separator"), ""); // remove newlines so strings can compare

		QueryResponse response = new QueryResponse(mapper, 200, new ByteArrayInputStream(json.getBytes()));

		List<Query> queries = response.getQueries();
		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getErrors().size(), equalTo(0));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(0).getTimestamp(), equalTo(1362034800000L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(0).longValue(), equalTo(1L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(1).getTimestamp(), equalTo(1362121200000L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(1).longValue(), equalTo(2L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(2).getTimestamp(), equalTo(1362207600000L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(2).longValue(), equalTo(3L));
	}

	@Test(expected = JsonSyntaxException.class)
	public void getQueriesWith400ErrorNoJson() throws IOException
	{
		String responseBody = "Not JSON";

		new QueryResponse(mapper, 400, new ByteArrayInputStream(responseBody.getBytes()));
	}

	@Test(expected = JsonSyntaxException.class)
	public void getQueriesWith500ErrorNoJson() throws IOException
	{
		String responseBody = "Not JSON";

		new QueryResponse(mapper, 500, new ByteArrayInputStream(responseBody.getBytes()));
	}

	@Test(expected = JsonSyntaxException.class)
	public void getQueriesWith200ErrorNoJson() throws IOException
	{
		String responseBody = "Not JSON";

		new QueryResponse(mapper, 200, new ByteArrayInputStream(responseBody.getBytes()));
	}

	/**
	 * Verify result if something other than 200, 400, and 500 response code.
	 */
	public void getQueriesWith300NoJson() throws IOException
	{
		String responseBody = "Not JSON";

		QueryResponse response = new QueryResponse(mapper, 300, new ByteArrayInputStream(responseBody.getBytes()));

		assertThat(response.getQueries(), equalTo(Collections.<Query>emptyList()));
		assertThat(response.getBody(), equalTo(responseBody));
		assertThat(response.getStatusCode(), equalTo(300));
		assertThat(response.getErrors().size(), equalTo(0));
	}
}