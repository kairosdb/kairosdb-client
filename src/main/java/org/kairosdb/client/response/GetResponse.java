/*
 * Copyright 2013 Proofpoint Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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