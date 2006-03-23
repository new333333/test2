package com.sitescape.ef.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator;
import java.util.ArrayList;




/**
 * @hibernate.class table="SS_FolderEntries" dynamic-update="true" lazy="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * <code>FolderEntry</code> represents a entry or a reply.
 *
 */
public class FolderEntry extends WorkflowControlledEntry implements WorkflowSupport {

    protected boolean allowEdits = false;
    protected HistoryStamp reservedDoc;
    protected boolean sendMail = false;
    public static final int ABSTRACT=1;
    public static final int FILESET= 2;
    public static final int URL=3;
    public static final int WEBFILE=4;
    protected int docContent=ABSTRACT;
    protected List replies;//initialized by hibernate access=field
    protected HKey docHKey;
    protected int replyCount=0;
    protected int nextDescendant=1;
    protected Date lastActivity;
    protected int totalReplyCount=0;
    protected FolderEntry topEntry;
    protected FolderEntry parentEntry;
    protected String owningFolderSortKey;
     //missing
    String docProps;
 
    public FolderEntry() {
        super();
    }
    public String getAnyOwnerType() {
    	return AnyOwner.FOLDERENTRY;
    }
    public EntityIdentifier getEntityIdentifier() {
    	return new EntityIdentifier(getId(), EntityIdentifier.EntityType.folderEntry);
    }
    /**
     * @hibernate.property
     * @return
     */

    public int getDocContent() {
        return this.docContent;
    }
    public void setDocContent(int docContent) {
        this.docContent = docContent;
    }
     public Folder getParentFolder() {
        return (Folder)getParentBinder();
    }
    public void setParentFolder(Folder parentFolder) {
        setParentBinder(parentFolder);
    }
    public Folder getTopFolder() {
		Folder f = getParentFolder().getTopFolder();
  		if (f != null) {
  			return f;
  		} else {
  			return getParentFolder();
  		}

    }
    /** 
     * @hibernate.property 
     * @return
     */
    public boolean isAllowEdits() {
        return allowEdits;
    }
    public void setAllowEdits(boolean allowEdits) {
        this.allowEdits = allowEdits;
    }

    /**
     * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="reserved_"
     */
    public HistoryStamp getReservedDoc() {
        return this.reservedDoc;
    }

    public void setReservedDoc(HistoryStamp reservedDoc) {
        this.reservedDoc = reservedDoc;
    }
    public void setReservedDoc(User reservedDoc) {
        setReservedDoc(new HistoryStamp(reservedDoc));
    }    
    /** 	
     * @hibernate.property
     * @return
     */
    public boolean isSendMail() {
        return this.sendMail;
    }

    public void setSendMail(boolean sendMail) {
        this.sendMail = sendMail;
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
     * @hibernate.property length="512" 
     * @return
     */
    public String getOwningFolderSortKey() {
        return owningFolderSortKey;
    }
    protected void setOwningFolderSortKey(String owningFolderSortKey) {
        this.owningFolderSortKey = owningFolderSortKey;
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
     * @hibernate.many-to-one class="com.sitescape.ef.domain.FolderEntry"
     * @return
     */
    public FolderEntry getTopEntry() {
        return topEntry;
    }
    public void setTopEntry(FolderEntry topEntry) {
        this.topEntry = topEntry;
    }

    /**
     * @hibernate.many-to-one class="com.sitescape.ef.domain.FolderEntry"
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
        child.setOwningFolderSortKey(owningFolderSortKey);
        ++replyCount;
        addAncestor(child);
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
    
}
