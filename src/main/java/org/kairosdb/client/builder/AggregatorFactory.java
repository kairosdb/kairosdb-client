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
import org.kairosdb.client.builder.aggregator.PercentileAggregator;
import org.kairosdb.client.builder.aggregator.RateAggregator;
import org.kairosdb.client.builder.aggregator.SamplingAggregator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

public class AggregatorFactory
{
	public enum Trim
	{
		FIRST ("first"),
		LAST ("last"),
		BOTH ("both");

		private String text;

		Trim(String text)
		{
			this.text = text;
		}

		@Override
		public String toString()
		{
			return this.text;
		}

		};

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
	 * Creates an aggregator that returns the percentile value for a given percentage of all values over each time period as specified.
	 * For example, "0.5" and "5 minutes" would returns the median of data points for each 5 minute period.
	 *
	 * @param value percentage
	 * @param unit unit of time
	 * @return percentile aggregator
	 */
	public static PercentileAggregator createPercentileAggregator(double percentile, int value, TimeUnit unit)
	{
		return new PercentileAggregator(percentile, value, unit);
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
	 * Creates an aggregator that returns the last data point for the time range.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return last aggregator
	 */
	public static SamplingAggregator createLastAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("last", value, unit);
	}

	/**
	 * Creates an aggregator that returns the first data point for the time range.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return first aggregator
	 */
	public static SamplingAggregator createFirstAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("first", value, unit);
	}

	/**
	 * Creates an aggregator that marks gaps in data according to sampling rate with a null data point.
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return gap marking aggregator
	 */
	public static SamplingAggregator createDataGapsMarkingAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("gaps", value, unit);
	}

	/**
	 * Creates an aggregator that returns a best fit line through the datapoints using the least squares algorithm..
	 *
	 * @param value value for time period.
	 * @param unit unit of time
	 * @return least squares aggregator
	 */
	public static SamplingAggregator createLeastSquaresAggregator(int value, TimeUnit unit)
	{
		return new SamplingAggregator("least_squares", value, unit);
	}

	/**
	 * Creates an aggregator that computes the difference between successive data points.
	 *
	 * @return diff aggregator
	 */
	public static Aggregator createDiffAggregator()
	{
		return new Aggregator("diff");
	}

	/**
	 * Creates an aggregator that computes the sampling rate of change for the data points.
	 *
	 * @return sampler aggregator
	 */
	public static Aggregator createSamplerAggregator()
	{
		return new Aggregator("sampler");
	}

	/**
	 * Creates an aggregator that scales each data point by a factor.
	 *
	 * @param factor factor to scale by
	 * @return sampler aggregator
	 */
	public static CustomAggregator createScaleAggregator(double factor)
	{
		return new CustomAggregator("scale", "\"factor\":" + factor);
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
	 *      Aggregator aggregator = AggregatorFactory.createCustomAggregator("scale", "\"factor\": 0.75");
	 * </pre>
	 *
	 * <p>
	 *      This produces aggregator JSON that looks like this:
	 * </p>
	 *
	 * <pre>
	 *      "name":"scale",
	 *      "factor": 0.75
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

	/**
	 * Creates an aggregator that saves the results of the query to a new metric.
	 *
	 * @param newMetricName metric to save results to
	 * @return save as aggregator
	 */
	public static CustomAggregator createSaveAsAggregator(String newMetricName)
	{
		checkNotNullOrEmpty(newMetricName, "newMetricName cannot be null or empty");
		return new CustomAggregator("save_as", "\"metric_name\":\"" + newMetricName + "\"");
	}

	/**
	 * Creates an aggregator that trim of the first, last, or both data points returned by the query.
	 *
	 * @param trim what to trim
	 * @return trim aggregator
	 */
	public static CustomAggregator createTrimAggregator(Trim trim)
	{
		checkNotNull(trim, "trim cannot be null");
		return new CustomAggregator("trim", "\"trim\":\"" + trim + "\"");
	}
}