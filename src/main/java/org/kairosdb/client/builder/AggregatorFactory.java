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

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

public class AggregatorFactory
{

	/**
	 * Creates an aggregator that returns the minimum values for each time period as specified.
	 * For example, "5 minutes" would returns the minimum value for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return average aggregator
	 */
	public static SamplingAggregator minAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("min", value, unit);
	}

	/**
	 * Creates an aggregator that returns the maximum values for each time period as specified.
	 * For example, "5 minutes" would returns the maximum value for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return average aggregator
	 */
	public static SamplingAggregator maxAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("max", value, unit);
	}

	/**
	 * Creates an aggregator that returns the average values for each time period as specified.
	 * For example, "5 minutes" would returns the average value for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return average aggregator
	 */
	public static SamplingAggregator averageAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("sum", value, unit);
	}

	/**
	 * Creates an aggregator that returns the standard deviation values for each time period as specified.
	 * For example, "5 minutes" would returns the standard deviation for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return average aggregator
	 */
	public static SamplingAggregator standardDeviationAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("sum", value, unit);
	}

	/**
	 * Creates an aggregator that returns the data points sorted by timestamp.
	 * @return sort aggregator
	 */
	public static Aggregator createSortAggregator()
	{
		return new AggregatorImpl("sort");
	}

	/**
	 * Creates an aggregator that returns the rate of change between each pair of data points
	 * @return rate aggregator
	 */
	public static Aggregator createRateAggregator()
	{
		return new AggregatorImpl("rate");
	}

	private static class AggregatorImpl implements Aggregator
	{
		private String name;

		private AggregatorImpl(String name)
		{
			this.name = checkNotNullOrEmpty(name);
		}

		@Override
		public String getName()
		{
			return name;
		}

		@Override
		public String toJson()
		{
			return "\"name\": \"" + name + "\"";
		}
	}
}