package com.sitescape.ef.web.util;

import java.util.Enumeration;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.sitescape.ef.util.ConfigPropsUtil;

public class DebugHelper {
	
	public static void testRequestEnv(String caller, HttpServletRequest request) {
		if(ConfigPropsUtil.getBoolean("request.env.print")) {
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
	}
	
	
	public static void testRequestEnv(String caller, PortletRequest request) {
		if(ConfigPropsUtil.getBoolean("request.env.print")) {
			System.out.println("\tlogin name: [" + request.getRemoteUser() + "]");
			System.out.println("\tprincipal: [" + request.getUserPrincipal() + "]");

			PortletSession pses = request.getPortletSession(false);
			
			if(pses != null) {
				Integer ac = (Integer) pses.getAttribute("total.access.count", 
						PortletSession.APPLICATION_SCOPE);
				if(ac == null)
					ac = new Integer(1);
				else
					ac = new Integer(ac.intValue() + 1);
				pses.setAttribute("total.access.count", ac, PortletSession.APPLICATION_SCOPE);
				
				Integer cac = (Integer) pses.getAttribute(caller + ".access.count", 
						PortletSession.APPLICATION_SCOPE);
				if(cac == null)
					cac = new Integer(1);
				else
					cac = new Integer(cac.intValue() + 1);
				pses.setAttribute(caller + ".access.count", cac,
						PortletSession.APPLICATION_SCOPE);
					
				System.out.println("*** [" + caller + "] ***");
				
				System.out.println("\tsession id: [" + pses.getId() + "]");
				System.out.println("\tsession object: [" + pses + "]");
				System.out.println("\tsession attributes: [" + getSessionAttributesAsString(pses) + "]");
			}
			else {
				System.out.println("\tNo session object exists");				
			}
		}
	}
	
	private static String getSessionAttributesAsString(HttpSession ses) {
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
	
	private static String getSessionAttributesAsString(PortletSession ses) {
		StringBuffer sb = new StringBuffer();
		for(Enumeration e = ses.getAttributeNames(PortletSession.APPLICATION_SCOPE);e.hasMoreElements();) {
			String name = (String) e.nextElement();
			Object value = ses.getAttribute(name, PortletSession.APPLICATION_SCOPE);
			if(sb.length() > 0)
				sb.append(", ");
			sb.append(name).append("=").append(value.toString());
		}
		return sb.toString();
	}
}
