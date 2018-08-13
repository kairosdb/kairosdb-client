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
package org.kairosdb.client.response;

import com.google.common.base.MoreObjects;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Resulting object from a Query.
 */
public class QueryResult
{
	private List<Result> results;

	@SerializedName("sample_size")
	private long sampleSize;

	public QueryResult(List<Result> results, long sampleSize)
	{
		this.results = results;
		this.sampleSize = sampleSize;
	}

	public List<Result> getResults()
	{
		return results;
	}

	/**
	 * Returns the number of data points returned by the query prior to aggregation. Aggregation by reduce the number
	 * of data points actually returned.
	 *
	 * @return number of data points returned by the query
	 */
	public long getSampleSize()
	{
		return sampleSize;
	}

	public Result getFirstResultByGroup(GroupResult matchingGroup)
	{
		for (Result result : results)
		{
			if (result != null)
			{
				for (GroupResult groupResult : result.getGroupResults())
				{
					if (matchingGroup.equals(groupResult))
						return (result);
				}
			}
		}

		return null;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		QueryResult that = (QueryResult) o;

		if (sampleSize != that.sampleSize) return false;
		return results != null ? results.equals(that.results) : that.results == null;

	}

	@Override
	public int hashCode()
	{
		int result = results != null ? results.hashCode() : 0;
		result = 31 * result + (int) (sampleSize ^ (sampleSize >>> 32));
		return result;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("results", results)
				.add("sampleSize", sampleSize)
				.toString();
	}
}