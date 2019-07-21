package org.kairosdb.client.builder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static net.javacrumbs.jsonunit.JsonAssert.assertJsonEquals;
import static net.javacrumbs.jsonunit.JsonAssert.when;
import static net.javacrumbs.jsonunit.core.Option.IGNORING_ARRAY_ORDER;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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

	@Test
	public void test() throws IOException
	{
		QueryTagBuilder builder = QueryTagBuilder.getInstance()
				.setStart(1, TimeUnit.HOURS);
		builder.addMetric("metricName")
				.addTag("foo", Collections.singleton("bar"))
				.addTag("fi", Collections.singleton("fum"));

		assertThat(builder.build(), equalTo("{\"metrics\":[{\"name\":\"metricName\",\"tags\":{\"fi\":[\"fum\"],\"foo\":[\"bar\"]}}],\"start_relative\":{\"value\":1,\"unit\":\"HOURS\"}}"));
	}

	@Test
	public void testMultipleTags() throws IOException
	{
		QueryTagBuilder builder = QueryTagBuilder.getInstance()
				.setStart(1, TimeUnit.HOURS);
		Set<String> tags = new HashSet<>();
		Set<String> tags1 = new HashSet<>();
		tags.add("bar");
		tags.add("Bar");
		tags.add("bars");

		tags1.add("fum");
		tags1.add("Fum");
		tags1.add("fums");

		builder.addMetric("metricName")
				.addTag("foo",tags)
				.addTag("fi",tags1);
    assertJsonEquals(
        builder.build(),
            "{\"metrics\":[{\"name\":\"metricName\",\"tags\":{\"foo\":[\"bar\",\"Bar\",\"bars\"],\"fi\":[\"fum\",\"Fum\",\"fums\"]}}],\"start_relative\":{\"value\":1,\"unit\":\"HOURS\"}}",
				when(IGNORING_ARRAY_ORDER));
	}
}