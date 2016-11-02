package org.kairosdb.client.response;

import java.util.List;
import java.util.Map;

public class TagResults
{
	private String name;
	private Map<String, List<String>> tags;
	
	public TagResults(String name, Map<String, List<String>> tags)
	{
		this.name = name;
		this.tags = tags;
	}

	public String getName()
	{
		return name;
	}

	public Map<String, List<String>> getTags()
	{
		return tags;
	}
}
