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

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.response.GroupResult;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Results from a ValueGrouper. The group field is group number the results were placed into.
 */
public class ValueGroupResult extends GroupResult
{
	@SerializedName("range_size")
	private int rangeSize;

	private GroupingNumber group;

	public ValueGroupResult(int rangeSize, GroupingNumber group)
	{
		super("value");

		checkArgument(rangeSize > 0);

		this.rangeSize = rangeSize;
		this.group = checkNotNull(group);
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
	 *
	 * @return group number
	 */
	public GroupingNumber getGroup()
	{
		return group;
	}
}