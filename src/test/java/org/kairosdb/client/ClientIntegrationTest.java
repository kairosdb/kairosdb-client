package org.kairosdb.client;

import com.google.common.base.Stopwatch;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kairosdb.client.builder.*;
import org.kairosdb.client.builder.grouper.BinGrouper;
import org.kairosdb.client.builder.grouper.TagGrouper;
import org.kairosdb.client.builder.grouper.TimeGrouper;
import org.kairosdb.client.builder.grouper.ValueGrouper;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.UnexpectedResponseException;
import org.kairosdb.core.exception.DatastoreException;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.lang.Thread.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class ClientIntegrationTest
{
	//This ensures that consecutive runs of tests will not interfere with each other.
	private static final String RAND = UUID.randomUUID().toString();
	private static final String HTTP_METRIC_NAME_1 = "httpMetric1"+RAND;
	private static final String HTTP_METRIC_NAME_2 = "httpMetric2"+RAND;
	private static final String HTTP_TAG_NAME_1 = "httpTag1";
	private static final String HTTP_TAG_NAME_2 = "httpTag2";
	private static final String HTTP_TAG_VALUE_1 = "httpTagValue1";
	private static final String HTTP_TAG_VALUE_2 = "httpTagValue2";
	private static final String HTTP_TAG_VALUE_3 = "httpTagValue3";

	private static final String TELNET_METRIC_NAME_1 = "telnetMetric1+RAND";
	private static final String TELNET_METRIC_NAME_2 = "telnetMetric2+RAND";
	private static final String TELNET_TAG_NAME_1 = "telnetTag1";
	private static final String TELNET_TAG_NAME_2 = "telnetTag2";
	private static final String TELNET_TAG_VALUE_1 = "telnetTag1";
	private static final String TELNET_TAG_VALUE_2 = "telnetTag2";

	private static final String SSL_METRIC_NAME_1 = "sslMetric1"+RAND;
	private static final String SSL_METRIC_NAME_2 = "sslMetric2"+RAND;
	private static final String SSL_TAG_NAME_1 = "sslTag1";
	private static final String SSL_TAG_NAME_2 = "sslTag2";
	private static final String SSL_TAG_VALUE_1 = "sslTag1";
	private static final String SSL_TAG_VALUE_2 = "sslTag2";
	public static final String KAIROS_URL = "http://localhost:8082";

	private static InMemoryKairosServer kairos;

	//Kairos needs to be ran separately for these tests to work.  Updated
	//dependencies are preventing kairos from running in this way.
	@BeforeClass
	public static void setupClass() throws InterruptedException
	{
		kairos = new InMemoryKairosServer(new File("src/test/resources/kairos.properties"));
		kairos.start();

		while (!kairos.isStarted())
		{
			Thread.sleep(5);
		}
	}

	@AfterClass
	public static void tearDownClass() throws DatastoreException, InterruptedException
	{
		kairos.shutdown();
	}

	@Test
	public void test_telnetClient()
			throws IOException, DataFormatException, InterruptedException
	{
		TelnetClient client = new TelnetClient("localhost", 4245);

		try
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric(TELNET_METRIC_NAME_1);
			metric1.addTag(TELNET_TAG_NAME_1, TELNET_TAG_VALUE_1);
			long timestamp1 = System.currentTimeMillis();
			metric1.addDataPoint(timestamp1, 20);

			Metric metric2 = metricBuilder.addMetric(TELNET_METRIC_NAME_2);
			metric2.addTag(TELNET_TAG_NAME_2, TELNET_TAG_VALUE_2);
			long timestamp2 = System.currentTimeMillis();
			metric2.addDataPoint(timestamp2, 40);

			client.putMetrics(metricBuilder);

			client.shutdown();

			// Because Telnet is Asynchronous, it takes some time before the datapoints get written.
			// Wait for Kairos to notify us that they have been written.
			watiForEvent();

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric(TELNET_METRIC_NAME_1);
			builder.addMetric(TELNET_METRIC_NAME_2);

			HttpClient httpClient = new HttpClient(KAIROS_URL);
			QueryResponse query = httpClient.query(builder);
			assertThat(query.getQueries().size()).isEqualTo(2);

			assertThat(query.getQueries().get(0).getResults().size()).isEqualTo(1);
			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size()).isEqualTo(1);
			assertThat(dataPoints.get(0).getTimestamp()).isEqualTo(timestamp1);
			assertThat(dataPoints.get(0).longValue()).isEqualTo(20L);

			assertThat(query.getQueries().get(1).getResults().size()).isEqualTo(1);
			dataPoints = query.getQueries().get(1).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size()).isEqualTo(1);
			assertThat(dataPoints.get(0).getTimestamp()).isEqualTo(timestamp2);
			assertThat(dataPoints.get(0).longValue()).isEqualTo(40L);
		}
		finally
		{
			//noinspection ThrowFromFinallyBlock
			client.shutdown();
		}
	}

	@Test
	public void test_httpClient_no_results_from_query()
			throws IOException, InterruptedException
	{
		try (HttpClient client = new HttpClient(KAIROS_URL))
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric(HTTP_METRIC_NAME_1);
			metric1.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_1);
			long timestamp1 = System.currentTimeMillis();
			metric1.addDataPoint(timestamp1, 20);

			Metric metric2 = metricBuilder.addMetric(HTTP_METRIC_NAME_2);
			metric2.addTag(HTTP_TAG_NAME_2, HTTP_TAG_VALUE_2);
			long timestamp2 = System.currentTimeMillis();
			metric2.addDataPoint(timestamp2, 40);
			metric2.addTtl(1000);

			// Push Metrics
			client.pushMetrics(metricBuilder);

			watiForEvent();

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric("bogus");

			QueryResponse query = client.query(builder);
			assertThat(query.getQueries().size()).isEqualTo(1);
			assertThat(query.getQueries().get(0).getResults().size()).isEqualTo(0);
		}
	}

	@Test
	public void test_httpClient() throws IOException, DataFormatException, InterruptedException
	{
		try (HttpClient client = new HttpClient(KAIROS_URL))
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric(HTTP_METRIC_NAME_1);
			metric1.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_1);
			long timestamp1 = System.currentTimeMillis();
			metric1.addDataPoint(timestamp1, 20);

			Metric metric2 = metricBuilder.addMetric(HTTP_METRIC_NAME_2);
			metric2.addTag(HTTP_TAG_NAME_2, HTTP_TAG_VALUE_2);
			long timestamp2 = System.currentTimeMillis();
			metric2.addDataPoint(timestamp2, 40);

			// Push Metrics
			client.pushMetrics(metricBuilder);

			//Thread.sleep(1000);
			watiForEvent();

			// Check Metric names
			List<String> metricNames = client.getMetricNames();

			assertThat(metricNames).contains(HTTP_METRIC_NAME_1, HTTP_METRIC_NAME_2);

			// Check Status
			int status = client.getStatusCheck();

			assertThat(status).isEqualTo(204);

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric(HTTP_METRIC_NAME_1);
			builder.addMetric(HTTP_METRIC_NAME_2);

			QueryResponse query = client.query(builder);
			System.out.println(query);
			assertThat(query.getQueries().size()).isEqualTo(2);
			assertThat(query.getQueries().get(0).getResults().size()).isEqualTo(1);

			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size()).isEqualTo(1);
			assertThat(dataPoints.get(0).getTimestamp()).isEqualTo(timestamp1);
			assertThat(dataPoints.get(0).longValue()).isEqualTo(20L);
		}
	}

	@Test
	public void test_httpClient_multiTagValues() throws IOException, DataFormatException, InterruptedException
	{
		try (HttpClient client = new HttpClient(KAIROS_URL))
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric(HTTP_METRIC_NAME_1);
			metric1.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_1);
			long timestamp1 = System.currentTimeMillis();
			metric1.addDataPoint(timestamp1, 20);

			Metric metric2 = metricBuilder.addMetric(HTTP_METRIC_NAME_1);
			metric2.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_2);
			long timestamp2 = System.currentTimeMillis();
			metric2.addDataPoint(timestamp2, 40);

			Metric metric3 = metricBuilder.addMetric(HTTP_METRIC_NAME_1);
			metric3.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_3);
			long timestamp3 = System.currentTimeMillis();
			metric3.addDataPoint(timestamp3, 30);

			// Push Metrics
			client.pushMetrics(metricBuilder);

			watiForEvent();

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			QueryMetric queryMetric = builder.addMetric(HTTP_METRIC_NAME_1);
			queryMetric.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_2, HTTP_TAG_VALUE_3);

			QueryResponse query = client.query(builder);

			System.out.println(query);

			assertThat(query.getQueries().size()).isEqualTo(1);
			assertThat(query.getQueries().get(0).getResults().size()).isEqualTo(1);
			assertThat(query.getQueries().get(0).getResults().get(0).getTags().size()).isEqualTo(1);
			assertThat(query.getQueries().get(0).getResults().get(0).getTags().get(HTTP_TAG_NAME_1).size()).isEqualTo(2);
			assertThat(query.getQueries().get(0).getResults().get(0).getTags().get(HTTP_TAG_NAME_1)).contains(HTTP_TAG_VALUE_2, HTTP_TAG_VALUE_3);

			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size()).isEqualTo(2);
			assertThat(dataPoints.get(0).getTimestamp()).isEqualTo(timestamp2);
			assertThat(dataPoints.get(0).longValue()).isEqualTo(30L);
			assertThat(dataPoints.get(1).getTimestamp()).isEqualTo(timestamp3);
			assertThat(dataPoints.get(1).longValue()).isEqualTo(40L);
		}
	}

	/**
	 The purpose of this test is to exercise the JSON parsing code. We want to verify that Kairos does not
	 return any errors which means that the aggregators and groupBys are all valid.
	 */
	@Test
	public void test_aggregatorsAndGroupBy() throws IOException, InterruptedException
	{
		try (HttpClient client = new HttpClient(KAIROS_URL))
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric(HTTP_METRIC_NAME_1);
			metric1.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_1);
			metric1.addDataPoint(System.currentTimeMillis(), 20);
			metric1.addDataPoint(System.currentTimeMillis(), 21);
			metric1.addDataPoint(System.currentTimeMillis(), 22);
			metric1.addDataPoint(System.currentTimeMillis(), 23);

			// Push Metrics
			client.pushMetrics(metricBuilder);

			watiForEvent();

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);

			QueryMetric metric = builder.addMetric(HTTP_METRIC_NAME_1);
			metric.addAggregator(AggregatorFactory.createStandardDeviationAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createRateAggregator(TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createSumAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createDivAggregator(5));
			metric.addAggregator(AggregatorFactory.createCountAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createAverageAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createMinAggregator(1, TimeUnit.SECONDS));

			metric.addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.SECONDS)
					.withSamplingAlignment().withStartTimeAlignment());
			metric.addAggregator(AggregatorFactory.createMinAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createPercentileAggregator(0.3, 1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createLastAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createFirstAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createDiffAggregator());
			metric.addAggregator(AggregatorFactory.createLeastSquaresAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createSamplerAggregator());
			metric.addAggregator(AggregatorFactory.createScaleAggregator(.05));
			metric.addAggregator(AggregatorFactory.createSaveAsAggregator("newMetricName"));
			metric.addAggregator(AggregatorFactory.createTrimAggregator(AggregatorFactory.Trim.BOTH));
			metric.addAggregator(AggregatorFactory.createSimpleMovingAverage(2));
			metric.addAggregator(AggregatorFactory.createDataGapsMarkingAggregator(1, TimeUnit.SECONDS));

			metric.addGrouper(new TagGrouper(HTTP_TAG_NAME_1, HTTP_TAG_NAME_2));
			metric.addGrouper(new TimeGrouper(new RelativeTime(1, TimeUnit.MILLISECONDS), 3));
			metric.addGrouper(new ValueGrouper(4));
			metric.addGrouper(new BinGrouper(2.0, 3.0, 4.0));

			QueryResponse response = client.query(builder);
			assertThat(response.getQueries().size()).isEqualTo(1);
		}
	}

	@Test
	public void test_ssl() throws IOException, DataFormatException, InterruptedException
	{
		System.setProperty("javax.net.ssl.trustStore", "src/test/resources/ssl.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "testtest");


		try (HttpClient client = new HttpClient("https://localhost:8443"))
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();
			Metric metric1 = metricBuilder.addMetric(SSL_METRIC_NAME_1);
			metric1.addTag(SSL_TAG_NAME_1, SSL_TAG_VALUE_1);
			long timestamp1 = System.currentTimeMillis();
			metric1.addDataPoint(timestamp1, 20);

			Metric metric2 = metricBuilder.addMetric(SSL_METRIC_NAME_2);
			metric2.addTag(SSL_TAG_NAME_2, SSL_TAG_VALUE_2);
			long timestamp2 = System.currentTimeMillis();
			metric2.addDataPoint(timestamp2, 40);

			// Push Metrics
			client.pushMetrics(metricBuilder);

			watiForEvent();

			// Check Metric names
			List<String> metricNames = client.getMetricNames();

			assertThat(metricNames).contains(SSL_METRIC_NAME_1, SSL_METRIC_NAME_2);

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric(SSL_METRIC_NAME_1);
			builder.addMetric(SSL_METRIC_NAME_2);

			QueryResponse query = client.query(builder);
			assertThat(query.getQueries().size()).isEqualTo(2);
			assertThat(query.getQueries().get(0).getResults().size()).isEqualTo(1);

			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size()).isEqualTo(1);
			assertThat(dataPoints.get(0).getTimestamp()).isEqualTo(timestamp1);
			assertThat(dataPoints.get(0).longValue()).isEqualTo(20L);
		}
	}

		@SuppressWarnings("PointlessArithmeticExpression")
		@Test
		public void test_limit() throws IOException, DataFormatException, InterruptedException
		{
			try (HttpClient client = new HttpClient(KAIROS_URL))
			{
				MetricBuilder metricBuilder = MetricBuilder.getInstance();

				long time = System.currentTimeMillis();
				Metric metric = metricBuilder.addMetric("limitMetric").addTag("host", "a");
				metric.addDataPoint(time - 8, 20);
				metric.addDataPoint(time - 7, 21);
				metric.addDataPoint(time - 6, 22);
				metric.addDataPoint(time - 5, 23);
				metric.addDataPoint(time - 4, 24);
				metric.addDataPoint(time - 3, 25);
				metric.addDataPoint(time - 2, 26);
				metric.addDataPoint(time - 1, 27);
				metric.addDataPoint(time - 0, 28);

				// Push Metrics
				client.pushMetrics(metricBuilder);

				watiForEvent();

				// Query metrics
				QueryBuilder builder = QueryBuilder.getInstance();
				builder.setStart(1, TimeUnit.MINUTES);

				QueryMetric query = builder.addMetric("limitMetric");
				query.setLimit(5);

				QueryResponse response = client.query(builder);

				List<DataPoint> dataPoints = response.getQueries().get(0).getResults().get(0).getDataPoints();
				assertThat(dataPoints.size()).isEqualTo(5);

				assertThat(dataPoints.get(0).longValue()).isEqualTo(20L);
				assertThat(dataPoints.get(1).longValue()).isEqualTo(21L);
				assertThat(dataPoints.get(2).longValue()).isEqualTo(22L);
				assertThat(dataPoints.get(3).longValue()).isEqualTo(23L);
				assertThat(dataPoints.get(4).longValue()).isEqualTo(24L);
			}
		}

		@SuppressWarnings("PointlessArithmeticExpression")
		@Test
		public void test_Order() throws IOException, DataFormatException, InterruptedException
		{
			try (HttpClient client = new HttpClient(KAIROS_URL))
			{
				MetricBuilder metricBuilder = MetricBuilder.getInstance();

				long time = System.currentTimeMillis();
				Metric metric = metricBuilder.addMetric("orderMetric").addTag("host", "a");
				metric.addDataPoint(time - 4, 20);
				metric.addDataPoint(time - 3, 21);
				metric.addDataPoint(time - 2, 22);
				metric.addDataPoint(time - 1, 23);
				metric.addDataPoint(time - 0, 24);

				// Push Metrics
				client.pushMetrics(metricBuilder);

				watiForEvent();

				// Query metrics
				QueryBuilder builder = QueryBuilder.getInstance();
				builder.setStart(1, TimeUnit.MINUTES);

				QueryMetric query = builder.addMetric("orderMetric");
				query.setOrder(QueryMetric.Order.DESCENDING);

				QueryResponse response = client.query(builder);

				List<DataPoint> dataPoints = response.getQueries().get(0).getResults().get(0).getDataPoints();
				assertThat(dataPoints.size()).isEqualTo(5);

				assertThat(dataPoints.get(0).longValue()).isEqualTo(24L);
				assertThat(dataPoints.get(1).longValue()).isEqualTo(23L);
				assertThat(dataPoints.get(2).longValue()).isEqualTo(22L);
				assertThat(dataPoints.get(3).longValue()).isEqualTo(21L);
				assertThat(dataPoints.get(4).longValue()).isEqualTo(20L);
			}
		}

		@Test  //todo this fails when not running internal kairos server
		public void test_customDataType() throws IOException, InterruptedException
		{
			try (HttpClient client = new HttpClient(KAIROS_URL))
			{
				client.registerCustomDataType("complex", Complex.class);
				MetricBuilder metricBuilder = MetricBuilder.getInstance();

				Metric metric1 = metricBuilder.addMetric("metric1", "complex-number");
				metric1.addTag("host", "a");
				long timestamp1 = System.currentTimeMillis();
				metric1.addDataPoint(timestamp1, new Complex(4, 5));

				// Push Metrics
				client.pushMetrics(metricBuilder);

				watiForEvent();

				// Query Metric
				QueryBuilder queryBuilder = QueryBuilder.getInstance();
				queryBuilder.setStart(2, TimeUnit.MINUTES);
				queryBuilder.addMetric("metric1");

				QueryResponse queryResponse = client.query(queryBuilder);
				assertThat(queryResponse.getQueries().size()).isEqualTo(1);
				assertThat(queryResponse.getQueries().get(0).getResults().size()).isEqualTo(1);

				List<DataPoint> dataPoints = queryResponse.getQueries().get(0).getResults().get(0).getDataPoints();
				assertThat(dataPoints).contains(new DataPoint(timestamp1, new Complex(4, 5)));
			}
		}

		@Test
		public void test_rollup()
				throws IOException
		{
			try (HttpClient client = new HttpClient(KAIROS_URL))
			{
				RollupBuilder builder = RollupBuilder.getInstance("rollupTask", new RelativeTime(2, TimeUnit.DAYS));

				// Rollup 1
				Rollup rollup1 = builder.addRollup("rollup1.rollup");
				QueryBuilder builder1 = rollup1.addQuery();
				builder1.setStart(1, TimeUnit.HOURS);
				builder1.addMetric("foobar1").addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.MINUTES));

				Rollup rollup2 = builder.addRollup("rollup2.rollup");
				QueryBuilder builder2 = rollup2.addQuery();
				builder2.setStart(1, TimeUnit.MINUTES);
				builder2.addMetric("foobar2").addAggregator(AggregatorFactory.createSumAggregator(1, TimeUnit.MINUTES));


				// when: rollup is create
				RollupTask task = client.createRollupTask(builder);

				String id = task.getId();

				// then: verify rollup created
				assertThat(task.getName()).isEqualTo("rollupTask");
				assertThat(task.getExecutionInterval()).isEqualTo(new RelativeTime(2, TimeUnit.DAYS));
				assertThat(task.getRollups().size()).isEqualTo(2);
				assertRollup(task.getRollups().get(0), new RelativeTime(1, TimeUnit.HOURS), "rollup1.rollup", "foobar1","max");
				assertRollup(task.getRollups().get(1), new RelativeTime(1, TimeUnit.MINUTES), "rollup2.rollup", "foobar2","sum");

				// when: get all rollups
				List<RollupTask> rollupTasks = client.getRollupTasks();

				// then: verify all rollups
				assertThat(rollupTasks.size()).isEqualTo(1);
				assertThat(rollupTasks.get(0).getName()).isEqualTo("rollupTask");
				assertThat(rollupTasks.get(0).getExecutionInterval()).isEqualTo(new RelativeTime(2, TimeUnit.DAYS));
				assertThat(rollupTasks.get(0).getRollups().size()).isEqualTo(2);
				assertRollup(rollupTasks.get(0).getRollups().get(0), new RelativeTime(1, TimeUnit.HOURS), "rollup1.rollup", "foobar1","max");
				assertRollup(rollupTasks.get(0).getRollups().get(1), new RelativeTime(1, TimeUnit.MINUTES), "rollup2.rollup", "foobar2","sum");

				// when: get rollup
				RollupTask rollupTask = client.getRollupTask(id);

				// then: verify rollup returned
				assertThat(rollupTask.getName()).isEqualTo("rollupTask");
				assertThat(rollupTask.getExecutionInterval()).isEqualTo(new RelativeTime(2, TimeUnit.DAYS));
				assertThat(rollupTask.getRollups().size()).isEqualTo(2);

				// when: rollup is deleted
				client.deleteRollupTask(id);

				// then: verify rollup deleted
				try
				{
					client.getRollupTask(id);
				}
				catch (UnexpectedResponseException e)
				{
					assertThat(e.getStatusCode()).isEqualTo(404);
				}
			}
		}

	private void assertRollup(Rollup actual, RelativeTime startTime, String saveAs, String metricName, String aggregatorName)
	{
		assertThat(actual.getStartRelative()).isEqualTo(startTime);
		assertThat(actual.getSaveAs()).isEqualTo(saveAs);
		assertThat(actual.getMetrics().size()).isEqualTo(1);
		assertThat(actual.getMetrics().get(0).getName()).isEqualTo(metricName);
		assertThat(actual.getMetrics().get(0).getAggregators().size()).isEqualTo(1);
		assertThat(actual.getMetrics().get(0).getAggregators().get(0).getName()).isEqualTo(aggregatorName);
	}

	private void watiForEvent() throws InterruptedException
	{
		//sleep(2000);
		//todo uncomment this when we get kairos running inside again with new update.
		boolean done = false;
		Stopwatch stopwatch = Stopwatch.createStarted();
		while (!done)
		{
			sleep(200);
			if (kairos.getDataPointListener().getEvent() != null || stopwatch.elapsed(java.util.concurrent.TimeUnit.SECONDS) > 1)
			{
				done = true;
			}
		}
	}

	private class Complex
	{
		private long real;
		private long imaginary;

		private Complex(long real, long imaginary)
		{
			this.real = real;
			this.imaginary = imaginary;
		}

		@Override
		public boolean equals(Object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o == null || getClass() != o.getClass())
			{
				return false;
			}

			Complex complex = (Complex) o;

			return imaginary == complex.imaginary && real == complex.real;

		}

		@Override
		public int hashCode()
		{
			int result = (int) (real ^ (real >>> 32));
			result = 31 * result + (int) (imaginary ^ (imaginary >>> 32));
			return result;
		}

		@Override
		public String toString()
		{
			return "Complex{" +
					"real=" + real +
					", imaginary=" + imaginary +
					'}';
		}
	}
}
