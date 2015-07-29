KairosDB Client
================

The KairosDB client is a Java library that makes sending metrics and querying the KairosDB server simple.
The HttpClient class is used to push metrics or query the KairosDB server. The library uses the builder pattern to
simplify the task of creating the JSON that is used by the client.

## Sending Metrics

Sending metrics is done by using the MetricBuilder. You simply add a metric, the tags associated with the metric, and
the data points.


	MetricBuilder builder = MetricBuilder.getInstance();
	builder.addMetric("metric1")
			.addTag("host", "server1")
			.addTag("customer", "Acme")
			.addDataPoint(System.currentTimeMillis(), 10)
			.addDataPoint(System.currentTimeMillis(), 30L);
    HttpClient client = new HttpClient("http://localhost:8080");
	Response response = client.pushMetrics(builder);
	client.shutdown();

## Querying Data Points

Querying data points is similarly done by using the QueryBuilder class. A query requires a date range. The start date is
required, but the end date defaults to NOW if not specified. The metric(s) that you are querying for is also required.
Optionally, tags may be added to narrow down the search.

	QueryBuilder builder = QueryBuilder.getInstance();
    builder.setStart(2, TimeUnit.MONTHS)
           .setEnd(1, TimeUnit.MONTHS)
           .addMetric("metric1")
           .addAggregator(AggregatorFactory.createSumAggregator(5, TimeUnit.MINUTES));
    HttpClient client = new HttpClient("http://localhost:8080");
    QueryResponse response = client.query(builder);
   	client.shutdown();

## Querying Metric Names

You can get a list of all metric names in KairosDB.

	HttpClient client = new HttpClient("http://localhost:8080");
	GetResponse response = client.getMetricNames();

	System.out.println("Response Code =" + response.getStatusCode());
	for (String name : response.getResults())
    {
    	System.out.println(name);
    }
  	client.shutdown();

## Querying Tag Names
Similarly you can get a list of all tag names in KairosDB.

	HttpClient client = new HttpClient("http://localhost:8080");
	GetResponse response = client.getTagNames();

	System.out.println("response=" + response.getStatusCode());
	for (String name : response.getResults())
	{
		System.out.println(name);
	}
	client.shutdown();

## Querying Tag Values
And a list of all tag values.

	HttpClient client = new HttpClient("http://localhost:8080");
	GetResponse response = client.getTagValues();

	System.out.println("response=" + response.getStatusCode());
	for (String name : response.getResults())
    {
    	System.out.println(name);
    }
   	client.shutdown();


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


	HttpClient client = new HttpClient("http://localhost:8080");
	client.registerCustomDataType("complex", ComplexNumber.class); // "complex" is the group type

	MetricBuilder metricBuilder = MetricBuilder.getInstance();
	Metric metric = metricBuilder.addMetric("metric1", "complex-number");  // "complex-number" is the registered type
	metric.addTag("host", "myHost");
	metric.addDataPoint(System.currentTimeMillis(), new ComplexNumber(2.3, 3.4));
	metric.addDataPoint(System.currentTimeMillis(), new ComplexNumber(1.1, 5));

	client.pushMetrics(metricBuilder);


Last, you must cast to your new type following a query for a metric.

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


## Copyright and License

Copyright 2013 Proofpoint Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

[http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
