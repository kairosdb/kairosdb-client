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
	public static SamplingAggregator createMinAggregator(int value, TimeUnit unit)
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
	public static SamplingAggregator createMaxAggregator(int value, TimeUnit unit)
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
	public static SamplingAggregator createAverageAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("avg", value, unit);
	}

	/**
	 * Creates an aggregator that returns the standard deviation values for each time period as specified.
	 * For example, "5 minutes" would returns the standard deviation for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return average aggregator
	 */
	public static SamplingAggregator createStandardDeviationAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("dev", value, unit);
	}

	/**
	 * Creates an aggregator that returns the sum of all values over each time period as specified.
	 * For example, "5 minutes" would returns the sum of data points for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return average aggregator
	 */
	public static SamplingAggregator createSumAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("sum", value, unit);
	}

	/**
	 * Creates an aggregator with a custom json fragment. This method is used for custom aggregators that have been added to
	 * KairosDB. This does not create an aggregator on the server. The name must match the custom aggregator on the
	 * server.
	 *
	 * <p>
	 * Example:
	 * </p>
	 *
	 * <pre>
	 *      Aggregator aggregator = AggregatorFactory.createCustomAggregator("histogram", "\"percentile\": 0.75");
	 * </pre>
	 *
	 * <p>
	 *      This produces aggregator JSON that looks like this:
	 * </p>
	 *
	 * <pre>
	 *      "name":"histogram",
	 *      "percentile": 0.75
	 * </pre>
	 *
	 * @param name name of the aggregator.
	 * @param json aggregator JSON fragment
	 * @return customer aggregator
	 */
	public static Aggregator createCustomAggregator(String name, String json)
	{
		return new CustomAggregator(name, json);
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