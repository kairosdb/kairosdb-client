package org.kairosdb.client.deserializer;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.kairosdb.client.Client;
import org.kairosdb.client.builder.DataPoint;
import org.kairosdb.client.response.GroupResult;
import org.kairosdb.client.response.Results;
import org.kairosdb.client.response.grouping.DefaultGroupResult;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class ResultsDeserializer implements JsonDeserializer<Results>
{
	private Client client;

	public ResultsDeserializer(Client client)
	{
		this.client = checkNotNull(client);
	}

	@Override
	public Results deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException
	{
		// Name
		String name = json.getAsJsonObject().get("name").getAsString();
		checkState(name != null, "Missing name");

		// Tags
		JsonElement tagsElement = json.getAsJsonObject().get("tags");
		Map<String, List<String>> tags = context.deserialize(tagsElement, new TypeToken<Map<String, List<String>>>(){}.getType());

		// Group_By
		JsonElement group_by = json.getAsJsonObject().get("group_by");
		List<GroupResult> groupResults = context.deserialize(group_by, new TypeToken<List<GroupResult>>(){}.getType());

		String type = null;
		for (GroupResult groupResult : groupResults)
		{
			if (groupResult.getName().equals("type"))
			{
				type = ((DefaultGroupResult)groupResult).getType();
			}
		}

		checkState(type != null, "Missing type");

		// Data points
		final Class dataPointValueClass = client.getDataPointValueClass(type);
		checkState(dataPointValueClass != null, "type: " + type + " is not registered to a custom data type.");

		List<DataPoint> dataPoints = new ArrayList<DataPoint>();
		JsonArray array = (JsonArray) json.getAsJsonObject().get("values");
		for (JsonElement element : array)
		{
			JsonArray pair = element.getAsJsonArray();
			dataPoints.add(new DataPoint(pair.get(0).getAsLong(), context.<DataPoint>deserialize(pair.get(1), dataPointValueClass)));
		}

		return new Results(name, tags, dataPoints, groupResults);
	}
}
