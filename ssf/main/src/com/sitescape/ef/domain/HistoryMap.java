
package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.sitescape.ef.ObjectKeys;

/**
 * @hibernate.class table="SS_HistoryMap" dynamic-update="true"
 * 
 * @author Janet McCann
 * Manage the historyMap for a folder.  As an optimization,
 * the historyMap is managed by a cached session object and only updated periodically.  Because of
 * this the historyMap may be incorrect if a system crashes.
 *
 */
public class HistoryMap {
	private UserPerFolderPK id;
	private LinkedHashMap historyMap;
	private Date lastPrune;
	
	protected HistoryMap() {		
		//only called by hibernate.  Prevent null maps.
	}
	public HistoryMap(UserPerFolderPK key) {
		setId(key);
		setHistoryMap(new LinkedHashMap());
	}
	public HistoryMap(Long principalId, Long folderId) {
		setId(new UserPerFolderPK(principalId, folderId));
		setHistoryMap(new LinkedHashMap());
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
	public LinkedHashMap getHistoryMap() {
		return historyMap;
	}
	public void setHistoryMap(LinkedHashMap historyMap) {
		this.historyMap = historyMap;
	}
	/**
	 * @hibernate.property
	 * @param entry
	 */
	protected Date getLastPrune() {
		return lastPrune;
	}
	protected void setLastPrune(Date lastPrune) {
		this.lastPrune = lastPrune;
	}

	public void setSeenInPlace(Entry entry, Date now) {
      	Long id = entry.getId();
		historyMap.put(id, now);
		pruneMap(now);
	}
	public void setSeen(Entry entry) {
		setSeen(entry, new Date());
	}
	public void setSeen(Entry entry, Date now) {
      	Long id = entry.getId();
 		if (historyMap.containsKey(id)) historyMap.remove(id);
		historyMap.put(id, now);
		pruneMap(now);
	}

    protected void pruneMap(Date now) {
    	Iterator it;
    	Map.Entry me;
    	long nowT = now.getTime();
    	if ((lastPrune == null) || ((nowT - lastPrune.getTime()) > ObjectKeys.SEEN_HISTORY_MAP_TIMEOUT)) {
        	ArrayList removeList = new ArrayList();
    		it = historyMap.entrySet().iterator();
    		while (it.hasNext()) {
    			me = (Map.Entry) it.next();
    			if (nowT - ((Date)me.getValue()).getTime() > ObjectKeys.SEEN_HISTORY_MAP_TIMEOUT) {
    				removeList.add(me.getKey());
    			}
    		}
       		for (int i=0; i<removeList.size(); ++i) {
    			historyMap.remove(removeList.get(i));
    		}
    		setLastPrune(now);
		}
    }
    
    public void sortMap() {
    	Comparator c = new HistoryMapComparator();
    	SortedSet ss = new TreeSet(c);
    	ss.addAll(historyMap.entrySet());
    	historyMap.clear();
    	Iterator it = ss.iterator();
    	while (it.hasNext()) {
    		Map.Entry me = (Map.Entry) it.next();
    		historyMap.put(me.getKey(), me.getValue());
    	}
    }

	public Long getNextHistoryEntry() {
		Long entryId = null;
		//No entryId was specified, so get the most recent entry on the list (which is the last on the list)
		Iterator it = historyMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry me = (Map.Entry) it.next();
			entryId = (Long) me.getKey();
		}
		return entryId;
	}
	public Long getNextHistoryEntry(Long currentEntryId) {
		Long entryId = null;
		if (historyMap.containsKey(currentEntryId)) {
			Iterator it = historyMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				if (((Long)me.getKey()).equals(currentEntryId)) {
					break;
				}
				entryId = (Long)me.getKey();
			}
		}
		return entryId;
	}
	public Long getPreviousHistoryEntry(Long currentEntryId) {
		Long entryId = null;
		if (historyMap.containsKey(currentEntryId)) {
			Iterator it = historyMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				if (((Long)me.getKey()).equals(currentEntryId)) {
					if (it.hasNext()) {
						Map.Entry nextMe = (Map.Entry) it.next();
						entryId = (Long) nextMe.getKey();
						break;
					} else {
						break;
					}
				}
			}
		}
		return entryId;
	}
}
