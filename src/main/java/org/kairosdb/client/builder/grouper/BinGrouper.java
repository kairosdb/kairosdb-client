package org.kairosdb.client.builder.grouper;

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.Grouper;

import java.util.Arrays;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

public class BinGrouper extends Grouper
{

	@SerializedName("bins")
	private Double[] bins;

	public BinGrouper(Double... bins)
	{
		super("bin");

		checkNotNull(bins, "bins cannot be null");
		checkArgument(bins.length > 0, "bins cannot be empty");
		this.bins = bins;
	}

	public BinGrouper(List<Double> bins)
	{
		super("bin");
		checkNotNull(bins, "bins cannot be null");
		checkArgument(bins.size() > 0, "bins cannot be empty");
		this.bins = (Double[]) bins.toArray();
	}

	public List<Double> getBins()
	{
		return Arrays.asList(bins);
	}

	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;

		BinGrouper that = (BinGrouper) o;
		return Arrays.equals(bins, that.bins);
	}

	@Override
	public int hashCode()
	{
		int result = super.hashCode();
		result = 31 * result + Arrays.hashCode(bins);
		return result;
	}
}
