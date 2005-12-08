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
* @hibernate.query name="find-User-Company" query="from User user where user.name=:userName and user.zoneName=:zoneName"
* @author Jong Kim
*
*/
public class Principal extends Entry {
	protected boolean disabled=false;
    protected String name;
    protected String lcName;
    protected String foreignName="";
    protected List memberOf;
    protected String signature="";    
    protected String stringId;
    protected String zoneName;
    protected Long preferredWorkspaceId;
    protected boolean reserved;
    protected boolean defaultIdentity;
    //events the principal is assigned to
    protected List assignments;
    
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
    private List getHAttachments() {return allAttachments;}
    private void setHAttachments(List attachments) {this.allAttachments = attachments;}   	
     /**
 	 * @hibernate.bag  lazy="true" inverse="true" cascade="all,delete-orphan" embed-xml="false"
 	 * @hibernate.key column="principal"
     * @hibernate.one-to-many class="com.sitescape.ef.domain.Event"
     * @return
     */
    private List getHEvents() {return allEvents;}
    private void setHEvents(List events) {this.allEvents = events;}   	

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

 
    protected static String encodeXmlRef(Long id) {
        return "<principalRef principalId=\"" + id +  "\"/>";
    }    
  
    protected static String encodeXmlRefs(Set ref) {
        Object obj;
        Long id;
        if ((ref == null) || ref.isEmpty()) return "";
        StringBuffer buf = new StringBuffer(64);
        Iterator iter = ref.iterator();
        while (iter.hasNext()) {
            obj = iter.next();
            if (obj instanceof Long) {
                id = (Long)obj;
            } else {
                Principal p = (Principal)obj;
                id = p.getId();               
            }
            buf.append(encodeXmlRef(id));
        }
        return buf.toString();
    }
    /**
     * Decode a jDom element that is a principalRef into a Long
     * @param ref
     * @return Long
     */
    protected static Long decodeXmlRef(Element ref) {
        return new Long(ref.getAttributeValue("principalId"));  
    } 
    /**
     * Decode the child elements that are principalRefs and return as a set of Longs
     * @param ref
     * @return Set of Long
     */
    protected static Set decodeXmlRefs(Element ref) {
        List refs = ref.getChildren("principalRef");
 
        Iterator iter;
        Set result = new HashSet();
        if ((refs == null) || refs.isEmpty()) return result;
        iter = refs.iterator();
  	    while (iter.hasNext()) {
   	        result.add(decodeXmlRef((Element)iter.next()));                            
   	    }
        return result;
    }
    public String toString() {
    	return zoneName + ":" + name;
    }
 }

