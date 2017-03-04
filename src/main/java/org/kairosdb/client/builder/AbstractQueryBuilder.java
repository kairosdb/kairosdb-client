package org.kairosdb.client.builder;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.util.Date;

import static com.google.common.base.Preconditions.*;

/**
 * Abstract class for querying KairosDB.
 * @param <B> the builder
 */
public abstract class AbstractQueryBuilder<B extends AbstractQueryBuilder<B>>
{
	@SerializedName("start_absolute")
	protected Long startAbsolute;

	@SerializedName("end_absolute")
	protected Long endAbsolute;

	@SerializedName("start_relative")
	protected RelativeTime startRelative;

	@SerializedName("end_relative")
	protected RelativeTime endRelative;

	protected transient Gson mapper;

	protected AbstractQueryBuilder()
	{
		mapper = buildGson();
	}

	/**
	 * Builds Gson used by this implementation
	 */
	protected abstract Gson buildGson();

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
	 * The beginning time of the time range.
	 *
	 * @param start start time
	 * @return the builder
	 */
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public B setStart(Date start)
	{
		checkNotNull(start, "start cannot be null");
		checkArgument(startRelative == null, "Both relative and absolute start times cannot be set.");

		this.startAbsolute = start.getTime();
		return (B) this;
	}

	/**
	 * The beginning time of the time range relative to now. For example, return all data points that starting 2 days
	 * ago.
	 *
	 * @param duration relative time value
	 * @param unit     unit of time
	 * @return the builder
	 */
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public B setStart(int duration, TimeUnit unit)
	{
		checkArgument(duration > 0, "duration must be greater than 0");
		checkNotNull(unit, "unit cannot be null");
		checkArgument(startAbsolute == null, "Both relative and absolute start times cannot be set.");

		startRelative = new RelativeTime(duration, unit);
		return (B) this;
	}

	/**
	 * The ending value of the time range. Must be later in time than the start time. An end time is not required
	 * and default to now.
	 *
	 * @param end end time
	 * @return the builder
	 */
	@SuppressWarnings("unchecked")
	public B setEnd(Date end)
	{
		checkArgument(endRelative == null, "Both relative and absolute end times cannot be set.");
		this.endAbsolute = end.getTime();
		return (B) this;
	}

	/**
	 * The ending time of the time range relative to now.
	 *
	 * @param duration relative time value
	 * @param unit     unit of time
	 * @return the builder
	 */
	@SuppressWarnings({"unchecked", "ConstantConditions"})
	public B setEnd(int duration, TimeUnit unit)
	{
		checkNotNull(unit, "unit cannot be null");
		checkArgument(duration > 0, "duration must be greater than 0");
		checkArgument(endAbsolute == null, "Both relative and absolute end times cannot be set.");
		endRelative = new RelativeTime(duration, unit);
		return (B) this;
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

	protected void validateTimes()
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

	@SuppressWarnings("SimplifiableIfStatement")
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		AbstractQueryBuilder<?> that = (AbstractQueryBuilder<?>) o;

		if (startAbsolute != null ? !startAbsolute.equals(that.startAbsolute) : that.startAbsolute != null)
			return false;
		if (endAbsolute != null ? !endAbsolute.equals(that.endAbsolute) : that.endAbsolute != null)
			return false;
		if (startRelative != null ? !startRelative.equals(that.startRelative) : that.startRelative != null)
			return false;
		return endRelative != null ? endRelative.equals(that.endRelative) : that.endRelative == null;
	}

	@Override
	public int hashCode()
	{
		int result = startAbsolute != null ? startAbsolute.hashCode() : 0;
		result = 31 * result + (endAbsolute != null ? endAbsolute.hashCode() : 0);
		result = 31 * result + (startRelative != null ? startRelative.hashCode() : 0);
		result = 31 * result + (endRelative != null ? endRelative.hashCode() : 0);
		return result;
	}
}
