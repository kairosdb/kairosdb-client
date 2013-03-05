//
//  HttpClientTest.java
//
// Copyright 2013, Proofpoint Inc. All rights reserved.
//        
package org.kairosdb.client;

import org.junit.Test;
import org.kairosdb.client.response.GetResponse;

import java.io.IOException;

public class HttpClientTest
{

	@Test
	public void test() throws IOException
	{
		HttpClient client = new HttpClient("localhost", 9000);
		GetResponse response = client.getTagValues();

		System.out.println("response=" + response.getStatusCode());
		for (String name : response.getResults())
		{
			System.out.println(name);
		}
	}
}