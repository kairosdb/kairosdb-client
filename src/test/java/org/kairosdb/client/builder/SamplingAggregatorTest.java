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
import org.kairosdb.client.builder.aggregator.SamplingAggregator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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

	@Test(expected = IllegalArgumentException.class)
	public void testWithStartTimeAlignmentNegativeStartTimeInvalid()
	{
		new SamplingAggregator("sum", 1, TimeUnit.DAYS).withStartTimeAlignment(-1);
	}

	@Test
	public void testWithSamplingAlignment()
	{
		SamplingAggregator aggregator = new SamplingAggregator("sum", 1, TimeUnit.DAYS).withSamplingAlignment();

		assertFalse(aggregator.isAlignStartTime());
		assertTrue(aggregator.isAlignSampling());
	}

	@Test
	public void testWithStartTimeAlignment()
	{
		SamplingAggregator aggregator = new SamplingAggregator("sum", 1, TimeUnit.DAYS).withStartTimeAlignment();

		assertFalse(aggregator.isAlignSampling());
		assertTrue(aggregator.isAlignStartTime());
		assertFalse(aggregator.isAlignEndTime());
		assertThat(aggregator.getStartTimeAlignmentStartTime(), equalTo(0L));
	}

	@Test
	public void testWithEndTimeAlignment()
	{
		SamplingAggregator aggregator = new SamplingAggregator("sum", 1, TimeUnit.DAYS).withEndTimeAlignment();

		assertFalse(aggregator.isAlignSampling());
		assertFalse(aggregator.isAlignStartTime());
		assertTrue(aggregator.isAlignEndTime());
		assertThat(aggregator.getStartTimeAlignmentStartTime(), equalTo(0L));
	}

	@Test
	public void testWithStartTimeAlignmentWithStartTime()
	{
		SamplingAggregator aggregator = new SamplingAggregator("sum", 1, TimeUnit.DAYS).withStartTimeAlignment(444);

		assertFalse(aggregator.isAlignSampling());
		assertTrue(aggregator.isAlignStartTime());
		assertFalse(aggregator.isAlignEndTime());
		assertThat(aggregator.getStartTimeAlignmentStartTime(), equalTo(444L));
	}

	@Test
	public void testWithEndTimeAlignmentWithStartTime()
	{
		SamplingAggregator aggregator = new SamplingAggregator("sum", 1, TimeUnit.DAYS).withEndTimeAlignment(444);

		assertFalse(aggregator.isAlignSampling());
		assertFalse(aggregator.isAlignStartTime());
		assertTrue(aggregator.isAlignEndTime());
		assertThat(aggregator.getStartTimeAlignmentStartTime(), equalTo(444L));
	}
}