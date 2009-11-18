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
package org.kablink.teaming.web.util;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.search.SearchFieldResult;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.util.Http;
import org.kablink.util.Validator;



public class WebUrlUtil {
	
	public static final int FILE_URL_ACTION = 1;
	public static final int FILE_URL_ENTITY_TYPE = 2;
	public static final int FILE_URL_ENTITY_ID = 3;
	public static final int FILE_URL_FILE_ID = 4;
	public static final int FILE_URL_DATE = 5;
	public static final int FILE_URL_VERSION = 6;
	public static final int FILE_URL_NAME = 7;
	public static final int FILE_URL_ARG_LENGTH = 8;
	
	private static final Log logger = LogFactory.getLog(WebUrlUtil.class);

	private static final int WEB_PROTOCOL_CONTEXT_HTTP	= 1;
	private static final int WEB_PROTOCOL_CONTEXT_HTTPS	= 2;
	private static final int WEB_PROTOCOL_HTTP 			= 3;
	private static final int WEB_PROTOCOL_HTTPS			= 4;
	
	private static int adapterWebProtocol 	= -1;
	private static int servletWebProtocol 	= -1;
	private static int rssWebProtocol 		= -1;
	private static int atomWebProtocol 		= -1;
	private static int icalWebProtocol 		= -1;
	private static int ssfsWebProtocol 		= -1;
	private static int simpleURLWebProtocol = -1;
	
	private static final String SSFS_HOST_REWRITE = "ssfs.default.host.rewrite";
	private static final String SSFS_IGNORE_PASSWORD_ENABLED = "ssfs.ignore.password.enabled";
	private static final String FILE_URL_ENCODE_FILENAME = "file.url.encode.filename";
	
	private static String multiHomingSubPath = null;
	
	enum WebApp {
		SSF,
		SSFS,
		SIMPLE_URL,
		MOBILE
	}

	public static StringBuffer getSSFSContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SSFS, req, (req != null)? req.isSecure():null, getSsfsWebProtocol(), true);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "/ssfs");
		
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static StringBuffer getSSFSContextRootURL(PortletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SSFS, req, (req != null)? req.isSecure():null, getSsfsWebProtocol(), true);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "/ssfs");
		
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static String getSimpleURLContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SIMPLE_URL, req, req.isSecure(), getSimpleURLWebProtocol(), false);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SIMPLEURL_CTX, "/teaming");
		
		sb.append(ctx).append("/");
		
		return sb.toString();
	}
	
	public static StringBuffer getMobileURLContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.MOBILE, req, req.isSecure(), getSimpleURLWebProtocol(), false);

		sb.append("/");

		return sb;
	}
	
	public static String getSimpleURLContextRootURL(PortletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SIMPLE_URL, req, req.isSecure(), getSimpleURLWebProtocol(), false);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SIMPLEURL_CTX, "/teaming");
		
		sb.append(ctx).append("/");
		
		return sb.toString();
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
	
	public static String getAdapterRootURL(boolean secure, String hostname, int port) {
		return getSSFContextRootURL(secure, hostname.toLowerCase(), port, false).append("a/").toString();
	}
	public static String getAdapterRootUrl() {
		return getSSFContextRootURL((HttpServletRequest) null) + "a/";
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
	
	public static String getAtomRootURL(HttpServletRequest req) {
		return getAtomRootURL(req, ((req != null)? req.isSecure() : false));
	}
	
	public static String getAtomRootURL(PortletRequest req) {
		return getAtomRootURL(req, ((req != null)? req.isSecure() : false));
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
	
	public static String getAtomRootURL(HttpServletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getAtomWebProtocol()).append("atom/").toString();
	}
	
	public static String getAtomRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getAtomWebProtocol()).append("atom/").toString();
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
	public static String getWapLandingPage(HttpServletRequest request, String userId) {
		AdaptedPortletURL adapterUrl = new AdaptedPortletURL(request, "ss_forum", true);
		adapterUrl.setParameter(WebKeys.ACTION, WebKeys.ACTION_MOBILE_AJAX);
		adapterUrl.setParameter(WebKeys.URL_ENTRY_ID, userId);
		adapterUrl.setParameter(WebKeys.URL_ENTITY_TYPE, EntityIdentifier.EntityType.user.name());
		return adapterUrl.toString();
	}
	public static String getFileUrl(PortletRequest req, String path, FileAttachment fAtt) {
		return getFileUrl(WebUrlUtil.getServletRootURL(req), path, fAtt);
	}
	public static String getFileUrl(HttpServletRequest req, String path, FileAttachment fAtt) {
		return getFileUrl(WebUrlUtil.getServletRootURL(req), path, fAtt);
	}
	public static String getFileUrl(String webPath, String action, FileAttachment fAtt) {
		DefinableEntity entity = fAtt.getOwner().getEntity();

		if (fAtt instanceof VersionAttachment) {
			VersionAttachment version = (VersionAttachment)fAtt;
			return getFileUrl(webPath, action, entity.getId().toString(), entity.getEntityType().name(), String.valueOf(fAtt.getId()),  
					String.valueOf(version.getModification().getDate().getTime()), String.valueOf(version.getVersionNumber()), 
					version.getFileItem().getName());
		}
		return getFileUrl(webPath, action, entity.getId().toString(), entity.getEntityType().name(), String.valueOf(fAtt.getId()),  
				String.valueOf(fAtt.getModification().getDate().getTime()), null, 
				fAtt.getFileItem().getName());
	}
	public static String getFileUrl(PortletRequest req, String action, DefinableEntity entity, String fileName) {
		return getFileUrl(WebUrlUtil.getServletRootURL(req), action, entity, fileName);
	}
	public static String getFileUrl(HttpServletRequest req, String action, DefinableEntity entity, String fileName) {
		return getFileUrl(WebUrlUtil.getServletRootURL(req), action, entity, fileName);
	}
	public static String getFileUrl(String webPath, String action, DefinableEntity entity, String fileName) {
		if (entity == null) return "";
		FileAttachment fAtt = null;
		if (!fileName.equals("")) 
			try {
				fAtt = entity.getFileAttachment(fileName);
			} catch (Exception e) {}
		if (fAtt != null) {
			return WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_FILE, entity.getId().toString(), 
				entity.getEntityType().name(), String.valueOf(fAtt.getId()), String.valueOf(fAtt.getModification().getDate().getTime()), null, fileName);
		} else {
			return WebUrlUtil.getFileUrl(webPath, WebKeys.ACTION_READ_FILE, entity.getId().toString(), 
					entity.getEntityType().name(), null, String.valueOf(new Date().getTime()) , null, fileName);
			
		}
	}
	public static String getFileUrl(PortletRequest req, String path, Map searchResults) {
		return getFileUrl(WebUrlUtil.getServletRootURL(req), path, 	searchResults);
	}
	public static String getFileUrl(HttpServletRequest req, String path, Map searchResults) {
		return getFileUrl(WebUrlUtil.getServletRootURL(req), path, searchResults);
	}
	public static String getFileUrl(String webPath, String action, Map searchResults) {
		return getFileUrl(webPath, action, searchResults, null);
	}
	public static String getFileUrl(String webPath, String action, Map searchResults, String file) {
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.valueOf((String)searchResults.get(org.kablink.util.search.Constants.ENTITY_FIELD));
		String entityId = (String)searchResults.get(org.kablink.util.search.Constants.DOCID_FIELD);
		String fileTime=null,fileName=null,fileId=null;
		Object fileIdResult = searchResults.get(org.kablink.util.search.Constants.FILE_ID_FIELD);
		if (fileIdResult == null) return "";
		//since their may be more than one attachment, we get need a consistent picture of the first one.
		if (Validator.isNull(file)) {
			if (fileIdResult instanceof SearchFieldResult) {
				fileId = ((SearchFieldResult)fileIdResult).getValueArray().get(0);
			} else {
				fileId = fileIdResult.toString();
			}
			
		} else {
			//looking for a specific file
			fileName = file;
			if (fileIdResult instanceof SearchFieldResult) {
				List<String> values = ((SearchFieldResult)fileIdResult).getValueArray();
				SearchFieldResult namesAndIds = (SearchFieldResult)searchResults.get(org.kablink.util.search.Constants.FILENAME_AND_ID_FIELD);	
				List<String> names = namesAndIds.getValueArray();
				for (int i=0; i<values.size(); ++i) {
						for (int j=0; j<names.size(); ++j) {
						if (fileName.equals(getFileInfoById(names.get(j),values.get(i)))) {
							fileId = values.get(i);
							break;
						}
					}
				}
			} else {
				fileId = fileIdResult.toString();
				if (!fileName.equals(getFileInfoById((String)searchResults.get(org.kablink.util.search.Constants.FILENAME_AND_ID_FIELD),fileId))) {
					fileId = null;
				}
			}
		}
		if (fileId == null) return "";
		//attachments only index 1 and don't have the extend fields to group file attributes
		if (org.kablink.util.search.Constants.DOC_TYPE_ATTACHMENT.equals(searchResults.get(org.kablink.util.search.Constants.DOC_TYPE_FIELD))) {
			fileName = (String)searchResults.get(org.kablink.util.search.Constants.FILENAME_FIELD);
			fileTime = (String)searchResults.get(org.kablink.util.search.Constants.FILE_TIME_FIELD);
		} else {
			Object fName = searchResults.get(org.kablink.util.search.Constants.FILENAME_AND_ID_FIELD);
			if (fName instanceof String) {
				fileName = getFileInfoById((String)fName,fileId);
			} else {
				List<String> names = ((SearchFieldResult)fName).getValueArray();
				for (int i=0; i<names.size(); ++i) {
					fileName = getFileInfoById(names.get(i),fileId);
					if (!fileName.equals(""))
						break;
				}
			}
			Object fTime = searchResults.get(org.kablink.util.search.Constants.FILE_TIME_AND_ID_FIELD);
			if (fTime instanceof String) {
				fileTime = getFileInfoById((String)fTime,fileId);
			} else {
				List<String> times = ((SearchFieldResult)fTime).getValueArray();
				for (int i=0; i<times.size(); ++i) {
					fileTime = getFileInfoById(times.get(i),fileId);
					if (!fileTime.equals(""))
						break;
				}
			}
		}

		if (fileTime == null || fileTime == "") fileTime = "1";  //doesn't matter
		return getFileUrl(webPath, action, entityId, entityType.name(), fileId, fileTime, null, fileName);
			
	}
	
	// split apart the FILE..._AND_ID field and find the word associated with
	// with this fileId
	public static String getFileInfoById(String fieldData, String fileId) {
		String info = "";
		String data = "";
	
		try {
			String[] fields = fieldData.split(org.kablink.util.search.Constants.UNIQUE_PREFIX);
			if (fields.length == 0)
				return "";
			if (fields.length == 1) {
				data = fields[0].substring(fileId.length());
				return data;
			}
			for (int i = 1; i < fields.length; i++) {
				int start = fields[i].indexOf(fileId);
				if (start > -1) {
					data = fields[i].substring(fileId.length());
					return data.trim();
				}
			}
		} catch (Exception e) {
		}
		return info;
	}
	
	
	public static String getFileUrl(String webPath, String action, String entityId, String entityType, String fileId, String attDate, String version, 
			String fileName) {
		if (Validator.isNull(fileId)) fileId = "-";
		if (Validator.isNull(version)) version = "last";
		if (Validator.isNull(webPath)) webPath = WebUrlUtil.getServletRootURL();
		StringBuffer webUrl = new StringBuffer(webPath + action);
		webUrl.append(Constants.SLASH + entityType);
		webUrl.append(Constants.SLASH + entityId);
		webUrl.append(Constants.SLASH + fileId); //for fall back
		webUrl.append(Constants.SLASH + attDate); //for browser caching
		webUrl.append(Constants.SLASH + version);
		if(SPropsUtil.getBoolean(FILE_URL_ENCODE_FILENAME, false)) {
			///encodeUrl replaces spaces with '+' which readFileController doesn't handle
			fileName = StringUtils.replace(Http.encodeURL(fileName), "+", "%20");
		}
		webUrl.append(Constants.SLASH + fileName);  
		return webUrl.toString();
	}

	public static String getSSFContextRootURL(PortletRequest req) {
		boolean secure = ((req != null)? req.isSecure() : false);
		return getSSFContextRootURL(req, secure,
				secure?WEB_PROTOCOL_CONTEXT_HTTPS:WEB_PROTOCOL_CONTEXT_HTTP).toString();
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
	
	private static StringBuffer getSSFContextRootURL(boolean secure, String hostname, int port, boolean fromStatic) {
		StringBuffer sb = getHostAndPort(WebApp.SSF, hostname, port, secure, false, fromStatic);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");

		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static String getRelativeSSFContextRootURL() {
		String ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		return ctx + "/";
	}
	
	public static String getSSFContextRootURL(HttpServletRequest req) {
		boolean secure = ((req != null)? req.isSecure() : false);
		return getSSFContextRootURL(req, secure,
				secure?WEB_PROTOCOL_CONTEXT_HTTPS:WEB_PROTOCOL_CONTEXT_HTTP).toString();
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
		boolean fromStatic = false;
		
		if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTP) {
			if(req != null) {
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
				fromStatic = true;
			}
		}
		else if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTPS) {
			if(req != null) {				
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
				fromStatic = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
			secure = false;		
			fromStatic = true;
		}
		else { // WEB_PROTOCOL_HTTPS
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			secure = true;		
			fromStatic = true;
		}
		
		StringBuffer sb = getHostAndPort(webApp, host, port, secure, portRequired, fromStatic);
		
		if(logger.isTraceEnabled())
			logger.trace("(s) Generated host and port is " + sb.toString());
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(WebApp webApp, PortletRequest req, Boolean isSecure, int webProtocol, boolean portRequired) {
		String host = null;
		int port = -1;
		boolean secure;
		boolean fromStatic = false;
		
		if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTP) {
			if(req != null) {
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
				fromStatic = true;
			}
		}
		else if(webProtocol == WEB_PROTOCOL_CONTEXT_HTTPS) {
			if(req != null) {
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
				fromStatic = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
			secure = false;		
			fromStatic = true;
		}
		else { // WEB_PROTOCOL_HTTPS
			host = getStaticHostName();
			port = SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
			secure = true;			
			fromStatic = true;
		}
		
		StringBuffer sb = getHostAndPort(webApp, host, port, secure, portRequired, fromStatic);
		
		if(logger.isTraceEnabled())
			logger.trace("(p) Generated host and port is " + sb.toString());
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(WebApp webApp, String host, int port, boolean secure, boolean portRequired, boolean fromStatic) {

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
			// Rewrite WebDAV URL only if SSO proxy is performing authentication.
			if(SPropsUtil.getBoolean(SSFS_IGNORE_PASSWORD_ENABLED, false)) {
				String ssfsHostRewrite = SPropsUtil.getString(SSFS_HOST_REWRITE, "");
				// Rewrite WebDAV URL only if rewrite-hostname is specified AND the current zone is the default zone.
				if(Validator.isNotNull(ssfsHostRewrite) &&
						RequestContextHolder.getRequestContext().getZoneName().equals(SZoneConfig.getDefaultZoneName())) {
					host = ssfsHostRewrite;
				}
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
		
		if(fromStatic && !getMultiHomingSubPath().equals(""))
			sb.append(Constants.SLASH).append(getMultiHomingSubPath());
		
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
	
	private static int getAtomWebProtocol() {
		init();
		return atomWebProtocol;
	}
	
	private static int getIcalWebProtocol() {
		init();
		return icalWebProtocol;
	}
	
	private static int getSsfsWebProtocol() {
		init();
		return ssfsWebProtocol;
	}
		
	private static int getSimpleURLWebProtocol() {
		init();
		return simpleURLWebProtocol;
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
			
			prot = SPropsUtil.getString("atom.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				atomWebProtocol = WEB_PROTOCOL_HTTP;
			else if(prot.equalsIgnoreCase("https"))
				atomWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				atomWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				atomWebProtocol = WEB_PROTOCOL_CONTEXT_HTTP;
			
			
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

			prot = SPropsUtil.getString("simpleurl.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				simpleURLWebProtocol = WEB_PROTOCOL_HTTP;
			else if(prot.equalsIgnoreCase("https"))
				simpleURLWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				simpleURLWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				simpleURLWebProtocol = WEB_PROTOCOL_CONTEXT_HTTP;			
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

	public static String getMobileURL(boolean isSecure, String hostname, int port) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_mobile", false, isSecure, hostname, port);
		return adapterUrl.toString();
	}

	private static String getMultiHomingSubPath() {
		if(multiHomingSubPath == null) {
			multiHomingSubPath = SPropsUtil.getString("sso.proxy.multihoming.subpath", "");
			if(multiHomingSubPath.startsWith("/"))
				multiHomingSubPath = multiHomingSubPath.substring(1);
			if(multiHomingSubPath.endsWith("/"))
				multiHomingSubPath = multiHomingSubPath.substring(0, multiHomingSubPath.length()-1);
		}
		return multiHomingSubPath;
	}
}
