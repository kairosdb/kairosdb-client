package org.kairosdb.client;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kairosdb.client.builder.Aggregator;
import org.kairosdb.client.builder.Grouper;
import org.kairosdb.client.deserializer.AggregatorDeserializer;
import org.kairosdb.client.deserializer.GroupByDeserializer;
import org.kairosdb.client.deserializer.GrouperDeserializer;
import org.kairosdb.client.deserializer.ListMultiMapDeserializer;
import org.kairosdb.client.deserializer.ResultsDeserializer;
import org.kairosdb.client.deserializer.TimeZoneDeserializer;
import org.kairosdb.client.response.GroupResult;
import org.kairosdb.client.response.Result;

import java.io.Reader;
import java.lang.reflect.Type;
import java.util.TimeZone;

public class JsonMapper
{
	private Gson mapper;

	public JsonMapper(DataPointTypeRegistry typeRegistry)
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(GroupResult.class, new GroupByDeserializer());
		builder.registerTypeAdapter(Result.class, new ResultsDeserializer(typeRegistry));
		builder.registerTypeAdapter(ListMultimap.class, new ListMultiMapDeserializer());
		builder.registerTypeAdapter(TimeZone.class, new TimeZoneDeserializer());
		builder.registerTypeAdapter(Grouper.class, new GrouperDeserializer());
		builder.registerTypeAdapter(Aggregator.class, new AggregatorDeserializer());

		mapper = builder.create();
	}

	public <T> T fromJson(Reader json, Type typeOfT)
	{
		return mapper.fromJson(json, typeOfT);
	}

	public <T> T fromJson(String json, Type typeOfT)
	{
		return mapper.fromJson(json, typeOfT);
	}

}
