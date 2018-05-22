package org.kairosdb.client.builder;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.io.Resources;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.kairosdb.client.Client;
import org.kairosdb.client.DataPointTypeRegistry;
import org.kairosdb.client.HttpClient;
import org.kairosdb.client.JsonMapper;
import org.kairosdb.client.builder.grouper.TagGrouper;
import org.kairosdb.client.response.Response;
import org.kairosdb.client.response.RollupResponse;

import java.io.IOException;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertEquals;


public class RollupBuilderTest
{
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void test_build_noRollups()
            throws IOException
    {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("No roll-ups added");

        RollupBuilder builder = RollupBuilder.getInstance("rollup1", new RelativeTime(2, TimeUnit.DAYS));
        builder.build();
    }

    @Test
    public void test_build_noQueries()
            throws IOException
    {
        thrown.expect(NullPointerException.class);
        thrown.expectMessage("No queries added to rollup rollup1");

        RollupBuilder builder = RollupBuilder.getInstance("rollup1", new RelativeTime(2, TimeUnit.DAYS));
        builder.addRollup("rollup1");
        builder.build();
    }

    @Test
    public void test_build_invalidQueryTime()
            throws IOException
    {
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("Start time must be specified");

        RollupBuilder builder = RollupBuilder.getInstance("rollup1", new RelativeTime(2, TimeUnit.DAYS));
        Rollup rollup = builder.addRollup("rollup1");
        rollup.addQuery().setEnd(1, TimeUnit.MINUTES);
        builder.build();
    }

    @Test
    public void test()
            throws IOException
    {
        JsonMapper mapper = new JsonMapper(new DataPointTypeRegistry());
        String expectedJson = Resources.toString(Resources.getResource("rollups.json"), Charsets.UTF_8);

        RollupBuilder builder = RollupBuilder.getInstance("rollupTask", new RelativeTime(1, TimeUnit.HOURS));
        Rollup rollup = builder.addRollup("myRollupMetric");
        rollup.addQuery()
                .setStart(1, TimeUnit.HOURS)
                .addMetric("metric1")
                .addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.MINUTES))
                .addAggregator(AggregatorFactory.createCountAggregator(1, TimeUnit.MINUTES))
                .addGrouper(new TagGrouper("tag1", "tag2"));

        String json = builder.build();

        assertEquals(mapper.fromJson(expectedJson, RollupTask.class), equalTo(mapper.fromJson(json, RollupTask.class)));
    }
}