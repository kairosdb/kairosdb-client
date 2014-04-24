package org.kairosdb.client.builder.grouper;

import org.kairosdb.client.builder.Grouper;

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Grouper that that takes custom json.
 */
public class CustomGrouper extends Grouper
{
	private String json;

	public CustomGrouper(String name, String json)
	{
		super(name);
		this.json = checkNotNullOrEmpty(json);
	}

	public String toJson()
	{
		return "\"name\": \"" + getName() + "\", " + json;
	}
}
