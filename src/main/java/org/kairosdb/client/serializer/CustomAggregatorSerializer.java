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
package org.kairosdb.client.serializer;


import com.google.gson.*;
import org.kairosdb.client.builder.aggregator.CustomAggregator;

import java.lang.reflect.Type;

/**
 * Used by the JSON parser to serialize a custom aggregator.
 */
public class CustomAggregatorSerializer implements JsonSerializer<CustomAggregator>
{
	@Override
	public JsonElement serialize(CustomAggregator src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonParser parser = new JsonParser();
		return parser.parse(src.toJson());
	}
}