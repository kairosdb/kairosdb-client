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
import org.kairosdb.client.builder.DataPoint;

import java.lang.reflect.Type;

/**
 * Used by the JSON parser to serialize a DataPoint.
 */
public class DataPointSerializer implements JsonSerializer<DataPoint>
{
	@Override
	public JsonElement serialize(DataPoint src, Type typeOfSrc, JsonSerializationContext context)
	{
		JsonArray array = new JsonArray();
		array.add(new JsonPrimitive(src.getTimestamp()));
		array.add(context.serialize(src.getValue()));
		return array;
	}
}
