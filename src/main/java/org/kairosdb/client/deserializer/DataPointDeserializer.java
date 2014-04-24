package org.kairosdb.client.deserializer;

import com.google.gson.*;
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.DoubleDataPoint;
import org.kairosdb.client.builder.LongDataPoint;

import java.lang.reflect.Type;

/**
 * Called by the JSON parser to deserialize a DataPoint.
 */
public class DataPointDeserializer implements JsonDeserializer<DataPoint>
{
	@Override
	public DataPoint deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		JsonArray array = json.getAsJsonArray();

		if (array.get(1).getAsString().contains("."))
		{
			return new DoubleDataPoint(array.get(0).getAsLong(), array.get(1).getAsDouble());
		}
		else
		{
			return new LongDataPoint(array.get(0).getAsLong(), array.get(1).getAsLong());
		}
	}
}
