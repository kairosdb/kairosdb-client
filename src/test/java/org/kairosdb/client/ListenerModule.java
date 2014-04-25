package org.kairosdb.client;

import com.google.inject.Binder;
import com.google.inject.Module;

public class ListenerModule implements Module
{
	public static TestDataPointListener listener = new TestDataPointListener();

	@Override
	public void configure(Binder binder)
	{
		binder.bind(TestDataPointListener.class).toInstance(listener);
	}
}

