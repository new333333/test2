/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.List;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jdom.Element;

import com.sitescape.util.Validator;
import com.sitescape.ef.util.CollectionUtil;

/**
* @hibernate.class table="SS_Principals" dynamic-update="true" node="Principal"
* @hibernate.discriminator type="string" length="1" column="type"
* @hibernate.query name="find-User-Company" query="from com.sitescape.ef.domain.User user where user.name=:userName and user.zoneName=:zoneName"
* @hibernate.mapping auto-import="false"
* @hibernate.cache usage="read-write"
* need auto-import = false so names don't collide with jbpm
* @author Jong Kim
*
*/
public class Principal extends Entry implements MultipleWorkflowSupport {
	protected boolean disabled=false;
    protected String name;
    protected String lcName;
    protected String foreignName="";
    protected List memberOf;
    protected String signature="";    
    protected String zoneName;
    protected Long preferredWorkspaceId;
    protected boolean reserved;
    protected boolean defaultIdentity;
    //events the principal is assigned to
    protected List assignments;
    protected List workflowStates;   
    protected HistoryStamp workflowChange;
    
	/**
 	 * @hibernate.map  lazy="true" inverse="true" cascade="all,delete-orphan" embed-xml="false"
 	 * @hibernate.key column="principal"
     * @hibernate.map-key column="name" type="string"
	 * @hibernate.one-to-many class="com.sitescape.ef.domain.CustomAttribute"
     * @return
     */
    private Map getHCustomAttributes() {return customAttributes;}
    private void setHCustomAttributes(Map customAttributes) {this.customAttributes = customAttributes;}   	
   
    /**
     * @hibernate.bag  lazy="true" inverse="true" cascade="all,delete-orphan" embed-xml="false"
 	 * @hibernate.key column="principal"
 	 * @hibernate.one-to-many class="com.sitescape.ef.domain.Attachment"
   	 */
    private List getHAttachments() {return attachments;}
    private void setHAttachments(List attachments) {this.attachments = attachments;}   	
     /**
 	 * @hibernate.bag  lazy="true" inverse="true" cascade="all,delete-orphan" embed-xml="false"
 	 * @hibernate.key column="principal"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.Event"
     * @return
     */
    private List getHEvents() {return allEvents;}
    private void setHEvents(List events) {this.allEvents = events;}   	

    /**
	 * @hibernate.bag lazy="true" inverse="true" cascade="all,delete-orphan" 
     * @hibernate.key column="principal"
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
     * @hibernate.property
     * @return
     */
    public boolean isDefaultIdentity() {
    	return defaultIdentity;
    }
    public void setDefaultIdentity(boolean defaultIdentity) {
    	this.defaultIdentity = defaultIdentity;
    }
    
    /**
     * @hibernate.property
     * Reserverd principals cannot be deleted or disabled
     * @return
     */
    public boolean isReserved() {
    	return reserved;
    }
    public void setReserved(boolean reserved) {
    	this.reserved = reserved;
    }
    /**
     * @hibernate.property
     * @hibernate.column name="preferredWorkspace"
     * Load ourselves - cause not always needed and don't want to proxy
     * @return
     */
    public Long getPreferredWorkspaceId() {
      	return preferredWorkspaceId;
    }
    public void setPreferredWorkspaceId(Long ws) {
       	this.preferredWorkspaceId = ws;         	
    }
 
    
    /**
     * @hibernate.property length="100" not-null="true"
     */
    public String getZoneName() {
    	return this.zoneName;
    }
    public void setZoneName(String id) {
    	this.zoneName = id;
    }

	/**
     * @hibernate.property length="256"
	 * @return
	 */	
	public String getSignature() {
	    return this.signature;
	}
	public void setSignature(String signature) {
	    this.signature = signature;
	} 
    /**
     * @hibernate.bag table="SS_AssignmentsMap" lazy="true" inverse="true" cascade="persist,merge,save-update" optimistic-lock="false" embed-xml="false"
     * @hibernate.key column="principal"
	 * @hibernate.many-to-many fetch="join" class="com.sitescape.ef.domain.Event"
	 * @hibernate.column name="event" sql-type="char(32)"
    */
	private List getHAssignments() {return assignments;}
	private void setHAssignments(List assignments) {this.assignments = assignments;}
     
	public List getAssignments() {
     	if (assignments == null) assignments = new ArrayList();
     	return assignments;
     }
     public void setAssigments(Collection newAssigments) {
   		if (assignments == null) assignments = new ArrayList();
		Set newM = CollectionUtil.differences(newAssigments, assignments);
		Set remM = CollectionUtil.differences(assignments, newAssigments);
		this.assignments.addAll(newM);
		this.assignments.removeAll(remM);
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Event e = (Event)iter.next();
			e.getAssignees().add(this);
		}
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Event e = (Event)iter.next();
			e.getAssignees().remove(this);
		}
     } 	
     
     /**
     * @hibernate.property 
     * @return Return disabled
     */
    public boolean isDisabled() {
        return this.disabled;
    }
    public void setDisabled(boolean disabled) {
       this.disabled = disabled;
    }
    /**
     * Along with the zone this is used to force case-insensitive 
     * names to be unique in db
     * 
     * @hibernate.property length="82"
     * @return Returns the lowercase version of loginName
     */
    protected String getLcName() {
        return this.lcName;
    }

    protected void setLcName(String lcName) {
        this.lcName = lcName;
    }

    /**
     * @hibernate.property length="82"
     * @return Returns the loginName.
     */
    public String getName() {
        return this.name;
    }
    /**
     * @param loginName The loginName to set.
     */
    public void setName(String name) {
    	if (Validator.isNull(name)) throw new IllegalArgumentException("null name");
        this.name = name;
    }
 
    /**
     * @hibernate.property length="256"
     * @return
     */
    public String getForeignName() {
    	return foreignName;
    }
    public void setForeignName(String foreignName) {
    	this.foreignName = foreignName;
    }
    /**
     * Group membership is managed by the Group
     * @hibernate.bag table="SS_PrincipalMembership" lazy="true" inverse="true" cascade="persist,merge,save-update" optimistic-lock="false"  node="."
	 * @hibernate.key column="userId" 
	 * @hibernate.many-to-many column="groupId" fetch="join" class="com.sitescape.ef.domain.Group" node="Group" embed-xml="false"
	 * @hibernate.cache usage="read-write"
     */
    private List getHMemberOf() {return memberOf;}
    private void setHMemberOf(List memberOf) {this.memberOf = memberOf;}
    
    public List getMemberOf() {
    	if (memberOf == null) memberOf = new ArrayList();
    	return memberOf;
    }
   	/**
	 * Remove the current groups from the user and add new groups.
	 * This method will also add/remove the user from each group as needed.
	 * @param groups
	 */
    public void setMemberOf(Collection groups) {
   		if (memberOf == null) memberOf = new ArrayList();
		Set newM = CollectionUtil.differences(groups, memberOf);
		Set remM = CollectionUtil.differences(memberOf, groups);
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Group g = (Group)iter.next();
			g.getMembers().add(this);
		}
		for (Iterator iter=remM.iterator(); iter.hasNext();) {
			Group g = (Group)iter.next();
			g.getMembers().remove(this);
		}
     } 	

 
    public String toString() {
    	return zoneName + ":" + name;
    }
 }

