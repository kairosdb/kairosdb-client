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

import java.util.Date;

public class TimeValidatorTest
{

	@Test
	public void test_AbsoluteStartBeforeAbsoluteEnd_Valid() 
	{
		long now = System.currentTimeMillis();
		RelativeTime startTime = new RelativeTime(2, TimeUnit.WEEKS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.DAYS);
		TimeValidator.validateEndTimeLaterThanStartTime(new Date(startTime.getTimeRelativeTo(now)), new Date(endTime.getTimeRelativeTo(now)));
	}

	@Test(expected = IllegalStateException.class)
	public void test_AbsoluteStartLaterThanAbsoluteEnd_Invalid() 
	{
		long now = System.currentTimeMillis();
		RelativeTime startTime = new RelativeTime(2, TimeUnit.DAYS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.WEEKS);
		TimeValidator.validateEndTimeLaterThanStartTime(new Date(startTime.getTimeRelativeTo(now)), new Date(endTime.getTimeRelativeTo(now)));
	}

	@Test
	public void test_RelativeStartBeforeAbsoluteEnd_Valid() 
	{
		RelativeTime startTime = new RelativeTime(2, TimeUnit.WEEKS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.DAYS);
		TimeValidator.validateEndTimeLaterThanStartTime(startTime, new Date(endTime.getTimeRelativeTo(System.currentTimeMillis())));
	}

	@Test(expected = IllegalStateException.class)
	public void test_RelativeStartLaterThanAbsoluteEnd_Invalid() 
	{
		RelativeTime startTime = new RelativeTime(2, TimeUnit.DAYS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.WEEKS);
		TimeValidator.validateEndTimeLaterThanStartTime(startTime, new Date(endTime.getTimeRelativeTo(System.currentTimeMillis())));
	}

	@Test
	public void test_AbsoluteStartBeforeRelativeEnd_Valid() 
	{
		RelativeTime startTime = new RelativeTime(2, TimeUnit.WEEKS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.DAYS);
		TimeValidator.validateEndTimeLaterThanStartTime(new Date(startTime.getTimeRelativeTo(System.currentTimeMillis())), endTime);
	}

	@Test(expected = IllegalStateException.class)
	public void test_AbsoluteStartLaterThanRelativeEnd_Invalid() 
	{
		RelativeTime startTime = new RelativeTime(2, TimeUnit.DAYS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.WEEKS);
		TimeValidator.validateEndTimeLaterThanStartTime(new Date(startTime.getTimeRelativeTo(System.currentTimeMillis())), endTime);
	}

	@Test
	public void test_RelativeStartBeforeRelativeEnd_Valid() 
	{
		RelativeTime startTime = new RelativeTime(2, TimeUnit.WEEKS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.DAYS);
		TimeValidator.validateEndTimeLaterThanStartTime(startTime, endTime);
	}

	@Test(expected = IllegalStateException.class)
	public void test_RelativeStartLaterThanRelativeEnd_Invalid() 
	{
		RelativeTime startTime = new RelativeTime(2, TimeUnit.DAYS);
		RelativeTime endTime = new RelativeTime(2, TimeUnit.WEEKS);
		TimeValidator.validateEndTimeLaterThanStartTime(startTime, endTime);
	}

}