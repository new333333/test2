package com.sitescape.ef.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Iterator;
import java.util.ArrayList;




/**
 * @hibernate.class table="SS_FolderEntries" dynamic-update="true" lazy="true"
 * @hibernate.mapping auto-import="false"
 * need auto-import = false so names don't collide with jbpm
 * <code>FolderEntry</code> represents a entry or a reply.
 *
 */
public class FolderEntry extends AclControlledEntry implements MultipleWorkflowSupport {

	protected Folder parentFolder;
    protected boolean allowEdits = false;
    protected HistoryStamp reservedDoc;
    protected boolean sendMail = false;
    public static final int ABSTRACT=1;
    public static final int FILESET= 2;
    public static final int URL=3;
    public static final int WEBFILE=4;
    protected int docContent=ABSTRACT;
    protected List replies;
    protected HKey docHKey;
    protected int replyCount=0;
    protected int nextDescendant=1;
    protected Date lastActivity;
    protected int totalReplyCount=0;
    protected FolderEntry topEntry;
    protected FolderEntry parentEntry;
    protected String owningFolderSortKey;
    protected List workflowStates;   
    protected HistoryStamp workflowChange;
    //missing
    String docProps;
 
    public FolderEntry() {
        super();
    }
	/**
 	 * @hibernate.map  lazy="true" inverse="true" cascade="all,delete-orphan"
	 * @hibernate.key column="folderEntry"
 	 * @hibernate.map-key column="name" type="string"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.CustomAttribute"
     * @return
     */
    private Map getHCustomAttributes() {return customAttributes;}
    private void setHCustomAttributes(Map customAttributes) {this.customAttributes = customAttributes;}   	
    
    /**
     * @hibernate.bag  lazy="true" inverse="true" cascade="all,delete-orphan" 
 	 * @hibernate.key column="folderEntry"
 	 * @hibernate.one-to-many class="com.sitescape.ef.domain.Attachment"
   	 */
    private List getHAttachments() {return attachments;}
    private void setHAttachments(List attachments) {this.attachments = attachments;}   	

   /**
	* @hibernate.bag lazy="true" inverse="true" cascade="all,delete-orphan" 
    * @hibernate.key column="folderEntry"
    * @hibernate.one-to-many class="com.sitescape.ef.domain.Event"
    * @return
    */
    private List getHEvents() {return allEvents;}
    private void setHEvents(List events) {this.allEvents = events;}   	
    /**
     * @hibernate.bag lazy="true" cascade="all,delete-orphan" inverse="true" optimistic-lock="false"
     * @hibernate.key column="parentEntry"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.FolderEntry"
     */
    private List getHReplies() {return replies;}
    private void setHReplies(List replies) {this.replies = replies;}   	
   
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
    /**
     * @hibernate.many-to-one
     * @return
     */
    public Folder getParentFolder() {
        return parentFolder;
    }
    public void setParentFolder(Folder parentFolder) {
        this.parentFolder = parentFolder;
    }
    public Folder getTopFolder() {
		Folder f = parentFolder.getTopFolder();
  		if (f != null) {
  			return f;
  		} else {
  			return parentFolder;
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
	 * @hibernate.bag lazy="true" inverse="true" cascade="all,delete-orphan" 
     * @hibernate.key column="folderEntry"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.WorkflowStateObject"
     * @return
     */
     public List getHWorkflowStates() {
        return workflowStates;
        
     }
     public void setHWorkflowStates(List workflowStates) {
        this.workflowStates = workflowStates;
     }

     public List getWorkflowStates() {
   	 	if (workflowStates == null) return new ArrayList();
   	 	return workflowStates;  
     }
     public void setWorkflowStates(List workflowStates) {
    	 //Since ids are assigned on WorkflowState, don't need to do anything
    	 //special to reduce updates.
    	 this.workflowStates = workflowStates;
     }
   
     public void addWorkflowState(WorkflowState state) {
    	List wf = getWorkflowStates();
    	
    	for (int i=0; i<wf.size(); ++i) {
    		WorkflowState c = (WorkflowState)wf.get(i);
    		if (c.getTokenId().equals(state.getTokenId())) {
    			wf.remove(c);
    		}
    	}
    	wf.add(state);
    }
    public void removeWorkflowState(WorkflowState state) {
    	List wf = getWorkflowStates();
    	
    	for (int i=0; i<wf.size(); ++i) {
    		WorkflowState c = (WorkflowState)wf.get(i);
    		if (c.getTokenId().equals(state.getTokenId())) {
    			wf.remove(c);
    		}
    	}
    }
    /**
     * @hibernate.component class="com.sitescape.ef.domain.HistoryStamp" prefix="wrk_" 
     */
    public HistoryStamp getWorkflowChange() {
        return this.workflowChange;
    }
    public void setWorkflowChange(HistoryStamp workflowChange) {
        this.workflowChange = workflowChange;
    }

    /**
     * @hibernate.component class="com.sitescape.ef.domain.HKey" prefix="entry_"
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
        child.setParentFolder(parentFolder);
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
        parentFolder.removeEntry(child);
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
     * A reply was repmoved somewhere in the decendant tree
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
