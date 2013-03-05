//
//  GetResponse.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client.response;

import java.util.ArrayList;
import java.util.List;

public class GetResponse extends Response
{
	private List<String> results = new ArrayList<String>();

	public GetResponse(int statusCode)
	{
		super(statusCode);
	}

	public GetResponse(int statusCode, List<String> results)
	{
		super(statusCode);
		this.results = new ArrayList<String>(results);
	}

	public List<String> getResults()
	{
		return results;
	}
}