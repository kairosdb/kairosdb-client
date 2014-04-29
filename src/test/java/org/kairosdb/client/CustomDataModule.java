package org.kairosdb.client;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class CustomDataModule implements Module
{
	@Override
	public void configure(Binder binder)
	{
		binder.bind(ComplexNumberDataPointFactory.class).in(Scopes.SINGLETON);
	}
}
