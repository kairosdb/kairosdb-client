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
package org.kairosdb.client.builder.grouper;

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.Grouper;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.kairosdb.client.util.Preconditions.checkNotNullOrEmpty;

/**
 * Grouper used to group by tag names.
 */
public class TagGrouper extends Grouper
{
	@SerializedName("tags")
	private List<String> tagNames = new ArrayList<String>();

	public TagGrouper(String... tagNames)
	{
		super("tag");
		checkArgument(tagNames.length > 0);
		for (String tagName : tagNames)
		{
			this.tagNames.add(checkNotNullOrEmpty(tagName));
		}
	}

	public TagGrouper(List<String> tagNames)
	{
		super("tag");
		checkNotNull(tagNames);
		this.tagNames = tagNames;
	}

	public List<String> getTagNames()
	{
		return tagNames;
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

		TagGrouper that = (TagGrouper) o;
		return !(tagNames != null ? !tagNames.equals(that.tagNames) : that.tagNames != null);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + (tagNames != null ? tagNames.hashCode() : 0);
		return result;
	}
}