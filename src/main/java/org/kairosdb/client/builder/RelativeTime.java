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

import java.util.Calendar;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.TimeZone;

/**
 * A time unit relative to now.
 */
public class RelativeTime
{
	private int value;
	private TimeUnit unit;
	private transient Calendar calendar;

	public RelativeTime()
	{  //used by gson
		calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}

	public RelativeTime(int value, TimeUnit unit)
	{
		this.value = value;
		this.unit = unit;
		calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
	}

	/**
	 * The time's value.
	 *
	 * @return time value
	 */
	public int getValue()
	{
		return value;
	}

	/**
	 * The unit of time.
	 *
	 * @return time unit
	 */
	public String getUnit()
	{
		return unit.toString();
	}

	/**
	 * Returns the time in milliseconds relative to the specified time. For example, if this object is set to 3 days,
	 * this method returns 3 days from <code>time</code>.
	 *
	 * @param time time to calculate the relative time.
	 * @return time in milliseconds relative to the specified time
	 */
	@SuppressWarnings("MagicConstant")
	public long getTimeRelativeTo(long time)
	{
		int field = 0;
		if (unit == TimeUnit.SECONDS)
		{
			field = Calendar.SECOND;
		}
		else if (unit == TimeUnit.MINUTES)
		{
			field = Calendar.MINUTE;
		}
		else if (unit == TimeUnit.HOURS)
		{
			field = Calendar.HOUR;
		}
		else if (unit == TimeUnit.DAYS)
		{
			field = Calendar.DATE;
		}
		else if (unit == TimeUnit.WEEKS)
		{
			field = Calendar.WEEK_OF_MONTH;
		}
		else if (unit == TimeUnit.MONTHS)
		{
			field = Calendar.MONTH;
		}
		else if (unit == TimeUnit.YEARS)
		{
			field = Calendar.YEAR;
		}

		calendar.setTimeInMillis(time);
		calendar.add(field, -value);

		return calendar.getTime().getTime();
	}

	//Intentionally leaving out the calendar, it messes up unit tests and is not needed for equality
	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		RelativeTime that = (RelativeTime) o;
		return value == that.value && unit == that.unit;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(value, unit);
	}

	@Override
	public String toString()
	{
		return new StringJoiner(", ", RelativeTime.class.getSimpleName() + "[", "]")
				.add("value=" + value)
				.add("unit=" + unit)
				.add("calendar=" + calendar)
				.toString();
	}
}