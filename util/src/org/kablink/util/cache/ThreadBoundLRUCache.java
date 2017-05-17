/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.util.cache;

import java.lang.invoke.MethodHandles;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple LRU cache bound to a thread.
 * <p>
 * By the definition of LRU, the least recently referenced entry is evicted 
 * from the cache when the cache reaches its maximum allowed capacity.
 * 
 * @author Jong
 *
 */
public class ThreadBoundLRUCache {

	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private static final ThreadLocal<LRUCache> threadCache = new ThreadLocal<LRUCache>();
	
	private static final ThreadLocal<CacheStats> threadCacheStats = new ThreadLocal<CacheStats>();
	
	private static final String KEY_PART_SEPARATOR = "$";
	
	/**
	 * Allocate a LRU cache and bind it to the calling thread.
	 * 
	 * @param sizeLimit
	 */
	public static void initialize(int sizeLimit) {
		if(logger.isDebugEnabled())
			logger.debug("Initializing thread-bound LRU cache with sizeLimit=" + sizeLimit);
		threadCache.set(new LRUCache(sizeLimit));
		threadCacheStats.set(new CacheStats());
	}
	
	/**
	 * Unbind LRU cache from the calling thread and free it up.
	 */
	public static void destroy() {
		CacheStats cs = threadCacheStats.get();
		if(cs != null && logger.isDebugEnabled())
			logger.debug("Destroying thread-bound LRU cache: hits=" + cs.hits + ", misses=" + cs.misses + ", typeMismatch=" + cs.typeMismatch + ", currentSize=" + threadCache.get().size());
		threadCache.set(null);
		threadCacheStats.set(null);
	}
	
	/**
	 * Put the value given the key. Note that the value comes before the key
	 * in the argument list.
	 * 
	 * @param value
	 * @param key
	 */
	public static void put(Object value, String key) {
		LRUCache cache = threadCache.get();
		if(cache != null)
			cache.put(key, value);
	}
	
	public static void put(Object value, String[] keys) {
		LRUCache cache = threadCache.get();
		if(cache != null)
			cache.put(getSingleKey(keys), value);
	}
	
	public static void put(Object value, Object... keys) {
		LRUCache cache = threadCache.get();
		if(cache != null)
			cache.put(getSingleKey(keys), value);		
	}
	
	/**
	 * Get cached value given the key in a (sort of) type-safe way.
	 * This to certain extent shields application from dealing with type-related headache and
	 * exceptions. But this type checking is limited because it can deal with it only at the
	 * top level and not recursively. For instance, if cache value is of List<A> type, then the
	 * code would be able to check that the cached value is a list, but it won't be able to 
	 * check that the elements of the list are of type A. The Java generic doesn't support that. 
	 * 
	 * @param clazz
	 * @param key
	 * @return
	 */
	public static <T> T get(Class<T> clazz, String key) {
		LRUCache cache = threadCache.get();
		if(cache != null) { // cache exists for this thread
			Object value = cache.get(key);
			if(value != null) { // value exists for this key
				if(clazz.isAssignableFrom(value.getClass())) {
					// Type-safe hit
					threadCacheStats.get().hits++;
					return (T) value;
				}
				else {
					// For whatever reason, the value of this key isn't of expected type.
					// We can't make use of this cache entry. Remove it from the cache to prevent
					// further headache before returning null to the caller.
					threadCacheStats.get().typeMismatch++;
					cache.remove(key);
					logger.warn("Found type [" + value.getClass().getName() + "] when expecting type [" + clazz.getName() + "] for key [" + key + "]");
				}
			}
			else {
				threadCacheStats.get().misses++;
			}
		}
		return null;
	}
	
	public static <T> T get(Class<T> clazz, String[] keys) {
		LRUCache cache = threadCache.get();
		if(cache != null) { // cache exists for this thread
			String key = getSingleKey(keys);
			Object value = cache.get(key);
			if(value != null) { // value exists for this key
				if(clazz.isAssignableFrom(value.getClass())) {
					// Type-safe hit
					threadCacheStats.get().hits++;
					return (T) value;
				}
				else {
					// For whatever reason, the value of this key isn't of expected type.
					// We can't make use of this cache entry. Remove it from the cache to prevent
					// further headache before returning null to the caller.
					threadCacheStats.get().typeMismatch++;
					cache.remove(key);
					logger.warn("Found type [" + value.getClass().getName() + "] when expecting type [" + clazz.getName() + "] for key [" + key + "]");
				}
			}
			else {
				threadCacheStats.get().misses++;
			}
		}
		return null;
	}
	
	public static <T> T get(Class<T> clazz, Object... keys) {
		LRUCache cache = threadCache.get();
		if(cache != null) { // cache exists for this thread
			String key = getSingleKey(keys);
			Object value = cache.get(key);
			if(value != null) { // value exists for this key
				if(clazz.isAssignableFrom(value.getClass())) {
					// Type-safe hit
					threadCacheStats.get().hits++;
					return (T) value;
				}
				else {
					// For whatever reason, the value of this key isn't of expected type.
					// We can't make use of this cache entry. Remove it from the cache to prevent
					// further headache before returning null to the caller.
					threadCacheStats.get().typeMismatch++;
					cache.remove(key);
					logger.warn("Found type [" + value.getClass().getName() + "] when expecting type [" + clazz.getName() + "] for key [" + key + "]");
				}
			}
			else {
				threadCacheStats.get().misses++;
			}
		}
		return null;
	}
	
	public static void remove(Object... keys) {
		LRUCache cache = threadCache.get();
		if(cache != null)
			cache.remove(getSingleKey(keys));
	}
	
	public static void remove(String[] keys) {
		LRUCache cache = threadCache.get();
		if(cache != null)
			cache.remove(getSingleKey(keys));
	}
	
	public static void remove(String key) {
		LRUCache cache = threadCache.get();
		if(cache != null)
			cache.remove(key);
	}

	private static String getSingleKey(Object[] keys) {
		if(keys == null)
			return null;
		if(keys.length == 0)
			return "";
		StringBuilder sb = new StringBuilder(keys[0].toString());
		for(int i = 1; i < keys.length; i++) {
			sb.append(KEY_PART_SEPARATOR)
			.append(keys[i].toString());
		}
		return sb.toString();
	}
	
	private static class CacheStats {
		// Number of cache hits. This counts only when cache exists.
		private long hits;
		// Number of cache misses. This counts only when cache exists.
		private long misses;
		// Number of times cache mis results from type-mismatch.
		// This signals either programming error or collision in key name space.
		private long typeMismatch;
	}
}
