package org.kairosdb.client.builder;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.kairosdb.client.DataPointTypeRegistry;
import org.kairosdb.client.JsonMapper;
import org.kairosdb.client.builder.grouper.TagGrouper;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class RollupBuilderTest
{
	@Test
	public void test_build_noRollups()
	{
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
			RollupBuilder builder = RollupBuilder.getInstance("rollup1", new RelativeTime(2, TimeUnit.DAYS));
			builder.build();
		});
		assertThat(e.getMessage(), containsString("No roll-ups added"));
	}

	@Test
	public void test_build_noQueries()
	{
		NullPointerException e = assertThrows(NullPointerException.class, () -> {
			RollupBuilder builder = RollupBuilder.getInstance("rollup1", new RelativeTime(2, TimeUnit.DAYS));
			builder.addRollup("rollup1");
			builder.build();
		});
		assertThat(e.getMessage(), containsString("No queries added to rollup rollup1"));
	}

	@Test
	public void test_build_invalidQueryTime()
	{
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
			RollupBuilder builder = RollupBuilder.getInstance("rollup1", new RelativeTime(2, TimeUnit.DAYS));
			Rollup rollup = builder.addRollup("rollup1");
			rollup.addQuery().setEnd(1, TimeUnit.MINUTES);
			builder.build();
		});
		assertThat(e.getMessage(), containsString("Start time must be specified"));
	}

	@Test
	public void test()
			throws IOException
	{
		JsonMapper mapper = new JsonMapper(new DataPointTypeRegistry());
		String expectedJson = Resources.toString(Resources.getResource("rollup.json"), Charsets.UTF_8);

		RollupBuilder builder = RollupBuilder.getInstance("rollupTask", new RelativeTime(1, TimeUnit.HOURS));
		Rollup rollup = builder.addRollup("myRollupMetric");
		rollup.addQuery()
				.setStart(1, TimeUnit.HOURS)
				.addMetric("metric1")
				.addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.MINUTES))
				.addAggregator(AggregatorFactory.createCountAggregator(1, TimeUnit.MINUTES))
				.addGrouper(new TagGrouper("tag1", "tag2"));

		String json = builder.build();

		assertThat(mapper.fromJson(json, RollupTask.class), equalTo(mapper.fromJson(expectedJson, RollupTask.class)));
	}
}
