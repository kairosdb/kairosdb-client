/*
 * Copyright 2013 Proofpoint Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.kairosdb.client.response;

import java.util.Collections;
import java.util.List;

/**
 * List of errors returned by KairosDB.
 */
public class ErrorResponse
{
	private List<String> errors;

	@SuppressWarnings("unused")
	public ErrorResponse(List<String> errors)
	{
		this.errors = errors;
	}

	@SuppressWarnings("unused")
	public ErrorResponse(String error)
	{
		errors = Collections.singletonList(error);
	}

	@SuppressWarnings("unused")
	public List<String> getErrors()
	{
		return (errors);
	}

	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder("Errors: ");
		for (String error : errors)
		{
			builder.append(error).append("\n");
		}

		return builder.toString();
	}
}