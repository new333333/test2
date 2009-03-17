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
/*
 * Created on Nov 16, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.kablink.teaming.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.kablink.teaming.NotSupportedException;
import org.kablink.teaming.util.CollectionUtil;
import org.kablink.util.Validator;

/**
 * @hibernate.subclass discriminator-value="G" dynamic-update="true" 
 *
 */
public class Group extends UserPrincipal implements GroupPrincipal {
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
			UserPrincipal p = (UserPrincipal)iter.next();
			p.getMemberOf().add(this);
		}
		for (Iterator iter=remM.iterator(); iter.hasNext();) {
			UserPrincipal p = (UserPrincipal)iter.next();
			p.getMemberOf().remove(this);
		}
  	} 	
    
    public void addMember(IPrincipal member) {
    	if (!(member instanceof UserPrincipal)) throw new NotSupportedException("Must be a User or Group");
		if (members == null) members = new ArrayList();
    	if (members.contains(member)) return;
    	members.add(member);
    	member.getMemberOf().add(this);
    }
    public void removeMember(IPrincipal member) {
		if (members == null) members = new ArrayList();
    	members.remove(member);
    	member.getMemberOf().remove(this);
    }
 
}
