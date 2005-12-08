
package com.sitescape.ef.domain;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.domain.UserPerFolderPK;

/**
 * @hibernate.class table="SS_SeenMap" dynamic-update="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * 
 * @author Janet McCann
 * Manage the seen map for a folder.  
 */
public class SeenMap {
	protected UserPerFolderPK id;
	protected Map seenMap;
	protected Date lastPrune;
	
	
	protected SeenMap() {		
		//only called by hibernate.  Prevent null maps.
	}
	public SeenMap(UserPerFolderPK key) {
		setId(key);
		setSeenMap(new HashMap());
	}
	public SeenMap(Long principalId, Long folderId) {
		setId(new UserPerFolderPK(principalId, folderId));
		setSeenMap(new HashMap());
	}
	/**
 	 * @hibernate.composite-id
	 **/
	public UserPerFolderPK getId() {
		return id;
	}
	public void setId(UserPerFolderPK id) {
		this.id = id;
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
      	Date seen,modDate,now;
      	boolean ret = false;
      	Long id = entry.getId();
      	seen = (Date)seenMap.get(id);
		modDate = entry.getModification().getDate();
   		now = new Date();
        if (seen == null) {
    		if ((now.getTime() - modDate.getTime()) > ObjectKeys.SEEN_MAP_TIMEOUT) {
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