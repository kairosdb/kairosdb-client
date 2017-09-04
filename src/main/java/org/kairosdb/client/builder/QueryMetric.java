/*
 * Copyright 2013 Proofpoint Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.client.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Query request for a metric. If a metric is queried by name only then all data points for all tags are returned.
 * You can narrow down the query by adding tags so only data points associated with those tags are returned.
 * <br>
 * <br>
 * Aggregators may be added to the metric. An aggregator performs an operation on the data such as summing or averaging.
 * If multiple aggregators are added, the output of the first is sent to the input of the next, and so forth until all
 * aggregators have been processed, These are processed in the order they were added.
 * <br>
 * <br>
 * <br>
 * <br>
 * The results of the query can be grouped in various ways using a grouper. For example, if you had a metric with a
 * customer tag, the resulting data points could be grouped by the different customers. Multiple groupers can be used
 * so you could, for example, group by tag and value.
 * <br><br>
 * Note that aggregation is very fast but grouping can slow down the query.
 */
@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class QueryMetric
{
	public enum Order
	{
		ASCENDING("asc"),
		DESCENDING("desc");

		private String text;

		Order(String text)
		{
			this.text = text;
		}

		@Override
		public String toString()
		{
			return this.text;
		}
	}

	@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
	private final String name;

	private final ListMultimap<String, String> tags = ArrayListMultimap.create();

	@SerializedName("group_by")
	private final List<Grouper> groupers = new ArrayList<Grouper>();

	private final List<Aggregator> aggregators = new ArrayList<Aggregator>();

	private Integer limit;

	private Order order;

	public QueryMetric(String name)
	{
		this.name = checkNotNullOrEmpty(name);
	}

	/**
	 * Add a map of tags.
	 *
	 * @param tags tags to add
	 * @return the metric
	 */
	public QueryMetric addMultiValuedTags(Map<String, List<String>> tags)
	{
		checkNotNull(tags);

		for (String key : tags.keySet())
		{
			this.tags.putAll(key, tags.get(key));
		}

		return this;
	}

	/**
	 * Add a map of tags. This narrows the query to only show data points associated with the tags' values.
	 *
	 * @param tags tags to add
	 * @return the metric
	 */
	public QueryMetric addTags(Map<String, String> tags)
	{
		checkNotNull(tags);

		for (String key : tags.keySet())
		{
			this.tags.put(key, tags.get(key));
		}

		return this;
	}

	/**
	 * Adds a tag with multiple values. This narrows the query to only show data points associated with the tag's values.
	 *
	 * @param name   tag name
	 * @param values tag values
	 * @return the metric
	 */
	public QueryMetric addTag(String name, String... values)
	{
		checkNotNullOrEmpty(name);
		checkArgument(values.length > 0);

		for (String value : values)
		{
			checkNotNullOrEmpty(value);
		}

		tags.putAll(name, Arrays.asList(values));

		return (this);
	}

	/**
	 * Adds an aggregator to the metric.
	 *
	 * @param aggregator aggregator to add
	 * @return the metric
	 */
	public QueryMetric addAggregator(Aggregator aggregator)
	{
		checkNotNull(aggregator);
		aggregators.add(aggregator);
		return this;
	}

	/**
	 * Add a grouper to the metric.
	 *
	 * @param grouper grouper to add
	 * @return the metric
	 */
	public QueryMetric addGrouper(Grouper grouper)
	{
		checkNotNull(grouper);

		groupers.add(grouper);
		return this;
	}

	/**
	 * Limits the number of data point returned from the query. The limit is done before aggregators are executed.
	 * @param limit maximum number of data points to return
	 */
	public void setLimit(int limit)
	{
		checkArgument(limit > 0, "limit must be greater than 0");
		this.limit = limit;
	}

	/**
	 * Orders the data points. The server default is ascending.
	 * @param order how data points are sorted
	 */
	public void setOrder(Order order)
	{
		checkNotNull(order);
		this.order = order;
	}

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		QueryMetric that = (QueryMetric) o;

		if (!name.equals(that.name))
			return false;
		if (tags != null ? !tags.equals(that.tags) : that.tags != null)
			return false;
		if (groupers != null ? !groupers.equals(that.groupers) : that.groupers != null)
			return false;
		if (aggregators != null ? !aggregators.equals(that.aggregators) : that.aggregators != null)
			return false;
		if (limit != null ? !limit.equals(that.limit) : that.limit != null)
			return false;
		return order == that.order;
	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + (tags != null ? tags.hashCode() : 0);
		result = 31 * result + (groupers != null ? groupers.hashCode() : 0);
		result = 31 * result + (aggregators != null ? aggregators.hashCode() : 0);
		result = 31 * result + (limit != null ? limit.hashCode() : 0);
		result = 31 * result + (order != null ? order.hashCode() : 0);
		return result;
	}
}
