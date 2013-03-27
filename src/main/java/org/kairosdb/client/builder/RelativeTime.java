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

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * A time unit relative to now.
 */
public class RelativeTime
{
	private int value;
	private TimeUnit unit;
	private Calendar calendar;

	@JsonCreator
	public RelativeTime(@JsonProperty("value") int value, @JsonProperty("unit") TimeUnit unit)
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
	 *
	 * @return  time in milliseconds relative to the specified time
	 */
	public long getTimeRelativeTo(long time)
	{
		int field = 0;
		if (unit == TimeUnit.SECONDS )
			field = Calendar.SECOND;
		else if (unit == TimeUnit.MINUTES)
			field = Calendar.MINUTE;
		else if (unit == TimeUnit.HOURS)
			field = Calendar.HOUR;
		else if (unit == TimeUnit.DAYS)
			field = Calendar.DATE;
		else if (unit == TimeUnit.WEEKS)
			field = Calendar.WEEK_OF_MONTH;
		else if (unit == TimeUnit.MONTHS)
			field = Calendar.MONTH;
		else if (unit == TimeUnit.YEARS)
			field = Calendar.YEAR;

		calendar.setTimeInMillis(time);
		calendar.add(field, -value);

		return calendar.getTime().getTime();
	}
}