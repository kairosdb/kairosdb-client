package org.kairosdb.client.serializer;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.TimeZone;

public class TimeZoneSerializer implements JsonSerializer<TimeZone>
{
	@Override
	public JsonElement serialize(TimeZone timeZone, Type type, JsonSerializationContext jsonSerializationContext)
	{
		return new JsonPrimitive(timeZone.getID());
	}
}
