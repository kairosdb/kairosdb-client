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

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.DataPoint;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Query Results. This is the results of a single query.
 */
public class Result
{
	private String name;
	private Map<String, List<String>> tags;

	@SerializedName("values")
	private List<DataPoint> dataPoints;

	@SerializedName("group_by")
	private List<GroupResult> groupResults;

	public Result(String name,
	               Map<String, List<String>> tags,
	               List<DataPoint> dataPoints,
	               List<GroupResult> groupResults)
	{
		this.name = name;
		this.tags = tags;
		this.groupResults = groupResults;
		this.dataPoints = dataPoints;
	}

	public String getName()
	{
		return name;
	}

	public List<DataPoint> getDataPoints()
	{
		return dataPoints != null ? dataPoints : Collections.EMPTY_LIST;
	}

	public Map<String, List<String>> getTags()
	{
		return tags != null ? tags : Collections.emptyMap();
	}

	@SuppressWarnings("WeakerAccess")
	public List<GroupResult> getGroupResults()
	{
		return groupResults != null ? groupResults : Collections.EMPTY_LIST;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Result result = (Result) o;

		if (!name.equals(result.name)) return false;
		if (!tags.equals(result.tags)) return false;
		if (!dataPoints.equals(result.dataPoints)) return false;
		return groupResults.equals(result.groupResults);

	}

	@Override
	public int hashCode()
	{
		int result = name.hashCode();
		result = 31 * result + tags.hashCode();
		result = 31 * result + dataPoints.hashCode();
		result = 31 * result + groupResults.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("name", name)
				.add("tags", tags)
				.add("dataPoints", dataPoints)
				.add("groupResults", groupResults)
				.toString();
	}
}