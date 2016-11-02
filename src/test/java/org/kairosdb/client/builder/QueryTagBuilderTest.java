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

import java.io.IOException;
import java.util.Date;

import org.junit.Test;

public class QueryTagBuilderTest
{
	@Test(expected = NullPointerException.class)
	public void test_MetricNameNull_Invalid()
	{
		QueryTagBuilder.getInstance().addMetric(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_MetricNameEmpty_Invalid()
	{
		QueryTagBuilder.getInstance().addMetric("");
	}

	@Test(expected = NullPointerException.class)
	public void test_AbsoluteStartNull_Invalid()
	{
		QueryTagBuilder.getInstance().setStart(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_AbsoluteStartAndRelativeStartSet_Invalid()
	{
		QueryTagBuilder.getInstance().setStart(new Date()).setStart(3, TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeStartAndAbsoluteStartSet_Invalid()
	{
		QueryTagBuilder.getInstance().setStart(3, TimeUnit.DAYS).setStart(new Date());
	}

	@Test(expected = NullPointerException.class)
	public void test_RelativeStartUnitNull_Invalid() throws IOException
	{
		QueryTagBuilder.getInstance().setStart(3, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeStartValueZero_Invalid() throws IOException
	{
		QueryTagBuilder.getInstance().setStart(0, TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeStartValueNegative_Invalid() throws IOException
	{
		QueryTagBuilder.getInstance().setStart(-1, TimeUnit.DAYS);
	}

	@Test(expected = NullPointerException.class)
	public void test_RelativeEndUnitNull_Invalid() throws IOException
	{
		QueryTagBuilder.getInstance().setEnd(3, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeEndValueZero_Invalid() throws IOException
	{
		QueryTagBuilder.getInstance().setEnd(0, TimeUnit.DAYS);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_RelativeEndValueNegative_Invalid() throws IOException
	{
		QueryTagBuilder.getInstance().setEnd(-1, TimeUnit.DAYS);
	}

	@Test(expected = IllegalStateException.class)
	public void test_startTimeNotSpecified_Invalid() throws IOException
	{
		QueryTagBuilder.getInstance().build();
	}

	@Test(expected = IllegalStateException.class)
	public void test_endTimeAbsoluteBeforeStartTimeAbsolute_invalid() throws IOException
	{
		QueryTagBuilder.getInstance()
				.setStart(new Date())
				.setEnd(new Date(System.currentTimeMillis() - 10000))
				.build();
	}

	@Test(expected = IllegalStateException.class)
	public void test_endTimeRelativeBeforeThanStartTimeRelative_invalid() throws IOException
	{
		QueryTagBuilder.getInstance()
				.setStart(2, TimeUnit.DAYS)
				.setEnd(2, TimeUnit.WEEKS)
				.build();
	}

	@Test(expected = IllegalStateException.class)
	public void test_endTimeRelativeBeforeStartTimeAbsolute_invalid() throws IOException
	{
		QueryTagBuilder.getInstance()
				.setStart(new Date())
				.setEnd(2, TimeUnit.WEEKS)
				.build();
	}

	@Test(expected = IllegalStateException.class)
	public void test_endTimeAbsoluteBeforeStartTimeRelative_invalid() throws IOException
	{
		QueryTagBuilder.getInstance()
				.setStart(60, TimeUnit.SECONDS)
				.setEnd(new Date(1000))
				.build();
	}

}