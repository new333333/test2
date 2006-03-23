/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.ef.security.function.WorkArea;
import com.sitescape.ef.util.CollectionUtil;
import com.sitescape.util.Validator;
/**
 * @hibernate.subclass discriminator-value="G" dynamic-update="true" 
 *
 */
public class Group extends Principal implements WorkArea {
    private List members;  //initialized by hibernate access=field  
    
    private Boolean functionMembershipInherited = Boolean.TRUE;//initialized by hibernate access=field
    
    private static final String WORK_AREA_TYPE = "GROUP";
    
    public EntityIdentifier getEntityIdentifier() {
    	return new EntityIdentifier(getId(), EntityIdentifier.EntityType.group);
    }
    public String getTitle() {
    	String title = super.getTitle();
    	if (Validator.isNull(title)) return getName();
    	return title;
    }
   
    public List getMembers() {
    	if (members == null) members = new ArrayList();
    	return members;
    }
    /**
     * Set the group membership.  Each members memberOf set will by updated
     * @param members
     */
    public void setMembers(Collection newMembers) { 		
   		if (newMembers == null) newMembers = new ArrayList();
		if (members == null) members = new ArrayList();
		Set newM = CollectionUtil.differences(newMembers, members);
		Set remM = CollectionUtil.differences(members, newMembers);
		this.members.addAll(newM);
		this.members.removeAll(remM);
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Principal p = (Principal)iter.next();
			p.getMemberOf().add(this);
		}
		for (Iterator iter=newM.iterator(); iter.hasNext();) {
			Principal p = (Principal)iter.next();
			p.getMemberOf().remove(this);
		}
  	} 	
    
    public void addMember(Principal member) {
    	if (members.contains(member)) return;
    	members.add(member);
    	member.getMemberOf().add(this);
    }
    public void removeMember(Principal member) {
    	members.remove(member);
    	member.getMemberOf().remove(this);
    }
    public void setUserMembers(Collection newMembers) {
   		if (newMembers == null) newMembers = new ArrayList();
   		//Remove users that are in the existing set, but not the new one
  		for (Iterator iter=members.iterator();iter.hasNext();) {
   			Principal p = (Principal)iter.next();
   			//Existing user not in new set
  			if ((p instanceof User) && !newMembers.contains(p)) {
  				members.remove(p);
   				p.getMemberOf().remove(this);
   			}			
   		}
   		//Add new users
  		for (Iterator iter=newMembers.iterator();iter.hasNext();) {
   			User u = (User)iter.next();
   			if (!members.contains(u)) {
   				u.getMemberOf().add(this);
   				members.add(u);
   			}
   		}	
   	} 	

    public void setGroupMembers(Collection newMembers) {
   		if (newMembers == null) newMembers = new ArrayList();
   		//Remove groups that are in the existing set, but not the new one
  		for (Iterator iter=members.iterator();iter.hasNext();) {
   			Principal p = (Principal)iter.next();
   			//Existing group not in new set
  			if ((p instanceof Group) && !newMembers.contains(p)) {
  				members.remove(p);
   				p.getMemberOf().remove(this);
   			}			
   		}
   		//Add new groups
  		for (Iterator iter=newMembers.iterator();iter.hasNext();) {
   			Group g = (Group)iter.next();
   			if (!members.contains(g)) {
   				g.getMemberOf().add(this);
   				members.add(g);
   			}
   		}	
   	}
    
	public Long getWorkAreaId() {
		return getId();
	}
	public String getWorkAreaType() {
		return WORK_AREA_TYPE;
	}
	public WorkArea getParentWorkArea() {
		// Group can be a child of many other groups. No single parent.
		// TODO Then where should we inherit the function membership from?
		return null; // For now
	}
	
	// I have separate sets of methods for handling functionMembershipInherited
	// field - one for WorkArea interface and the other for Hibernate persistence.
	// Strictly speaking this separation is not at all necessary. But in order
	// to allow people to continue working with existing databases without having
	// to re-build them, I had to allow nulls for existing records, and hence
	// this ugly code. 
	public boolean isFunctionMembershipInherited() {
    	if(functionMembershipInherited == null)
    		return true; // Default value
    	else		
    		return functionMembershipInherited.booleanValue();
	}

	public void setFunctionMembershipInherited(boolean functionMembershipInherited) {
        this.functionMembershipInherited = Boolean.valueOf(functionMembershipInherited);
    }
	

}
