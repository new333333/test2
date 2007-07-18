/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import com.sitescape.team.util.CollectionUtil;
import com.sitescape.util.Validator;
/**
 * @hibernate.subclass discriminator-value="G" dynamic-update="true" 
 *
 */
public class Group extends Principal  {
    private List members;  //initialized by hibernate access=field  
      
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.group;
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
		if (members == null) members = new ArrayList();
    	if (members.contains(member)) return;
    	members.add(member);
    	member.getMemberOf().add(this);
    }
    public void removeMember(Principal member) {
		if (members == null) members = new ArrayList();
    	members.remove(member);
    	member.getMemberOf().remove(this);
    }
    
}
