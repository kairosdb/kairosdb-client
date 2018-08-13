package org.kairosdb.client.response;

import com.google.common.base.MoreObjects;

import java.util.List;
import java.util.Map;

/**
 * Tag Query Results. This is the results of a single query.
 */
public class TagResult
{
	private String name;
	private Map<String, List<String>> tags;

	public TagResult(String name, Map<String, List<String>> tags)
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

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		TagResult tagResult = (TagResult) o;

		return name != null ? name.equals(tagResult.name) : tagResult.name == null &&
				(tags != null ? tags.equals(tagResult.tags) : tagResult.tags == null);
	}

	@Override
	public int hashCode()
	{
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (tags != null ? tags.hashCode() : 0);
		return result;
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.add("name", name)
				.add("tags", tags)
				.toString();
	}
}