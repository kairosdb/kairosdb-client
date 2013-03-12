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
import org.kairosdb.client.serializer.DataPointSerializer;

import java.util.*;

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * A metric contains measurements or data points. Each data point has a time stamp of when the measurement occurred
 * and a value that is either a long or double and optionally contains tags. Tags are labels that can be added to better
 * identify the metric. For example, if the measurement was done on server1 then you might add a tag named "host"
 * with a value of "server1". Note that a metric must have at least one tag.
 */
public class Metric
{
	private String metricName;
	private Map<String, String> tags = new HashMap<String, String>();

	@JsonSerialize(using = DataPointSerializer.class)
	@JsonProperty("datapoints")
	private List<DataPoint> dataPoints = new ArrayList<DataPoint>();

	protected Metric(String metricName)
	{
		this.metricName = checkNotNullOrEmpty(metricName);
	}

	/**
	 * Adds a tag to the data point.
	 * @param name tag identifier
	 * @param value tag value
	 * @return the metric the tag was added to
	 */
	public Metric addTag(String name, String value)
	{
		checkNotNullOrEmpty(name);
		checkNotNullOrEmpty(value);
		tags.put(name, value);

		return this;
	}

	/**
	 * Adds the data point to the metric.
	 *
	 * @param timestamp when the measurement occurred
	 * @param value the measurement value
	 * @return the metric
	 */
	public Metric addDataPoint(long timestamp, long value)
	{
		LongDataPoint dataPoint = new LongDataPoint(timestamp, value);
		dataPoints.add(dataPoint);
		return this;
	}

	/**
	 * Adds the data point to the metric with a timestamp of now.
	 *
	 * @param value the measurement value
	 * @return the metric
	 */
	public Metric addDataPoint(long value)
	{
		return addDataPoint(System.currentTimeMillis(), value);
	}

	/**
	 * Adds the data point to the metric.
	 *
	 * @param timestamp when the measurement occurred
	 * @param value the measurement value
	 * @return the metric
	 */
	public Metric addDataPoint(long timestamp, double value)
	{
		DoubleDataPoint dataPoint = new DoubleDataPoint(timestamp, value);
		dataPoints.add(dataPoint);
		return this;
	}

	/**
	 * Adds the data point to the metric with a timestamp of now.
	 *
	 * @param value the measurement value
	 * @return the metric
	 */
	public Metric addDataPoint(double value)
	{
		return addDataPoint(System.currentTimeMillis(), value);
	}

	public List<DataPoint> getDataPoints()
	{
		return Collections.unmodifiableList(dataPoints);
	}

	/**
	 * Returns the metric name.
	 *
	 * @return metric name
	 */
	public String getName()
	{
		return metricName;
	}

	/**
	 * Returns the tags associated with the data point.
	 * @return tag for the data point
	 */
	public Map<String, String> getTags()
	{
		return Collections.unmodifiableMap(tags);
	}
}
