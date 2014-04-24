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
}