package org.kairosdb.client.response;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
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
	public void testConstructorNullMapperInvalid()
	{
		new QueryResponse(null, 200, new ByteArrayInputStream("bogus".getBytes()));
	}

	@Test
	public void getJson() throws IOException
	{
		String json = Resources.toString(Resources.getResource("response_valid.json"), Charsets.UTF_8);
		json = json.replaceAll(System.getProperty("line.separator"), ""); // remove newlines so strings can compare

		QueryResponse response = new QueryResponse(mapper, 200, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getJson(), equalTo(json));
		assertThat(response.getJson(), equalTo(json)); // Verify that called twice it still works
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getErrors().size(), equalTo(0));
		assertThat(response.getQueries(), equalTo(Collections.<Queries>emptyList()));
	}

	/**
	 * Does not popuplate the errors list but returns json instead
	 */
	@Test
	public void getJsonWithErrors() throws IOException
	{
		String json = "{\"errors\":[\"query.start_time relative or absolute time must be set\"]}";

		QueryResponse response = new QueryResponse(mapper, 400, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getJson(), equalTo(json));
		assertThat(response.getJson(), equalTo(json)); // Verify that called twice it still works
		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().size(), equalTo(0));
	}

	@Test
	public void getQueriesWithErrors() throws IOException
	{
		String json = "{\"errors\":[\"query.start_time relative or absolute time must be set\"]}";

		QueryResponse response = new QueryResponse(mapper, 400, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getQueries(), equalTo(Collections.<Queries>emptyList()));
		assertThat(response.getJson(), equalTo(""));
		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().size(), equalTo(1));
		assertThat(response.getErrors().get(0), equalTo("query.start_time relative or absolute time must be set"));
	}

	@Test
	public void getQueries() throws IOException, DataFormatException
	{
		String json = Resources.toString(Resources.getResource("response_valid.json"), Charsets.UTF_8);

		QueryResponse response = new QueryResponse(mapper, 200, new ByteArrayInputStream(json.getBytes()));

		List<Queries> queries = response.getQueries();
		assertThat(response.getJson(), equalTo(""));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getErrors().size(), equalTo(0));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(0).getTimestamp(), equalTo(1362034800000L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(0).longValue(), equalTo(1L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(1).getTimestamp(), equalTo(1362121200000L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(1).longValue(), equalTo(2L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(2).getTimestamp(), equalTo(1362207600000L));
		assertThat(queries.get(0).getResults().get(0).getDataPoints().get(2).longValue(), equalTo(3L));
	}
}