package org.kairosdb.client.serializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import org.junit.Test;
import org.kairosdb.client.builder.DoubleDataPoint;
import org.kairosdb.client.builder.LongDataPoint;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DataPointSerializerTest
{
	@Test
	public void test_long()
	{
		JsonArray element = (JsonArray) new DataPointSerializer().serialize(new LongDataPoint(8383, 2), null, null);

		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(8383));
		array.add(new JsonPrimitive(2));
		assertThat(element, equalTo(array));
	}

	@Test
	public void test_double()
	{
		JsonArray element = (JsonArray) new DataPointSerializer().serialize(new DoubleDataPoint(8383, 2.3), null, null);

		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(8383));
		array.add(new JsonPrimitive(2.3));
		assertThat(element, equalTo(array));
	}
}
