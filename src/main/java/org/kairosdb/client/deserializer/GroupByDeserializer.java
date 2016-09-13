package org.kairosdb.client.deserializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.kairosdb.client.response.GroupResult;
import org.kairosdb.client.response.grouping.*;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * Called by the JSON parser to deserialize groub-by.
 */
public class GroupByDeserializer implements JsonDeserializer<GroupResult>
{
	@Override
	public GroupResult deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
			throws JsonParseException
	{
		JsonObject jsGroupBy = jsonElement.getAsJsonObject();

		JsonElement nameElement = jsGroupBy.get("name");
		if (nameElement == null || nameElement.getAsString().isEmpty())
		{
			throw new JsonParseException("name cannot be null");
		}

		String name = nameElement.getAsString();
		if (name.equals("type"))
		{
			return jsonDeserializationContext.deserialize(jsonElement, DefaultGroupResult.class);
		}
		else if (name.equals("tag"))
		{
			return jsonDeserializationContext.deserialize(jsonElement, TagGroupResult.class);
		}
		else if (name.equals("time"))
		{
			return jsonDeserializationContext.deserialize(jsonElement, TimeGroupResult.class);
		}
		else if (name.equals("value"))
		{
			return jsonDeserializationContext.deserialize(jsonElement, ValueGroupResult.class);
		}
		else
		{
			Map<String, Object> result = jsonDeserializationContext.deserialize(jsGroupBy, new TypeToken<Map<String, Object>>(){}.getType());
			return new CustomGroupResult(result);
		}
	}
}
