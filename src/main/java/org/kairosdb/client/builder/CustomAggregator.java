//
//  CustomAggregator.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.builder;

import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Creates an aggregator that takes custom JSON.
 */
public class CustomAggregator implements Aggregator
{
	private String name;
	private String json;

	public CustomAggregator(String name, String json)
	{
		this.name = checkNotNullOrEmpty(name);
		this.json = checkNotNullOrEmpty(json);
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String toJson()
	{
		return "\"name\":\"" + name + "\"," +  json;
	}
}