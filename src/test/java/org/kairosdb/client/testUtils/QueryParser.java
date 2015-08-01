package org.kairosdb.client.testUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.*;
import org.kairosdb.client.builder.Grouper;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.builder.grouper.TagGrouper;
import org.kairosdb.client.builder.grouper.TimeGrouper;
import org.kairosdb.client.builder.grouper.ValueGrouper;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TimeZone;

public class QueryParser
{
	private final Gson gson;

	public QueryParser()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ListMultimap.class, new ListMuliMapDeserializer());
		gsonBuilder.registerTypeAdapter(TimeZone.class, new TimeZoneDeserializer());
		gsonBuilder.registerTypeAdapter(Grouper.class, new GrouperDeserializer());
		gson = gsonBuilder.create();
	}

	public QueryBuilder parse(String json)
	{
		return gson.fromJson(json, QueryBuilder.class);
	}

	private class ListMuliMapDeserializer implements JsonDeserializer<ListMultimap<String, String>>
	{
		@Override
		public ListMultimap<String, String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
		{
			ListMultimap<String, String> map = ArrayListMultimap.create();
			JsonObject asJsonObject = jsonElement.getAsJsonObject();
			for (Map.Entry<String, JsonElement> entry : asJsonObject.entrySet())
			{
				map.put(entry.getKey(), entry.getValue().getAsString());
			}
			return map;
		}
	}

	private class TimeZoneDeserializer implements JsonDeserializer<TimeZone> {

		@Override
		public TimeZone deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
		{
			return TimeZone.getTimeZone(jsonElement.getAsString());
		}
	}

	private class GrouperDeserializer implements JsonDeserializer<Grouper> {

		@Override
		public Grouper deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
		{
			JsonObject jsGroupBy = jsonElement.getAsJsonObject();

			JsonElement nameElement = jsGroupBy.get("name");
			if (nameElement == null || nameElement.getAsString().isEmpty())
			{
				throw new JsonParseException("name cannot be null");
			}

			String name = nameElement.getAsString();
			if (name.equals("tag"))
			{
				return jsonDeserializationContext.deserialize(jsonElement, TagGrouper.class);
			}
			else if (name.equals("time"))
			{
				return jsonDeserializationContext.deserialize(jsonElement, TimeGrouper.class);
			}
			else if (name.equals("value"))
			{
				return jsonDeserializationContext.deserialize(jsonElement, ValueGrouper.class);
			}
			else
			{
				throw new JsonParseException("Invalid group_by: " + name);
			}
		}
	}
}
