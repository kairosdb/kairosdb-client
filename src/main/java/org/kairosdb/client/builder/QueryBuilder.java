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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Builder used to create the JSON to query KairosDB.
 * <br>
 * <br>
 * The query returns the data points for the given metrics for the specified time range. The time range can
 * be specified as absolute or relative. Absolute times are a given point in time. Relative times are relative to now.
 * The end time is not required and defaults to now.
 * <br>
 * <br>
 * For example, if you specify a relative start time of 30 minutes, all matching data points for the last 30 minutes
 * will be returned. If you specify a relative start time of 30 minutes and a relative end time of 10 minutes, then
 * all matching data points that occurred between the last 30 minutes up to and including the last 10 minutes are returned.
 */
@SuppressWarnings("UnusedDeclaration")
public class QueryBuilder extends AbstractQueryBuilder<QueryBuilder>
{
	@SerializedName("cache_time")
	private int cacheTime;

	@SerializedName("time_zone")
	private TimeZone timeZone;

	private List<QueryMetric> metrics = new ArrayList<>();

	private QueryBuilder()
	{
		super();
	}

	@Override
	protected Gson buildGson()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(CustomAggregator.class, new CustomAggregatorSerializer());
		builder.registerTypeAdapter(CustomGrouper.class, new CustomGrouperSerializer());
		builder.registerTypeAdapter(ListMultimap.class, new ListMultiMapSerializer());
		builder.registerTypeAdapter(QueryMetric.Order.class, new OrderSerializer());
		builder.registerTypeAdapter(TimeZone.class, new TimeZoneSerializer());

		return builder.create();
	}

	/**
	 * How long to cache this exact query. The default is to never cache.
	 *
	 * @param cacheTime cache time in milliseconds
	 * @return the builder
	 */
	public QueryBuilder setCacheTime(int cacheTime)
	{
		checkArgument(cacheTime > 0, "Cache time must be greater than 0.");
		this.cacheTime = cacheTime;
		return this;
	}

	/**
	 * Adds a QueryMetric object to the QueryBuilder
	 *
	 * @param metric a QueryMetric object
	 * @return the builder
	 */
	public QueryMetric addMetric(QueryMetric metric)
	{
		checkNotNull(metric,"metric cannot be null");
		metrics.add(metric);
		return metric;
	}


	/**
	 * Returns a new query builder.
	 *
	 * @return new query builder
	 */
	public static QueryBuilder getInstance()
	{
		return new QueryBuilder();
	}

	/**
	 * The metric to query for.
	 *
	 * @param name metric name
	 * @return the builder
	 */
	public QueryMetric addMetric(String name)
	{
		checkNotNullOrEmpty(name, "Name cannot be null or empty.");

		QueryMetric metric = new QueryMetric(name);
		metrics.add(metric);
		return metric;
	}

	/**
	 * Returns the cache time.
	 *
	 * @return cache time
	 */
	public int getCacheTime()
	{
		return cacheTime;
	}

	/**
	 * Returns the list metrics to query for.
	 *
	 * @return metrics
	 */
	public List<QueryMetric> getMetrics()
	{
		return metrics;
	}

	/**
	 * Returns the time zone. The default time zone is UTC.
	 *
	 * @return time zone
	 */
	public TimeZone getTimeZone()
	{
		if (timeZone == null)
			return TimeZone.getTimeZone("UTC");
		return timeZone;
	}

	@SuppressWarnings("ConstantConditions")
	public QueryBuilder setTimeZone(TimeZone timeZone)
	{
		checkNotNull(timeZone, "timezone cannot be null");

		this.timeZone = timeZone;
		return this;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		if (!super.equals(o)) {
			return false;
		}

		QueryBuilder that = (QueryBuilder) o;

		if (cacheTime != that.cacheTime) {
			return false;
		}
		if (timeZone == null)
		{
			if (that.timeZone != null)
			{
				return false;
			}
		} else if (!timeZone.equals(that.timeZone))
		{
			return false;
		}
		return metrics.equals(that.metrics);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + cacheTime;
		result = 31 * result + timeZone.hashCode();
		result = 31 * result + metrics.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return "QueryBuilder{" +
				"cacheTime=" + cacheTime +
				", timeZone=" + timeZone +
				", metrics=" + metrics +
				", startAbsolute=" + startAbsolute +
				", endAbsolute=" + endAbsolute +
				", startRelative=" + startRelative +
				", endRelative=" + endRelative +
				'}';
	}
}