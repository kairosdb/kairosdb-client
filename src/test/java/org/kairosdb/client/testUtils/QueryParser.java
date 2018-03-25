package org.kairosdb.client.testUtils;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kairosdb.client.builder.Grouper;
import org.kairosdb.client.builder.QueryBuilder;
import org.kairosdb.client.deserializer.GrouperDeserializer;
import org.kairosdb.client.deserializer.ListMultiMapDeserializer;
import org.kairosdb.client.deserializer.TimeZoneDeserializer;

import java.util.TimeZone;

public class QueryParser
{
	private final Gson gson;

	public QueryParser()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(ListMultimap.class, new ListMultiMapDeserializer());
		gsonBuilder.registerTypeAdapter(TimeZone.class, new TimeZoneDeserializer());
		gsonBuilder.registerTypeAdapter(Grouper.class, new GrouperDeserializer());
		gson = gsonBuilder.create();
	}

	public QueryBuilder parse(String json)
	{
		return gson.fromJson(json, QueryBuilder.class);
	}
}
