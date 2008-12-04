/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */

package org.kablink.teaming.domain;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;

import org.kablink.teaming.ObjectKeys;
import org.kablink.util.search.Constants;


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
	protected Map seenMap;
	protected Date lastPrune;
	
	
	protected SeenMap() {		
		//only called by hibernate.  Prevent null maps.
	}

	public SeenMap(Long principalId) {
		setSeenMap(new HashMap());
		setPrincipalId(principalId);
	}
	/**
 	 * @hibernate.id generator-class="assigned"
 	 */
	public Long getPrincipalId() {
		return principalId;
	}
	public void setPrincipalId(Long principalId) {
		this.principalId = principalId;
	}	

	
	/**
	 * @hibernate.property type="org.springframework.orm.hibernate3.support.BlobSerializableType" not-null="true"
	 * @return
	 */
	public Map getSeenMap() {
		return seenMap;
	}
	protected void setSeenMap(Map seenMap) {
		this.seenMap = seenMap;
	}

	/**
	 * @hibernate.property
	 * @param entry
	 */
	protected Date getLastPrune() {
		return lastPrune;
	}
	protected void setLastPrune(Date lastSeenPrune) {
		this.lastPrune = lastSeenPrune;
	}

    protected void pruneMap(Date now) {
    	Iterator it;
    	Map.Entry me;
    	long nowT = now.getTime();
    	if ((lastPrune == null) || ((nowT - lastPrune.getTime()) > ObjectKeys.SEEN_MAP_TIMEOUT)) {
        	ArrayList removeList = new ArrayList();
    		it = seenMap.entrySet().iterator();
    		while (it.hasNext()) {
    			 me = (Map.Entry) it.next();
    			if (nowT - ((Date)me.getValue()).getTime() > ObjectKeys.SEEN_MAP_TIMEOUT) {
    				removeList.add(me.getKey());
    			}
    		}
    		for (int i=0; i<removeList.size(); ++i) {
    			seenMap.remove(removeList.get(i));
    		}
    	   	removeList.clear();
    	    setLastPrune(now);
    	}

    }
    public void setSeen(Entry entry) {
    	if (entry instanceof FolderEntry) checkAndSetSeen((FolderEntry) entry, true);
    }
    public void setSeen(FolderEntry entry) {
    	checkAndSetSeen(entry, true);
    }
    public boolean checkIfSeen(FolderEntry entry) {
    	return checkAndSetSeen(entry, false);
    }
	protected boolean checkAndSetSeen(FolderEntry entry, boolean setIt) {
		if (!entry.isTop()) return true; //only maintain for top
		return checkAndSetSeen(entry.getId(), entry.getLastActivity(), setIt);
	}
	public boolean checkAndSetSeen(Map entry, boolean setIt) {
		if (Constants.ENTRY_TYPE_REPLY.equals(entry.get(Constants.ENTRY_TYPE_FIELD))) return true;
		Long id = new Long((String)entry.get(Constants.DOCID_FIELD));
		Date modDate = (Date)entry.get(Constants.LASTACTIVITY_FIELD);		
		if (modDate == null) return true;
    	return checkAndSetSeen(id, modDate, setIt);
	}	
    public boolean checkIfSeen(Map entry) {
		if (Constants.ENTRY_TYPE_REPLY.equals(entry.get(Constants.ENTRY_TYPE_FIELD))) return true;
     	Long id = new Long((String)entry.get(Constants.DOCID_FIELD));
		Date modDate = (Date)entry.get(Constants.LASTACTIVITY_FIELD);		
    	if (modDate == null) return true;
    	return checkAndSetSeen(id, modDate, false);
    }   
    
	public boolean checkAndSetSeen(Long id, Date modDate, boolean setIt) {
      	Date seen,now;
      	boolean ret = false;
      	seen = (Date)seenMap.get(id);
   		now = new Date();
        if (seen == null) {
    		if ((modDate != null) && (now.getTime() - modDate.getTime()) > ObjectKeys.SEEN_MAP_TIMEOUT) {
     		    ret = true;
    		}
    	} else {
    		if (modDate != null && seen.compareTo(modDate) >= 0) {
    			ret = true; 
    		}
    	}
		if (setIt) {
			if (!ret) seenMap.put(id, now);
			pruneMap(now);
		}
		return ret;
	}	
}