package org.kairosdb.client.response;

import java.util.List;

/**
 * Resulting object from a tag query.
 */
public class TagQuery
{
	private List<TagResult> results;

	public TagQuery(List<TagResult> results)
	{
		this.results = results;
	}

	public List<TagResult> getResults()
	{
		return results;
	}

}