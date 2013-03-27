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

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.kairosdb.client.response.grouping.TagGroupResults;
import org.kairosdb.client.response.grouping.TimeGroupResults;
import org.kairosdb.client.response.grouping.ValueGroupResults;

@JsonTypeInfo(
		use = JsonTypeInfo.Id.NAME,
		include = JsonTypeInfo.As.PROPERTY,
		property = "name")
@JsonSubTypes({
		@JsonSubTypes.Type(value = ValueGroupResults.class, name="value"),
		@JsonSubTypes.Type(value = TagGroupResults.class, name="tag"),
		@JsonSubTypes.Type(value = TimeGroupResults.class, name="time")
})
public interface GroupResults
{

}