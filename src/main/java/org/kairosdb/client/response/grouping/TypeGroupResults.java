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
package org.kairosdb.client.response.grouping;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;
import org.kairosdb.client.response.GroupResults;

public class TypeGroupResults implements GroupResults
{
	private static final String NAME = "type";
	private String type;

	@JsonCreator
	public TypeGroupResults(@JsonProperty("type") String type)
	{
		this.type = type;
	}

	/**
	 * Name of the grouper.
	 *
	 * @return grouper name
	 */
	public String getName()
	{
		return NAME;
	}

	/**
	 * Type that the results were grouped by.
	 *
	 * @return type name that the results were grouped by
	 */
	public String getType()
	{
		return type;
	}
}