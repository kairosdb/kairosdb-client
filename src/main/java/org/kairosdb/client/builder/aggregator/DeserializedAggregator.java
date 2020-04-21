package org.kairosdb.client.builder.aggregator;

import com.google.common.collect.ImmutableMap;
import org.kairosdb.client.builder.Aggregator;

import java.util.Map;

import static java.util.Objects.requireNonNull;

public class DeserializedAggregator extends Aggregator
{
    private final Map<String, Object> properties;
    public DeserializedAggregator(Map<String, Object> properties)
    {
        super((String)properties.get("name"));
        this.properties = requireNonNull(properties, "properties cannot be null");
    }

    public ImmutableMap<String, Object> getProperties()
    {
        return ImmutableMap.copyOf(properties);
    }
}
