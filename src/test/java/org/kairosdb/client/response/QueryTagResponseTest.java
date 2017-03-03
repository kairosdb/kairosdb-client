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
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class QueryTagResponseTest
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
		new QueryTagResponse(null, 200, new ByteArrayInputStream("bogus".getBytes()));
	}

	@Test
	public void getJson() throws IOException
	{
		String json = Resources.toString(Resources.getResource("query_tag_response_valid.json"), Charsets.UTF_8);
		json = json.replaceAll(System.getProperty("line.separator"), ""); // remove newlines so strings can compare

		QueryTagResponse response = new QueryTagResponse(mapper, 200, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getErrors().size(), equalTo(0));
	}

	@Test
	public void getJsonWithErrors() throws IOException
	{
		String json = "{\"errors\":[\"query.start_time relative or absolute time must be set\"]}";

		QueryTagResponse response = new QueryTagResponse(mapper, 400, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().size(), equalTo(1));
		assertThat(response.getErrors().get(0), equalTo("query.start_time relative or absolute time must be set"));
	}

	@Test
	public void getQueriesWithErrors() throws IOException
	{
		String json = "{\"errors\":[\"query.start_time relative or absolute time must be set\"]}";

		QueryTagResponse response = new QueryTagResponse(mapper, 400, new ByteArrayInputStream(json.getBytes()));

		assertThat(response.getQueries(), equalTo(Collections.<TagQuery>emptyList()));
		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().size(), equalTo(1));
		assertThat(response.getErrors().get(0), equalTo("query.start_time relative or absolute time must be set"));
	}

	@Test
	public void getQueries() throws IOException, DataFormatException
	{
		String json = Resources.toString(Resources.getResource("query_tag_response_valid.json"), Charsets.UTF_8);
		json = json.replaceAll(System.getProperty("line.separator"), ""); // remove newlines so strings can compare

		QueryTagResponse response = new QueryTagResponse(mapper, 200, new ByteArrayInputStream(json.getBytes()));

		List<TagQuery> queries = response.getQueries();
		assertThat(response.getBody(), equalTo(json));
		assertThat(response.getStatusCode(), equalTo(200));
		assertThat(response.getErrors().size(), equalTo(0));
		assertThat(queries.get(0).getResults().get(0).getTags().get("host"), hasItems("localhost", "host2"));
		assertThat(queries.get(0).getResults().get(0).getTags().get("rollup"), hasItems("kairos.import_export_unit_test_rollup"));
		assertThat(queries.get(0).getResults().get(0).getTags().get("status"), hasItems("success"));
	}

	@Test(expected = JsonSyntaxException.class)
	public void getQueriesWith400ErrorNoJson() throws IOException
	{
		String responseBody = "Not JSON";

		new QueryTagResponse(mapper, 400, new ByteArrayInputStream(responseBody.getBytes()));
	}

	@Test(expected = JsonSyntaxException.class)
	public void getQueriesWith500ErrorNoJson() throws IOException
	{
		String responseBody = "Not JSON";

		new QueryTagResponse(mapper, 500, new ByteArrayInputStream(responseBody.getBytes()));
	}

	@Test(expected = JsonSyntaxException.class)
	public void getQueriesWith200ErrorNoJson() throws IOException
	{
		String responseBody = "Not JSON";

		new QueryTagResponse(mapper, 200, new ByteArrayInputStream(responseBody.getBytes()));
	}

	/**
	 * Verify result if something other than 200, 400, and 500 response code.
	 */
	@Test
	public void getQueriesWith300NoJson() throws IOException
	{
		String responseBody = "Not JSON";

		QueryTagResponse response = new QueryTagResponse(mapper, 300, new ByteArrayInputStream(responseBody.getBytes()));

		assertThat(response.getQueries(), equalTo(Collections.<TagQuery>emptyList()));
		assertThat(response.getBody(), equalTo(responseBody));
		assertThat(response.getStatusCode(), equalTo(300));
		assertThat(response.getErrors().size(), equalTo(0));
	}

}