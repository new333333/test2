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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;

import com.sun.org.apache.bcel.internal.generic.ISTORE;




/**
 * @hibernate.class table="SS_FolderEntries" dynamic-update="true" lazy="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * <code>FolderEntry</code> represents a entry or a reply.
 *
 */
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
    public FolderEntry() {
        super();
    }
    public FolderEntry(FolderEntry entry) {
    	super(entry);
    	//DO not copy reservation, replies, docHKey, replyCount, nextDescendant, topReplyCount
    	// topEntry, parentEntry, owningBinderKey, lockedFileCount, subscribed
    	lastActivity = entry.lastActivity;
    	postedBy = entry.postedBy;
    	
    }
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
    public HistoryStamp getReservation() {
        return this.reservation;
    }

    public void setReservation(HistoryStamp reservation) {
        this.reservation = reservation;
    }
    public void setReservation(User owner) {
        setReservation(new HistoryStamp(owner));
    }    
    
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
        removeAncestor(child);
        getParentFolder().removeEntry(child);
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
    
}
