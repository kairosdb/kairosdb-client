package org.kairosdb.client.builder;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Objects.requireNonNull;
import static org.kairosdb.client.util.Preconditions.checkArgument;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Query request for tags. You can narrow down the query by adding tags.
 * Only metrics that include the tag and matches one of the values are returned.
 */
public class QueryTagMetric
{
	@SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
	private final String name;

	private final ListMultimap<String, String> tags = ArrayListMultimap.create();

	public QueryTagMetric(String name)
	{
		this.name = checkNotNullOrEmpty(name, "name cannot be null or empty");
	}

	/**
	 * Add a map of tags. This narrows the query to only show metadata associated with the tags' values.
	 *
	 * @param tags tags to add
	 * @return the metric
	 */
	public QueryTagMetric addTags(Map<String, String> tags)
	{
		requireNonNull(tags);

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
	public QueryTagMetric addTag(String name, Set<String> values)
	{
		checkNotNullOrEmpty(name, "name cannot be null or empty");
		checkArgument(values.size() > 0, "value must be greater than 0");

		for (String value : values)
		{
			checkNotNullOrEmpty(value, "value cannot be null or empty");
		}

		tags.putAll(name, values);

		return (this);
	}
	
	public QueryTagMetric addTag(String name, String... values)
	{
		checkNotNullOrEmpty(name, "name cannot be null or empty");
		checkArgument(values.length > 0, "value must be greater than 0");
		ArrayList<String> valueList = new ArrayList<>();
		for (String value : values)
		{
			checkNotNullOrEmpty(value, "value cannot be null or empty");
			if(!valueList.contains(value))
			valueList.add(value);
		}
		
		tags.putAll(name, valueList);
		
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