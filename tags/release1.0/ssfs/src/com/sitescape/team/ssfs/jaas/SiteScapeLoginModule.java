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
package com.sitescape.team.ssfs.jaas;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import javax.security.auth.login.LoginException;

import org.apache.slide.jaas.spi.SlidePrincipal;
import org.apache.slide.simple.authentication.JAASLoginModule;

import com.sitescape.util.ServerDetector;

public class SiteScapeLoginModule extends JAASLoginModule {

	/**
	 * This method overrides <code>JAASLoginModule</code> so that the login
	 * module can work appropriately under all supported app servers. 
	 * This tweak is necessary because not all app servers exercise the same
	 * behavior when it comes to interfacing with jaas login module. It appears
	 * that the API for commit method is sufficiently ambiguous (or lacks
	 * precise details) that different app servers interpret them differently. 
	 */
    public boolean commit() throws LoginException {
    	if (ServerDetector.isJBoss()) {
	        if (m_authenticated) {
	            m_subject.getPrincipals().add(m_principal);        	
	            SiteScapeGroup group = new SiteScapeGroup("Roles");
	            group.addMember(new SlidePrincipal("root"));
	            
	            m_subject.getPrincipals().add(m_group);
	            m_subject.getPrincipals().add(group);
	            for (int i = 0; i < m_roles.length; i++) {
	                m_subject.getPrincipals().add(m_roles[i]);
	            }
	        }
	        m_committed = true;
	        return m_authenticated;
    	}
    	else {
    		return super.commit();
    	}
    }

    public static class SiteScapeGroup implements Group {
        
        private final HashSet m_members = new HashSet();
        private String name;
        
        public SiteScapeGroup(String name) {
        	this.name = name;
        }
        
        public boolean addMember(Principal user) {
            return m_members.add(user);
        }
        
        public boolean isMember(Principal member) {
            return m_members.contains(member);
        }
        
        public Enumeration members() {
            class MembersEnumeration implements Enumeration {
                private Iterator m_iter;
                public MembersEnumeration(Iterator iter) {
                    m_iter = iter;
                }
                public boolean hasMoreElements () {
                    return m_iter.hasNext();
                }
                public Object nextElement () {
                    return m_iter.next();
                }
            }

            return new MembersEnumeration(m_members.iterator());
        }

        public boolean removeMember(Principal user) {
            return m_members.remove(user);
        }
        
        public String getName() {
            return name;
        }
    }
}
