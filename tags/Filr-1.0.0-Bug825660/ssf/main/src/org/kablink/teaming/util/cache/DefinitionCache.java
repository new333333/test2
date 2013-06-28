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
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.NoDefinitionByTheIdException;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;

public class DefinitionCache {

	private static Log logger = LogFactory.getLog(DefinitionCache.class);
	private static Boolean enabled;
	private static Long resetIntervalInSecond;
	private static String hibernateCacheProvider;
	private static Long freshnessSeconds;
	
	private static ConcurrentMap<String,CacheEntry> cache = new ConcurrentHashMap<String,CacheEntry>();
	// Guarded by this class.
	private static long lastResetTime = System.currentTimeMillis();

	public static Document getDocumentWithDefinition(Definition definition) {
		// The fact that the caller is looking up a cache entry by definition
		// domain object indicates that the caller has already paid the price
		// of constructing the intermediary form of the data (= definition 
		// domain object). The more we can switch the caller of this to using
		// getDocumentWithId method instead, the more efficient it would be.
		if(isEnabled()) { // cache enabled
			if(getResetIntervalInSecond() > 0) {
				// Periodic reset is enabled.
				checkAndReset();
			}
			return doGetDocumentWithDefinition(definition);
		}
		else { // cache disabled - falls back to old mechanism
			return getDocumentWithoutCache(definition);
		}
	}
	
	public static Document getDocumentWithId(String definitionId) throws NoDefinitionByTheIdException {
		// The fact that the caller is looking up a cache entry by definition ID
		// is a good sign that the caller has probably skipped the intermediary
		// form of the data (= definition domain object).
		if(definitionId == null)
			throw new IllegalArgumentException("definition ID must be specified");
		
		if(isEnabled()) { // cache enabled
			if(getResetIntervalInSecond() > 0) {
				// Periodic reset is enabled.
				checkAndReset();
			}
			return doGetDocumentWithId(definitionId);
		}
		else { // cache disabled - falls back to old mechanism
			if(logger.isDebugEnabled())
				logger.debug("getDocumentWithId [" + definitionId + "] - cache disabled, loading definition object");
			Definition definition = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
			return getDocumentWithoutCache(definition);
		}
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
		if(logger.isDebugEnabled())
			logger.debug("invalidate [" + definitionId + "]");
		if(definitionId != null)
			cache.remove(definitionId);
	}
		
	public static void clear() {
		logger.debug("clear");
		cache.clear();
	}
	
	public static int size() {
		return cache.size();
	}
	
	private static Document doGetDocumentWithId(String definitionId) throws NoDefinitionByTheIdException {
		if(getHibernateCacheProvider().equals("memcached")) {
			// This signals that memcached is in use instead of ehcache.
			// In this case, we need to rely on the latest modification time of the definition
			// object in order to determine if cache entry is stale or not. So, we need to
			// load the definition and call the other method.
			CacheEntry ce = cache.get(definitionId);
			if(ce != null) { // Cache entry found.
				// To reduce the cost associated with repeatedly fetching definition object
				// from Hibernate's second-level cache every time application asks for 
				// definition document (which happens VERY frequently and repeatedly in our 
				// application), we define what is acceptable level of freshness required for 
				// definition cache and use it to determine whether it is necessary to check
				// the source for cache entry staleness.
				// Note: In reality, however, this additional smart may not make as much
				// difference due to Hibernate's session cache.
				long currentTime = System.currentTimeMillis();
				if(currentTime - ce.lastUpdateTime > getFreshnessSeconds()*1000L) {
					// Allowed freshness threshold exceeded - We have to check definition object
					if(logger.isDebugEnabled())
						logger.debug("doGetDocumentWithId [" + definitionId + "] - cache hit (not fresh enough), " + String.valueOf((currentTime-ce.lastUpdateTime)/1000) + " seconds passed since last update, loading definition object");
					return doGetDocumentWithDefinition(getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId()));
				}
				else {
					// Allowed freshness threshold hasn't exceeded. So, this cache entry is still good.
					if(logger.isDebugEnabled())
						logger.debug("doGetDocumentWithId [" + definitionId + "] - cache hit (fresh)");					
					return ce.doc;
				}
			}
			else { // Cache entry not found. 
				if(logger.isDebugEnabled())
					logger.debug("doGetDocumentWithId [" + definitionId + "] - cache miss, loading definition object");
				return doGetDocumentWithDefinition(getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId()));				
			}
		}
		else {
			// ehcache is in use. In this case, cache validation is performed asynchronously
			// by a listener using a hook in ehcache implementation. So, we don't have to 
			// check the latest modification time of the definition, which means that we
			// do not have to load it. 
			CacheEntry ce = null;
			Document doc;
			ce = cache.get(definitionId);
			if(ce == null) {
				// Not in cache
				Definition definition = getCoreDao().loadDefinition(definitionId, RequestContextHolder.getRequestContext().getZoneId());
				doc = generateDocument(definition);
				if(doc != null) {
					// This cache only deals with valid definition
					cache.put(definitionId, new CacheEntry(doc, getLastModTime(definition)));
					if(logger.isDebugEnabled())
						logger.debug("doGetDocumentWithId [" + definitionId + "] - cache miss, loaded definition, generated doc");
				}
				else {
					if(logger.isDebugEnabled())
						logger.debug("doGetDocumentWithId [" + definitionId + "] - cache miss, loaded definition, unable to generate doc");	
				}					
			}
			else {
				// Found in cache. In this case, we don't have to worry about stale entry.
				doc = ce.doc;
				if(logger.isDebugEnabled())
					logger.debug("doGetDocumentWithId [" + definitionId + "] - cache hit");
			}
			return doc;
		}
	}
		
	private static Document doGetDocumentWithDefinition(Definition definition) {
		CacheEntry ce = null;
		Document doc;
		if(definition.getId() != null) {
			// This cache only deals with persistent definition
			ce = cache.get(definition.getId());
			if(ce == null) {
				// Not yet cached
				doc = generateDocument(definition);
				if(doc != null) {
					// This cache only deals with valid definition
					cache.put(definition.getId(), new CacheEntry(doc, getLastModTime(definition)));
					if(logger.isDebugEnabled())
						logger.debug("doGetDocumentWithDefinition [" + definition.getId() + "] - cache miss, generated doc");
				}
				else {
					if(logger.isDebugEnabled())
						logger.debug("doGetDocumentWithDefinition [" + definition.getId() + "] - cache miss, unable to generate doc");	
				}
			}
			else {
				// Cache entry found. 
				// Check if the entry is valid or stale with regard to the definition domain object.
				// This is done by comparing the last modification times. This technique works 
				// regardless of the type of cache provider used for Hibernate second-level cache.
				if(ce.isStale(getLastModTime(definition))) {
					// This cache entry is stale. We need to update the cache.
					doc = generateDocument(definition);
					if(doc != null) {
						// This cache only deals with valid definition
						ce.update(doc, getLastModTime(definition));
						if(logger.isDebugEnabled())
							logger.debug("doGetDocumentWithDefinition [" + definition.getId() + "] - cache hit (stale), generated doc");
					}
					else {
						// Failed to update the cache. Remove the entry.
						cache.remove(definition.getId());
						if(logger.isDebugEnabled())
							logger.debug("doGetDocumentWithDefinition [" + definition.getId() + "] - cache hit (stale), unable to generate doc");	
					}
				}
				else {
					// This cache entry is valid.
					ce.update(); // Indicate that this cache entry is in synch with the definition object
					doc = ce.doc;
					if(logger.isDebugEnabled())
						logger.debug("doGetDocumentWithDefinition [" + definition.getId() + "] - cache hit (valid)");	
				}
			}
		}	
		else {
			// For transient definition, generated document isn't cached.
			doc = generateDocument(definition);
			logger.debug("doGetDocumentWithDefinition [NO ID] - generated doc");	
		}
		
		// Under error condition, the return value may be null, which seems bad at first. 
		// But I'm not changing this behavior since that's how the Definition.getDefinition() 
		// method was originally implemented (meaning this doesn't make anything worse
		// than before).
		return doc;
	}
	
	private static Document getDocumentWithoutCache(Definition definition) {
		Document doc = generateDocument(definition);
		if(doc != null) {
			if(logger.isDebugEnabled())
				logger.debug("getDocumentWithoutCache [" + definition.getId() + "] - cache disabled, generated doc");
		}
		else {
			if(logger.isDebugEnabled())
				logger.debug("getDocumentWithoutCache [" + definition.getId() + "] - cache disabled, unable to generate doc");
		}
		return doc;
	}
	
	private static Document generateDocument(Definition def) {
		if(logger.isDebugEnabled())
			logger.debug("generateDocument [" + def.getId() + "]");
		// Because getDocument() method of Definition class has protected 
		// visibility (so as to prevent application code from calling it
		// directly), we can't call it from here via usual means. Instead, 
		// we use this special facility to invoke the method.
		return (Document) InvokeUtil.invokeGetter(def, "document");		
	}
	
	private static synchronized void checkAndReset() {
		// Access to this method is guarded by this class, to guarantee
		// check-and-act semantics in atomic manner.
		// Also, by encapsulating synchronization within this method, we incur
		// no overhead in normal working condition, since periodic reset is
		// disabled by default.
		long currentTime = System.currentTimeMillis();
		if(currentTime - lastResetTime > getResetIntervalInSecond() * 1000) {
			if(logger.isDebugEnabled())
				logger.debug(String.valueOf((currentTime - lastResetTime)/1000) + " seconds passed since last reset - clear cache");  
			clear();
			lastResetTime = currentTime;
		}
	}
	
	private static boolean isEnabled() {
		if(enabled == null) {
			enabled = Boolean.valueOf(SPropsUtil.getBoolean("definition.cache.enabled", true));
			if(logger.isDebugEnabled())
				logger.debug("definition.cache.enabled: " + enabled.toString());
		}
		return enabled.booleanValue();
	}
	
	private static long getResetIntervalInSecond() {
		if(resetIntervalInSecond == null) {
			// This is a hidden config setting, not exposed in ssf.properties.
			resetIntervalInSecond = Long.valueOf(SPropsUtil.getLong("definition.cache.reset.interval", 0));
			if(logger.isDebugEnabled())
				logger.debug("definition.cache.reset.interval: " + resetIntervalInSecond.toString());
		}
		return resetIntervalInSecond.longValue();
	}
	
	private static String getHibernateCacheProvider() {
		if(hibernateCacheProvider == null) {
			hibernateCacheProvider = SPropsUtil.getString("hibernate.cache.provider", "ehcache");
			if(logger.isDebugEnabled())
				logger.debug("hibernate.cache.provider: " + hibernateCacheProvider);
		}
		return hibernateCacheProvider;
	}
	
	private static long getFreshnessSeconds() {
		if(freshnessSeconds == null) {
			// Due to Hibernate's session cache, it doesn't make much difference how often
			// the same definition object is asked for as long as the requests are made
			// within the same session. For that reason, I will use zero as the default
			// value indicating that cache entry has to be completely up-to-date.
			// Important: If you decide to expose this property and allow non-zero value,
			// then you may need to re-work how cache entry is invalidated when definition
			// object is modified witihin the same node. Specifically, you will have to move 
			// call to DefinitionCache.invalidate found in DefinitionModuleImpl.setDefinition
			// out of a transaction context to make sure that the cache invalidation does
			// not occur until after the transaction complete. Otherwise, there might be
			// small window of vulnerability (at least in theory) where old definition is 
			// loaded into memory again due to race condition.
			freshnessSeconds = Long.valueOf(SPropsUtil.getLong("definition.cache.freshness.seconds", 0));
			if(logger.isDebugEnabled())
				logger.debug("definition.cache.freshness.seconds: " + freshnessSeconds.toString());
		}
		return freshnessSeconds.longValue();
	}
	
	private static long getLastModTime(Definition def) {
		if(def.getModification() == null) return 0;
		if(def.getModification().getDate() == null) return 0;
		return def.getModification().getDate().getTime();
	}
	
	private static CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
	
	static class CacheEntry {
		private Document doc;
		private long lastModTime; // last known modification time of the definition represented by this cache entry
		private long lastUpdateTime; // last time this cache entry was updated
		CacheEntry(Document doc, long lastModTime) {
			this.update(doc, lastModTime);
		}
		void update(Document doc, long lastModTime) {
			this.doc = doc;
			this.lastModTime = lastModTime;
			this.update();
		}
		void update() {
			this.lastUpdateTime = System.currentTimeMillis();			
		}
		boolean isStale(long lmt) {
			return lmt > this.lastModTime;
		}
	}
}
