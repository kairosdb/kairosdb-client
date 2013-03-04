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
				.addTag("customer", "Acme");
				.addDataPoint(1, 10)
				.addDataPoint(2, 30L)

		HttpClient client = new HttpClient("myServer", 9000);
		Response response = client.pushMetrics(builder);

## Querying

Querying metrics is similarly done by using the QueryBuilder class. A query requires a date range. The start date is
required, but the end date defaults to NOW if not specified. The metric(s) that you are querying for is also required.
Optionally, tags may be added to narrow down the search.

		QueryBuilder builder = QueryBuilder.getInstance();
        builder.setStart(2, TimeUnit.MONTHS)
                .setEnd(1, TimeUnit.MONTHS)
                .addMetric("metric1", "sum");

        HttpClient client = new HttpClient("localhost", 9000);
        QueryResponse response = client.query(builder);

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