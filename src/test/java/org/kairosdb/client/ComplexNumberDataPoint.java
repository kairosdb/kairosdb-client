package org.kairosdb.client;


import com.google.inject.Inject;
import org.json.JSONException;
import org.json.JSONWriter;
import org.kairosdb.core.DataPoint;

import java.io.DataOutput;
import java.io.IOException;

public class ComplexNumberDataPoint implements DataPoint
{
	private long real;
	private long imaginary;
	private long timestamp;

	@Inject
	public ComplexNumberDataPoint(long timestamp, long real, long imaginary)
	{
		this.real = real;
		this.imaginary = imaginary;
		this.timestamp = timestamp;
	}

	public long getReal()
	{
		return real;
	}

	public long getImaginary()
	{
		return imaginary;
	}

	@Override
	public long getTimestamp()
	{
		return timestamp;
	}

	@Override
	public void writeValueToBuffer(DataOutput buffer) throws IOException
	{
		buffer.writeLong(real);
		buffer.writeLong(imaginary);
	}

	@Override
	public void writeValueToJson(JSONWriter writer) throws JSONException
	{
		writer.object().key("real").value(real).key("imaginary").value(imaginary).endObject();
	}

	@Override
	public String getApiDataType()
	{
		return null;
	}

	@Override
	public String getDataStoreDataType()
	{
		return "jsabin-complex";
	}

	@Override
	public boolean isLong()
	{
		return false;
	}

	@Override
	public long getLongValue()
	{
		return 0;
	}

	@Override
	public boolean isDouble()
	{
		return false;
	}

	@Override
	public double getDoubleValue()
	{
		return 0;
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}

		ComplexNumberDataPoint that = (ComplexNumberDataPoint) o;

		return imaginary == that.imaginary && real == that.real && timestamp == that.timestamp;

	}

	@Override
	public int hashCode()
	{
		int result = (int) (real ^ (real >>> 32));
		result = 31 * result + (int) (imaginary ^ (imaginary >>> 32));
		result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}
}
