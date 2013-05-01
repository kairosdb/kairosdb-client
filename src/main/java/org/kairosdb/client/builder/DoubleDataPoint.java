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

public class DoubleDataPoint extends DataPoint
{
	private double value;

	/**
	 * Creates a data point whose value is a double.
	 *
	 * @param timestamp when the data point was measured
	 * @param value value of the data point
	 */
	public DoubleDataPoint(long timestamp, double value)
	{
		super(timestamp);
		this.value = value;
	}

	/**
	 * Returns the value.
	 *
	 * @return the value
	 */
	public double getValue()
	{
		return value;
	}

	@Override
	public boolean isInteger()
	{
		return false;
	}
}