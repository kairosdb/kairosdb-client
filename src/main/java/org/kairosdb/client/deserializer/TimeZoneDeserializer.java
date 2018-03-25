package org.kairosdb.client.deserializer;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.TimeZone;

public class TimeZoneDeserializer implements JsonDeserializer<TimeZone>
{
    @Override
    public TimeZone deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException
    {
        return TimeZone.getTimeZone(jsonElement.getAsString());
    }
}
