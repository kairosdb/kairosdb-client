KairosDB Client
================
[![Build Status](https://travis-ci.org/kairosdb/kairosdb-client.svg?branch=develop)](https://travis-ci.org/kairosdb/kairosdb-client)

The KairosDB client is a Java library that makes sending metrics and querying the KairosDB server simple.
The HttpClient class is used to push metrics or query the KairosDB server. The library uses the builder pattern to
simplify the task of creating the JSON that is used by the client. If an error occurs an 
UnexpectedResponseException is thrown which contains the HTTP response code.

## Sending Metrics

Sending metrics is done by using the MetricBuilder. You simply add a metric, the tags associated with the metric, and
the data points.


	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		MetricBuilder builder = MetricBuilder.getInstance();
		builder.addMetric("metric1")
				.addTag("host", "server1")
				.addTag("customer", "Acme")
				.addDataPoint(System.currentTimeMillis(), 10)
				.addDataPoint(System.currentTimeMillis(), 30L);
		client.pushMetrics(builder);
	}

## Querying Data Points

Querying data points is similarly done by using the QueryBuilder class. A query requires a date range. The start date is
required, but the end date defaults to NOW if not specified. The metric(s) that you are querying for is also required.
Optionally, tags may be added to narrow down the search.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(2, TimeUnit.MONTHS)
				.setEnd(1, TimeUnit.MONTHS)
				.addMetric("metric1")
				.addAggregator(AggregatorFactory
				.createAverageAggregator(5, TimeUnit.MINUTES));
		QueryResponse response = client.query(builder);
	}
  
## Querying Metric Tags

Querying metric tags is done by using the QueryTagBuilder class. A query requires a date range. The start date is
required, but the end date defaults to NOW if not specified. The metric(s) that you are querying for is also required.
Optionally, tags may be added to narrow down the search.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		QueryTagBuilder builder = QueryTagBuilder.getInstance();
		builder.setStart(2, TimeUnit.MONTHS)
				.setEnd(1, TimeUnit.MONTHS)
				.addMetric("metric1");
		QueryTagResponse response = client.queryTags(builder);
	}

## Querying Metric Names

You can get a list of all metric names in KairosDB.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		List<String> metricNames = client.getMetricNames();
		for (String metricName : metricNames)
		{
			System.out.println(metricName);
		}
	}
	
## Delete a Metric

You can delete a metric and all its data.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		client.deleteMetric("myMetric");
	}
	
## Delete Data Points

Or delete a set of data point for a given metric.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		QueryBuilder builder = QueryBuilder.getInstance();
		builder.setStart(2, TimeUnit.MONTHS)
				.setEnd(1, TimeUnit.MONTHS)
				.addMetric("metric1");
		client.delete(builder);
	}

	
## Check Server Health

You can check the server health by calling the getStatus() or getStatusCheck() methods. Status check returns 204 if healthy
and 500 if not. The getStatus() method returns JSON with a list of checks and their status.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		int statusCode = client.getStatusCheck();

		List<String> status = client.getStatus();
	}
	
## Get the version of KairosDB

You get get the version string for the server.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		String version = client.getVersion();
	}


## Create Roll-up Task

You can create a roll-up task using the RollupBuilder.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		RollupBuilder builder = RollupBuilder.getInstance("Metric1_rollupTask", new RelativeTime(1, TimeUnit.DAYS));
		Rollup rollup1 = builder.addRollup("metric1_rollup");
		QueryBuilder builder1 = rollup1.addQuery();
		builder1.setStart(1, TimeUnit.HOURS);
		builder1.addMetric("metric1")
				.addAggregator(AggregatorFactory.createMaxAggregator(1, TimeUnit.MINUTES));
		RollupTask rollupTask = client.createRollupTask(builder);
	}

## Get Roll-up Task

Or get a roll-up task

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		RollupTask rollupTask = client.getRollupTask("ddafbb87-3063-4013-8e98-da2ff8671caf");
	}


## Delete Roll-up task

Or delete a roll-up task

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		client.deleteRollupTask("ddafbb87-3063-4013-8e98-da2ff8671caf");
	}

## Custom Data Types
Starting with version 0.9.4 of KairosDB, you can store more than just numbers as values. This version of the client
has been modified to support custom data types. Note that custom types is only supported by the HTTP client.
The Telnet protocol for KairosDB does not support custom types.

Longs, doubles, and Strings are now supported by default. If,
however, you want to store and query for other data types, additional work is required.

You must first understand the different custom type values you must specify on the client; group type and registered type.
The group type is used for JSON serialization/de-serialization. The registered type is used by the server to identify which
DataPointFactory it will use to serialize/de-serialize the data to the data store.

Next, you must modify KairosDB to handle the new data type.

Next, you must create an object for the new data type. This is simply a POJO that contains fields for the new type.
For example, if you wanted to store complex numbers as your new data type you would create a ComplexNumber class. The
client uses GSON to serialize/de-serialize this class.

    public class ComplexNumber
    {
        private double real;
        private double imaginary;

        public ComplexNumber(double real, double imaginary)
        {
            this.real = real;
            this.imaginary = imaginary;
        }
    }

This Class must then be registered with the client by its group type. In this example, "complex" is the name of the group type
registered in KairosDB. Then, when you add the metric you must specify the registered type. In this example, "complex-number"
is the registered type in KairosDB.


	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		client.registerCustomDataType("complex", ComplexNumber.class); // "complex" is the group type

		MetricBuilder metricBuilder = MetricBuilder.getInstance();
		Metric metric = metricBuilder.addMetric("metric1", "complex-number");  // "complex-number" is the registered type
		metric.addTag("host", "myHost");
		metric.addDataPoint(System.currentTimeMillis(), new ComplexNumber(2.3, 3.4));
		metric.addDataPoint(System.currentTimeMillis(), new ComplexNumber(1.1, 5));

		client.pushMetrics(metricBuilder);
	}


Last, you must cast to your new type following a query for a metric.

	try(HttpClient client = new HttpClient("http://localhost:8080"))
	{
		QueryBuilder queryBuilder = QueryBuilder.getInstance();
		queryBuilder.addMetric("metric1");
		queryBuilder.setStart(1, TimeUnit.HOURS);

		QueryResponse response = client.query(queryBuilder);
		List<DataPoint> dataPoints = response.getQueries().get(0).getResults().get(0).getDataPoints();

		for (DataPoint dataPoint : dataPoints)
		{
			ComplexNumber complex = (ComplexNumber) dataPoint.getValue();
			System.out.println(complex.real + " + " + complex.imaginary + "i");
		}
	}

## KairosDB compatibility

Version 2.3.0 of the client was tested with KairosDB version 1.2.1-1.

## Contributions

We want your contributions but please do pull requests on the develop branch and not the master.

## Copyright and License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.