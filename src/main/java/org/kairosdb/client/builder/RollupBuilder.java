package org.kairosdb.client.builder;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.sun.istack.internal.NotNull;
import org.kairosdb.client.builder.aggregator.CustomAggregator;
import org.kairosdb.client.builder.grouper.CustomGrouper;
import org.kairosdb.client.serializer.CustomAggregatorSerializer;
import org.kairosdb.client.serializer.CustomGrouperSerializer;
import org.kairosdb.client.serializer.ListMultiMapSerializer;
import org.kairosdb.client.serializer.OrderSerializer;
import org.kairosdb.client.serializer.TimeZoneSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

public class RollupBuilder
{
    private final transient Gson mapper;

    @SuppressWarnings("FieldCanBeLocal")
    @NotNull
    private final String name;

    @SuppressWarnings("FieldCanBeLocal")
    @NotNull
    @SerializedName("execution_interval")
    private final RelativeTime executionInterval;

    private final List<Rollup> rollups = new ArrayList<Rollup>();

    private RollupBuilder(String name, RelativeTime executionInterval)
    {
        this.name = checkNotNullOrEmpty(name, "name cannot be null or empty");
        this.executionInterval = checkNotNull(executionInterval, "executionInterval cannot be null");

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(CustomAggregator.class, new CustomAggregatorSerializer());
        builder.registerTypeAdapter(CustomGrouper.class, new CustomGrouperSerializer());
        builder.registerTypeAdapter(ListMultimap.class, new ListMultiMapSerializer());
        builder.registerTypeAdapter(QueryMetric.Order.class, new OrderSerializer());
        builder.registerTypeAdapter(TimeZone.class, new TimeZoneSerializer());

        mapper = builder.create();
    }

    /**
     * Returns a new query builder.
     *
     * @return new query builder
     */
    public static RollupBuilder getInstance(String name, RelativeTime executionInterval)
    {
        checkNotNullOrEmpty(name, "name cannot be null or empty");
        checkNotNull(executionInterval, "executionInterval cannot be null");

        return new RollupBuilder(name, executionInterval);
    }

    public Rollup addRollup(String saveAs)
    {
        Rollup rollup = new Rollup(saveAs);
        rollups.add(rollup);

        return rollup;
    }

    public String build()
            throws IOException
    {
        validate();
        return mapper.toJson(this);
    }

    private void validate()
    {
        checkState(rollups.size() > 0, "No roll-ups added");
        for (Rollup rollup : rollups) {
           rollup.validate();
        }
    }
}
