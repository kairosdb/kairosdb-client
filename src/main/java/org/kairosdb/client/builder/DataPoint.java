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
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A measurement. Contains the time when the measurement occurred and its value.
 */
public class DataPoint
{
	private long timestamp;
	private Object value;

	public DataPoint(long timestamp, Object value)
	{
		this.timestamp = timestamp;
		this.value = checkNotNull(value);
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

	public Object getValue()
	{
		return value;
	}

	public String stringValue() throws DataFormatException
	{
		return value.toString();
	}

	public long longValue() throws DataFormatException
	{
		try
		{
			return ((Number)value).longValue();
		}
		catch (Exception e)
		{
			throw new DataFormatException("Value is not a long");
		}
	}

	public double doubleValue() throws DataFormatException
	{
		try
		{
			return ((Number)value).doubleValue();
		}
		catch (Exception e)
		{
			throw new DataFormatException("Value is not a double");
		}
	}

	public boolean isDoubleValue()
	{
		return value instanceof Double;
	}

	public boolean isIntegerValue()
	{
		return value instanceof Long || value instanceof Integer;
	}

	@Override
	public String toString()
	{
		return "DataPoint{" +
				"timestamp=" + timestamp +
				", value=" + value +
				'}';
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		DataPoint dataPoint = (DataPoint) o;

		return timestamp == dataPoint.timestamp && value.equals(dataPoint.value);

	}

	@Override
	public int hashCode()
	{
		int result = (int) (timestamp ^ (timestamp >>> 32));
		result = 31 * result + value.hashCode();
		return result;
	}
}