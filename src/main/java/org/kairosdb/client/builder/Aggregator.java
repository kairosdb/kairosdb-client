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

/**
 * An aggregator manipulates data points. For example, a sum aggregator would add data point together.
 */
public interface Aggregator
{
	/**
	 * Returns the aggregator's name.
	 * @return aggregator name
	 */
	String getName();

	/**
	 * Returns the aggregator serialized to JSON.
	 *
	 * @return JSON serialization of the aggregator
	 */
	public String toJson();
}