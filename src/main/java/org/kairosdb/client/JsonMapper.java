package org.kairosdb.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kairosdb.client.deserializer.GroupByDeserializer;
import org.kairosdb.client.deserializer.ResultsDeserializer;
import org.kairosdb.client.response.GroupResult;
import org.kairosdb.client.response.Results;

import java.io.Reader;
import java.lang.reflect.Type;

public class JsonMapper
{
	private Gson mapper;

	public JsonMapper(DataPointTypeRegistry typeRegistry)
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(GroupResult.class, new GroupByDeserializer());
		builder.registerTypeAdapter(Results.class, new ResultsDeserializer(typeRegistry));
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
