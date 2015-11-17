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
package org.kairosdb.client.builder.aggregator;

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.Aggregator;
import org.kairosdb.client.builder.TimeUnit;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class SamplingAggregator extends Aggregator
{
	private Sampling sampling;

	@SerializedName("align_start_time")
	private Boolean alignStartTime;

	@SerializedName("align_sampling")
	private Boolean alignSampling;

	@SerializedName("start_time")
	private Long startTime;

	public SamplingAggregator(String name, int value, TimeUnit unit)
	{
		super(name);
		checkArgument(value > 0, "value must be greater than 0.");

		sampling = new Sampling(value, unit);
	}

	public int getValue()
	{
		return sampling.value;
	}

	public TimeUnit getUnit()
	{
		return sampling.unit;
	}

	/**
	 * <p>
	 * Alignment based on the sampling size. For example if your sample size is either milliseconds,
	 * seconds, minutes or hours then the start of the range will always be at the top
	 * of the hour.  The effect of setting this to true is that your data will
	 * take the same shape when graphed as you refresh the data.
	 * </p>
	 * <p/>
	 * <p>
	 * Only one alignment type can be used.
	 * </p>
	 */
	public SamplingAggregator withSamplingAlignment()
	{
		alignSampling = true;

		return this;
	}

	/**
	 * <p>
	 * Alignment based on the aggregation range rather than the value of the first
	 * data point within that range.
	 * </p>
	 * <p/>
	 * <p>
	 * Only one alignment type can be used.
	 * </p>
	 */
	public SamplingAggregator withStartTimeAlignment()
	{
		alignStartTime = true;

		return this;
	}

	/**
	 * <p>
	 * Alignment that starts based on the specified time. For example, if startTime
	 * is set to noon today,then alignment starts at noon today.
	 * </p>
	 * <p/>
	 * <p>
	 * Only one alignment type can be used.
	 * </p>
	 *
	 * @param startTime the alignment start time
	 */
	public SamplingAggregator withStartTimeAlignment(long startTime)
	{
		checkArgument(startTime >= 0, "startTime cannot be negative");
		alignStartTime = true;
		this.startTime = startTime;

		return this;
	}

	@Deprecated
	/**
	 * @deprecated Use withSamplingAlignment() and withStartTimeAlignment()
	 */
	public SamplingAggregator withAlignment(Boolean alignStartTime, Boolean alignSampling)
	{
		this.alignStartTime = alignStartTime;
		this.alignSampling = alignSampling;

		return this;
	}

	public Boolean isAlignStartTime()
	{
		return alignStartTime != null ? alignStartTime : false;
	}

	public Boolean isAlignSampling()
	{
		return alignSampling != null ? alignSampling : false;
	}

	public long getStartTimeAlignmentStartTime()
	{
		return startTime != null ? startTime : 0;
	}

	private class Sampling
	{
		private Sampling(int value, TimeUnit unit)
		{
			this.value = value;
			this.unit = checkNotNull(unit);
		}

		private int value;
		private TimeUnit unit;
	}

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		SamplingAggregator that = (SamplingAggregator) o;

		if (sampling != null ? !sampling.equals(that.sampling) : that.sampling != null)
			return false;
		if (alignStartTime != null ? !alignStartTime.equals(that.alignStartTime) : that.alignStartTime != null)
			return false;
		if (alignSampling != null ? !alignSampling.equals(that.alignSampling) : that.alignSampling != null)
			return false;
		return !(startTime != null ? !startTime.equals(that.startTime) : that.startTime != null);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + (sampling != null ? sampling.hashCode() : 0);
		result = 31 * result + (alignStartTime != null ? alignStartTime.hashCode() : 0);
		result = 31 * result + (alignSampling != null ? alignSampling.hashCode() : 0);
		result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
		return result;
	}
}