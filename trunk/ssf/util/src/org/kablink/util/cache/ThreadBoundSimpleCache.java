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
 * Simple cache implementation bound to a thread.
 * <p>
 * The cache has no size limit but has expiration for entries.
 * Setting proper expiration time indirectly controls how large the cache can grow,
 * which works well most of time except when there are burst of "puts" during short
 * period of time in a tight loop.
 * 
 * @author Jong
 *
 */
public class ThreadBoundSimpleCache {
	
	private static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	private static final ThreadLocal<SimpleCache> threadCache = new ThreadLocal<SimpleCache>();
	
	private static final String KEY_PART_SEPARATOR = "$";
	
	public static void initialize(long timeToLiveInSeconds) {
		if(logger.isDebugEnabled())
			logger.debug("Initializing thread-bound simple cache with timeToLiveInSeconds=" + timeToLiveInSeconds);
		threadCache.set(new SimpleCache(timeToLiveInSeconds));
	}
	
	public static void destroy() {
		SimpleCache cache = threadCache.get();
		if(cache != null && logger.isDebugEnabled()) {
			logger.debug("Destroying thread-bound simple cache: puts=" + cache.putCount + ", gets=" + cache.getCount + ", hits=" + cache.hitCount + ", misses=" + cache.missCount + ", currentSize=" + cache.size() + ", peakSize=" + cache.peakSize);
		}
		threadCache.set(null);
	}
	
	/**
	 * Put the value given the key. Note that the value comes before the key
	 * in the argument list.
	 * 
	 * @param value
	 * @param key
	 */
	public static void put(Object value, String key) {
		SimpleCache cache = threadCache.get();
		if(cache != null)
			putIntoCache(key, value, cache);
			cache.put(key, value);
	}
	
	public static void put(Object value, String[] keys) {
		SimpleCache cache = threadCache.get();
		if(cache != null)
			putIntoCache(getSingleKey(keys), value, cache);
	}
	
	public static void put(Object value, Object... keys) {
		SimpleCache cache = threadCache.get();
		if(cache != null)
			putIntoCache(getSingleKey(keys), value, cache);	
	}
	
	private static void putIntoCache(String key, Object value, SimpleCache cache) 
	throws IllegalArgumentException {
		Object previousValue = cache.put(key, value);
		if(previousValue != null) {
			// This "put" is replacing previous value for the same key. Make sure that
			// there's consistent continuity in the type between the old and new values. 
			// NOTE: We might want to relax the constraint down the road and allow application
			//       to use this cache in polymorphic way (where different concrete classes of
			//       objects can be associated with the same key for the lifetime of the cache).
			//       But for now we want to focus on simplicity, robustness, and error prevention,
			//       so we will simply treat it as a programming error.
			if(!previousValue.getClass().equals(value.getClass())) {
				String msg = "Replacing old type [" + previousValue.getClass().getName() + "] with new type [" + value.getClass().getName() + "] for key [" + key + "]";
				logger.error(msg);
				throw new IllegalArgumentException();
			}
		}
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
		SimpleCache cache = threadCache.get();
		if(cache != null) { // cache exists for this thread
			return getFromCache(key, clazz, cache);
		}
		return null;
	}
	
	public static <T> T get(Class<T> clazz, String[] keys) {
		SimpleCache cache = threadCache.get();
		if(cache != null) { // cache exists for this thread
			String key = getSingleKey(keys);
			return getFromCache(key, clazz, cache);
		}
		return null;
	}
	
	public static <T> T get(Class<T> clazz, Object... keys) {
		SimpleCache cache = threadCache.get();
		if(cache != null) { // cache exists for this thread
			String key = getSingleKey(keys);
			return getFromCache(key, clazz, cache);
		}
		return null;
	}
	
	private static <T> T getFromCache(String key, Class<T> valueClass, SimpleCache cache) 
	throws IllegalArgumentException {
		Object value = cache.get(key);
		if(value != null) { // value exists for this key
			if(valueClass.isAssignableFrom(value.getClass())) {
				// Type-safe hit. This cast should be safe although compiler doesn't know that.
				return (T) value;
			}
			else {
				// The value of this key isn't of expected type. Must be programming error either on the put or get side (or both).
				String msg = "Found type [" + value.getClass().getName() + "] when expecting type [" + valueClass.getName() + "] for key [" + key + "]";
				logger.error(msg);
				throw new IllegalArgumentException(msg);
			}
		}
		return null;
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
	
	private static class SimpleCache extends HashMapCache<String,Object>{
		private long putCount;
		private long getCount;
		private long hitCount;
		private long missCount;
		private long peakSize;
		
		SimpleCache(long timeToLiveInSeconds) {
			super(timeToLiveInSeconds);
		}
		
		@Override
		public Object get(String key) {
			getCount++;
			Object val = super.get(key);
			if(val != null)
				hitCount++;
			else
				missCount++;
			return val;
		}
		
		@Override
		public Object put(String key, Object value) {
			putCount++;
			Object previousValue = super.put(key, value);
			if(this.size() > peakSize)
				peakSize = this.size();
			return previousValue;
		}
	}
}
