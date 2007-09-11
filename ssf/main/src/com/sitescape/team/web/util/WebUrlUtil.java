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
package com.sitescape.team.web.util;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Http;

public class WebUrlUtil {
	
	private static final int WEB_PROTOCOL_CONTEXT_HTTP	= 1;
	private static final int WEB_PROTOCOL_CONTEXT_HTTPS	= 2;
	private static final int WEB_PROTOCOL_HTTP 			= 3;
	private static final int WEB_PROTOCOL_HTTPS			= 4;
	
	private static int adapterWebProtocol 	= -1;
	private static int servletWebProtocol 	= -1;
	private static int rssWebProtocol 		= -1;
	private static int icalWebProtocol 		= -1;
	private static int ssfsWebProtocol 		= -1;
	
	public static StringBuffer getSSFSContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(req, req.isSecure(), getSsfsWebProtocol(), true);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "/ssfs");
		
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static StringBuffer getSSFSContextRootURL(PortletRequest req) {
		StringBuffer sb = getHostAndPort(req, req.isSecure(), getSsfsWebProtocol(), true);
		
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
		StringBuffer sb = getHostAndPort(req, secure, webProtocol, false);
				
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
		StringBuffer sb = getHostAndPort(req, secure, webProtocol, false);
				
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(HttpServletRequest req, Boolean isSecure, int webProtocol, boolean portRequired) {
		String host;
		int port;
		boolean secure;
		
		if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTP) {
			if(req != null) {
				host = req.getServerName();
				port = req.getServerPort();
				if(isSecure != null)
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure();
			}
			else {
				host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
				port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
				secure = false;
			}
		}
		else if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTPS) {
			if(req != null) {
				host = req.getServerName();
				port = req.getServerPort();
				if(isSecure != null)
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure();
			}
			else {
				host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
				port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
				secure = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
			port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
			secure = false;		
		}
		else { // WEB_PROTOCOL_HTTPS
			host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
			port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			secure = true;			
		}
		
		return getHostAndPort(host, port, secure, portRequired);
	}
	
	private static StringBuffer getHostAndPort(PortletRequest req, Boolean isSecure, int webProtocol, boolean portRequired) {
		String host;
		int port;
		boolean secure;
		
		if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTP) {
			if(req != null) {
				host = req.getServerName();
				port = req.getServerPort();
				if(isSecure != null) // context override value supplied
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure(); // get the context value from the req object
			}
			else {
				host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
				port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
				// context override value isSecure is relevant only when req is non-null
				secure = false;
			}
		}
		else if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTPS) {
			if(req != null) {
				host = req.getServerName();
				port = req.getServerPort();
				if(isSecure != null) // context override value supplied
					secure = isSecure; // don't use req.isSecure() !
				else
					secure = req.isSecure(); // get the context value from the req object
			}
			else {
				host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
				port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
				// context override value isSecure is relevant only when req is non-null
				secure = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
			port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
			secure = false;		
		}
		else { // WEB_PROTOCOL_HTTPS
			host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
			port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			secure = true;			
		}
		
		return getHostAndPort(host, port, secure, portRequired);
	}
	
	private static StringBuffer getHostAndPort(String host, int port, boolean secure, boolean portRequired) {

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
}
