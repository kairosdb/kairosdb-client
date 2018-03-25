package org.kairosdb.client.deserializer;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Map;

public class ListMultiMapDeserializer implements JsonDeserializer<ListMultimap<String, String>>
{
    @Override
    public ListMultimap<String, String> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException
    {
        ListMultimap<String, String> map = ArrayListMultimap.create();
        JsonObject asJsonObject = jsonElement.getAsJsonObject();
        for (Map.Entry<String, JsonElement> entry : asJsonObject.entrySet()) {
            map.put(entry.getKey(), entry.getValue().getAsString());
        }
        return map;
    }
}
