package com.sitescape.ef.liferay.events;

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
