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

import org.kairosdb.client.builder.Grouper;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Grouper used to group by metric value. Groups are a range of values specified by range size. For example,
 * if rangeSize is 10, then all values between 0 and 9 are put into the first group, 10-19 in the second group, etc.
 */
public class ValueGrouper extends Grouper
{
	private int rangeSize;

	public ValueGrouper(int rangeSize)
	{
		super("value");
		checkArgument(rangeSize > 0);
		this.rangeSize = rangeSize;
	}

	public int getRangeSize()
	{
		return rangeSize;
	}
}