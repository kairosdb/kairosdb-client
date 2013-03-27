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
import org.kairosdb.client.response.GroupResults;

/**
 * Results from a ValueGrouper. The group field is group number the results were placed into.
 */
public class ValueGroupResults implements GroupResults
{
	private static final String NAME = "value";
	private int rangeSize;
	private GroupingNumber group;

	@JsonCreator
	public ValueGroupResults(@JsonProperty("range_size") int rangeSize, @JsonProperty("group") GroupingNumber group)
	{
		this.rangeSize = rangeSize;
		this.group = group;
	}

	/**
	 * Grouper name
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
	public int getRangeSize()
	{
		return rangeSize;
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