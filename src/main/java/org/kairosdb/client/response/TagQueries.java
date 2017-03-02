package org.kairosdb.client.response;

import java.util.List;

public class TagQueries
{
	private List<TagResults> results;

	public TagQueries(List<TagResults> results)
	{
		this.results = results;
	}

	public List<TagResults> getResults()
	{
		return results;
	}

}
