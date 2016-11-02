package org.kairosdb.client.builder;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

public class QueryTagMetric
{
	@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
	private final String name;
	
	private final ListMultimap<String, String> tags = ArrayListMultimap.create();
	
	public QueryTagMetric(String name)
	{
		this.name = checkNotNullOrEmpty(name);
	}
	
	/**
	 * Add a map of tags. This narrows the query to only show metadata associated with the tags' values.
	 *
	 * @param tags tags to add
	 * @return the metric
	 */
	public QueryTagMetric addTags(Map<String, String> tags)
	{
		checkNotNull(tags);

		for (String key : tags.keySet())
		{
			this.tags.put(key, tags.get(key));
		}

		return this;
	}

	/**
	 * Adds a tag with multiple values. This narrows the query to only show metadata associated with the tag's values.
	 *
	 * @param name   tag name
	 * @param values tag values
	 * @return the metric
	 */
	public QueryTagMetric addTag(String name, String... values)
	{
		checkNotNullOrEmpty(name);
		checkArgument(values.length > 0);

		for (String value : values)
		{
			checkNotNullOrEmpty(value);
		}

		tags.putAll(name, Arrays.asList(values));

		return (this);
	}
	
	public String getName()
	{
		return name;
	}
	
	public ListMultimap<String, String> getTags()
	{
		return tags;
	}

}
