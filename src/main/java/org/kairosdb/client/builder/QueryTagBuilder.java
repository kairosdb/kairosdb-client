package org.kairosdb.client.builder;

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

import java.util.ArrayList;
import java.util.List;

import org.kairosdb.client.serializer.ListMultiMapSerializer;

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
