package org.kairosdb.client.builder.grouper;

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.builder.Grouper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;
import static org.kairosdb.client.util.Preconditions.checkArgument;


public class BinGrouper extends Grouper
{

	@SerializedName("bins")
	private List<Double> bins;

	public BinGrouper(Double... bins)
	{
		super("bin");

		requireNonNull(bins, "bins cannot be null");
		checkArgument(bins.length > 0, "bins cannot be empty");
		this.bins = Arrays.asList(bins);
	}

	public BinGrouper(List<Double> bins)
	{
		super("bin");
		requireNonNull(bins, "bins cannot be null");
		checkArgument(bins.size() > 0, "bins cannot be empty");
		this.bins = bins;
	}

	public List<Double> getBins()
	{
		return bins;
	}


	@Override
	public boolean equals(Object o)
	{
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		if (!super.equals(o)) return false;
		BinGrouper that = (BinGrouper) o;
		return Objects.equals(bins, that.bins);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(super.hashCode(), bins);
	}
}
