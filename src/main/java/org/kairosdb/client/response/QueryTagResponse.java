package org.kairosdb.client.response;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import static org.weakref.jmx.internal.guava.base.Preconditions.checkNotNull;

import java.util.List;


/**
 * Response returned by KairosDB.
 */
public class QueryTagResponse
{
	private List<TagQueryResult> queries;

	@SuppressWarnings("WeakerAccess")
	public QueryTagResponse(List<TagQueryResult> queries)
	{
		checkNotNull(queries, "queries cannot be null");
		this.queries = queries;
	}

	@SuppressWarnings("unused")
	public List<TagQueryResult> getQueries()
	{
		return queries == null ? ImmutableList.of() : queries;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		QueryTagResponse that = (QueryTagResponse) o;

		return queries != null ? queries.equals(that.queries) : that.queries == null;
	}

	@Override
	public int hashCode()
	{
		return queries != null ? queries.hashCode() : 0;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("queries", queries)
				.toString();
	}
}