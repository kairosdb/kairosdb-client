package org.kairosdb.client.response;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * Resulting object from a tag query.
 */
public class TagQueryResult
{
	private List<TagResult> results;

	public TagQueryResult(List<TagResult> results)
	{
		this.results = results;
	}

	public List<TagResult> getResults()
	{
		return results == null ? ImmutableList.of(): results;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TagQueryResult that = (TagQueryResult) o;

		return results != null ? results.equals(that.results) : that.results == null;

	}

	@Override
	public int hashCode()
	{
		return results != null ? results.hashCode() : 0;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("results", results)
				.toString();
	}
}