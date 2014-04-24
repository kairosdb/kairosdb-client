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

import org.kairosdb.client.builder.aggregator.CustomAggregator;
import org.kairosdb.client.builder.aggregator.RateAggregator;
import org.kairosdb.client.builder.aggregator.SamplingAggregator;

import static com.google.common.base.Preconditions.checkArgument;

public class AggregatorFactory
{

	/**
	 * Creates an aggregator that returns the minimum values for each time period as specified.
	 * For example, "5 minutes" would returns the minimum value for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return min aggregator
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
	 * @return max aggregator
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
	 * @return standard deviation aggregator
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
	 * @return sum aggregator
	 */
	public static SamplingAggregator createSumAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("sum", value, unit);
	}

	/**
	 * Creates an aggregator that returns the count of all values over each time period as specified.
	 * For example, "5 minutes" would returns the count of data points for each 5 minute period.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return count aggregator
	 */
	public static SamplingAggregator createCountAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("count", value, unit);
	}

	/**
	 * Creates an aggregator that divides each value by the divisor.
	 *
	 * @param divisor divisor.
	 * @return div aggregator
	 */
	public static CustomAggregator createDivAggregator(double divisor)
	{
		checkArgument(divisor != 0, "Divisor cannot be zero.");
		return new CustomAggregator("div", "\"divisor\":" + divisor);
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
	public static CustomAggregator createCustomAggregator(String name, String json)
	{
		return new CustomAggregator(name, json);
	}

	/**
	 * Creates an aggregator that returns the rate of change between each pair of data points
	 *
	 * @param unit unit of time
	 * @return rate aggregator
	 */
	public static RateAggregator createRateAggregator(TimeUnit unit)
	{
		return new RateAggregator(unit);
	}
}