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
package com.strategicgains.hyperexpress;

import java.util.List;

import com.strategicgains.hyperexpress.domain.Link;
import com.strategicgains.hyperexpress.domain.Resource;
import com.strategicgains.hyperexpress.fluent.LinkResolver;
import com.strategicgains.hyperexpress.fluent.RelationshipDefinition;
import com.strategicgains.hyperexpress.fluent.TokenBinder;
import com.strategicgains.hyperexpress.fluent.TokenResolver;

/**
 * A Singleton object to manage creation of Link and Resource instances.
 * 
 * @author toddf
 * @since May 5, 2014
 */
public class HyperExpress
{
	private static final HyperExpress INSTANCE = new HyperExpress();

	private DefaultResourceFactory resourceFactory;
	private RelationshipDefinition relationshipBuilder;
	private LinkResolver linkResolver;
	private ThreadLocal<TokenResolver> tokenResolver;

	/*
	 * Private to prevent external instantiation.
	 */
	private HyperExpress()
	{
		resourceFactory = new DefaultResourceFactory();
		relationshipBuilder = new RelationshipDefinition();
		linkResolver = relationshipBuilder.createResolver();
		tokenResolver = new ThreadLocal<TokenResolver>();
	}


	// SECTION: STATIC - PUBLIC METHODS

    public static void registerResourceFactory(ResourceFactoryStrategy factoryStrategy, String contentType)
    {
    	INSTANCE._registerResourceFactory(factoryStrategy, contentType);
    }

	public static Resource createResource(Object object, String contentType)
	{
		return INSTANCE._createResource(object, contentType);
	}

	public static Resource createCollectionResource(Class<?> componentType, String contentType)
	{
		return INSTANCE._createCollectionResource(componentType, contentType);
	}

	/**
	 * The HyperExpress to define relationships between resource types and namespaces.
	 * 
	 * @return the RelationshipDefinition contained in this singleton.
	 */
	public static RelationshipDefinition defineRelationships()
	{
		return INSTANCE.relationshipBuilder;
	}

	public static TokenResolver bindToken(String token, String value)
	{
		return INSTANCE._bindToken(token, value);
	}

//	public static HyperExpress bindToken(String token, UUID uuid)
//	{
//		return bindToken(token, Identifiers.UUID.format(uuid));
//		return null;
//	}

//	public static HyperExpress bindToken(String token, Identifier identifier)
//	{
//		return bindToken(token, Identifiers.UUID.format(identifier));
//	}

	public static void bindTokensFor(Object object)
	{
		INSTANCE._bindTokensFor(object);
	}
	public static void collectionTokenBinder(TokenBinder callback)
	{
		INSTANCE._addTokenBinder(callback);
	}

	public static void clearTokenBindings()
	{
		INSTANCE._clearTokenBindings();
	}


	// SECTION: PRIVATE INSTANCE METHODS

	/**
	 * @param factoryStrategy
	 * @param contentType
	 */
    private void _registerResourceFactory(ResourceFactoryStrategy factoryStrategy, String contentType)
    {
    	resourceFactory.addStrategy(factoryStrategy, contentType);
    }

	/**
	 * @param object
	 * @param contentType
	 * @return
	 */
    private Resource _createResource(Object object, String contentType)
    {
    	Resource r = resourceFactory.createResource(object, contentType);
    	List<Link> links = linkResolver.resolve(object, _getTokenResolver());
    	r.addLinks(links);
    	r.addNamespaces(linkResolver.getNamespaces());
	    return r;
    }

	/**
	 * @param childType
	 * @param contentType
	 * @return
	 */
    private Resource _createCollectionResource(Class<?> componentType, String contentType)
    {
    	Resource r = resourceFactory.createResource(null, contentType);
    	r.addLinks(linkResolver.resolveCollectionOf(componentType, _getTokenResolver()));
    	r.addNamespaces(linkResolver.getNamespaces());
	    return r;
    }

	private TokenResolver _bindToken(String token, String value)
    {
		TokenResolver tr = _getTokenResolver();

		if (tr == null)
		{
			tr = new TokenResolver();
			tokenResolver.set(tr);
		}

		return tr.bindToken(token, value);
    }

	private TokenResolver _addTokenBinder(TokenBinder callback)
    {
		TokenResolver tr = _getTokenResolver();

		if (tr == null)
		{
			tr = new TokenResolver();
			tokenResolver.set(tr);
		}

		return tr.callback(callback);
    }

	private void _clearTokenBindings()
	{
		TokenResolver tr = _getTokenResolver();

		if (tr != null)
		{
			tr.clear();
			tokenResolver.remove();
		}
	}

	private TokenResolver _getTokenResolver()
	{
		return tokenResolver.get();
	}

	private void _bindTokensFor(Object object)
	{
		TokenResolver tr = _getTokenResolver();

		if (tr != null)
		{
			tr.callTokenBinders(object);
		}
	}
}
