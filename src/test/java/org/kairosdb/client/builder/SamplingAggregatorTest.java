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
package org.kairosdb.client.builder;

import org.junit.Test;

public class SamplingAggregatorTest
{

	@Test(expected = NullPointerException.class)
	public void test_nullName_invalid()
	{
		new SamplingAggregator(null, 1 ,TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_emptyName_invalid()
	{
		new SamplingAggregator("", 1 ,TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_valueNegative_invalid()
	{
		new SamplingAggregator("sum", -1 ,TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_valueZero_invalid()
	{
		new SamplingAggregator("sum", 0 ,TimeUnit.DAYS);
	}

	@Test(expected = NullPointerException.class)
	public void test_unitNull_invalid()
	{
		new SamplingAggregator("sum", 1, null);
	}
}