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

package org.kablink.teaming.util.feed;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

/**
 * Cahce of the entries added or modified in the past few minutes
 * @author Peter Hurley
 */
public class TeamingFeedCache {

	private static final int updateInterval = 1;             //Interval in minutes between updating the cache
	public static final int feedClientUpdateInterval = 5;    //Interval in minutes between when the feed client does its update
	private static final int searchInterval = 11;            //Find the entries created in the last n minutes
	private static final int maxSearchHits = 1000;           //Maximum # of search results returned
	
	private static ConcurrentMap<Long, FeedCache> zonedCache = new ConcurrentHashMap<Long, FeedCache>();

	private static FeedCache getFeedCacheForTheZone() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	return zonedCache.get(zoneId);
	}
	private static void setFeedCacheForTheZone(FeedCache cache) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	zonedCache.put(zoneId, cache);
	}
	
    public static void updateMap(AllModulesInjected bs) {
    	FeedCache cache = getFeedCacheForTheZone();
    	Date now = new Date();
    	if (cache == null || (now.getTime() >= cache.getLastUpdate().getTime() + updateInterval*60*1000)) {
    		// Time to create a fresh new cache for the current zone.
    		FeedCache newCache = new FeedCache();
    		Map<Long,Date> newBinderMap = new HashMap<Long,Date>();
    		Criteria crit = SearchUtils.entriesForTeamingFeedCache(now, searchInterval);
    		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
    		String adminUserName = SZoneConfig.getAdminUserName(zoneName);
    		User admin = bs.getProfileModule().getUser(adminUserName);
    		//Run this as "admin" because this cache is used by everyone
    		Map results = bs.getBinderModule().executeSearchQuery(crit, Constants.SEARCH_MODE_NORMAL, 0, maxSearchHits,
    				org.kablink.teaming.module.shared.SearchUtils.fieldNamesList(Constants.BINDER_ID_FIELD,Constants.MODIFICATION_DATE_FIELD, Constants.ENTRY_ANCESTRY),
    				admin.getId(), false, true);
        	List items = (List) results.get(ObjectKeys.SEARCH_ENTRIES);
        	if (items != null) {
    	    	Iterator it = items.iterator();
    	    	while (it.hasNext()) {
    	    		Map entry = (Map)it.next();
    	    		String binderId = (String)entry.get(Constants.BINDER_ID_FIELD);
    				if (binderId != null) {
    					Date entryModificationDate = (Date)entry.get(Constants.MODIFICATION_DATE_FIELD);
    					if (entryModificationDate != null) {
    						if (!newBinderMap.containsKey(Long.valueOf(binderId)) ||
    								newBinderMap.get(Long.valueOf(binderId)).before(entryModificationDate)) {
    							newBinderMap.put(Long.valueOf(binderId), entryModificationDate);
    						}
    					}
    					SearchFieldResult ancestry = (SearchFieldResult)entry.get(Constants.ENTRY_ANCESTRY);
    					for (Object id : ancestry.getValueSet()) {
    						if (!newBinderMap.containsKey(Long.valueOf(id.toString())) ||
    								newBinderMap.get(Long.valueOf(id.toString())).before(entryModificationDate)) {
    							newBinderMap.put(Long.valueOf(id.toString()), entryModificationDate);
    						}
    					}
    					newCache.setHasSiteEntries(true);
    				}
    	    	}
        	}
        	newCache.setBinderMap(newBinderMap);
        	newCache.setLastUpdate(now);
	        setFeedCacheForTheZone(newCache);
    	}
    }
    
    private static class FeedCache {
    	// The following three fields need to be kept together so that they are consistent
    	// relative to each other within a zone-specific cache.
    	private Map<Long,Date> binderMap;
    	private boolean hasSiteEntries;
    	private Date lastUpdate;
    	FeedCache() {
    		binderMap = new HashMap<Long,Date>();
    		hasSiteEntries = false;
    		lastUpdate = new Date(System.currentTimeMillis() - updateInterval*60*1000 - 1);
    	}
		public Map<Long,Date> getBinderMap() {
			return binderMap;
		}
		public void setBinderMap(Map<Long,Date> binderMap) {
			this.binderMap = binderMap;
		}
		public boolean isHasSiteEntries() {
			return hasSiteEntries;
		}
		public void setHasSiteEntries(boolean hasSiteEntries) {
			this.hasSiteEntries = hasSiteEntries;
		}
		public Date getLastUpdate() {
			return lastUpdate;
		}
		public void setLastUpdate(Date lastUpdate) {
			this.lastUpdate = lastUpdate;
		}
    }
    
    public static boolean checkIfBinderHasNewSiteEntries(AllModulesInjected bs, Date date) {
    	FeedCache cache = getFeedCacheForTheZone();
    	if(cache != null)
    		return cache.isHasSiteEntries();
    	else
    		return false;
    }
    public static boolean checkIfBinderHasNewEntries(AllModulesInjected bs, Long binderId, Date date) {
    	FeedCache cache = getFeedCacheForTheZone();
    	if(cache != null) {
    		Map<Long,Date> binderMap = cache.getBinderMap();
        	if (binderMap.containsKey(binderId) && 
        			binderMap.get(binderId).after(date)) return true;    		
    	}
    	return false;
    }
    public static boolean checkBindersForNewEntries(AllModulesInjected bs, Set<Long> binderIds, Date date) {
    	FeedCache cache = getFeedCacheForTheZone();
    	if(cache != null) {
    		//Look to see if there are any binderIds in common, using the smallest list
    		Map<Long,Date> binderMap = cache.getBinderMap();
    		if (binderMap.size() < binderIds.size()) {
	    		for (Long binderId : binderMap.keySet()) {
		        	if (binderIds.contains(binderId) && 
		        			binderMap.get(binderId).after(date)) return true;
	    		}
    		} else {
        		for (Long binderId : binderIds) {
    	        	if (binderMap.containsKey(binderId) && 
    	        			binderMap.get(binderId).after(date)) return true;
        		}
    		}
    	}
    	return false;
    }
}
