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
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

public class SamplingAggregator implements Aggregator
{
	private String name;
	private int value;
	private TimeUnit unit;

	public SamplingAggregator(String name, int value, TimeUnit unit)
	{
		checkArgument(value > 0, "value must be greater than 0.");

		this.name = checkNotNullOrEmpty(name);
		this.value = value;
		this.unit = checkNotNull(unit);
	}

	public String getName()
	{
		return name;
	}

	@Override
	public String toJson()
	{
		return "\"name\": \"" + name + "\", \"sampling\":{\"value\": " + value + ", \"unit\": \"" + unit.toString() + "\"}";
	}

	public int getValue()
	{
		return value;
	}

	public TimeUnit getUnit()
	{
		return unit;
	}
}