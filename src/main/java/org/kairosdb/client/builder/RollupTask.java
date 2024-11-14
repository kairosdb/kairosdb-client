package org.kairosdb.client.builder;

import com.google.gson.annotations.SerializedName;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

public class RollupTask
{
    private final String id;
    private final List<Rollup> rollups = new ArrayList<>();

    private final String name;

    @SerializedName("execution_interval")
    private final RelativeTime executionInterval;

    private final long lastModified;

    public RollupTask(String id, String name, RelativeTime executionInterval, long lastModified)
    {
        this.id = checkNotNullOrEmpty(id, "id cannot be null or empty");
        this.name = checkNotNullOrEmpty(name, "name cannot be null or empty");
        this.executionInterval = requireNonNull(executionInterval, "executionInterval cannot be null");
        this.lastModified = lastModified;
    }

    public String getId()
    {
        return id;
    }

    public List<Rollup> getRollups()
    {
        return rollups;
    }

    public String getName()
    {
        return name;
    }

    public RelativeTime getExecutionInterval()
    {
        return executionInterval;
    }

    public long getLastModified()
    {
        return lastModified;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RollupTask that = (RollupTask) o;
        return lastModified == that.lastModified && Objects.equals(id, that.id) && Objects.equals(rollups, that.rollups) && Objects.equals(name, that.name) && Objects.equals(executionInterval, that.executionInterval);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(id, rollups, name, executionInterval, lastModified);
    }

    @Override
    public String toString()
    {
        return "RollupTask{" +
                "id='" + id + '\'' +
                ", rollups=" + rollups +
                ", name='" + name + '\'' +
                ", executionInterval=" + executionInterval +
                ", lastModified=" + lastModified +
                '}';
    }
}
