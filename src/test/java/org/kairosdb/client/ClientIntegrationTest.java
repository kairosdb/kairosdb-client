package org.kairosdb.client;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kairosdb.client.builder.*;
import org.kairosdb.client.response.GetResponse;
import org.kairosdb.client.response.QueryResponse;
import org.kairosdb.client.response.Response;
import org.kairosdb.core.Main;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.core.exception.KairosDBException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class ClientIntegrationTest
{
	public static final String HTTP_METRIC_NAME_1 = "httpMetric1";
	public static final String HTTP_METRIC_NAME_2 = "httpMetric2";
	public static final String HTTP_TAG_NAME_1 = "httpTag1";
	public static final String HTTP_TAG_NAME_2 = "httpTag2";
	public static final String HTTP_TAG_VALUE_1 = "httpTagValue1";
	public static final String HTTP_TAG_VALUE_2 = "httpTagValue2";

	public static final String TELNET_METRIC_NAME_1 = "telnetMetric1";
	public static final String TELNET_METRIC_NAME_2 = "telnetMetric2";
	public static final String TELNET_TAG_NAME_1 = "telnetTag1";
	public static final String TELNET_TAG_NAME_2 = "telnetTag2";
	public static final String TELNET_TAG_VALUE_1 = "telnetTag1";
	public static final String TELNET_TAG_VALUE_2 = "telnetTag2";

	private static InMemoryKairosServer kairos;

	@BeforeClass
	public static void setupClass() throws InterruptedException
	{
		kairos = new InMemoryKairosServer();
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
	public void test_telnetClient() throws IOException
	{
		TelnetClient client = new TelnetClient("localhost", 4242);

		try
		{
			MetricBuilder metricBuilder = MetricBuilder.getInstance();

			Metric metric1 = metricBuilder.addMetric(TELNET_METRIC_NAME_1);
			metric1.addTag(TELNET_TAG_NAME_1, TELNET_TAG_NAME_1);
			long timestamp1 = System.currentTimeMillis();
			metric1.addDataPoint(timestamp1, 20);

			Metric metric2 = metricBuilder.addMetric(TELNET_METRIC_NAME_2);
			metric2.addTag(TELNET_TAG_NAME_2, TELNET_TAG_VALUE_2);
			long timestamp2 = System.currentTimeMillis();
			metric2.addDataPoint(timestamp2, 40);

			client.pushMetrics(metricBuilder);
		}
		finally
		{
			client.shutdown();
		}

		// todo query and verify that metrics were added
	}

	@Test
	public void test_httpClient() throws InterruptedException, IOException, URISyntaxException
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
			assertThat(dataPoints, hasItem((DataPoint)new LongDataPoint(timestamp1, 20L)));
		}
		finally
		{
			client.shutdown();
		}
	}


	public static class InMemoryKairosServer extends Thread
	{
		private Main kairos;
		private boolean started;

		@Override
		public void run()
		{
			try
			{
				kairos = new Main(null);
				kairos.startServices();
				setStarted();
			}
			catch (KairosDBException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		public synchronized boolean isStarted()
		{
			return started;
		}

		private synchronized void setStarted()
		{
			started = true;
		}

		public void shutdown() throws InterruptedException, DatastoreException
		{
			kairos.stopServices();
		}
	}
}
