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
package org.kairosdb.client.builder.grouper;

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.Grouper;
import org.kairosdb.client.builder.RelativeTime;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Grouper used to group by time range. The combination of rangeSize and count determine the grouping range. The
 * rangeSize is the time unit and the count is the number of time units in the range. For example, a rangeSize of
 * 1 days with a count of 7, creates 1 day groups for a week. A rangeSize of 1 hours with a count of 168 (7 * 24),
 * creates groups for each hour of the week.
 */
public class TimeGrouper extends Grouper
{
	@SerializedName("range_size")
	private RelativeTime rangeSize;

	@SerializedName("group_count")
	private int count;

	public TimeGrouper(RelativeTime rangeSize, int count)
	{
		super("time");
		checkArgument(count > 0);
		this.rangeSize = checkNotNull(rangeSize);
		this.count = count;
	}

	public RelativeTime getRangeSize()
	{
		return rangeSize;
	}

	public int getCount()
	{
		return count;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		TimeGrouper that = (TimeGrouper) o;
		return count == that.count && !(rangeSize != null ? !rangeSize.equals(that.rangeSize) : that.rangeSize != null);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + (rangeSize != null ? rangeSize.hashCode() : 0);
		result = 31 * result + count;
		return result;
	}
}