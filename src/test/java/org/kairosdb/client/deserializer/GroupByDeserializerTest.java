package org.kairosdb.client.deserializer;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import org.junit.Test;

public class GroupByDeserializerTest
{

	@Test(expected = JsonParseException.class)
	public void test_missingName_invalid()
	{
		JsonObject json = new JsonObject();
		json.add("value", new JsonPrimitive(5));

		new GroupByDeserializer().deserialize(json, null, null);
	}

	@Test(expected = JsonParseException.class)
	public void test_invalid_name()
	{
		JsonObject json = new JsonObject();
		json.add("name", new JsonPrimitive("bogus"));
		json.add("value", new JsonPrimitive(5));

		new GroupByDeserializer().deserialize(json, null, null);
	}
}
