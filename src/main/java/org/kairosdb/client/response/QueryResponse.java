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
import com.google.common.collect.ImmutableList;

import java.util.List;
import static org.weakref.jmx.internal.guava.base.Preconditions.checkNotNull;

/**
 Response returned by KairosDB.
 */
public class QueryResponse
{
	private List<QueryResult> queries;

	public QueryResponse(List<QueryResult> queries)
	{
		checkNotNull(queries, "queries cannot be null");
		this.queries = queries;
	}

	@SuppressWarnings("unused")
	public List<QueryResult> getQueries()
	{
		return queries == null ? ImmutableList.of() : queries;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		QueryResponse that = (QueryResponse) o;

		return queries.equals(that.queries);

	}

	@Override
	public int hashCode()
	{
		return queries.hashCode();
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("queries", queries)
				.toString();
	}
}