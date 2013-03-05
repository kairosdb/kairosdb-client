/*
 * Copyright 2013 Proofpoint Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.client;

import org.junit.Test;
import org.kairosdb.client.builder.MetricBuilder;
import org.kairosdb.client.response.Response;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class ClientTest
{
	@Test(expected = NullPointerException.class)
	public void test_nullBuilder_invalid() throws IOException, URISyntaxException
	{
		FakeClient client = new FakeClient("host", 80);

		client.pushMetrics(null);
	}

	@Test
	public void test_ErrorResponse() throws IOException, URISyntaxException
	{
		MetricBuilder builder = MetricBuilder.getInstance();
		FakeClient client = new FakeClient("host", 80);
		client.setResponseCode(400);
		client.setResponseJson("{\"errors\":[\"Error1\", \"Error2\"]}");

		Response response = client.pushMetrics(builder);

		assertThat(response.getStatusCode(), equalTo(400));
		assertThat(response.getErrors().get(0), equalTo("Error1"));
		assertThat(response.getErrors().get(1), equalTo("Error2"));
	}
}
