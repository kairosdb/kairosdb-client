package org.kairosdb.client.response.grouping;

import com.google.gson.annotations.SerializedName;
import org.kairosdb.client.response.GroupResult;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Grouping by bins.
 */
public class BinGroupResult extends GroupResult
{
	@SerializedName("bins")
	private List<Double> bins;

	@SerializedName("group")
	private Map<String, Integer> group;

	public BinGroupResult(List<Double> bins, Map<String, Integer> group)
	{
		super("bin");
		this.bins = checkNotNull(bins);
		this.group = checkNotNull(group);
	}

	/**
	 * List of bins that the results were grouped by.
	 *
	 * @return bins bins that results were grouped by
	 */
	public List<Double> getBins()
	{
		return bins;
	}

	/**
	 * Returns the bin number.
	 *
	 * @return bin number
	 */
	public int getBinNumber()
	{
		return group.get("bin_number");
	}
}