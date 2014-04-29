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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class DataPointTest
{

	@Test(expected = IllegalArgumentException.class)
	public void test_timestampNegative_invalid()
	{
		MetricBuilder.getInstance().addMetric("metric").addDataPoint(-1, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_timestampZero_invalid()
	{
		MetricBuilder.getInstance().addMetric("metric").addDataPoint(0, 3);
	}

	@Test
	public void test_constructor_longValue() throws DataFormatException
	{
		DataPoint dataPoint = new DataPoint(93939393, 30L);

		assertThat(dataPoint.longValue(), equalTo(30L));
		assertThat(dataPoint.getValue(), instanceOf(Long.class));
		assertThat(dataPoint.isIntegerValue(), equalTo(true));
		assertThat(dataPoint.isDoubleValue(), equalTo(false));
	}

	@Test
	public void test_constructor_doubleValue() throws DataFormatException
	{
		DataPoint dataPoint = new DataPoint(93939393, 30.3);

		assertThat(dataPoint.doubleValue(), equalTo(30.3));
		assertThat(dataPoint.getValue(), instanceOf(Double.class));
		assertThat(dataPoint.isIntegerValue(), equalTo(false));
		assertThat(dataPoint.isDoubleValue(), equalTo(true));
	}

	@Test(expected = DataFormatException.class)
	public void test_longValue_wrong_type_invalid() throws DataFormatException
	{
		DataPoint dataPoint = new DataPoint(388383, "foo");

		dataPoint.longValue();
	}

	@Test(expected = DataFormatException.class)
	public void test_doubleValue_wrong_type_invalid() throws DataFormatException
	{
		DataPoint dataPoint = new DataPoint(388383, "foo");

		dataPoint.doubleValue();
	}
}