package org.kairosdb.client.builder.grouper;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.*;

public class BinGrouperTest
{

	@Test(expected = NullPointerException.class)
	public void test_constructor_nullBins_invalid()
	{
		new BinGrouper((Double[]) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_emptyBins_invalid()
	{
		new BinGrouper();
	}

	@Test(expected = NullPointerException.class)
	public void test_constructor_nullBinsList_invalid()
	{
		new BinGrouper((List<Double>) null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void test_constructor_emptyBinsList_invalid()
	{
		new BinGrouper(new ArrayList<Double>());
	}

	@Test
	public void test_constructor_name()
	{
		BinGrouper grouper = new BinGrouper(2.0, 3.0, 4.0);

		assertThat(grouper.getName(), equalTo("bin"));
	}

	@Test
	public void test_constructorList_name()
	{
		BinGrouper grouper = new BinGrouper(List.of(2.1, 3.1, 4.1));

		assertThat(grouper.getName(), equalTo("bin"));
	}

	@Test
	public void test_constructor_bins()
	{
		BinGrouper grouper = new BinGrouper(2.1, 3.1, 4.1);

		assertThat(grouper.getBins(), hasItems(2.1, 3.1, 4.1));
	}

	@Test
	public void test_constructor_bins_from_list()
	{
		BinGrouper grouper = new BinGrouper(List.of(2.1, 3.1, 4.1));

		assertThat(grouper.getBins(), hasItems(2.1, 3.1, 4.1));
	}
}