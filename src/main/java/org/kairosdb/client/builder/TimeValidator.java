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

import java.util.Date;

import static com.google.common.base.Preconditions.checkState;

public class TimeValidator
{
	private TimeValidator()
	{
	}

	public static void validateEndTimeLaterThanStartTime(Date startTime, Date endTime)
	{
		checkState(endTime.after(startTime), "Start time cannot be later than the ending time");
	}
	
	public static void validateEndTimeLaterThanStartTime(RelativeTime startTime, RelativeTime endTime)
	{
		long now = System.currentTimeMillis();
		checkState(startTime.getTimeRelativeTo(now) < endTime.getTimeRelativeTo(now), "Start time cannot be later than the ending time");
	}

	public static void validateEndTimeLaterThanStartTime(Date startTime, RelativeTime endTime)
	{
		long now = System.currentTimeMillis();
		checkState(startTime.getTime() < endTime.getTimeRelativeTo(now), "Start time cannot be later than the ending time");
	}

	public static void validateEndTimeLaterThanStartTime(RelativeTime startTime, Date endTime)
	{
		long now = System.currentTimeMillis();
		checkState(startTime.getTimeRelativeTo(now) < endTime.getTime(),"Start time cannot be later than the ending time");
	}
}