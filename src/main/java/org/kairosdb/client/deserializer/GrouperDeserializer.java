package org.kairosdb.client.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.kairosdb.client.builder.Grouper;
import org.kairosdb.client.builder.grouper.TagGrouper;
import org.kairosdb.client.builder.grouper.TimeGrouper;
import org.kairosdb.client.builder.grouper.ValueGrouper;

import java.lang.reflect.Type;

public class GrouperDeserializer implements JsonDeserializer<Grouper>
{
    @Override
    public Grouper deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException
    {
        JsonObject jsGroupBy = jsonElement.getAsJsonObject();

        JsonElement nameElement = jsGroupBy.get("name");
        if (nameElement == null || nameElement.getAsString().isEmpty()) {
            throw new JsonParseException("name cannot be null");
        }

        String name = nameElement.getAsString();
        switch (name) {
            case "tag":
                return jsonDeserializationContext.deserialize(jsonElement, TagGrouper.class);
            case "time":
                return jsonDeserializationContext.deserialize(jsonElement, TimeGrouper.class);
            case "value":
                return jsonDeserializationContext.deserialize(jsonElement, ValueGrouper.class);
            default:
                throw new JsonParseException("Invalid group_by: " + name);
        }
    }
}
