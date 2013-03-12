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

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.kairosdb.client.serializer.AggregatorSerializer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Query request for a metric. If a metric is queried by name only then all data points for all tags are returned.
 * You can narrow down the query by adding tags so only data points associated with those tags are returned.
 * <p/>
 * Aggregators may be added to the metric. An aggregator performs an operation on the data such as summing or averaging.
 * If multiple aggregators are added, the output of the first is sent to the input of the next, and so forth until all
 * aggregators have been processed, These are processed in the order they were added.
 */
public class QueryMetric
{
	private Map<String, String> tags = new LinkedHashMap<String, String>();

	@JsonSerialize(using = AggregatorSerializer.class, include=JsonSerialize.Inclusion.NON_EMPTY)
	private List<String> aggregators = new ArrayList<String>();

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

		if (aggregators == null)
			aggregators = new ArrayList<String>();
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

	public List<String> getAggregators()
	{
		return aggregators;
	}
}