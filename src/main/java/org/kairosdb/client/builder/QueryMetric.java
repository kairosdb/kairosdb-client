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

import java.util.LinkedHashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Query request for a metric. If a metric is queried by name only then all data points for all tags are returned.
 * You can narrow down the query by adding tags so only data points associated with those tags are returned.
 *
 * The aggregator defines the operation that is performed on the data points on the server before they are returned.
 * For example, if the "sum" aggregator is specified, multiple data points at the same timestamp are added together and
 * returned as a single data point.
 */
public class QueryMetric
{
	private Map<String, String> tags = new LinkedHashMap<String, String>();
	private String name;
	private String aggregate;

	QueryMetric(String name, String aggregator)
	{
		this.name = checkNotNullOrEmpty(name);
		this.aggregate = checkNotNullOrEmpty(aggregator);
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
	 * @param name tag name
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
	 * Returns the aggregate.
	 *
	 * @return aggregate
	 */
	public String getAggregate()
	{
		return aggregate;
	}
}