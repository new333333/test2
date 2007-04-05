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
	
	/**
	 * Returns URL up to the SSF web app context root. The URL starts with a
	 * scheme and ends with a "/" character (e.g. http://abc.com:8080/ssf/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @return
	 */
	public static StringBuffer getContextRootURL(HttpServletRequest req) {
		if(req == null)
			return getContextRootURL(req, false);
		else
			return getContextRootURL(req, req.isSecure());
	}
	
	/**
	 * Returns URL up to the SSF web app context path. The URL starts with a 
	 * scheme and ends with a "/" character (e.g. http://abc.com:8080/ssf/).
	 * <p>
	 * If <code>req</code> is null, it uses system config information stored 
	 * in ssf.properties (which is static) to construct the URL as opposed to 
	 * the dynamic data available in the <code>HttpServletRequest</code>.   
	 * 
	 * @param req may be null
	 * @param secure
	 * @return
	 */
	public static StringBuffer getContextRootURL(HttpServletRequest req, boolean secure) {

		StringBuffer sb = getHostAndPort(req, secure);
				
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static StringBuffer getContextRootURL(PortletRequest req, boolean secure) {

		StringBuffer sb = getHostAndPort(req, secure);
				
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static StringBuffer getSSFSContextRootURL() {
		return getSSFSContextRootURL(false);
	}
	
	public static StringBuffer getSSFSContextRootURL(boolean secure) {
		HttpServletRequest req = null; // Just to fake the compiler
		StringBuffer sb = getHostAndPort(req, secure);
		
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
	 * @return
	 */
	public static String getAdapterRootURL(HttpServletRequest req) {
		if(req == null)
			return getAdapterRootURL(req, false);
		else
			return getAdapterRootURL(req, req.isSecure());
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
	public static String getAdapterRootURL(HttpServletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("a/").toString();
	}
	
	public static String getAdapterRootURL(PortletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("a/").toString();
	}
	
	public static String getServletRootURL() {
		return getServletRootURL(null, false);
	}

	public static String getServletRootURL(HttpServletRequest req) {
		return getServletRootURL(req, req.isSecure());
	}
	
	/**
	 * Returns URL up to the SSF's regular servlet root. The returned URL ends
	 * with a "/" character (e.g. http://abc.com:8080/ssf/s/).
	 * 
	 * @param req
	 * @param secure
	 * @return
	 */
	public static String getServletRootURL(HttpServletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("s/").toString();
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
		if(req == null)
			return getRssRootURL(req, false);
		else
			return getRssRootURL(req, req.isSecure());
	}
	
	public static String getRssRootURL(PortletRequest req) {
		if(req == null)
			return getRssRootURL(req, false);
		else
			return getRssRootURL(req, req.isSecure());
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
		if(req == null)
			return getIcalRootURL(req, false);
		else
			return getIcalRootURL(req, req.isSecure());
	}
	
	public static String getIcalRootURL(PortletRequest req) {
		if(req == null)
			return getIcalRootURL(req, false);
		else
			return getIcalRootURL(req, req.isSecure());
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
	public static String getRssRootURL(HttpServletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("rss/").toString();
	}
	
	public static String getRssRootURL(PortletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("rss/").toString();
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
	public static String getIcalRootURL(HttpServletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("ical/").toString();
	}
	
	public static String getIcalRootURL(PortletRequest req, boolean secure) {
		return getContextRootURL(req, secure).append("ical/").toString();
	}
	
	public static String getEntryViewURL(FolderEntry entry) {
		String entryUrl="";
		try {
			AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", false);
			url.setParameter("action", WebKeys.ACTION_VIEW_FOLDER_ENTRY);
			url.setParameter(WebKeys.URL_BINDER_ID, entry.getParentFolder().getId().toString());
			url.setParameter(WebKeys.URL_ENTRY_ID, entry.getId().toString());
			entryUrl = url.toString();
		}
		catch(Exception e) {}
		return entryUrl;
	}
	
	private static StringBuffer getHostAndPort(HttpServletRequest req, boolean secure) {

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
		String host = null;
		if(req == null) {
			host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
		}
		else {
			host = req.getServerName();
		}
		sb.append(host);
		
		// Port number
		int port;
		if(req == null) {
			if(secure)
				port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			else
				port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
		}
		else {
			port = req.getServerPort();
		}
		if(secure) {
			if(port != Http.HTTPS_PORT) {
				sb.append(Constants.COLON).append(port);
			}
		}
		else {
			if(port != Http.HTTP_PORT) {
				sb.append(Constants.COLON).append(port);
			}			
		}
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(PortletRequest req, boolean secure) {

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
		String host = null;
		if(req == null) {
			host = SPropsUtil.getString(SPropsUtil.SSF_HOST);
		}
		else {
			host = req.getServerName();
		}
		sb.append(host);
		
		// Port number
		int port;
		if(req == null) {
			if(secure)
				port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			else
				port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
		}
		else {
			port = req.getServerPort();
		}
		if(secure) {
			if(port != Http.HTTPS_PORT) {
				sb.append(Constants.COLON).append(port);
			}
		}
		else {
			if(port != Http.HTTP_PORT) {
				sb.append(Constants.COLON).append(port);
			}			
		}
		
		return sb;
	}
}
