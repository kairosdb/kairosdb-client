package org.kairosdb.client.deserializer;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import org.kairosdb.client.builder.Aggregator;
import org.kairosdb.client.builder.aggregator.DeserializedAggregator;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkNotNull;

public class AggregatorDeserializer implements JsonDeserializer<Aggregator>
{
    private Gson gson = new Gson();

    @Override
    public Aggregator deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext)
            throws JsonParseException
    {
        Type mapType = new TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> map = gson.fromJson(jsonElement, mapType);
        return new DeserializedAggregator(map);
    }
}
