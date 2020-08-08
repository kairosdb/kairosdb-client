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
import org.kairosdb.client.response.GroupResult;

import static java.util.Objects.requireNonNull;
import static org.kairosdb.client.util.Preconditions.checkArgument;


/**
 * Results from a ValueGrouper. The group field is group number the results were placed into.
 */
public class ValueGroupResult extends GroupResult
{
	@SerializedName("range_size")
	private int rangeSize;

	private GroupingNumber group;

	@SuppressWarnings("WeakerAccess")
	public ValueGroupResult(int rangeSize, GroupingNumber group)
	{
		super("value");

		checkArgument(rangeSize > 0);

		this.rangeSize = rangeSize;
		this.group = requireNonNull(group);
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

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (!(o instanceof ValueGroupResult)) return false;
		if (!super.equals(o)) return false;

		ValueGroupResult that = (ValueGroupResult) o;

		if (rangeSize != that.rangeSize) return false;
		return group.equals(that.group);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + rangeSize;
		result = 31 * result + group.hashCode();
		return result;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("rangeSize", rangeSize)
				.add("group", group)
				.toString();
	}
}