package org.kairosdb.client.builder;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.kairosdb.client.serializer.ListMultiMapSerializer;

import java.util.ArrayList;
import java.util.List;

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Builder used to create the JSON to query tags from KairosDB.
 * <br>
 * <br>
 * This is similar to a regular query but just returns the tags (no data points)
 * for the range specified. The time range can be specified as absolute or relative.
 * Absolute times are a given point in time. Relative times are relative to now.
 * The end time is not required and defaults to now.
 * <br>
 * <br>
 * For example, if you specify a relative start time of 30 minutes, all matching data points for the last 30 minutes
 * will be returned. If you specify a relative start time of 30 minutes and a relative end time of 10 minutes, then
 * all matching data points that occurred between the last 30 minutes up to and including the last 10 minutes are returned.
 */
public class QueryTagBuilder extends AbstractQueryBuilder<QueryTagBuilder>
{
	private List<QueryTagMetric> metrics = new ArrayList<QueryTagMetric>();

	private QueryTagBuilder()
	{
		super();
	}

	@Override
	protected Gson buildGson()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(ListMultimap.class, new ListMultiMapSerializer());
		return builder.create();
	}

	/**
	 * Returns a new query tag builder.
	 *
	 * @return new query tag builder
	 */
	public static QueryTagBuilder getInstance()
	{
		return new QueryTagBuilder();
	}

	/**
	 * The metric to query tag for.
	 *
	 * @param name metric name
	 * @return the builder
	 */
	public QueryTagMetric addMetric(String name)
	{
		checkNotNullOrEmpty(name, "Name cannot be null or empty.");

		QueryTagMetric metric = new QueryTagMetric(name);
		metrics.add(metric);
		return metric;
	}

	/**
	 * Returns the list metrics to query for tags.
	 *
	 * @return metrics
	 */
	public List<QueryTagMetric> getMetrics()
	{
		return metrics;
	}
}