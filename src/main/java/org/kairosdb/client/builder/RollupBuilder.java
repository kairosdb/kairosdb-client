package org.kairosdb.client.builder;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.aggregator.CustomAggregator;
import org.kairosdb.client.builder.grouper.CustomGrouper;
import org.kairosdb.client.serializer.*;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import static java.util.Objects.requireNonNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;
import static org.kairosdb.client.util.Preconditions.checkState;

public class RollupBuilder
{
    private final transient Gson mapper;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private final String name;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    @SerializedName("execution_interval")
    private final RelativeTime executionInterval;

    private final List<Rollup> rollups = new ArrayList<>();

    private RollupBuilder(String name, RelativeTime executionInterval)
    {
        this.name = checkNotNullOrEmpty(name, "name cannot be null or empty");
        this.executionInterval = requireNonNull(executionInterval, "executionInterval cannot be null");

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
        requireNonNull(executionInterval, "executionInterval cannot be null");

        return new RollupBuilder(name, executionInterval);
    }

    public Rollup addRollup(String saveAs)
    {
        Rollup rollup = new Rollup(saveAs);
        rollups.add(rollup);

        return rollup;
    }

    public String build()
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
