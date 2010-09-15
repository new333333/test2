/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.gwt.server.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

/**
 * This class manages a cache of the IDs of the binders with entries
 * that have been added or modified in the past few minutes.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamCache {
    // The following store the system wide binder ID caches store for
    // all the zones in the system.
	private static ConcurrentMap<Long, BinderIDCache> m_zonedCache = new ConcurrentHashMap<Long, BinderIDCache>();

	/*
	 * Inner class use to track the binder IDs that are being cached.
	 */
    private static class BinderIDCache {
    	// Note:  The following three fields need to be kept together so
    	//    that they are consistent relative to each other within a
    	//    zone-specific cache.
    	private boolean m_hasSiteEntries;
    	private Date m_lastUpdate;
    	private Map<Long, Date> m_binderMap;
    	
    	/*
    	 * Constructor method.
    	 */
    	private BinderIDCache() {
    		m_binderMap      = new HashMap<Long,Date>();
    		m_hasSiteEntries = false;
    		m_lastUpdate     = new Date(
    			System.currentTimeMillis() -
    			mToMS(GwtActivityStreamHelper.m_activityStreamParams.getCacheRefresh()) - 1);
    	}
    	
    	/**
    	 * Returns the binder map from the binder ID cache.
    	 * 
    	 * @return
    	 */
		public Map<Long,Date> getBinderMap() {
			return m_binderMap;
		}
		
		/**
		 * Returns the date of the last time the binder ID cache was
		 * updated.
		 * 
		 * @return
		 */
		public Date getLastUpdate() {
			return m_lastUpdate;
		}
		
		/**
		 * Returns true if there are new site wide entries available
		 * from the binder ID cache.
		 * 
		 * @return
		 */
		public boolean isHasSiteEntries() {
			return m_hasSiteEntries;
		}
		
		/**
		 * Store a new binder map in the binder ID cache.
		 * @param binderMap
		 * 
		 */
		public void setBinderMap(Map<Long,Date> binderMap) {
			m_binderMap = binderMap;
		}
		
		/**
		 * Updates the has site entries flag in this binder ID cache.
		 * 
		 * @param hasSiteEntries
		 */
		public void setHasSiteEntries(boolean hasSiteEntries) {
			m_hasSiteEntries = hasSiteEntries;
		}
		
		/**
		 * Sets the date of the last time the binder ID cache was
		 * updated.
		 * 
		 * @param lastUpdate
		 */
		public void setLastUpdate(Date lastUpdate) {
			m_lastUpdate = lastUpdate;
		}
    }

    /**
     * Returns true if any of the binders in a set have new entries
     * available and false otherwise.
     * 
     * @param bs
     * @param binderIds
     * @param date
     * 
     * @return
     */
    public static boolean checkBindersForNewEntries(AllModulesInjected bs, Set<Long> binderIds, Date date) {
    	// Do we have a binder ID cache available?
    	BinderIDCache cache = getBinderIDCacheForTheZone();
    	if(null != cache) {
    		// Yes!  Look to see if there are any binder IDs in common,
    		// using the smallest list.
    		Map<Long,Date> binderMap = cache.getBinderMap();
    		if (binderMap.size() < binderIds.size()) {
	    		for (Long binderId: binderMap.keySet()) {
		        	if (binderIds.contains(binderId) && binderMap.get(binderId).after(date)) {
		        		return true;
		        	}
	    		}	
    		}
    		
    		else {
        		for (Long binderId: binderIds) {
    	        	if (binderMap.containsKey(binderId) && binderMap.get(binderId).after(date)) {
    	        		return true;
    	        	}
        		}
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Returns true if a specific binder has new entries available and
     * false otherwise.
     * 
     * @param bs
     * @param binderId
     * @param date
     * 
     * @return
     */
    public static boolean checkIfBinderHasNewEntries(AllModulesInjected bs, Long binderId, Date date) {
    	BinderIDCache cache = getBinderIDCacheForTheZone();
    	if (null != cache) {
    		Map<Long,Date> binderMap = cache.getBinderMap();
        	if (binderMap.containsKey(binderId) && 
        			binderMap.get(binderId).after(date)) {
        		return true;    		
        	}
    	}
    	return false;
    }
    
	/**
	 * Returns true if the binder ID cache has new entries available
	 * and false otherwise.
	 * 
	 * @param bs
	 * @param date
	 * 
	 * @return
	 */
    public static boolean checkIfBinderHasNewSiteEntries(AllModulesInjected bs, Date date) {
    	BinderIDCache cache = getBinderIDCacheForTheZone();
    	boolean reply = ((null != cache) ? cache.isHasSiteEntries() : false);
   		return reply;
    }
    
	/*
	 * Returns the binder ID cache for the current zone.
	 */
	private static BinderIDCache getBinderIDCacheForTheZone() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	return m_zonedCache.get(zoneId);
	}
	
    /*
     * Returns the millisecond equivalent of an interval in minutes.
     */
    private static int mToMS(int minutes) {
    	return (minutes * 60 * 1000);
    }
    
	/*
	 * Stores a new binder ID cache for the current zone.
	 */
	private static void setBinderIDCacheForTheZone(BinderIDCache cache) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	m_zonedCache.put(zoneId, cache);
	}

	/**
	 * If necessary, updates the binder ID cache for the current zone.
	 * 
	 * @param bs
	 */
    @SuppressWarnings("unchecked")
	public static void updateMap(AllModulesInjected bs) {
    	// Do we need to create/refresh the binder ID cache for the
    	// current zone?
    	BinderIDCache cache = getBinderIDCacheForTheZone();
    	Date now = new Date();
    	if((null == cache) ||
    			(now.getTime() >= (cache.getLastUpdate().getTime() + mToMS(GwtActivityStreamHelper.m_activityStreamParams.getCacheRefresh())))) {
    		// Yes!  Create a new one.
    		BinderIDCache newCache = new BinderIDCache();
    		Map<Long,Date> newBinderMap = new HashMap<Long,Date>();
    		
    		// Run the search for anything that's changed.  Note that
    		// we run it as admin because the cache is used by everyone
    		// in a zone.
    		Criteria crit = SearchUtils.entriesForTeamingFeedCache(now, GwtActivityStreamHelper.m_activityStreamParams.getLookback());
    		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
    		String adminUserName = SZoneConfig.getAdminUserName(zoneName);
    		User admin = bs.getProfileModule().getUser(adminUserName);
    		Map results = bs.getBinderModule().executeSearchQuery(crit, 0, GwtActivityStreamHelper.m_activityStreamParams.getMaxHits(), admin.getId(), false, true);
    		    		
    		// Did we find any changes that need to be cached?
        	List items = ((List) results.get(ObjectKeys.SEARCH_ENTRIES));
        	if ((null != items) && (!(items.isEmpty()))) {
        		// Yes!  Scan them.
    	    	for (Iterator it = items.iterator(); it.hasNext(); ) {
    	    		// Does this entry have an associated binder ID?
    	    		Map entry = ((Map) it.next());
    	    		String binderId = ((String) entry.get(Constants.BINDER_ID_FIELD));
    				if (null != binderId) {
    					// Yes!  Does it also have a modification date?
    					Date entryModificationDate = ((Date) entry.get(Constants.MODIFICATION_DATE_FIELD));
    					if (null != entryModificationDate) {
    						// Yes!  If we're not already tracking this
    						// binder or we tracking it with an older
    						// modification date...
    						if (!(newBinderMap.containsKey(Long.valueOf(binderId))) ||
    								newBinderMap.get(Long.valueOf(binderId)).before(entryModificationDate)) {
    							// ..store/update it.
    							newBinderMap.put(Long.valueOf(binderId), entryModificationDate);
    						}
    					}
    					
    					// Scan this entries ancestry.
    					SearchFieldResult ancestry = ((SearchFieldResult) entry.get(Constants.ENTRY_ANCESTRY));
    					for (Object id: ancestry.getValueSet()) {
    						// If we're not already tracking this
    						// binder or we tracking it with an older
    						// modification date...
    						if (!(newBinderMap.containsKey(Long.valueOf(id.toString()))) ||
    								newBinderMap.get(Long.valueOf(id.toString())).before(entryModificationDate)) {
    							// ..store/update it.
    							newBinderMap.put(Long.valueOf(id.toString()), entryModificationDate);
    						}
    					}
    					
    					// If we get here, there are new site wide
    					// entries available.
    					newCache.setHasSiteEntries(true);
    				}
    	    	}
        	}
        	
        	// Store the new binder map and the time we're updating
        	// it...
        	newCache.setBinderMap(newBinderMap);
        	newCache.setLastUpdate(now);
	        
	        // ...and store the binder ID cache for the zone.
	        setBinderIDCacheForTheZone(newCache);
    	}
    }
}
