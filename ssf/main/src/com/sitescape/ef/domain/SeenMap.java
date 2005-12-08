
package com.sitescape.ef.domain;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.module.shared.EntryIndexUtils;

/**
 * @hibernate.class table="SS_SeenMap" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 * @author Janet McCann
 * Manage the seen map for a user. 
 */
public class SeenMap {
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
	protected Map getSeenMap() {
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
    	checkAndSetSeen(entry, true);
    }
    public boolean checkIfSeen(Entry entry) {
    	return checkAndSetSeen(entry, false);
    }
	protected boolean checkAndSetSeen(Entry entry, boolean setIt) {
		return checkAndSetSeen(entry.getId(), entry.getModification().getDate(), setIt);
	}
    public boolean checkIfSeen(HashMap entry) {
      	Long id = new Long((String)entry.get(EntryIndexUtils.DOCID_FIELD));
		Date modDate = (Date)entry.get(EntryIndexUtils.MODIFICATION_DATE_FIELD);		
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
    		if (seen.compareTo(modDate) > 0) {
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