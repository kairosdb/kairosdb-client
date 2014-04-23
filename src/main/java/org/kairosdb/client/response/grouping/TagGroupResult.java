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

import org.kairosdb.client.response.GroupResult;

import java.util.List;
import java.util.Map;

public class TagGroupResult extends GroupResult
{
	private List<String> tags;
	private Map<String, String> group;

	public TagGroupResult(List<String> tags, Map<String, String> group)
	{
		super("tag");
		this.tags = tags;
		this.group = group;
	}

	/**
	 * List of tag names that the results were grouped by.
	 *
	 * @return tag name that the results were grouped by
	 */
	public List<String> getTags()
	{
		return tags;
	}

	/**
	 * List of tag names and their corresponding values for this group.
	 *
	 * @return tags for this grouping
	 */
	public Map<String, String> getGroup()
	{
		return group;
	}
}