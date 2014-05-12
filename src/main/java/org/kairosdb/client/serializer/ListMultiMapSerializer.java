package org.kairosdb.client.serializer;

import com.google.common.collect.ListMultimap;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class ListMultiMapSerializer implements JsonSerializer<ListMultimap>
{
	@Override
	public JsonElement serialize(ListMultimap src, Type typeOfSrc, JsonSerializationContext context)
	{
		return context.serialize(src.asMap());
	}
}
