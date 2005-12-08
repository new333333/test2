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

import com.sitescape.ef.util.CollectionUtil;

/**
 * @hibernate.subclass discriminator-value="G" dynamic-update="true" 
 *
 */
public class Group extends Principal {
    private List members;    

    /**
     * @hibernate.bag table="SS_PrincipalMembership" lazy="true" inverse="false" cascade="persist,merge,save-update" 
	 * @hibernate.key column="groupId" 
	 * @hibernate.many-to-many fetch="join" column="userId" class="com.sitescape.ef.domain.Principal"
     **/
    private List getHMembers() {return members;}
    private void setHMembers(List members) {this.members = members;}
    
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
}
