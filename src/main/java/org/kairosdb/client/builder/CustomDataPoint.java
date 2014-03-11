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

public class CustomDataPoint<T> extends DataPoint
{
	private final T value;
	private final String type;

	/**
	 * Creates a data point whose value is a custom type.
	 *
	 * @param timestamp when the data point was measured
	 * @param value value of the data point
	 * @param type the API data type
	 */
	public CustomDataPoint(long timestamp, T value, String type)
	{
		super(timestamp);
		this.value = value;
		this.type = type;
	}

	/**
	 * Returns the value.
	 *
	 * @return the value
	 */
	public T getValue()
	{
		return value;
	}

	public String getType() {
	  return type;
	}

	@Override
	public boolean isInteger()
	{
		return false;
	}
}