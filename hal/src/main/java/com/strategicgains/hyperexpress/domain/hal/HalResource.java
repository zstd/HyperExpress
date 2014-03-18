/*
    Copyright 2014, Strategic Gains, Inc.

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package com.strategicgains.hyperexpress.domain.hal;

import java.util.Collection;
import java.util.Map;

import com.strategicgains.hyperexpress.domain.LinkDefinition;
import com.strategicgains.hyperexpress.domain.Resource;

/**
 * @author toddf
 * @since Mar 18, 2014
 */
public interface HalResource
extends Resource
{
	public static final String REL_CURIES = "curies";

	public void addCurie(LinkDefinition LinkDefinition);
	public void addCuries(Collection<LinkDefinition> curries);
	public void embed(String rel, Object resource);
	public void embed(String rel, Collection<? extends Object> resources);
	Map<String, Object> getLinks();
	Map<String, Object> getEmbedded();
}