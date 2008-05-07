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
package com.sitescape.team.web.util;

import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Http;
import com.sitescape.util.Validator;
import com.sitescape.team.context.request.RequestContextHolder;

public class WebUrlUtil {
	
	private static final Log logger = LogFactory.getLog(WebUrlUtil.class);

	private static final int WEB_PROTOCOL_CONTEXT_HTTP	= 1;
	private static final int WEB_PROTOCOL_CONTEXT_HTTPS	= 2;
	private static final int WEB_PROTOCOL_HTTP 			= 3;
	private static final int WEB_PROTOCOL_HTTPS			= 4;
	
	private static int adapterWebProtocol 	= -1;
	private static int servletWebProtocol 	= -1;
	private static int rssWebProtocol 		= -1;
	private static int icalWebProtocol 		= -1;
	private static int ssfsWebProtocol 		= -1;
	
	private static final String SSFS_HOST_REWRITE = "ssfs.default.host.rewrite";
	
	enum WebApp {
		SSF,
		SSFS
	}

	public static StringBuffer getSSFSContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SSFS, req, req.isSecure(), getSsfsWebProtocol(), true);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "/ssfs");
		
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static StringBuffer getSSFSContextRootURL(PortletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SSFS, req, req.isSecure(), getSsfsWebProtocol(), true);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "/ssfs");
		
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	/**
	 * Returns URL up to the SSF portlet adapter root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/a/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @param secure
	 * @return
	 */
	public static String getAdapterRootURL(HttpServletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getAdapterWebProtocol()).append("a/").toString();
	}
	
	public static String getAdapterRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getAdapterWebProtocol()).append("a/").toString();
	}
	
	public static String getServletRootURL() {
		return getServletRootURL((HttpServletRequest) null, false);
	}

	public static String getServletRootURL(HttpServletRequest req) {
		return getServletRootURL(req, ((req != null)? req.isSecure() : false));
	}
	
	public static String getServletRootURL(PortletRequest req) {
		return getServletRootURL(req, ((req != null)? req.isSecure() : false));
	}
	
	/**
	 * Returns URL up to the SSF's regular servlet root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/s/).
	 * 
	 * @param req
	 * @param secure
	 * @return
	 */
	public static String getServletRootURL(HttpServletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getServletWebProtocol()).append("s/").toString();
	}

	public static String getServletRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getServletWebProtocol()).append("s/").toString();
	}

	/**
	 * Returns URL up to the SSF's RSS root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/rss/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 *  
	 * @param req may be null
	 * @return
	 */
	public static String getRssRootURL(HttpServletRequest req) {
		return getRssRootURL(req, ((req != null)? req.isSecure() : false));
	}
	
	public static String getRssRootURL(PortletRequest req) {
		return getRssRootURL(req, ((req != null)? req.isSecure() : false));
	}
	
	/**
	 * Returns URL up to the SSF's iCal root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/ical/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 *  
	 * @param req may be null
	 * @return
	 */
	public static String getIcalRootURL(HttpServletRequest req) {
		return getIcalRootURL(req, ((req != null)? req.isSecure() : false));
	}
	
	public static String getIcalRootURL(PortletRequest req) {
		return getIcalRootURL(req, ((req != null)? req.isSecure() : false));
	}
	
	/**
	 * Returns URL up to the SSF's RSS root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/rss/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @param secure
	 * @return
	 */
	public static String getRssRootURL(HttpServletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getRssWebProtocol()).append("rss/").toString();
	}
	
	public static String getRssRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getRssWebProtocol()).append("rss/").toString();
	}
	
	/**
	 * Returns URL up to the SSF's iCal root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/ical/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @param secure
	 * @return
	 */
	public static String getIcalRootURL(HttpServletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getIcalWebProtocol()).append("ical/").toString();
	}
	
	public static String getIcalRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getIcalWebProtocol()).append("ical/").toString();
	}
	
	public static String getEntryViewURL(FolderEntry entry) {
		return getEntryViewURL(entry.getParentFolder().getId().toString(), entry.getId().toString());
	}
	
	public static String getEntryViewURL(String parentBinderId, String entryId) {
		String entryUrl="";
		try {
			AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", false);
			url.setParameter("action", WebKeys.ACTION_VIEW_FOLDER_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, parentBinderId);
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			entryUrl = url.toString();
		}
		catch(Exception e) {}
		return entryUrl;
	}
	
	public static String getEntryPermalinkURL(FolderEntry entry) {
		return getEntryPermalinkURL(entry.getParentFolder().getId().toString(), 
				entry.getId().toString(), entry.getEntityType().toString());
	}
	
	public static String getEntryPermalinkURL(String parentBinderId, String entryId, String entityType) {
		String entryUrl="";
		try {
			AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", false);
			url.setParameter("action", WebKeys.ACTION_VIEW_PERMALINK);
			url.setParameter(WebKeys.URL_BINDER_ID, parentBinderId);
			url.setParameter(WebKeys.URL_ENTRY_ID, entryId);
			url.setParameter(WebKeys.URL_ENTITY_TYPE, entityType);
			entryUrl = url.toString();
		}
		catch(Exception e) {}
		return entryUrl;
	}
	
	private static StringBuffer getSSFContextRootURL(PortletRequest req, Boolean secure, int webProtocol) {
		StringBuffer sb = getHostAndPort(WebApp.SSF, req, secure, webProtocol, false);
				
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	private static StringBuffer getSSFContextRootURL(HttpServletRequest req, Boolean secure, int webProtocol) {
		StringBuffer sb = getHostAndPort(WebApp.SSF, req, secure, webProtocol, false);
				
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(WebApp webApp, HttpServletRequest req, Boolean isSecure, int webProtocol, boolean portRequired) {
		String host = null;
		int port = -1;
		boolean secure;
		
		if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTP) {
			if(req != null) {
				HttpSession ses = req.getSession(false);
				if(ses != null) {
					host = (String) ses.getAttribute(WebKeys.SERVER_NAME);
					Integer intPort = (Integer) ses.getAttribute(WebKeys.SERVER_PORT);
					if(intPort != null)
						port = intPort.intValue();
					if(logger.isTraceEnabled()) {
						if(host == null)
							logger.trace("*** No host name found in http session");
						else
							logger.trace("Host name from http session is " + host);
						if(intPort == null)
							logger.trace("*** No host port found in http session");
						else
							logger.trace("Host port from http session is " + intPort);
					}
				}
				else {
					if(logger.isTraceEnabled())
						logger.trace("*** No http session found from http request");
				}
				
				if(host == null)
					host = req.getServerName().toLowerCase();
				if(port == -1)
					port = req.getServerPort();
				
				if(isSecure != null)
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure();
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (http request) passed in. Getting values from properties file.");
				host = getStaticHostName();
				port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
				secure = false;
			}
		}
		else if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTPS) {
			if(req != null) {
				HttpSession ses = req.getSession(false);
				if(ses != null) {
					host = (String) ses.getAttribute(WebKeys.SERVER_NAME);
					Integer intPort = (Integer) ses.getAttribute(WebKeys.SERVER_PORT);
					if(intPort != null)
						port = intPort.intValue();
					if(logger.isTraceEnabled()) {
						if(host == null)
							logger.trace("*** No host name found in http session");
						else
							logger.trace("Host name from http session is " + host);
						if(intPort == null)
							logger.trace("*** No host port found in http session");
						else
							logger.trace("Host port from http session is " + intPort);
					}
				}
				else {
					if(logger.isTraceEnabled())
						logger.trace("*** No http session found from http request");
				}
				
				if(host == null)
					host = req.getServerName().toLowerCase();
				if(port == -1)
					port = req.getServerPort();
				
				if(isSecure != null)
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure();
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (http request) passed in. Getting values from properties file.");
				host = getStaticHostName();
				port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
				secure = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
			secure = false;		
		}
		else { // WEB_PROTOCOL_HTTPS
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			secure = true;			
		}
		
		StringBuffer sb = getHostAndPort(webApp, host, port, secure, portRequired);
		
		if(logger.isTraceEnabled())
			logger.trace("(s) Generated host and port is " + sb.toString());
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(WebApp webApp, PortletRequest req, Boolean isSecure, int webProtocol, boolean portRequired) {
		String host = null;
		int port = -1;
		boolean secure;
		
		if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTP) {
			if(req != null) {
				PortletSession ses = req.getPortletSession(false);
				if(ses != null) {
					host = (String) ses.getAttribute(WebKeys.SERVER_NAME, PortletSession.APPLICATION_SCOPE);
					Integer intPort = (Integer) ses.getAttribute(WebKeys.SERVER_PORT, PortletSession.APPLICATION_SCOPE);
					if(intPort != null)
						port = intPort.intValue();
					if(logger.isTraceEnabled()) {
						if(host == null)
							logger.trace("*** No host name found in portlet session");
						else
							logger.trace("Host name from portlet session is " + host);
						if(intPort == null)
							logger.trace("*** No host port found in portlet session");
						else
							logger.trace("Host port from portlet session is " + intPort);
					}
				}
				else {
					if(logger.isTraceEnabled())
						logger.trace("*** No portlet session found from portlet request");
				}
				
				if(host == null)
					host = req.getServerName().toLowerCase();
				if(port == -1)
					port = req.getServerPort();
				
				if(isSecure != null) // context override value supplied
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure(); // get the context value from the req object
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (portlet request) passed in. Getting values from properties file.");
				host = getStaticHostName();
				port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
				// context override value isSecure is relevant only when req is non-null
				secure = false;
			}
		}
		else if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTPS) {
			if(req != null) {
				PortletSession ses = req.getPortletSession(false);
				if(ses != null) {
					host = (String) ses.getAttribute(WebKeys.SERVER_NAME, PortletSession.APPLICATION_SCOPE);
					Integer intPort = (Integer) ses.getAttribute(WebKeys.SERVER_PORT, PortletSession.APPLICATION_SCOPE);
					if(intPort != null)
						port = intPort.intValue();
					if(logger.isTraceEnabled()) {
						if(host == null)
							logger.trace("*** No host name found in portlet session");
						else
							logger.trace("Host name from portlet session is " + host);
						if(intPort == null)
							logger.trace("*** No host port found in portlet session");
						else
							logger.trace("Host port from portlet session is " + intPort);
					}
				}
				else {
					if(logger.isTraceEnabled())
						logger.trace("*** No portlet session from portlet request");				
				}
				
				if(host == null)
					host = req.getServerName().toLowerCase();
				if(port == -1)
					port = req.getServerPort();
				
				if(isSecure != null) // context override value supplied
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure(); // get the context value from the req object
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (portlet request) passed in. Getting values from properties file.");
				host = getStaticHostName();
				port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
				// context override value isSecure is relevant only when req is non-null
				secure = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
			secure = false;		
		}
		else { // WEB_PROTOCOL_HTTPS
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			secure = true;			
		}
		
		StringBuffer sb = getHostAndPort(webApp, host, port, secure, portRequired);
		
		if(logger.isTraceEnabled())
			logger.trace("(p) Generated host and port is " + sb.toString());
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(WebApp webApp, String host, int port, boolean secure, boolean portRequired) {

		// Because URLs generated by this class can only be served up by a
		// Aspen server (in other words, it can not be served by WSRP consumer
		// because it is not a real portlet URL that the consumer understands), 
		// we need to encode the actual server name in the URL. 
		
		StringBuffer sb = new StringBuffer();
		
		// Scheme
		if(secure)
			sb.append(Http.HTTPS_WITH_SLASH);
		else
			sb.append(Http.HTTP_WITH_SLASH);
			
		// Server host
		
		// Special processing for WebDAV (ssfs) URL to workaround an issue in iChain.
		// If rewrite hostname is specified for webdav AND the context zone is the default zone,
		// apply this hack. Otherwise, leave it alone.
		if(webApp == WebApp.SSFS) {
			String ssfsHostRewrite = SPropsUtil.getString(SSFS_HOST_REWRITE, "");
			if(Validator.isNotNull(ssfsHostRewrite) &&
					RequestContextHolder.getRequestContext().getZoneName().equals(SZoneConfig.getDefaultZoneName())) {
				host = ssfsHostRewrite;
			}
		}
		
		sb.append(host);
		
		// Port number
		if(secure) {
			if(portRequired || (port != Http.HTTPS_PORT)) {
				sb.append(Constants.COLON).append(port);
			}
		}
		else {
			if(portRequired || (port != Http.HTTP_PORT)) {
				sb.append(Constants.COLON).append(port);
			}			
		}
		
		return sb;
	}
	
	private static int getAdapterWebProtocol() {
		init();
		return adapterWebProtocol;
	}
	
	private static int getServletWebProtocol() {
		init();
		return servletWebProtocol;
	}
	
	private static int getRssWebProtocol() {
		init();
		return rssWebProtocol;
	}
	
	private static int getIcalWebProtocol() {
		init();
		return icalWebProtocol;
	}
	
	private static int getSsfsWebProtocol() {
		init();
		return ssfsWebProtocol;
	}
		
	private static void init() {
		if(adapterWebProtocol == -1) {
			String prot;
			
			prot = SPropsUtil.getString("adapter.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				adapterWebProtocol = WEB_PROTOCOL_HTTP;
			else if(prot.equalsIgnoreCase("https"))
				adapterWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				adapterWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				adapterWebProtocol = WEB_PROTOCOL_CONTEXT_HTTP;
			
			prot = SPropsUtil.getString("servlet.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				servletWebProtocol = WEB_PROTOCOL_HTTP;
			else if(prot.equalsIgnoreCase("https"))
				servletWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				servletWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				servletWebProtocol = WEB_PROTOCOL_CONTEXT_HTTP;
			
			prot = SPropsUtil.getString("rss.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				rssWebProtocol = WEB_PROTOCOL_HTTP;
			else if(prot.equalsIgnoreCase("https"))
				rssWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				rssWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				rssWebProtocol = WEB_PROTOCOL_CONTEXT_HTTP;
			
			prot = SPropsUtil.getString("ical.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				icalWebProtocol = WEB_PROTOCOL_HTTP;
			else if(prot.equalsIgnoreCase("https"))
				icalWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				icalWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				icalWebProtocol = WEB_PROTOCOL_CONTEXT_HTTP;

			prot = SPropsUtil.getString("ssfs.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				ssfsWebProtocol = WEB_PROTOCOL_HTTP;
			else if(prot.equalsIgnoreCase("https"))
				ssfsWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				ssfsWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				ssfsWebProtocol = WEB_PROTOCOL_CONTEXT_HTTP;			
		}
	}
	
	private static String getStaticHostName() {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		if(zoneName.equals(SZoneConfig.getDefaultZoneName())) {
			// Default zone does not have virtual host name associated with it.
			// Even if it did, we use the value specified in the properties file.
			return SPropsUtil.getString(SPropsUtil.SSF_DEFAULT_HOST);
		}
		else {
			return getZoneModule().getVirtualHost(zoneName);
		}
	}
	
	private static ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

}
