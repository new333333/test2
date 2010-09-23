/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util.cache;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.SPropsUtil;

public class DefinitionCache {

	private static Log logger = LogFactory.getLog(DefinitionCache.class);
	private static ConcurrentMap<String,Document> cache = new ConcurrentHashMap<String,Document>();
	private static Boolean enabled;

	public static Document getDocument(Definition definition) {
		Document doc = null;
		if(enabled()) { // cache enabled
			if(definition.getId() != null) {
				// This cache only deals with persistent definition
				doc = cache.get(definition.getId());
				if(doc == null) {
					// Not yet cached
					doc = generateDocument(definition);
					if(doc != null) {
						// This cache only deals with valid definition
						cache.put(definition.getId(), doc);
						logger.debug("getDocument [" + definition.getId() + "] - cache miss, generated doc");
					}
					else {
						logger.debug("getDocument [" + definition.getId() + "] - cache miss, unable to generate doc");	
					}
				}
				else {
					logger.debug("getDocument [" + definition.getId() + "] - cache hit");	
				}
			}	
			else {
				logger.debug("getDocument - no id");	
			}
		}
		else { // cache disabled - falls back to old mechanism
			doc = generateDocument(definition);
			if(doc != null)
				logger.debug("getDocument [" + definition.getId() + "] - cache disabled, generated doc");
			else
				logger.debug("getDocument [" + definition.getId() + "] - cache disabled, unable to generate doc");
		}
		// Under error condition, the return value may be null, which seems bad at first. 
		// But I'm not changing this behavior since that's how the Definition.getDefinition() 
		// method was originally implemented (meaning this doesn't make anything worse
		// than before).
		return doc;
	}
		
	/*
	 * Return whether the specified document is identical (via reference identity) 
	 * to the document, if any, cached for the specified definition ID.
	 */
	public static boolean isCachedDocument(String definitionId, Document doc) {
		if(definitionId != null)
			return (doc == cache.get(definitionId));
		else
			return false;
	}
	
	public static void invalidate(String definitionId) {
		logger.debug("invalidate [" + definitionId + "]");
		if(definitionId != null)
			cache.remove(definitionId);
	}
		
	public static void clear() {
		logger.debug("clear");
		cache.clear();
	}
	
	private static Document generateDocument(Definition def) {
		logger.debug("generateDocument [" + def.getId() + "]");
		// Because getDocument() method of Definition class has protected 
		// visibility (so as to prevent application code from calling it
		// directly), we can't call it from here via usual means. Instead, 
		// we use this special facility to invoke the method.
		return (Document) InvokeUtil.invokeGetter(def, "document");		
	}
	
	private static boolean enabled() {
		if(enabled == null) {
			enabled = Boolean.valueOf(SPropsUtil.getBoolean("definition.cache.enabled", true));
		}
		return enabled.booleanValue();
	}
}
