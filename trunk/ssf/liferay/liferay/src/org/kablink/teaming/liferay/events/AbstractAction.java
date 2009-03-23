/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.liferay.events;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.liferay.portal.struts.Action;

public abstract class AbstractAction extends Action {
	
	protected void testRequestEnv(String caller, HttpServletRequest request) {
		System.out.println("\tlogin name: [" + request.getRemoteUser() + "]");
		System.out.println("\tprincipal: [" + request.getUserPrincipal() + "]");

		HttpSession ses = request.getSession(false);

		if(ses != null) {
			Integer ac = (Integer) ses.getAttribute("total.access.count");
			if(ac == null)
				ac = new Integer(1);
			else
				ac = new Integer(ac.intValue() + 1);
			ses.setAttribute("total.access.count", ac);
	
			Integer cac = (Integer) ses.getAttribute(caller + ".access.count");
			if(cac == null)
				cac = new Integer(1);
			else
				cac = new Integer(cac.intValue() + 1);
			ses.setAttribute(caller + ".access.count", cac);
				
			System.out.println("*** [" + caller + "] ***");
			
			System.out.println("\tsession id: [" + ses.getId() + "]");
			System.out.println("\tsession object: [" + ses + "]");
			System.out.println("\tsession attributes: [" + getSessionAttributesAsString(ses) + "]");
		}
		else {
			System.out.println("\tNo session object exists");
		}
	}
	
	private String getSessionAttributesAsString(HttpSession ses) {
		StringBuffer sb = new StringBuffer();
		for(Enumeration e = ses.getAttributeNames();e.hasMoreElements();) {
			String name = (String) e.nextElement();
			Object value = ses.getAttribute(name);
			if(sb.length() > 0)
				sb.append(", ");
			sb.append(name).append("=").append(value.toString());
		}
		return sb.toString();
	}
}
