package org.kairosdb.client;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class DataPointTypeRegistry
{
	private Map<String, Class> customGroupTypes = new HashMap<String, Class>();

	public DataPointTypeRegistry()
	{
		customGroupTypes.put("number", Number.class);
		customGroupTypes.put("text", String.class);
	}

	public void registerCustomDataType(String groupType, Class dataPointClass)
	{
		checkArgument(!customGroupTypes.containsKey(groupType), "Type has already been registered");

		customGroupTypes.put(groupType, dataPointClass);
	}

	public Class getDataPointValueClass(String groupType)
	{
		Class valueClass = customGroupTypes.get(groupType);
		return valueClass == null? Number.class : valueClass;
	}


}
