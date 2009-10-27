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

package org.kablink.teaming.domain;
import static org.kablink.util.search.Constants.COMMAND_DEFINITION_FIELD;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.SingletonViolationException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;


/**
 * Cahce of the entries added or modified in the past few minutes
 * @author Peter Hurley
 */
public class TeamingFeedCache extends ZonedObject {
	// This is a singleton class. 

	private static TeamingFeedCache instance; // A singleton instance

	private static final int updateInterval = 1;             //Interval in minutes between updating the cache
	public static final int feedClientUpdateInterval = 5;    //Interval in minutes between when the feed client does its update
	private static final int searchInterval = 11;            //Find the entries created in the last n minutes
	private static final int maxSearchHits = 10000;          //Maximum # of search results returned
	protected static Map<Long,Date> binderMap = null;
	protected static Boolean hasSiteEntries = true;
	protected static Date lastUpdate = null;
	
	
	protected TeamingFeedCache() {		
		if(instance != null)
			throw new SingletonViolationException(TeamingFeedCache.class);
		
		instance = this;
	}

	public Map<Long,Date> getBinderMap() {
		if (binderMap == null) setBinderMap(new HashMap<Long,Date>());;
		return binderMap;
	}
	protected static void setBinderMap(Map<Long,Date> seenMap) {
		binderMap = seenMap;
	}

	protected static Date getLastUpdate() {
		if (lastUpdate == null) {
			lastUpdate = new Date();
			lastUpdate.setTime(lastUpdate.getTime() - updateInterval*60*1000 - 1);
		}
		return lastUpdate;
	}
	protected static void setLastUpdate(Date newLastUpdate) {
		lastUpdate = newLastUpdate;
	}

    public static void updateMap(AllModulesInjected bs) {
    	Date now = new Date();
    	if (now.getTime() >= getLastUpdate().getTime() + updateInterval*60*1000) {
    		hasSiteEntries = false;
    		Map<Long,Date> newBinderMap = new HashMap<Long,Date>();
    		Criteria crit = SearchUtils.entriesForTeamingFeedCache(now, searchInterval);
    		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
    		String adminUserName = SZoneConfig.getAdminUserName(zoneName);
    		User admin = bs.getProfileModule().getUser(adminUserName);
    		//Run this as "admin" because this cache is used by everyone
    		Map results = bs.getBinderModule().executeSearchQuery(crit, 0, maxSearchHits, admin.getId());
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
    					hasSiteEntries = true;
    				}
    	    	}
        	}
        	setBinderMap(newBinderMap);
	        setLastUpdate(new Date());
    	}
    }
    public static boolean checkIfBinderHasNewSiteEntries(AllModulesInjected bs, Date date) {
    	return hasSiteEntries;
    }
    public static boolean checkIfBinderHasNewEntries(AllModulesInjected bs, Long binderId, Date date) {
    	if (binderMap.containsKey(binderId) && 
    			binderMap.get(binderId).after(date)) return true;
    	return false;
    }
}
