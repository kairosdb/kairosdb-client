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

import com.google.common.collect.ListMultimap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.aggregator.CustomAggregator;
import org.kairosdb.client.serializer.CustomAggregatorSerializer;
import org.kairosdb.client.serializer.ListMultiMapSerializer;
import org.kairosdb.client.serializer.OrderSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.*;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Builder used to create the JSON to query KairosDB.
 * <p/>
 * The query returns the data points for the given metrics for the specified time range. The time range can
 * be specified as absolute or relative. Absolute times are a given point in time. Relative times are relative to now.
 * The end time is not required and defaults to now.
 * <p/>
 * For example, if you specify a relative start time of 30 minutes, all matching data points for the last 30 minutes
 * will be returned. If you specify a relative start time of 30 minutes and a relative end time of 10 minutes, then
 * all matching data points that occurred between the last 30 minutes up to and including the last 10 minutes are returned.
 */
@SuppressWarnings("UnusedDeclaration")
public class QueryBuilder
{
	@SerializedName("start_absolute")
	private Long startAbsolute;

	@SerializedName("end_absolute")
	private Long endAbsolute;

	@SerializedName("start_relative")
	private RelativeTime startRelative;

	@SerializedName("end_relative")
	private RelativeTime endRelative;
	
	@SerializedName("time_zone")
	private String timeZone;
	
	@SerializedName("cache_time")
	private int cacheTime;

	private List<QueryMetric> metrics = new ArrayList<QueryMetric>();
	private transient Gson mapper;

	private QueryBuilder()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(CustomAggregator.class, new CustomAggregatorSerializer());
		builder.registerTypeAdapter(ListMultimap.class, new ListMultiMapSerializer());
		builder.registerTypeAdapter(QueryMetric.Order.class, new OrderSerializer());

		mapper = builder.create();
	}

	/**
	 * The beginning time of the time range.
	 *
	 * @param start start time
	 * @return the builder
	 */
	public QueryBuilder setStart(Date start)
	{
		checkNotNull(start);
		checkArgument(startRelative == null, "Both relative and absolute start times cannot be set.");

		this.startAbsolute = start.getTime();
		checkArgument(startAbsolute <= System.currentTimeMillis(), "Start time cannot be in the future.");
		return this;
	}

	/**
	 * The beginning time of the time range relative to now. For example, return all data points that starting 2 days
	 * ago.
	 *
	 * @param duration relative time value
	 * @param unit     unit of time
	 * @return the builder
	 */
	public QueryBuilder setStart(int duration, TimeUnit unit)
	{
		checkArgument(duration > 0);
		checkNotNull(unit);
		checkArgument(startAbsolute == null, "Both relative and absolute start times cannot be set.");

		startRelative = new RelativeTime(duration, unit);
		checkArgument(startRelative.getTimeRelativeTo(System.currentTimeMillis()) <= System.currentTimeMillis(), "Start time cannot be in the future.");
		return this;
	}

	/**
	 * The ending value of the time range. Must be later in time than the start time. An end time is not required
	 * and default to now.
	 *
	 * @param end end time
	 * @return the builder
	 */
	public QueryBuilder setEnd(Date end)
	{
		checkArgument(endRelative == null, "Both relative and absolute end times cannot be set.");
		this.endAbsolute = end.getTime();
		return this;
	}

	/**
	 * The ending time of the time range relative to now.
	 *
	 * @param duration relative time value
	 * @param unit     unit of time
	 * @return the builder
	 */
	public QueryBuilder setEnd(int duration, TimeUnit unit)
	{
		checkNotNull(unit);
		checkArgument(duration > 0);
		checkArgument(endAbsolute == null, "Both relative and absolute end times cannot be set.");
		endRelative = new RelativeTime(duration, unit);
		return this;
	}

	/**
	 * The time zone used for the query.
	 *
	 * @param timeZone canonical id of the time zone (see http://joda-time.sourceforge.net/timezones.html)
	 * @return the builder
	 */
	public QueryBuilder setTimeZone(String timeZone)
	{
		String[] validIDs = TimeZone.getAvailableIDs();
		int valid = 0;
		for (String str : validIDs) {
		      if (str != null && str.equals(timeZone)) {
		        valid = 1;
		        break;
		      }
		}
		checkArgument(valid == 1, "Time zone id is not valid.");
		this.timeZone = timeZone;
		return this;
	}
	
	/**
	 * How long to cache this exact query. The default is to never cache.
	 *
	 * @param cacheTime cache time in milliseconds
	 * @return the builder
	 */
	public QueryBuilder setCacheTime(int cacheTime)
	{
		checkArgument(cacheTime > 0, "Cache time must be greater than 0.");
		this.cacheTime = cacheTime;
		return this;
	}

	/**
	 * Returns a new query builder.
	 *
	 * @return new query builder
	 */
	public static QueryBuilder getInstance()
	{
		return new QueryBuilder();
	}

	/**
	 * The metric to query for.
	 *
	 * @param name metric name
	 * @return the builder
	 */
	public QueryMetric addMetric(String name)
	{
		checkNotNullOrEmpty(name, "Name cannot be null or empty.");

		QueryMetric metric = new QueryMetric(name);
		metrics.add(metric);
		return metric;
	}

	/**
	 * Returns the absolute range start time.
	 *
	 * @return absolute range start time
	 */
	public Date getStartAbsolute()
	{
		return new Date(startAbsolute);
	}

	/**
	 * Returns the absolute range end time.
	 *
	 * @return absolute range end time
	 */
	public Date getEndAbsolute()
	{
		return new Date(endAbsolute);
	}

	/**
	 * Returns the relative range start time.
	 *
	 * @return relative range start time
	 */
	public RelativeTime getStartRelative()
	{
		return startRelative;
	}

	/**
	 * Returns the relative range end time.
	 *
	 * @return relative range end time
	 */
	public RelativeTime getEndRelative()
	{
		return endRelative;
	}
	
	/**
	 * Returns the time zone id.
	 *
	 * @return time zone
	 */
	public String getTimeZone()
	{
		return timeZone;
	}

	/**
	 * Returns the cache time.
	 *
	 * @return cache time
	 */
	public int getCacheTime()
	{
		return cacheTime;
	}

	/**
	 * Returns the list metrics to query for.
	 *
	 * @return metrics
	 */
	public List<QueryMetric> getMetrics()
	{
		return metrics;
	}

	/**
	 * Returns the JSON string built by the builder. This is the JSON that can be used by the client to query KairosDB
	 *
	 * @return JSON
	 * @throws IOException if the query is invalid and cannot be converted to JSON
	 */
	public String build() throws IOException
	{
		validateTimes();

		return mapper.toJson(this);
	}

	private void validateTimes()
	{
		checkState(startAbsolute != null || startRelative != null, "Start time must be specified");

		if (endAbsolute != null)
		{
			if (startAbsolute != null)
				TimeValidator.validateEndTimeLaterThanStartTime(startAbsolute, endAbsolute);
			else
				TimeValidator.validateEndTimeLaterThanStartTime(startRelative, endAbsolute);
			}
		else if (endRelative != null)
		{
			if (startAbsolute != null)
				TimeValidator.validateEndTimeLaterThanStartTime(startAbsolute, endRelative);
			else
				TimeValidator.validateEndTimeLaterThanStartTime(startRelative, endRelative);
		}
	}
}