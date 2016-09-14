package org.kairosdb.client.deserializer;

import com.google.gson.*;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.kairosdb.client.builder.RelativeTime;
import org.kairosdb.client.builder.TimeUnit;
import org.kairosdb.client.response.GroupResult;
import org.kairosdb.client.response.grouping.*;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertThat;

public class GroupByDeserializerTest
{
	private Gson gson;

	@Before
	public void setup()
	{
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(GroupResult.class, new GroupByDeserializer());
		gson = builder.create();
	}

	@Test(expected = JsonParseException.class)
	public void test_missingName_invalid()
	{
		JsonObject json = new JsonObject();
		json.add("value", new JsonPrimitive(5));

		new GroupByDeserializer().deserialize(json, null, null);
	}

	@Test
	public void test_tag_grouper()
	{
		TagGroupResult result = (TagGroupResult) gson.fromJson("{'name': 'tag', 'tags': ['host'], 'group': {'host': 'server1'}}", GroupResult.class);

		assertThat(result, instanceOf(TagGroupResult.class));
		assertThat(result.getName(), equalTo("tag"));
		assertThat(result.getTags(), hasItems("host"));
		assertThat(result.getGroup().get("host"), equalTo("server1"));
	}

	@Test
	public void test_time_grouper()
	{
		String json = "{'name':'time','range_size':{'value':1,'unit':'MILLISECONDS'},'group_count':5,'group':{'group_number':2}}";

		TimeGroupResult result = (TimeGroupResult) gson.fromJson(json, GroupResult.class);

		assertThat(result, instanceOf(TimeGroupResult.class));
		assertThat(result.getName(), equalTo("time"));
		assertThat(result.getGroupCount(), equalTo(5));
		assertThat(result.getGroup().getGroupNumber(), equalTo(2));
		assertThat(result.getRangeSize(), equalTo(new RelativeTime(1, TimeUnit.MILLISECONDS)));
	}

	@Test
	public void test_value_grouper()
	{
		String json = "{'name':'value','range_size':100,'group':{'group_number':0}}";

		ValueGroupResult result = (ValueGroupResult) gson.fromJson(json, GroupResult.class);

		assertThat(result, instanceOf(ValueGroupResult.class));
		assertThat(result.getName(), equalTo("value"));
		assertThat(result.getGroup().getGroupNumber(), equalTo(0));
		assertThat(result.getRangeSize(), equalTo(100));
	}

	@Test
	public void test_bin_grouper()
	{
		String json =  "{'name': 'bin', 'bins': [5, 10, 20],'group': {'bin_number': 1}}";
		BinGroupResult result = (BinGroupResult) gson.fromJson(json, GroupResult.class);

		assertThat(result, instanceOf(BinGroupResult.class));
		assertThat(result.getName(), equalTo("bin"));
		assertThat(result.getBinNumber(), equalTo(1));
		assertThat(result.getBins(), hasItems(5.0, 10.0, 20.0));
	}

	@Test
	public void test_unknown_grouper()
	{
		CustomGroupResult result = (CustomGroupResult) gson.fromJson("{'name': 'bogus', 'value': 5}", GroupResult.class);

		assertThat(result, instanceOf(CustomGroupResult.class));
		assertThat(result.getName(), equalTo("bogus"));
		assertThat(result.getProperties().get("value"), CoreMatchers.<Object>equalTo(5.0));
	}
}
