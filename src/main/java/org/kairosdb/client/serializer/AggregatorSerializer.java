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

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import java.io.IOException;
import java.util.List;

public class AggregatorSerializer extends JsonSerializer<List<String>>
{
	@Override
	public void serialize(List<String> aggregators, JsonGenerator jgen, SerializerProvider serializerProvider)
			throws IOException
	{
		jgen.writeStartArray();

		for (String aggregatorJson : aggregators)
		{
			jgen.writeStartObject();
			jgen.writeRaw(aggregatorJson);
			jgen.writeEndObject();
		}
		jgen.writeEndArray();
	}
}