package org.kairosdb.client.builder;

import com.google.gson.annotations.SerializedName;
import com.sun.istack.internal.NotNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

public class RollupTask
{
    private final String id;
    private final List<Rollup> rollups = new ArrayList<>();

    @NotNull
    private final String name;

    @NotNull
    @SerializedName("execution_interval")
    private final Duration executionInterval;

    private final long lastModified;

    public RollupTask(String id, String name, Duration executionInterval, long lastModified)
    {
        this.id = checkNotNullOrEmpty(id, "id cannot be null or empty");
        this.name = checkNotNullOrEmpty(name, "name cannot be null or empty");
        this.executionInterval = checkNotNull(executionInterval, "executionInterval cannot be null");
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

    public Duration getExecutionInterval()
    {
        return executionInterval;
    }

    public long getLastModified()
    {
        return lastModified;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o)
    {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RollupTask that = (RollupTask) o;

        if (lastModified != that.lastModified) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (!rollups.equals(that.rollups)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }
        return executionInterval.equals(that.executionInterval);
    }

    @Override
    public int hashCode()
    {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + rollups.hashCode();
        result = 31 * result + name.hashCode();
        result = 31 * result + executionInterval.hashCode();
        result = 31 * result + (int) (lastModified ^ (lastModified >>> 32));
        return result;
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
