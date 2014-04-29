package org.kairosdb.client;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.DataPointFactory;

import java.io.DataInput;
import java.io.IOException;

public class ComplexNumberDataPointFactory implements DataPointFactory
{
	@Override
	public String getDataStoreType()
	{
		return "jsabin-complex";
	}

	@Override
	public String getGroupType()
	{
		return "complex";
	}

	@Override
	public DataPoint getDataPoint(long timestamp, JsonElement json) throws IOException
	{
		JsonObject complex = json.getAsJsonObject();
		return new ComplexNumberDataPoint(timestamp, complex.get("real").getAsLong(), complex.get("imaginary").getAsLong());
	}

	@Override
	public DataPoint getDataPoint(long timestamp, DataInput buffer) throws IOException
	{
		return new ComplexNumberDataPoint(timestamp, buffer.readLong(), buffer.readLong());
	}
}
