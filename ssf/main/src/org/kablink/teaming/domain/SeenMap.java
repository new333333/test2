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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.util.search.Constants;

import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TLongLongHashMap;
import gnu.trove.procedure.TLongLongProcedure;


/**
 * @hibernate.class table="SS_SeenMap" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 * @author Janet McCann
 * Manage the seen map for a user. 
 */
public class SeenMap extends ZonedObject {
	protected Long principalId;
	protected Object seenMap;
	protected Date lastPrune;
	protected Long pruneDays;	//used if this user has too many items in seenMap.
	
	
	protected SeenMap() {		
		//only called by hibernate.  Prevent null maps.
	}

	// Used by application
	public SeenMap(Long principalId) {
		setSeenMap(new TLongLongHashMap());
		setPrincipalId(principalId);
	}
	
	protected void setSeenMap(TLongLongHashMap troveMap) {
		this.seenMap = troveMap;
	}
	
	protected Long getPrincipalId() {
		return principalId;
	}
	protected void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}	

    protected void pruneMap(Date now) {
    	final Long seenMapTimeout;
    	if(pruneDays == null) {
        	seenMapTimeout = ObjectKeys.SEEN_MAP_TIMEOUT;    		
    	}
    	else {
    		//This user is being cut back, so use the value in pruneDays for the timeout
    		seenMapTimeout = pruneDays * 24 * 60 * 60 * 1000;  //Days converted into milliseconds
    	}
    	Long maxSeenMapSize = SPropsUtil.getLong("seen.maxNumberOfSeenEntries", 30_000L);
    	long nowT = now.getTime();
    	if ((lastPrune == null) || 
    			((nowT - lastPrune.getTime()) > seenMapTimeout) ||
    			(getMapSize(seenMap) > maxSeenMapSize) || 
    			(pruneDays != null && getMapSize(seenMap) < maxSeenMapSize * 60 / 100)) {
    		if(seenMap instanceof TLongLongHashMap) {
    			final TLongArrayList removeList = new TLongArrayList();
    			TLongLongHashMap troveMap = (TLongLongHashMap) seenMap;
    			troveMap.forEachEntry(new TLongLongProcedure() {
					@Override
					public boolean execute(long key, long val) {
						if(nowT - val > seenMapTimeout)
							removeList.add(key);
						return true;
					}
    			});
        		for (int i=0; i<removeList.size(); ++i)
        			troveMap.remove(removeList.get(i));
    		}
    		else {
    			// (Bugzilla #942648) JK 11/14/2016
    			// On-demand migration from java.util.Map to gnu.trove.map.hash.TLongLongHashMap
    			// for more efficient and smaller storage representation.
    			Map<Long,Date> jdkMap = (Map<Long,Date>) seenMap;
    	    	Map.Entry<Long,Date> me;
    			TLongLongHashMap troveMap = new TLongLongHashMap();
        		Iterator<Map.Entry<Long,Date>> it = jdkMap.entrySet().iterator();
        		while (it.hasNext()) {
        			 me = it.next();
        			if (nowT - ((Date)me.getValue()).getTime() <= seenMapTimeout)
        				// This entry is still valid - Transfer it to the new map.
        				troveMap.put(me.getKey(), ((Date)me.getValue()).getTime());
        		}
        		// Switch the map
        		seenMap = troveMap;
    		}   		
    	   	this.lastPrune = now;
    	    //See if this pruning got it down to below 80% of the max
    	    if (getMapSize(seenMap) > maxSeenMapSize * 80 / 100) {
    	    	//The map is too large, we will try pruning it more by limiting the pruneDays a little
    	    	if (pruneDays == null) {
    	    		pruneDays = ObjectKeys.SEEN_TIMEOUT_DAYS;
    	    	}
    	    	pruneDays--;
    	    	if (pruneDays < 0) pruneDays = 0L;
    	    	//The next pruning cycle should cut a few more out of the map
    	    	//This will occur when the next entry is marked seen.
    	    	
    	    } else if (getMapSize(seenMap) < maxSeenMapSize * 60 / 100) {
    	    	//The number of entries in the map is now below 60%, we can increase the prune days if they had been cut back
    	    	if (pruneDays != null) {
    	    		pruneDays++;
    	    		if (pruneDays >= ObjectKeys.SEEN_TIMEOUT_DAYS) {
    	    			//Ok, we are back to the default setting, clear pruneDays
    	    			pruneDays = null;
    	    		}
    	    	}
    	    }
    	}

    }
    
    public void setSeen(Entry entry) {
    	if (entry instanceof FolderEntry) 
    		checkAndSetSeen((FolderEntry) entry, true);
    }
    public void setSeen(FolderEntry entry) {
    	checkAndSetSeen(entry, true);
    }
    public void setSeen(Long entryId) {
    	checkAndSetSeen(entryId, null, true);
    }
    public void setSeen(Collection<Long> entryIds) {
    	if(entryIds.isEmpty()) return;	
		Date now = new Date();
		for(Long id:entryIds) {
			checkAndSetSeen(id, null, true, now, false);
		}
		// Prune the map only once at the end
		pruneMap(now);
    }
	private void setSeen(List<FolderEntry> entries) {
		if(entries.isEmpty()) return;
		Date now = new Date();
		for(FolderEntry entry:entries) {
			checkAndSetSeen(entry.getId(), entry.getLastActivity(), true, now, false);
		}
		// Prune the map only once at the end
		pruneMap(now);
	}
    /**
     * Mark an entry and all of its descendants (i.e. replies) as seen
     * @param entry
     */
    public void setSeenRecursive(FolderEntry entry) {
    	List<FolderEntry> entries = new ArrayList<FolderEntry>();
		LinkedList<FolderEntry> items = new LinkedList<FolderEntry>();
		items.add(entry);
		FolderEntry item;
		while((item = items.poll()) != null) {
			entries.add(item);
			items.addAll(item.getReplies());	
		}
		setSeen(entries);
    }
    public void setUnseen(Long entryId) {
    	if(mapContainsKey(seenMap, entryId))
    		mapRemove(seenMap, entryId);
    }
    public boolean checkIfSeen(FolderEntry entry) {
    	return checkAndSetSeen(entry, false);
    }
	protected boolean checkAndSetSeen(FolderEntry entry, boolean setIt) {	
		return checkAndSetSeen(entry.getId(), entry.getLastActivity(), setIt);
	}
	public boolean checkAndSetSeen(Map entry, boolean setIt) {
		Long id = new Long((String)entry.get(Constants.DOCID_FIELD));

     	// Do we have the date of the last activity?
		Date modDate = (Date)entry.get(Constants.LASTACTIVITY_FIELD);		
    	if ( modDate == null )
    	{
    		// No, use the modification date.
    		modDate = (Date) entry.get( Constants.MODIFICATION_DATE_FIELD );
    		if ( modDate == null )
    			return true;
    	}
    	return checkAndSetSeen(id, modDate, setIt);
	}	
    public boolean checkIfSeen(Map entry) {
     	Long id = new Long((String)entry.get(Constants.DOCID_FIELD));
     	
     	// Do we have the date of the last activity?
		Date modDate = (Date)entry.get(Constants.LASTACTIVITY_FIELD);		
    	if ( modDate == null )
    	{
    		// No, use the modification date.
    		modDate = (Date) entry.get( Constants.MODIFICATION_DATE_FIELD );
    		if ( modDate == null )
    			return true;
    	}
    	return checkAndSetSeen(id, modDate, false);
    }   
	public boolean checkAndSetSeen(Long id, Date modDate, boolean setIt) {
		return checkAndSetSeen(id, modDate, setIt, new Date(), true);
	}
	
	private boolean checkAndSetSeen(Long id, Date modDate, boolean setIt, Date now, boolean allowMapPruning) {
      	boolean ret = false;
      	long seenTime = -1L;
      	Object seen = mapGet(seenMap, id);
      	if(seen instanceof Date)
      		seenTime = ((Date)seen).getTime();
      	else if(seen instanceof Long)
      		seenTime = ((Long)seen).longValue();
    	Long seenMapTimeout = ObjectKeys.SEEN_MAP_TIMEOUT;
    	if (pruneDays != null) {
    		//This user is being cut back, so use the value in pruneDays for the timeout
    		seenMapTimeout = pruneDays * 24 * 60 * 60 * 1000;  //Days converted into milliseconds
    	}
        if (seenTime < 0) {
    		if ((modDate != null) && (now.getTime() - modDate.getTime()) > seenMapTimeout) {
     		    ret = true;
    		}
    	} else {
    		if (modDate != null && seenTime >= modDate.getTime()) {
    			ret = true; 
    		}
    	}
		if (setIt) {
			if (!ret) 
				mapPut(seenMap, id, now);
			if(allowMapPruning)
				pruneMap(now);
		}
		return ret;
	}

	private int getMapSize(Object map) {
		if(map instanceof TLongLongHashMap)
			return ((TLongLongHashMap)map).size();
		else if(map instanceof Map)
			return ((Map)map).size();
		else
			throw new IllegalArgumentException("Unexpected object type: " + map.getClass().getName());
	}
	
	private boolean mapContainsKey(Object map, Long key) {
		if(map instanceof TLongLongHashMap)
			return ((TLongLongHashMap)map).containsKey(key);
		else if(map instanceof Map)
			return ((Map)map).containsKey(key);
		else
			throw new IllegalArgumentException("Unexpected object type: " + map.getClass().getName());
	}
	
	private Object mapGet(Object map, Long key) {
		if(map instanceof TLongLongHashMap) {
			if(((TLongLongHashMap)map).containsKey(key))
				return Long.valueOf(((TLongLongHashMap)map).get(key)); // Long
			else
				return null;
		}
		else if(map instanceof Map)
			return ((Map)map).get(key); // Date
		else
			throw new IllegalArgumentException("Unexpected object type: " + map.getClass().getName());
	}
	
	private void mapPut(Object map, Long key, Date val) {
		if(map instanceof TLongLongHashMap)
			((TLongLongHashMap)map).put(key, val.getTime());
		else if(map instanceof Map)
			((Map)map).put(key, val);
		else
			throw new IllegalArgumentException("Unexpected object type: " + map.getClass().getName());
	}
	
	private void mapRemove(Object map, Long key) {
		if(map instanceof TLongLongHashMap)
			((TLongLongHashMap)map).remove(key);
		else if(map instanceof Map)
			((Map)map).remove(key);
		else
			throw new IllegalArgumentException("Unexpected object type: " + map.getClass().getName());
	}
}