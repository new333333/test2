/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

import org.kablink.teaming.fi.connection.ResourceDriver;
import org.kablink.teaming.security.function.WorkArea;

/**
 * ?
 * 
 * @hibernate.class table="SS_FolderEntries" dynamic-update="true" lazy="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * <code>FolderEntry</code> represents a entry or a reply.
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class FolderEntry extends WorkflowControlledEntry implements WorkflowSupport, Reservable {
    protected HistoryStamp reservation;
	protected List replies;//initialized by hibernate access=field
    protected HKey docHKey;
    protected int replyCount=0;
    protected int nextDescendant=1;
    protected Date lastActivity;
    protected int totalReplyCount=0;
    protected FolderEntry topEntry;
    protected FolderEntry parentEntry;
    protected String owningBinderKey;
    // Number of locked files - This refers to all "not-yet-cleared" locks
    // including both effective and expired locks. 
    protected String postedBy;
    protected boolean subscribed=false;
	protected Boolean preDeleted;
	protected Long preDeletedWhen;
	protected Long preDeletedBy;
    protected Long popularity;
    protected FolderEntryStats folderEntryStats;
    protected String resourceHandle;
    
    public FolderEntry() {
        super();
    }
    /**
     * Copy constructor.
     * @param entry must be specified
     * @param entryDef optional
     */
    public FolderEntry(FolderEntry entry, Definition entryDef) {
    	super(entry);
    	//DO not copy reservation, replies, docHKey, replyCount, nextDescendant, topReplyCount
    	// topEntry, parentEntry, owningBinderKey, lockedFileCount, subscribed
    	lastActivity = entry.lastActivity;
    	postedBy = entry.postedBy;
    	if(entryDef != null)
    		this.setEntryDef(entryDef);
    }
 	@Override
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.folderEntry;
	}
 
     public Folder getParentFolder() {
        return (Folder)getParentBinder();
    }
    public void setParentFolder(Folder parentFolder) {
        setParentBinder(parentFolder);
    }
    public Folder getRootFolder() {
		return getParentFolder().getRootFolder();

    }
    /** 
     * @hibernate.property 
     * @return
     */
    public String getPostedBy() {
    	return postedBy;
    }
    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
    /**
     * @hibernate.component class="org.kablink.teaming.domain.HistoryStamp" prefix="reserved_"
     */
    @Override
	public HistoryStamp getReservation() {
        return this.reservation;
    }

    @Override
	public void setReservation(HistoryStamp reservation) {
        this.reservation = reservation;
    }
    @Override
	public void setReservation(User owner) {
        setReservation(new HistoryStamp(owner));
    }    
    
    @Override
	public void clearReservation() {
    	this.reservation = null;
    }
    
    /**
     * @hibernate.component
     */
    public HKey getHKey() {
        return docHKey;
    }
    public void setHKey(HKey docHKey) {
        this.docHKey = docHKey;
    }
    public int getDocLevel() {
        return getHKey().getLevel();
    }
    public String getDocNumber() {
        return getHKey().getEntryNumber();
    }
    /**
     * @hibernate.property length="255" 
     * @return
     */
    public String getOwningBinderKey() {
        return owningBinderKey;
    }
    public void setOwningBinderKey(String owningBinderKey) {
        this.owningBinderKey = owningBinderKey;
    } 
    /**
     * @hibernate.property 
     * @return
     */
    public Date getLastActivity() {
        return this.lastActivity;
    }

    public void setLastActivity(Date lastActivity) {
        this.lastActivity = lastActivity;
    }
    public void updateLastActivity(Date lastActivity) {
    	if (lastActivity == null) return;
		//have to handle ourselves, cause hibernate uses 
    	//java.sql.TimeStamp and doesn't compare with Date
    	if ((this.lastActivity == null) ||
    			(this.lastActivity.getTime() < lastActivity.getTime())) {
    		this.lastActivity = lastActivity;
    		//propagate up the tree
    		if (topEntry != null) topEntry.updateLastActivity(lastActivity);
    	}
    }
    @Override
	public void setWorkflowChange(HistoryStamp workflowChange) {
    	super.setWorkflowChange(workflowChange);
    	if (workflowChange == null) return;
    	updateLastActivity(workflowChange.getDate()); 
    }
    /**
     * @hibernate.property not-null="true"
     */
    public int getReplyCount() {
        return this.replyCount;
    }
    protected void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }
    /**
     * @hibernate.property not-null="true"
     */
    public int getNextDescendant() {
        return this.nextDescendant;
    }
    protected void setNextDescendant(int nextDescendant) {
        this.nextDescendant = nextDescendant;
    }
    /**
     * @hibernate.property not-null="true"
     * @return
     */
    public int getTotalReplyCount() {
        return this.totalReplyCount;
    }

    protected void setTotalReplyCount(int totalReplyCount) {
        this.totalReplyCount = totalReplyCount;
    }



    /**
     * @hibernate.many-to-one class="org.kablink.teaming.domain.FolderEntry"
     * @return
     */
    public FolderEntry getTopEntry() {
        return topEntry;
    }
    public void setTopEntry(FolderEntry topEntry) {
        this.topEntry = topEntry;
    }
    @Override
	public boolean isTop() {
    	return topEntry == null;
    }

    /**
     * @hibernate.many-to-one class="org.kablink.teaming.domain.FolderEntry"
     * @return
     */
    public FolderEntry getParentEntry() {
         return parentEntry;
    }
    public void setParentEntry(FolderEntry parentEntry) {
        this.parentEntry = parentEntry;
    }
    public List getReplies() {
     	if (replies == null) replies = new ArrayList();
        return replies;
    }
    /*
     * Add a direct descendant
     */
    public void addReply(FolderEntry child) {
        getReplies().add(child);
        child.setParentEntry(this);
        if (topEntry == null) child.setTopEntry(this); else child.setTopEntry(topEntry);
        child.setHKey(new HKey(docHKey, nextDescendant++));
        child.setParentFolder(getParentFolder());
        child.setOwningBinderKey(owningBinderKey);
        ++replyCount;
        //if (!child.isPreDeleted()) addAncestor(child);  //Add the check for predeleted if you want to keep the counts accurate
        addAncestor(child);
    }
    public void addReply(FolderEntry child, int docNumber) {
       	if (docNumber < nextDescendant) throw new IllegalArgumentException("docNumber already exists");
       	nextDescendant = docNumber;
       	addReply(child);
   }
    public void removeReply(FolderEntry child) {
        if (!child.getParentEntry().getId().equals(this.getId())) {
            throw new NoFolderEntryByTheIdException(child.getId(),"Entry is not a child");
        }
        child.setParentEntry(null);
        child.setTopEntry(null);
        child.setHKey(null);
        getReplies().remove(child);
        --replyCount;
        //if (!child.isPreDeleted()) removeAncestor(child);  //Add the check for predeleted if you want to keep the counts accurate
        removeAncestor(child);
        getParentFolder().removeEntry(child);
    }

    public void preDeleteReply(FolderEntry child) {
        if (!child.getParentEntry().getId().equals(this.getId())) {
            throw new NoFolderEntryByTheIdException(child.getId(),"Entry is not a child");
        }
        removeAncestor(child);
    }

    public void restorePreDeletedReply(FolderEntry child) {
        if (!child.getParentEntry().getId().equals(this.getId())) {
            throw new NoFolderEntryByTheIdException(child.getId(),"Entry is not a child");
        }
        addAncestor(child);
    }
    
    //Currently, this is not called from anywhere.
    //The issue is where to add this such that it doesn't cause a large performance issue.
    //See bug report #641442
    public List recalculateTotalReplyCount() {
    	List<FolderEntry> changedReplies = new ArrayList<FolderEntry>();
    	Map<FolderEntry,Integer> allReplies = new HashMap<FolderEntry,Integer>();
		//Build a list of all replies
		buildTotalReplyList(allReplies, this);
		//Calculate all of the counts
		for (FolderEntry fe : allReplies.keySet()) {
			if (!fe.isPreDeleted() && !fe.isTop()) fe.getParentEntry().addAncestor(allReplies);
		}
		//Now see if any counts were wrong; if so, update them
		for (FolderEntry fe : allReplies.keySet()) {
			if (fe.getTotalReplyCount() != allReplies.get(fe)) {
				//The count didn't match, reset it
				fe.setTotalReplyCount(allReplies.get(fe));
				changedReplies.add(fe);
			}
		}
    	return changedReplies;
    }
    
    public void buildTotalReplyList(Map<FolderEntry,Integer> allReplies, FolderEntry reply) {
		if (!allReplies.containsKey(reply)) allReplies.put(reply, 0);
    	List<FolderEntry> replies = reply.getReplies();
    	for (FolderEntry child : replies) {
    		buildTotalReplyList(allReplies, child);
    	}
    }
    
    protected void addAncestor(Map<FolderEntry,Integer> allReplies) {
    	Integer count = allReplies.get(this);
    	if (!isPreDeleted()) {
    		allReplies.put(this, ++count);
	    	FolderEntry parent = getParentEntry();
	    	if (parent != null) {
	    		parent.addAncestor(allReplies);
	    	}
    	}
    }

    /* 
     * A reply was added somewhere in the tree.
     * Update cummulative replyCount
     */
    protected void addAncestor(FolderEntry reply) {
    	FolderEntry parent = getParentEntry();
       ++totalReplyCount;
       if (parent != null) {
           parent.addAncestor(reply);
       }
    }
    /*
     * A reply was removed somewhere in the decendant tree
     * Update cummulative replyCount
     */
    protected void removeAncestor(FolderEntry reply) {
    	FolderEntry parent = getParentEntry();
    	totalReplyCount = totalReplyCount - reply.getTotalReplyCount() -1;
    	if (totalReplyCount < 0) totalReplyCount = 0;
        if (parent != null) {
            parent.removeAncestor(reply);
        }
        
    }
    /**
     * Indicates if individual subscriptions to the is entry exist
     * Performance optimization
     * @hiberate.property
     * @return
     */
    public boolean isSubscribed() {
    	return subscribed;
    }
    public void setSubscribed(boolean subscribed) {
    	this.subscribed = subscribed;
    }
    
    /**
     * @hibernate.property
     * @return
     */
    public Boolean isPreDeleted() {
    	return ((null != preDeleted) && preDeleted);
    }
    public void setPreDeleted(Boolean preDeleted) {
    	this.preDeleted = preDeleted;
    }
    
	/**
	 * @hibernate.property 
	 * @return
	 */
	public Long getPreDeletedWhen() {
		return preDeletedWhen;
	}
	public void setPreDeletedWhen(Long preDeletedWhen) {
		this.preDeletedWhen = preDeletedWhen;
	}
	
	/**
     * @hibernate.property
	 * @return
     */
	public Long getPreDeletedBy() {
		return preDeletedBy;
	}
	public void setPreDeletedBy(Long preDeletedBy) {
		this.preDeletedBy = preDeletedBy;
	}
	
	
    //*****************WorkArea interface stuff***********/
    @Override
	public WorkArea getParentWorkArea() {
        //For replies, get the top entry as the parent
        return this.getTopEntry();
    }
    @Override
	public Set getChildWorkAreas() {
    	Set<Entry> result = new HashSet<Entry>();
		List<Entry> replies = this.getReplies();
		for (Entry reply : replies) {
			result.add(reply);
    	}
    	return result;
    }
	/**
	 * @hibernate.property not-null="true"
	 * @return
	 */
    @Override
	public boolean isFunctionMembershipInherited() {
    	if (this.isTop()) {
    		//The top entry does not inherit
    		return false;
    	} else {
    		//Replies always inherit from the parent
    		return true;
    	}
    }
    @Override
	public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
    }
	@Override
	public boolean isFunctionMembershipInheritanceSupported() {
		return true;
    }
    @Override
	public boolean isExtFunctionMembershipInherited() {
    	if (this.isTop()) {
    		//The top entry does not inherit
    		return false;
    	} else {
    		//Replies always inherit from the parent
    		return true;
    	}
    }
    @Override
	public void setExtFunctionMembershipInherited(boolean extFunctionMembershipInherited) {
    }
    @Override
	public Long getOwnerId() {
    	Principal owner = getOwner();
    	if (owner == null)	return null;
    	return owner.getId();
    }
     /**
      * Return the owner of the binder.
      * The owner default to the creator.
      * Used in access management.
      * @hibernate.many-to-one
      */
  	@Override
	public Principal getOwner() {
 	   	HistoryStamp creation = getCreation();
     	if ((creation != null) && creation.getPrincipal() != null) {
     		return creation.getPrincipal();
     	}
     	return null;
 		
 	}
 	@Override
	public void setOwner(Principal owner) {
 	}
     /*****************End WorkArea interface stuff***********/
 	
    public Long getPopularity() {
    	if(folderEntryStats != null) {
    		// Corresponding stats object exists, which means that the 'popularity' data 
    		// was already migrated from FolderEntry into FolderEntryStats.
    		return folderEntryStats.getPopularity();
    	}
    	else {
    		// The 'popularity' data has not been migrated from FolderEntry into FolderEntryStats yet.
    		return popularity;
    	}
    }
    
    public void setPopularity(Long popularity) {
    	if(popularity == null)
    		throw new IllegalArgumentException("Popularity must not be null");
    	if(folderEntryStats == null) {
    		// The 'popularity' data has not been migrated from FolderEntry into FolderEntryStats yet. Let's do it now.
    		folderEntryStats = new FolderEntryStats();
    		folderEntryStats.setFolderEntry(this); // bidirectional
    		folderEntryStats.setPopularity(popularity);
        	// Null out this field since it will no longer be used.
       	 	this.popularity = null;
    	}
    	else {
    		// The 'popularity' data has already been migrated from FolderEntry into FolderEntryStats
    		folderEntryStats.setPopularity(popularity);
    	}
    }
    
	public FolderEntryStats getFolderEntryStats() {
		return folderEntryStats;
	}
	public void setFolderEntryStats(FolderEntryStats folderEntryStats) {
		this.folderEntryStats = folderEntryStats;
	}
    
	public boolean noAclDredged() {
		ResourceDriver driver = getParentFolder().getResourceDriver();
		if (driver != null) {
			ResourceDriverConfig config = driver.getConfig();
			if (config != null) {
				return config.isAclAware();
			}
		}
		return false;
	}
	
	public String getResourceHandle() {
		return resourceHandle;
	}
	
	public void setResourceHandle(String resourceHandle) {
		this.resourceHandle = resourceHandle;
	}
	
    @Override
    public boolean supportsCustomFields() {
    	return !this.isAclExternallyControlled();
    }

}
