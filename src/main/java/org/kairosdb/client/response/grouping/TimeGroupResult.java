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

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.response.GroupResult;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class TimeGroupResult extends GroupResult
{
	@SerializedName("range_size")
	private RelativeTime rangeSize;

	@SerializedName("group_count")
	private int groupCount;
	private GroupingNumber group;

	@SuppressWarnings("WeakerAccess")
	public TimeGroupResult(RelativeTime rangeSize,
	                       int groupCount,
	                       GroupingNumber group)
	{
		super("time");

		checkArgument(groupCount > 0);

		this.rangeSize = checkNotNull(rangeSize);
		this.groupCount = groupCount;
		this.group = checkNotNull(group);
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
	 *
	 * @return group number
	 */
	public GroupingNumber getGroup()
	{
		return group;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof TimeGroupResult)) return false;
		if (!super.equals(o)) return false;

		TimeGroupResult that = (TimeGroupResult) o;

		if (groupCount != that.groupCount) return false;
		if (!group.equals(that.group)) return false;
		return rangeSize.equals(that.rangeSize);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + rangeSize.hashCode();
		result = 31 * result + groupCount;
		result = 31 * result + group.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("rangeSize", rangeSize)
				.add("groupCount", groupCount)
				.add("group", group)
				.toString();
	}
}