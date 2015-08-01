//
//  CustomAggregator.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder.aggregator;

import org.kairosdb.client.builder.Aggregator;

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Creates an aggregator that takes custom JSON.
 */
public class CustomAggregator extends Aggregator
{
	private String json;

	public CustomAggregator(String name, String json)
	{
		super(name);
		this.json = checkNotNullOrEmpty(json);
	}

	public String toJson()
	{
		return "{\"name\":\"" + getName() + "\"," +  json + "}";
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;

		CustomAggregator that = (CustomAggregator) o;
		return json.equals(that.json);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + json.hashCode();
		return result;
	}
}