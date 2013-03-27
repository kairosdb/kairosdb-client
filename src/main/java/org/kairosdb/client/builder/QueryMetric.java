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

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.kairosdb.client.serializer.AggregatorSerializer;
import org.kairosdb.client.serializer.GrouperSerializer;

import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Query request for a metric. If a metric is queried by name only then all data points for all tags are returned.
 * You can narrow down the query by adding tags so only data points associated with those tags are returned.
 * <p/>
 * Aggregators may be added to the metric. An aggregator performs an operation on the data such as summing or averaging.
 * If multiple aggregators are added, the output of the first is sent to the input of the next, and so forth until all
 * aggregators have been processed, These are processed in the order they were added.
 * <p/>
 *
 * The results of the query can be grouped in various ways using a grouper. For example, if you had a metric with a
 * customer tag, the resulting data points could be grouped by the different customers. Multiple groupers can be used
 * so you could, for example, group by tag and value.
 * <p/>
 * Note that aggregation is very fast but grouping can slow down the query.
 */
public class QueryMetric
{
	private Map<String, String> tags = new LinkedHashMap<String, String>();

	@JsonSerialize(using = AggregatorSerializer.class, include=JsonSerialize.Inclusion.NON_EMPTY)
	private List<String> aggregators = new ArrayList<String>();

	@JsonProperty("group_by")
	@JsonSerialize(using = GrouperSerializer.class, include=JsonSerialize.Inclusion.NON_EMPTY)
	private List<String> groupers = new ArrayList<String>();

	private String name;

	QueryMetric(String name)
	{
		this.name = checkNotNullOrEmpty(name);
	}

	/**
	 * Sets tags.
	 *
	 * @param tags tags to add
	 * @return the metric
	 */
	public QueryMetric setTags(Map<String, String> tags)
	{
		checkNotNull(tags);
		this.tags = new LinkedHashMap<String, String>(tags);

		return this;
	}

	/**
	 * Adds a tag. This narrows the query to only show data points associated with the tag.
	 *
	 * @param name  tag name
	 * @param value tag value
	 * @return the metric
	 */
	public QueryMetric addTag(String name, String value)
	{
		checkNotNullOrEmpty(name);
		checkNotNullOrEmpty(value);
		tags.put(name, value);

		return (this);
	}

	/**
	 * Returns tags associated with the metric.
	 *
	 * @return tags
	 */
	public Map<String, String> getTags()
	{
		return tags;
	}

	/**
	 * Returns the name of the metric.
	 *
	 * @return metric name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Adds an aggregator to the metric.
	 *
	 * @param json JSON representation of the aggregator.
	 * @return the metric
	 */
	public QueryMetric addAggregator(String json)
	{
		checkNotNullOrEmpty(json);

		aggregators.add(json);

		return this;
	}

	/**
	 * Adds an aggregator to the metric.
	 *
	 * @param aggregator aggregator to add
	 * @return the metric
	 */
	public QueryMetric addAggregator(Aggregator aggregator)
	{
		return addAggregator(aggregator.toJson());
	}

	/**
	 * Returns the list of aggregators in their JSON serialized form.
	 * @return list of serialized aggregators
	 */
	public List<String> getAggregators()
	{
		return Collections.unmodifiableList(aggregators);
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

		return addGrouper(grouper.toJson());
	}

	/**
	 * Adds a grouper to the metric.
	 *
	 * @param json JSON representation of the grouper.
	 * @return the metric
	 */
	public QueryMetric addGrouper(String json)
	{
		checkNotNullOrEmpty(json);

		groupers.add(json);

		return this;
	}

	/**
	 * Returns the list of groupers in their JSON serialized form.
	 * @return list of serialized groupers
	 */
	public List<String> getGroupers()
	{
		return Collections.unmodifiableList(groupers);
	}
}