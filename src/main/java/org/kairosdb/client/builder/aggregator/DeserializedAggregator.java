package org.kairosdb.client.builder.aggregator;

import com.google.common.collect.ImmutableMap;
import org.kairosdb.client.builder.Aggregator;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DeserializedAggregator extends Aggregator
{
    private final Map<String, Object> properties;
    public DeserializedAggregator(Map<String, Object> properties)
    {
        super((String)properties.get("name"));
        this.properties = checkNotNull(properties, "properties cannot be null");
    }

    public ImmutableMap<String, Object> getProperties()
    {
        return ImmutableMap.copyOf(properties);
    }
}
