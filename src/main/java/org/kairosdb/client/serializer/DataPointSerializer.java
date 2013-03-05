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
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.builder.DoubleDataPoint;
import org.kairosdb.client.builder.LongDataPoint;

import java.io.IOException;
import java.util.List;

public class DataPointSerializer extends JsonSerializer<List<DataPoint>>
{
	/**
	 * Method that can be called to ask implementation to serialize
	 * values of type this serializer handles.
	 *
	 * @param value    Value to serialize; can <b>not</b> be null.
	 * @param jgen     Generator used to output resulting Json content
	 * @param provider Provider that can be used to get serializers for
	 *                 serializing Objects value contains, if any.
	 */
	@Override
	public void serialize(List<DataPoint> value, JsonGenerator jgen, SerializerProvider provider) throws IOException
	{

		jgen.writeStartArray();
		for (DataPoint dataPoint : value)
		{
			jgen.writeStartArray();
			jgen.writeNumber(dataPoint.getTimestamp());

			if (dataPoint instanceof LongDataPoint)
				jgen.writeNumber(((LongDataPoint)dataPoint).getValue());
			else
				jgen.writeNumber(((DoubleDataPoint)dataPoint).getValue());
			jgen.writeEndArray();
		}
		jgen.writeEndArray();
	}
}