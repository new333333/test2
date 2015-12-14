/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server.LdapBrowser;

import org.kablink.teaming.gwt.client.ldapbrowser.LdapObject;

import org.springframework.ldap.core.ContextMapper;
import org.springframework.ldap.core.DirContextAdapter;

/**
 * ?
 * 
 * @author rvasudevan
 * @author drfoster@novell.com
 */
public class LdapObjectMapper implements ContextMapper {
	public static final String ORGANIZATION = "Organization";		//
	public static final String ORG_UNIT     = "organizationalUnit";	//
	public static final String OBJ_CLASS    = "objectClass";		//

	@Override
	public Object mapFromContext(Object o) {
		DirContextAdapter context = ((DirContextAdapter) o);

		// Map the data into our LDAP Object.
		LdapObject p = new LdapObject();
		p.setDn(context.getDn().toString());

		// Get the whole object class hierarchy for this object (they
		// will be comma separated.)
		String[] objectClass = ((String[]) context.getStringAttributes(OBJ_CLASS));
		p.setObjectClass(objectClass);

		
		if      (p.isObjectClassFound(ORGANIZATION)) p.setName(context.getStringAttribute("o") );	// For Organization,        the name comes from attribute 'o'.
		else if (p.isObjectClassFound(ORG_UNIT))     p.setName(context.getStringAttribute("ou"));	// For Organizational Unit, the name comes from attribute 'ou'.
		else if (p.isObjectClassFound("Country"))    p.setName(context.getStringAttribute("c") );
		else if (p.isObjectClassFound("Locality"))   p.setName(context.getStringAttribute("l") );
		else if (p.isObjectClassFound("domain"))     p.setName(context.getStringAttribute("dc"));
		else                                         p.setName(context.getStringAttribute("cn"));
		
		return p;
	}
}
