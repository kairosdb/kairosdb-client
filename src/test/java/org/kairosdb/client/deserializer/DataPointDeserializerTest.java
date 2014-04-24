package org.kairosdb.client.deserializer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import org.junit.Test;
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.DoubleDataPoint;
import org.kairosdb.client.builder.LongDataPoint;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DataPointDeserializerTest
{
	@Test
	public void test_double()
	{
		JsonArray json = new JsonArray();
		json.add(new JsonPrimitive(292929292));
		json.add(new JsonPrimitive(43.2));

		DataPoint dataPoint = new DataPointDeserializer().deserialize(json, null, null);

		assertThat(dataPoint.getTimestamp(), equalTo(292929292L));
		assertThat(dataPoint.isInteger(), equalTo(false));
		assertThat(((DoubleDataPoint)dataPoint).getValue(), equalTo(43.2));
	}

	@Test
	public void test_long()
	{
		JsonArray json = new JsonArray();
		json.add(new JsonPrimitive(292929292));
		json.add(new JsonPrimitive(43));

		DataPoint dataPoint = new DataPointDeserializer().deserialize(json, null, null);

		assertThat(dataPoint.getTimestamp(), equalTo(292929292L));
		assertThat(dataPoint.isInteger(), equalTo((true)));
		assertThat(((LongDataPoint)dataPoint).getValue(), equalTo(43L));
	}
}
