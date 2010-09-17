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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.util.WebHelper;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

/**
 * This class manages a cache of the IDs of the binders with entries
 * that have been added or modified in the past few minutes or the IDs
 * of the people that have made modifications in the past few minutes.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamCache {
	// The following are used for storing information in the session
	// cache.
	private static final String SESSION_ACTIVITY_STREAM_TRACKED_BINDER_IDS	="activityStreamTrackedBinderIds";
	private static final String SESSION_ACTIVITY_STREAM_TRACKED_PEOPLE_IDS	="activityStreamTrackedPeopleIds";
	private static final String SESSION_ACTIVITY_STREAM_UPDATE_DATE			="activityStreamUpdateDate";
	
    // The following stores the system wide ID caches for all the zones
    // in the system.
	private static ConcurrentMap<Long, ActivityStreamIDCache> m_zonedIDCaches = new ConcurrentHashMap<Long, ActivityStreamIDCache>();

	/*
	 * Inner class use to track the IDs being cached.
	 */
    private static class ActivityStreamIDCache {
    	// Note:  The following fields need to be kept together so that
    	//    they are consistent relative to each other within a
    	//    zone-specific cache.
    	private boolean			m_hasSiteEntries;
    	private Date			m_lastUpdate;
    	private Map<Long, Date>	m_binderMap;
    	private Map<Long, Date>	m_personMap;
    	
    	/*
    	 * Constructor method.
    	 */
    	private ActivityStreamIDCache() {
    		m_binderMap      = new HashMap<Long, Date>();
    		m_personMap      = new HashMap<Long, Date>();
    		m_hasSiteEntries = false;
    		m_lastUpdate     = new Date(
    			System.currentTimeMillis() -
    			mToMS(GwtActivityStreamHelper.m_activityStreamParams.getCacheRefresh()) - 1);
    	}
    	
    	/**
    	 * Returns the binder map from the ID cache.
    	 * 
    	 * @return
    	 */
		public Map<Long, Date> getBinderMap() {
			return m_binderMap;
		}
		
		/**
		 * Returns the date of the last time the ID cache was updated.
		 * 
		 * @return
		 */
		public Date getLastUpdate() {
			return m_lastUpdate;
		}
		
    	/**
    	 * Returns the person map from the ID cache.
    	 * 
    	 * @return
    	 */
		public Map<Long, Date> getPersonMap() {
			return m_personMap;
		}
		
		/**
		 * Returns true if there are new site wide entries available
		 * from the ID cache.
		 * 
		 * @return
		 */
		public boolean isHasSiteEntries() {
			return m_hasSiteEntries;
		}
		
		/**
		 * Store a new binder map in the ID cache.
		 * 
		 * @param binderMap
		 * 
		 */
		public void setBinderMap(Map<Long, Date> binderMap) {
			m_binderMap = binderMap;
		}
		
		/**
		 * Updates the has site entries flag in this ID cache.
		 * 
		 * @param hasSiteEntries
		 */
		public void setHasSiteEntries(boolean hasSiteEntries) {
			m_hasSiteEntries = hasSiteEntries;
		}
		
		/**
		 * Store a new person map in the ID cache.
		 * 
		 * @param personMap
		 * 
		 */
		public void setPersonMap(Map<Long, Date> personMap) {
			m_personMap = personMap;
		}
		
		/**
		 * Sets the date of the last time the ID cache was updated.
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
    public static boolean checkBindersForNewEntries(AllModulesInjected bs, List<String> binderIds, Date date) {
    	// Do we have an ID cache available?
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	if(null != cache) {
    		// Yes!  Look to see if there are any binder IDs in common,
    		// using the smallest list.
    		Map<Long, Date> binderMap = cache.getBinderMap();
    		if (binderMap.size() < binderIds.size()) {
	    		for (Long binderId: binderMap.keySet()) {
		        	if (binderIds.contains(binderId) && binderMap.get(binderId).after(date)) {
		        		return true;
		        	}
	    		}	
    		}
    		
    		else {
        		for (String binderIdS: binderIds) {
        			Long binderId = Long.parseLong(binderIdS);
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
     * @param binderIdS
     * @param date
     * 
     * @return
     */
    public static boolean checkIfBinderHasNewEntries(AllModulesInjected bs, String binderIdS, Date date) {
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	if (null != cache) {
    		Long binderId = Long.parseLong(binderIdS);
    		Map<Long, Date> binderMap = cache.getBinderMap();
        	if (binderMap.containsKey(binderId) && 
        			binderMap.get(binderId).after(date)) {
        		return true;    		
        	}
    	}
    	return false;
    }
    
	/**
	 * Returns true if the person ID cache has new entries available
	 * and false otherwise.
	 * 
	 * @param bs
	 * @param date
	 * 
	 * @return
	 */
    public static boolean checkIfHasNewSiteEntries(AllModulesInjected bs, Date date) {
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	boolean reply = ((null != cache) ? cache.isHasSiteEntries() : false);
   		return reply;
    }
    
    /**
     * Returns true if any of the persons in a set have new entries
     * available and false otherwise.
     * 
     * @param bs
     * @param personIds
     * @param date
     * 
     * @return
     */
    public static boolean checkPersonsForNewEntries(AllModulesInjected bs, List<String> personIds, Date date) {
    	// Do we have a person ID cache available?
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	if(null != cache) {
    		// Yes!  Look to see if there are any person IDs in common,
    		// using the smallest list.
    		Map<Long, Date> personMap = cache.getPersonMap();
    		if (personMap.size() < personIds.size()) {
	    		for (Long personId: personMap.keySet()) {
		        	if (personIds.contains(personId) && personMap.get(personId).after(date)) {
		        		return true;
		        	}
	    		}	
    		}
    		
    		else {
        		for (String personIdS: personIds) {
        			Long personId = Long.parseLong(personIdS);
    	        	if (personMap.containsKey(personId) && personMap.get(personId).after(date)) {
    	        		return true;
    	        	}
        		}
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Returns true if a specific person has new entries available and
     * false otherwise.
     * 
     * @param bs
     * @param personIdS
     * @param date
     * 
     * @return
     */
    public static boolean checkIfPersonHasNewEntries(AllModulesInjected bs, String personIdS, Date date) {
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	if (null != cache) {
    		Long personId = Long.parseLong(personIdS);
    		Map<Long, Date> personMap = cache.getPersonMap();
        	if (personMap.containsKey(personId) && 
        			personMap.get(personId).after(date)) {
        		return true;    		
        	}
    	}
    	return false;
    }
    
	/*
	 * Returns the ID cache for the current zone.
	 */
	private static ActivityStreamIDCache getIDCacheForTheZone() {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	return m_zonedIDCaches.get(zoneId);
	}

	/**
	 * Returns the cached tracked binder IDs list from the session cache.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getTrackedBinderIds(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		List<String> reply = ((List<String>) session.getAttribute(SESSION_ACTIVITY_STREAM_TRACKED_BINDER_IDS));
		if (null == reply) {
			reply = new ArrayList<String>();
		}
		return reply;
	}
	
	/**
	 * Returns the cached tracked people IDs list from the session cache.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<String> getTrackedPeopleIds(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		List<String> reply = ((List<String>) session.getAttribute(SESSION_ACTIVITY_STREAM_TRACKED_PEOPLE_IDS));
		if (null == reply) {
			reply = new ArrayList<String>();
		}
		return reply;
	}

	/**
	 * Returns the date of the last client update from the session
	 * cache.
	 * 
	 * @param request
	 * 
	 * @return
	 */
	public static Date getUpdateDate(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		Date reply = ((Date) session.getAttribute(SESSION_ACTIVITY_STREAM_UPDATE_DATE));
		if (null == reply) {
        	reply = new Date();
        	reply.setTime(reply.getTime() - GwtActivityStreamHelper.m_activityStreamParams.getClientRefresh() - 1);
		}
		return reply;
	}
	
    /*
     * Returns the millisecond equivalent of an interval in minutes.
     */
    private static int mToMS(int minutes) {
    	return (minutes * 60 * 1000);
    }
    
	/*
	 * Stores a new ID cache for the current zone.
	 */
	private static void setIDCacheForTheZone(ActivityStreamIDCache cache) {
    	Long zoneId = RequestContextHolder.getRequestContext().getZoneId();
    	m_zonedIDCaches.put(zoneId, cache);
	}

	/**
	 * Stores a tracked binder IDs list in the session cache.
	 * 
	 * @param request
	 * @param trackedBinderIds
	 */
	public static void setTrackedBinderIds(HttpServletRequest request, List<String> trackedBinderIds) {
        HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute(SESSION_ACTIVITY_STREAM_TRACKED_BINDER_IDS, trackedBinderIds);
	}
	
	/**
	 * Stores a tracked people IDs list in the session cache.
	 * 
	 * @param request
	 * @param trackedPeopleIds
	 */
	public static void setTrackedPeopleIds(HttpServletRequest request, List<String> trackedPeopleIds) {
        HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute(SESSION_ACTIVITY_STREAM_TRACKED_PEOPLE_IDS, trackedPeopleIds);
	}

	/**
	 * Stores the current date as the activity stream update date in
	 * the session cache.
	 * 
	 * @param request
	 */
	public static void setUpdateDate(HttpServletRequest request) {
        HttpSession session = WebHelper.getRequiredSession(request);
		session.setAttribute(SESSION_ACTIVITY_STREAM_UPDATE_DATE, new Date());
	}

	/*
	 * Constructs a new binder map for the ID cache.
	 */
	@SuppressWarnings("unchecked")
	private static Map<Long, Date> updateBinderMap(AllModulesInjected bs, Date now, ActivityStreamIDCache idCache) {
		Map<Long, Date> newBinderMap = new HashMap<Long, Date>();
		
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
					idCache.setHasSiteEntries(true);
				}
	    	}
    	}
    	
    	idCache.setBinderMap(newBinderMap);
    	return newBinderMap;
	}
	
	/*
	 * Constructs a new person map for the ID cache.
	 */
	private static Map<Long, Date> updatePersonMap(AllModulesInjected bs, Date now, ActivityStreamIDCache idCache) {
		Map<Long, Date> newPersonMap = new HashMap<Long, Date>();
		
//!		...this needs to be implemented...
		
    	idCache.setPersonMap(newPersonMap);
		return newPersonMap;
	}
	
	/**
	 * If necessary, updates the ID cache for the current zone.
	 * 
	 * @param bs
	 */
	public static void updateMaps(AllModulesInjected bs) {
    	// Do we need to create/refresh the ID cache for the current
    	// zone?
    	ActivityStreamIDCache cache = getIDCacheForTheZone();
    	Date now = new Date();
    	if((null == cache) ||
    			(now.getTime() >= (cache.getLastUpdate().getTime() + mToMS(GwtActivityStreamHelper.m_activityStreamParams.getCacheRefresh())))) {
    		// Yes!  Create a new one...
    		ActivityStreamIDCache newCache = new ActivityStreamIDCache();

    		// ...store new binder and person maps into...
    		updateBinderMap(bs, now, newCache);
    		updatePersonMap(bs, now, newCache);
    		        	
        	// ...store the time that we updated it...
        	newCache.setLastUpdate(now);
	        
	        // ...and store the ID cache for the zone.
	        setIDCacheForTheZone(newCache);
    	}
    }
}
