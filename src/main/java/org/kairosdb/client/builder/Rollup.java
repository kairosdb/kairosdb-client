package org.kairosdb.client.builder;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

import static java.util.Objects.requireNonNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

public class Rollup
{
    @SerializedName("save_as")
    private final String saveAs;

    @SerializedName("query")
    private QueryBuilder builder;

    Rollup(String saveAs)
    {
        this.saveAs = checkNotNullOrEmpty(saveAs, "saveAs cannot be null or empty");
    }

    public QueryBuilder addQuery()
    {
        builder = QueryBuilder.getInstance();
        return builder;
    }

    public Date getStartAbsolute()
    {
        return builder.getStartAbsolute();
    }

    public Date getEndAbsolute()
    {
        return builder.getEndAbsolute();
    }

    public RelativeTime getStartRelative()
    {
        return builder.getStartRelative();
    }

    public int getCacheTime()
    {
        return builder.getCacheTime();
    }

    public TimeZone getTimeZone()
    {
        return builder.getTimeZone();
    }

    public List<QueryMetric> getMetrics()
    {
        return builder.getMetrics();
    }

    public String getSaveAs()
    {
        return saveAs;
    }

    void validate()
    {
        requireNonNull(builder, "No queries added to rollup " + saveAs);
        builder.validate();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rollup rollup = (Rollup) o;
        return Objects.equals(saveAs, rollup.saveAs) && Objects.equals(builder, rollup.builder);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(saveAs, builder);
    }

    @Override
    public String toString()
    {
        return "Rollup{" +
                "saveAs='" + saveAs + '\'' +
                ", builder=" + builder +
                '}';
    }
}
