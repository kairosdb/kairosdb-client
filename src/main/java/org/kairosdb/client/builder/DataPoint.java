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

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A measurement. Contains the time when the measurement occurred and its value.
 *
 * @see LongDataPoint
 * @see DoubleDataPoint
 */
public abstract class DataPoint
{
	private long timestamp;

	protected DataPoint(long timestamp)
	{
		checkArgument(timestamp > 0);
		this.timestamp = timestamp;
	}
	/**
	 * Time when the data point was measured.
	 *
	 * @return time when the data point was measured
	 */
	public long getTimestamp()
	{
		return timestamp;
	}

	public abstract boolean isInteger();
}