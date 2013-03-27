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
package org.kairosdb.client.response.grouping;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.response.GroupResults;

public class TimeGroupResults implements GroupResults
{
	private static final String NAME = "time";
	private RelativeTime rangeSize;
	private int groupCount;
	private GroupingNumber group;

	@JsonCreator
	public TimeGroupResults(@JsonProperty("range_size") RelativeTime rangeSize,
	                       @JsonProperty("group_count") int groupCount,
	                       @JsonProperty("group") GroupingNumber group)
	{
		this.rangeSize = rangeSize;
		this.groupCount = groupCount;
		this.group = group;
	}

	/**
	 * Name of the grouper.
	 *
	 * @return grouper name
	 */
	public String getName()
	{
		return NAME;
	}

	/**
	 * The size of each range for the group.
	 *
	 * @return range size of the group
	 */
	public RelativeTime getRangeSize()
	{
		return rangeSize;
	}

	/**
	 * Number of groups.
	 *
	 * @return number of groups
	 */
	public int getGroupCount()
	{
		return groupCount;
	}

	/**
	 * How the results were group. This indicates the group number.
	 * @return group number
	 */
	public GroupingNumber getGroup()
	{
		return group;
	}
}