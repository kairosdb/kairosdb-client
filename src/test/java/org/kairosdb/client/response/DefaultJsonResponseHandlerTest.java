package org.kairosdb.client.response;

import com.google.common.reflect.TypeToken;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.*;

public class DefaultJsonResponseHandlerTest
{

	@Test
	public void test()
	{
//		DefaultJsonResponseHandler<String> handler = new DefaultJsonResponseHandler<String>(String.class);


		Type type = new TypeToken<List<QueryResult>>(){}.getType();
		DefaultJsonResponseHandler<List<QueryResult>> listDefaultJsonResponseHandler = new DefaultJsonResponseHandler<>(type);
	}
}