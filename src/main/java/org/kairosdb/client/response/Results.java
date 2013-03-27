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
package org.kairosdb.client.response;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.DoubleDataPoint;
import org.kairosdb.client.builder.LongDataPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@JsonIgnoreProperties({"tags"})
public class Results
{
	private String name;
	private Map<String, List<String>> tags;
	private List<DataPoint> dataPoints;
	private List<GroupResults> groupResults;

	@JsonCreator
	public Results(@JsonProperty("name")String name,
	               @JsonProperty("tags") Map<String, List<String>> tags,
	               @JsonProperty("values") List<String[]> values,
	               @JsonProperty("group_by") List<GroupResults> groupResults)
	{
		this.name = name;
		this.tags = tags;
		this.groupResults = groupResults;

		// todo how to not hold both objects in memory. Really want to only parse the data points until the caller asks for them
		dataPoints = new ArrayList<DataPoint>();
		for (String[] value : values)
		{
			long timestamp = Long.parseLong(value[0]);
			if (value[1].contains("."))
				dataPoints.add(new DoubleDataPoint(timestamp, Double.parseDouble(value[1])));
			else
				dataPoints.add(new LongDataPoint(timestamp, Long.parseLong(value[1])));
		}
	}

	public String getName()
	{
		return name;
	}

	public List<DataPoint> getDataPoints()
	{
		return dataPoints;
	}

	public Map<String, List<String>> getTags()
	{
		return tags;
	}

	public List<GroupResults> getGroupResults()
	{
		return groupResults;
	}
}