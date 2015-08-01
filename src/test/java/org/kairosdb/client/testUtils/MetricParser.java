package org.kairosdb.client.testUtils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.Metric;

import java.lang.reflect.Type;
import java.util.List;

public class MetricParser
{
	private static final Type listType = new TypeToken<List<Metric>>(){}.getType();

	private final Gson gson;

	public MetricParser()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(DataPoint.class, new DataPointDeserializer());
		gson = gsonBuilder.create();
	}

	public List<Metric> parse(String json)
	{
		return gson.fromJson(json, listType);
	}

	private class DataPointDeserializer implements JsonDeserializer<DataPoint>
	{
		@Override
		public DataPoint deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException
		{
			JsonArray array = jsonElement.getAsJsonArray();
			long timestamp = array.get(0).getAsLong();
			double value = array.get(1).getAsDouble();
			return new DataPoint(timestamp, value);
		}
	}
}
