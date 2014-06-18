package org.kairosdb.client.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.kairosdb.client.builder.QueryMetric;

import java.lang.reflect.Type;

public class OrderSerializer implements JsonSerializer<QueryMetric.Order>
{
	@Override
	public JsonElement serialize(QueryMetric.Order order, Type type, JsonSerializationContext jsonSerializationContext)
	{
		JsonParser parser = new JsonParser();
		return parser.parse(order.toString());
	}
}
