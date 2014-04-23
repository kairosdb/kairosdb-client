package org.kairosdb.client.response.grouping;

import org.kairosdb.client.response.GroupResult;

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Group that represents natural grouping based on the type of the data.
 */
public class DefaultGroupResult extends GroupResult
{
	private String type;

	public DefaultGroupResult(String name, String type)
	{
		super(name);
		this.type = checkNotNullOrEmpty(type);
	}

	/**
	 * Returns the type of data.
	 *
	 * @return type of the data
	 */
	public String getType()
	{
		return type;
	}
}
