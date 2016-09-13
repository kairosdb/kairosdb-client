package org.kairosdb.client.response.grouping;

import org.kairosdb.client.response.GroupResult;

import java.util.Map;

public class CustomGroupResult extends GroupResult
{
	private Map<String, Object> properties;

	public CustomGroupResult(Map<String, Object> properties)
	{
		super((String) properties.get("name"));
		this.properties = properties;
	}

	public Map<String, Object> getProperties()
	{
		return properties;
	}
}
