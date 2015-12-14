/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.util.CollectionUtil;
import org.kablink.util.Validator;

/**
 * ?
 * 
 * @hibernate.subclass discriminator-value="G" dynamic-update="true" 
 *
 * @author ?
 */
@SuppressWarnings("unchecked")
public class Group extends UserPrincipal implements GroupPrincipal {
	/**
	 * 
	 */
	public enum GroupType
	{
		/**
		 * Group that is used to hold team membership
		 * 
		 */
		team( (short)1 );
		
		short m_value;

		/**
		 * 
		 */
		GroupType( short value )
		{
			m_value = value;
		}

		/**
		 * 
		 */
		public short getValue()
		{
			return m_value;
		}
		
		/**
		 * 
		 */
		public static GroupType valueOf( short value )
		{
			switch( value )
			{
			case 1:
				return GroupType.team;

			default:
				throw new IllegalArgumentException( "Invalid db value " + value + " for enum GroupType" );
			}
		}
	}
	
	private List members;  //initialized by hibernate access=field  
    protected Date membersLastModified;

    private Boolean dynamic = Boolean.FALSE; //initialized by hibernate access=field
    private String ldapQuery;
    
    private Boolean ldapContainer; // false by default

    private Short groupType;
    
    // For use by Hibernate only
	protected Group() {
    }
	
	// For use by application
	public Group(IdentityInfo identityInfo) {
		super(identityInfo);
	}

	@Override
	public EntityIdentifier.EntityType getEntityType() {
		return EntityIdentifier.EntityType.group;
	}
    @Override
	public String getTitle() {
    	String title = super.getTitle();
    	if (Validator.isNull(title)) return getName();
    	return title;
    }
   
    @Override
	public List getMembers() {
    	if (members == null) members = new ArrayList();
    	return members;
    }
    /**
     * Set the group membership.  Each members memberOf set will by updated
     * @param members
     */
    @Override
	public void setMembers(Collection newMembers) { 		
    	if(isLdapContainer()) {
    		throw new UnsupportedOperationException("setMembers() is not supported on the container group '" + getName() + "'");
    	}
    	else {
	   		if (newMembers == null) newMembers = new ArrayList();
			if (members == null) members = new ArrayList();
			Set newM = CollectionUtil.differences(newMembers, members);
			Set remM = CollectionUtil.differences(members, newMembers);
			this.members.addAll(newM);
			this.members.removeAll(remM);
			for (Iterator iter=newM.iterator(); iter.hasNext();) {
				UserPrincipal p = (UserPrincipal)iter.next();
				p.getMemberOf().add(this);
                p.setMemberOfLastModified(new Date());
			}
			for (Iterator iter=remM.iterator(); iter.hasNext();) {
				UserPrincipal p = (UserPrincipal)iter.next();
				p.getMemberOf().remove(this);
                p.setMemberOfLastModified(new Date());
            }
            setMembersLastModified(new Date());
        }
  	} 	
    
    @Override
	public void addMember(IPrincipal member) {
    	if(isLdapContainer()) {
    		throw new UnsupportedOperationException("addMember() is not supported on the container group '" + getName() + "'");
    	}
    	else {
	    	if (!(member instanceof UserPrincipal)) throw new NotSupportedException("Must be a User or Group");
			if (members == null) members = new ArrayList();
	    	if (members.contains(member)) return;
	    	members.add(member);
	    	member.getMemberOf().add(this);
            member.setMemberOfLastModified(new Date());
            setMembersLastModified(new Date());
        }
    }
    @Override
	public void removeMember(IPrincipal member) {
    	if(isLdapContainer()) {
    		throw new UnsupportedOperationException("removeMember() is not supported on the container group '" + getName() + "'");
    	}
    	else {
			if (members == null) members = new ArrayList();
	    	members.remove(member);
	    	member.getMemberOf().remove(this);
            member.setMemberOfLastModified(new Date());
            setMembersLastModified(new Date());
    	}
    }

    public Date getMembersLastModified() {
        return membersLastModified;
    }

    public void setMembersLastModified(Date membersLastModified) {
        this.membersLastModified = membersLastModified;
    }

    public boolean isDynamic() {
		if(dynamic != null)
			return dynamic.booleanValue();
		else
			return false; // static by default
	}
	public void setDynamic(boolean dynamic) {
		this.dynamic = Boolean.valueOf(dynamic);
	}
	
	public String getLdapQuery() {
		return ldapQuery;
	}
	public void setLdapQuery(String ldapQuery) {
		this.ldapQuery = ldapQuery;
	}

	public boolean isLdapContainer() {
		if(ldapContainer == null)
			return false;
		return ldapContainer.booleanValue();
	}

	public void setLdapContainer(boolean ldapContainer) {
		this.ldapContainer = ldapContainer;
	}

	/**
	 * 
	 */
	public GroupType getGroupType()
	{
		if ( groupType == null )
			return null;

		return GroupType.valueOf( groupType.shortValue() );
	}

	/**
	 * 
	 */
	public void setGroupType( GroupType groupType )
	{
		if ( groupType == null )
			this.groupType = null;
		else
			this.groupType = groupType.getValue();
	}

	/**
	 * Returns true if this Group is a 'team' Group and false
	 * otherwise.
	 * 
	 * @return
	 */
	public boolean isTeamGroup() {
		GroupType gt = getGroupType();
		return ((null != gt) && gt.equals(GroupType.team));
	}
}
