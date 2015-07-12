package org.kairosdb.client;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kairosdb.client.builder.*;
import org.kairosdb.client.builder.grouper.TagGrouper;
import org.kairosdb.client.builder.grouper.TimeGrouper;
import org.kairosdb.client.builder.grouper.ValueGrouper;
import org.kairosdb.client.response.GetResponse;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.Response;
import org.kairosdb.core.exception.DatastoreException;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class ClientIntegrationTest
{
	public static final String HTTP_METRIC_NAME_1 = "httpMetric1";
	public static final String HTTP_METRIC_NAME_2 = "httpMetric2";
	public static final String HTTP_TAG_NAME_1 = "httpTag1";
	public static final String HTTP_TAG_NAME_2 = "httpTag2";
	public static final String HTTP_TAG_VALUE_1 = "httpTagValue1";
	public static final String HTTP_TAG_VALUE_2 = "httpTagValue2";
	public static final String HTTP_TAG_VALUE_3 = "httpTagValue3";

	public static final String TELNET_METRIC_NAME_1 = "telnetMetric1";
	public static final String TELNET_METRIC_NAME_2 = "telnetMetric2";
	public static final String TELNET_TAG_NAME_1 = "telnetTag1";
	public static final String TELNET_TAG_NAME_2 = "telnetTag2";
	public static final String TELNET_TAG_VALUE_1 = "telnetTag1";
	public static final String TELNET_TAG_VALUE_2 = "telnetTag2";

	public static final String SSL_METRIC_NAME_1 = "sslMetric1";
	public static final String SSL_METRIC_NAME_2 = "sslMetric2";
	public static final String SSL_TAG_NAME_1 = "sslTag1";
	public static final String SSL_TAG_NAME_2 = "sslTag2";
	public static final String SSL_TAG_VALUE_1 = "sslTag1";
	public static final String SSL_TAG_VALUE_2 = "sslTag2";

	private static InMemoryKairosServer kairos;

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

	@After
	public void tearDown()
	{
		kairos.getDataPointListener().setEvent(null);
	}

	@Test
	public void test_telnetClient() throws IOException, URISyntaxException, DataFormatException
	{
		DataPointEvent dataPointEvent = mock(DataPointEvent.class);
		kairos.getDataPointListener().setEvent(dataPointEvent);

		TelnetClient client = new TelnetClient("localhost", 4243);

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

			client.pushMetrics(metricBuilder);

			client.shutdown();

			// Because Telnet is Asynchronous, it takes some time before the datapoints get written.
			// Wait for Kairos to notify us that they have been written.
			verify(dataPointEvent, timeout(5000).times(1)).datapoint(TELNET_METRIC_NAME_1);
			verify(dataPointEvent, timeout(5000).times(1)).datapoint(TELNET_METRIC_NAME_2);

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric(TELNET_METRIC_NAME_1);
			builder.addMetric(TELNET_METRIC_NAME_2);

			HttpClient httpClient = new HttpClient("http://localhost:8080");
			QueryResponse query = httpClient.query(builder);
			assertThat(query.getQueries().size(), equalTo(2));

			assertThat(query.getQueries().get(0).getResults().size(), equalTo(1));
			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			System.out.println(timestamp1);
			assertThat(dataPoints.size(), equalTo(1));
			assertThat(dataPoints.get(0).getTimestamp(), equalTo(timestamp1));
			assertThat(dataPoints.get(0).longValue(), equalTo(20L));

			assertThat(query.getQueries().get(1).getResults().size(), equalTo(1));
			dataPoints = query.getQueries().get(1).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size(), equalTo(1));
			assertThat(dataPoints.get(0).getTimestamp(), equalTo(timestamp2));
			assertThat(dataPoints.get(0).longValue(), equalTo(40L));
		}
		finally
		{
			client.shutdown();
		}
	}

	@Test
	public void test_httpClient_no_results_from_query()
			throws InterruptedException, IOException, URISyntaxException, DataFormatException
	{
		HttpClient client = new HttpClient("http://localhost:8080");

		try
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
			Response response = client.pushMetrics(metricBuilder);

			assertThat(response.getStatusCode(), equalTo(204));
			assertThat(response.getErrors().size(), equalTo(0));

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric("bogus");

			QueryResponse query = client.query(builder);
			assertThat(query.getQueries().size(), equalTo(1));
			assertThat(query.getQueries().get(0).getResults().size(), equalTo(1));

			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size(), equalTo(0));
		}
		finally
		{
			client.shutdown();
		}}

	@Test
	public void test_httpClient() throws InterruptedException, IOException, URISyntaxException, DataFormatException
	{
		HttpClient client = new HttpClient("http://localhost:8080");

		try
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
			Response response = client.pushMetrics(metricBuilder);

			assertThat(response.getStatusCode(), equalTo(204));
			assertThat(response.getErrors().size(), equalTo(0));

			// Check Metric names
			GetResponse metricNames = client.getMetricNames();

			assertThat(metricNames.getStatusCode(), equalTo(200));
			assertThat(metricNames.getResults(), hasItems(HTTP_METRIC_NAME_1, HTTP_METRIC_NAME_2));

			// Check Tag names
			GetResponse tagNames = client.getTagNames();

			assertThat(tagNames.getStatusCode(), equalTo(200));
			assertThat(tagNames.getResults(), hasItems(HTTP_TAG_NAME_1, HTTP_TAG_NAME_2));

			// Check Tag values
			GetResponse tagValues = client.getTagValues();

			assertThat(tagValues.getStatusCode(), equalTo(200));
			assertThat(tagValues.getResults(), hasItems(HTTP_TAG_VALUE_1, HTTP_TAG_VALUE_2));

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric(HTTP_METRIC_NAME_1);
			builder.addMetric(HTTP_METRIC_NAME_2);

			QueryResponse query = client.query(builder);
			assertThat(query.getQueries().size(), equalTo(2));
			assertThat(query.getQueries().get(0).getResults().size(), equalTo(1));

			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size(), equalTo(1));
			assertThat(dataPoints.get(0).getTimestamp(), equalTo(timestamp1));
			assertThat(dataPoints.get(0).longValue(), equalTo(20L));
		}
		finally
		{
			client.shutdown();
		}}

	@Test
	public void test_httpClient_multiTagValues() throws InterruptedException, IOException, URISyntaxException, DataFormatException
	{
		HttpClient client = new HttpClient("http://localhost:8080");

		try
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
			Response response = client.pushMetrics(metricBuilder);

			assertThat(response.getStatusCode(), equalTo(204));
			assertThat(response.getErrors().size(), equalTo(0));

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			QueryMetric queryMetric = builder.addMetric(HTTP_METRIC_NAME_1);
			queryMetric.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_2, HTTP_TAG_VALUE_3);

			QueryResponse query = client.query(builder);

			assertThat(query.getQueries().size(), equalTo(1));
			assertThat(query.getQueries().get(0).getResults().size(), equalTo(1));
			assertThat(query.getQueries().get(0).getResults().get(0).getTags().size(), equalTo(1));
			assertThat(query.getQueries().get(0).getResults().get(0).getTags().get(HTTP_TAG_NAME_1).size(), equalTo(2));
			assertThat(query.getQueries().get(0).getResults().get(0).getTags().get(HTTP_TAG_NAME_1), hasItems(HTTP_TAG_VALUE_2, HTTP_TAG_VALUE_3));

			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size(), equalTo(2));
			assertThat(dataPoints.get(0).getTimestamp(), equalTo(timestamp2));
			assertThat(dataPoints.get(0).longValue(), equalTo(30L));
			assertThat(dataPoints.get(1).getTimestamp(), equalTo(timestamp3));
			assertThat(dataPoints.get(1).longValue(), equalTo(40L));
		}
		finally
		{
			client.shutdown();
		}
	}

	/**
	 * The purpose of this test is to exercise the JSON parsing code. We want to verify that Kairos does not
	 * return any errors which means that the aggregators and groupBys are all valid.
	 */
	@Test
	public void test_aggregatorsAndGroupBy() throws InterruptedException, IOException, URISyntaxException
	{
		HttpClient client = new HttpClient("http://localhost:8080");

		try
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric(HTTP_METRIC_NAME_1);
			metric1.addTag(HTTP_TAG_NAME_1, HTTP_TAG_VALUE_1);
			metric1.addDataPoint(System.currentTimeMillis(), 20);
			metric1.addDataPoint(System.currentTimeMillis(), 21);
			metric1.addDataPoint(System.currentTimeMillis(), 22);
			metric1.addDataPoint(System.currentTimeMillis(), 23);

			// Push Metrics
			Response response = client.pushMetrics(metricBuilder);

			assertThat(response.getStatusCode(), equalTo(204));
			assertThat(response.getErrors().size(), equalTo(0));

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
					.withAlignment(false, false));
			metric.addAggregator(AggregatorFactory.createMinAggregator(1, TimeUnit.SECONDS)
					.withAlignment(true, true));

			metric.addAggregator(AggregatorFactory.createPercentileAggregator(0.3, 1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createLastAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createFirstAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createDataGapsMarkingAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createDiffAggregator());
			metric.addAggregator(AggregatorFactory.createLeastSquaresAggregator(1, TimeUnit.SECONDS));
			metric.addAggregator(AggregatorFactory.createSamplerAggregator());
			metric.addAggregator(AggregatorFactory.createScaleAggregator(.05));

			metric.addGrouper(new TagGrouper(HTTP_TAG_NAME_1, HTTP_TAG_NAME_2));
			metric.addGrouper(new TimeGrouper(new RelativeTime(1, TimeUnit.MILLISECONDS), 3));
			metric.addGrouper(new ValueGrouper(4));

			QueryResponse query = client.query(builder);
			assertThat(query.getErrors().size(), equalTo(0));
		}
		finally
		{
			client.shutdown();
		}
	}

	@Test
	public void test_ssl() throws IOException, URISyntaxException, DataFormatException
	{
		System.setProperty("javax.net.ssl.trustStore", "src/test/resources/ssl.jks");
		System.setProperty("javax.net.ssl.trustStorePassword", "testtest");
		HttpClient client = new HttpClient("https://localhost:8443");

		try
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
			Response response = client.pushMetrics(metricBuilder);

			assertThat(response.getStatusCode(), equalTo(204));
			assertThat(response.getErrors().size(), equalTo(0));

			// Check Metric names
			GetResponse metricNames = client.getMetricNames();

			assertThat(metricNames.getStatusCode(), equalTo(200));
			assertThat(metricNames.getResults(), hasItems(SSL_METRIC_NAME_1, SSL_METRIC_NAME_2));

			// Check Tag names
			GetResponse tagNames = client.getTagNames();

			assertThat(tagNames.getStatusCode(), equalTo(200));
			assertThat(tagNames.getResults(), hasItems(SSL_TAG_NAME_1, SSL_TAG_NAME_2));

			// Check Tag values
			GetResponse tagValues = client.getTagValues();

			assertThat(tagValues.getStatusCode(), equalTo(200));
			assertThat(tagValues.getResults(), hasItems(SSL_TAG_VALUE_1, SSL_TAG_VALUE_2));

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);
			builder.addMetric(SSL_METRIC_NAME_1);
			builder.addMetric(SSL_METRIC_NAME_2);

			QueryResponse query = client.query(builder);
			assertThat(query.getQueries().size(), equalTo(2));
			assertThat(query.getQueries().get(0).getResults().size(), equalTo(1));

			List<DataPoint> dataPoints = query.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size(), equalTo(1));
			assertThat(dataPoints.get(0).getTimestamp(), equalTo(timestamp1));
			assertThat(dataPoints.get(0).longValue(), equalTo(20L));
		}
		finally
		{
			client.shutdown();
		}
	}

	@SuppressWarnings("PointlessArithmeticExpression")
	@Test
	public void test_limit() throws InterruptedException, IOException, URISyntaxException, DataFormatException
	{
		HttpClient client = new HttpClient("http://localhost:8080");

		try
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
			Response pushResponse = client.pushMetrics(metricBuilder);

			assertThat(pushResponse.getStatusCode(), equalTo(204));
			assertThat(pushResponse.getErrors().size(), equalTo(0));

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);

			QueryMetric query = builder.addMetric("limitMetric");
			query.setLimit(5);

			QueryResponse response = client.query(builder);
			assertThat(response.getErrors().size(), equalTo(0));

			List<DataPoint> dataPoints = response.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size(), equalTo(5));

			assertThat(dataPoints.get(0).longValue(), equalTo(20L));
			assertThat(dataPoints.get(1).longValue(), equalTo(21L));
			assertThat(dataPoints.get(2).longValue(), equalTo(22L));
			assertThat(dataPoints.get(3).longValue(), equalTo(23L));
			assertThat(dataPoints.get(4).longValue(), equalTo(24L));
		}
		finally
		{
			client.shutdown();
		}
	}

	@SuppressWarnings("PointlessArithmeticExpression")
	@Test
	public void test_Order() throws InterruptedException, IOException, URISyntaxException, DataFormatException
	{
		HttpClient client = new HttpClient("http://localhost:8080");

		try
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
			Response pushResponse = client.pushMetrics(metricBuilder);

			assertThat(pushResponse.getStatusCode(), equalTo(204));
			assertThat(pushResponse.getErrors().size(), equalTo(0));

			// Query metrics
			QueryBuilder builder = QueryBuilder.getInstance();
			builder.setStart(1, TimeUnit.MINUTES);

			QueryMetric query = builder.addMetric("orderMetric");
			query.setOrder(QueryMetric.Order.DESCENDING);

			QueryResponse response = client.query(builder);
			assertThat(response.getErrors().size(), equalTo(0));

			List<DataPoint> dataPoints = response.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints.size(), equalTo(5));

			assertThat(dataPoints.get(0).longValue(), equalTo(24L));
			assertThat(dataPoints.get(1).longValue(), equalTo(23L));
			assertThat(dataPoints.get(2).longValue(), equalTo(22L));
			assertThat(dataPoints.get(3).longValue(), equalTo(21L));
			assertThat(dataPoints.get(4).longValue(), equalTo(20L));
		}
		finally
		{
			client.shutdown();
		}
	}

	@Test
	public void test_customDataType() throws IOException, URISyntaxException, InterruptedException
	{
		HttpClient client = new HttpClient("http://localhost:8080");
		client.registerCustomDataType("complex", Complex.class);

		try
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric("metric1", "complex-number");
			metric1.addTag("host", "a");
			long timestamp1 = System.currentTimeMillis();
			metric1.addDataPoint(timestamp1, new Complex(4, 5));

			// Push Metrics
			Response response = client.pushMetrics(metricBuilder);

			assertThat(response.getStatusCode(), equalTo(204));

			// Query Metric
			QueryBuilder queryBuilder = QueryBuilder.getInstance();
			queryBuilder.setStart(2, TimeUnit.MINUTES);
			queryBuilder.addMetric("metric1");

			QueryResponse queryResponse = client.query(queryBuilder);
			assertThat(queryResponse.getQueries().size(), equalTo(1));
			assertThat(queryResponse.getQueries().get(0).getResults().size(), equalTo(1));

			List<DataPoint> dataPoints = queryResponse.getQueries().get(0).getResults().get(0).getDataPoints();
			assertThat(dataPoints, hasItem(new DataPoint(timestamp1, new Complex(4, 5))));
		}
		finally
		{
            client.shutdown();
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
