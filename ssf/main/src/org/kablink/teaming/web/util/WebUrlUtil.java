/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.asmodule.zonecontext.ZoneContextHolder;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.lucene.util.SearchFieldResult;
import org.kablink.teaming.module.zone.ZoneModule;
import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.servlet.ParamsWrappedHttpServletRequest;
import org.kablink.util.Http;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public class WebUrlUtil {
    // ReadFile operation types.
    public static final String FILE_URL_TYPE_ZIP="zip";
    public static final String FILE_URL_TYPE_SHARE="share";

	public static final int FILE_URL_ACTION = 1;
	public static final int FILE_URL_ENTITY_TYPE = 2;
	public static final int FILE_URL_ENTITY_ID = 3;
	public static final int FILE_URL_FILE_ID = 4;
	public static final int FILE_URL_DATE = 5;
	public static final int FILE_URL_VERSION = 6;
	public static final int FILE_URL_NAME = 7;
	public static final int FILE_URL_ARG_LENGTH = 8;
	public static final int FILE_URL_ZIP_ARG_LENGTH = 5;
	public static final int FILE_URL_ZIP_SINGLE_ARG_LENGTH = 6;
	public static final int FILE_URL_ZIP_SINGLE_FILE_ID = 5;
	
	// Used the parse the URL returned by getFileListZipUrl().
	public static final int FILE_URL_ZIPLIST_ARG_LENGTH			= 11;
	public static final int FILE_URL_ZIPLIST_ZIP				=  8;
	public static final int FILE_URL_ZIPLIST_FILE_IDS_OPERAND	=  4;
	public static final int FILE_URL_ZIPLIST_FILE_IDS			=  5;
	public static final int FILE_URL_ZIPLIST_FOLDER_IDS_OPERAND	=  6;
	public static final int FILE_URL_ZIPLIST_FOLDER_IDS			=  7;
	public static final int FILE_URL_ZIPLIST_OPERATION			=  3;
	public static final int FILE_URL_ZIPLIST_RECURSIVE_OPERAND	=  9;
	public static final int FILE_URL_ZIPLIST_RECURSIVE			= 10;
	
	// Used to parse the URL returned by getFolderZipUrl().
	public static final int FILE_URL_ZIPFOLDER_ARG_LENGTH			= 9;
	public static final int FILE_URL_ZIPFOLDER_ZIP					= 6;
	public static final int FILE_URL_ZIPFOLDER_FOLDER_ID			= 5;
	public static final int FILE_URL_ZIPFOLDER_OPERATION			= 3;
	public static final int FILE_URL_ZIPFOLDER_RECURSIVE_OPERAND	= 7;
	public static final int FILE_URL_ZIPFOLDER_RECURSIVE			= 8;
	
	// Used to parse the URL returned by getFolderAsCSVFileUrl().
	public static final int FILE_URL_CSVFOLDER_ARG_LENGTH			= 9;
	public static final int FILE_URL_CSVFOLDER_FOLDER_CSV			= 8;
	public static final int FILE_URL_CSVFOLDER_FOLDER_CSV_DELIM		= 6;
	public static final int FILE_URL_CSVFOLDER_FOLDER_ID			= 5;
	public static final int FILE_URL_CSVFOLDER_OPERATION			= 3;
	
	// Used the parse the URL returned by getFileEmailTemplateUrl().
	public static final int    FILE_URL_EMAIL_TEMPLATE_ARG_LENGTH		= 5;
	public static final int    FILE_URL_EMAIL_TEMPLATE_EMAIL_TEMPLATE	= 2;
	public static final int    FILE_URL_EMAIL_TEMPLATE_TYPE				= 3;
	public static final int    FILE_URL_EMAIL_TEMPLATE_FILENAME			= 4;
	public static final String FILE_URL_EMAIL_TEMPLATE					= "emailTemplate";
	public static final String FILE_URL_EMAIL_TEMPLATE_TYPE_CUSTOMIZED	= "customized";
	public static final String FILE_URL_EMAIL_TEMPLATE_TYPE_DEFAULT		= "default";
	
	// Used the parse the URL returned by getSharedPublicFileUrl().
	public static final int FILE_URL_SHARED_PUBLIC_FILE_ARG_LENGTH	= 7;
	public static final int FILE_URL_SHARED_PUBLIC_FILE_SHARE_ID	= 3;
	public static final int FILE_URL_SHARED_PUBLIC_FILE_PASSKEY		= 4;
	public static final int FILE_URL_SHARED_PUBLIC_FILE_OPERATION	= 5;
	public static final int FILE_URL_SHARED_PUBLIC_FILE_NAME		= 6;
	
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

	enum UrlType {
		adapter,
		servlet,
		rss,
		atom,
		ical,
		ssfs,
		simpleurl,
		unspecified
	}
	
	public static StringBuffer getSSFSContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SSFS, 
				req, 
				(req != null)? req.isSecure():null, 
						getSsfsWebProtocol(),
						UrlType.ssfs,
						!SPropsUtil.getBoolean("webdav.url.port.optional",true));
		
		//String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "/ssfs");
		String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "");
		
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static StringBuffer getSSFSContextRootURL(PortletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SSFS, 
				req, 
				(req != null)? req.isSecure():null, 
						getSsfsWebProtocol(), 
						UrlType.ssfs,
						!SPropsUtil.getBoolean("webdav.url.port.optional",true));
		
		//String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "/ssfs");
		String ctx = SPropsUtil.getString(SPropsUtil.SSFS_CTX, "");
		
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static String getSimpleURLContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SIMPLE_URL, req, req.isSecure(), getSimpleURLWebProtocol(), UrlType.simpleurl, false);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SIMPLEURL_CTX, "/novl");
		
		sb.append(ctx).append("/");
		
		return sb.toString();
	}
	
	public static String getSimpleURLContextBaseURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SIMPLE_URL, req, req.isSecure(), getSimpleURLWebProtocol(), UrlType.simpleurl, false);
		
		sb.append("/");
		
		return sb.toString();
	}
	
	public static StringBuffer getMobileURLContextRootURL(HttpServletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.MOBILE, req, req.isSecure(), getSimpleURLWebProtocol(), UrlType.simpleurl, false);

		sb.append("/");

		return sb;
	}
	
	public static String getSimpleURLContextRootURL(PortletRequest req) {
		StringBuffer sb = getHostAndPort(WebApp.SIMPLE_URL, req, req.isSecure(), getSimpleURLWebProtocol(), UrlType.simpleurl, false);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SIMPLEURL_CTX, "/novl");
		
		sb.append(ctx).append("/");
		
		return sb.toString();
	}
	
	public static String getSimpleWebdavRootURL(PortletRequest req) {
		return getSSFSContextRootURL(req) + "davs/";
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
		return getSSFContextRootURL(req, secure, getAdapterWebProtocol(), UrlType.adapter).append("a/").toString();
	}
	
	public static String getAdapterRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getAdapterWebProtocol(), UrlType.adapter).append("a/").toString();
	}
	
	public static String getAdapterRootURL(boolean secure, String hostname, int port) {
		return getSSFContextRootURL(secure, hostname.toLowerCase(), port).append("a/").toString();
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
		return getSSFContextRootURL(req, secure, getServletWebProtocol(), UrlType.servlet).append("s/").toString();
	}

	public static String getServletRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getServletWebProtocol(), UrlType.servlet).append("s/").toString();
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
		return getSSFContextRootURL(req, secure, getRssWebProtocol(), UrlType.rss).append("rss/").toString();
	}
	
	public static String getRssRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getRssWebProtocol(), UrlType.rss).append("rss/").toString();
	}
	
	public static String getAtomRootURL(HttpServletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getAtomWebProtocol(), UrlType.atom).append("atom/").toString();
	}
	
	public static String getAtomRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getAtomWebProtocol(), UrlType.atom).append("atom/").toString();
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
		return getSSFContextRootURL(req, secure, getIcalWebProtocol(), UrlType.ical).append("ical/").toString();
	}
	
	public static String getIcalRootURL(PortletRequest req, Boolean secure) {
		return getSSFContextRootURL(req, secure, getIcalWebProtocol(), UrlType.ical).append("ical/").toString();
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
		return getFileUrl(getServletRootURL(req), path, fAtt, false);
	}
	public static String getFileUrl(PortletRequest req, String path, FileAttachment fAtt, boolean useVersionNumber) {
		return getFileUrl(getServletRootURL(req), path, fAtt, useVersionNumber);
	}
	public static String getFileUrl(HttpServletRequest req, String path, FileAttachment fAtt) {
		return getFileUrl(getServletRootURL(req), path, fAtt, false);
	}
	public static String getFileUrl(HttpServletRequest req, String path, FileAttachment fAtt, 
			boolean useVersionNumber, boolean download) {
		return getFileUrl(getServletRootURL(req), path, fAtt, useVersionNumber, download);
	}
	public static String getFileUrl(String webPath, String action, FileAttachment fAtt) {
		return getFileUrl(webPath, action, fAtt, false);
	}
	public static String getFileUrl(String webPath, String action, FileAttachment fAtt, 
			boolean useVersionNumber) {
		return getFileUrl(webPath, action, fAtt, false, false);
	}
	//"download" is a flag which if true is used to cause an audit trail of the download to be made
	public static String getFileUrl(String webPath, String action, FileAttachment fAtt, 
			boolean useVersionNumber, boolean download) {
		DefinableEntity entity = fAtt.getOwner().getEntity();

		if (fAtt instanceof VersionAttachment) {
			VersionAttachment version = (VersionAttachment)fAtt;
			return getFileUrl(webPath, action, entity.getId().toString(), entity.getEntityType().name(), String.valueOf(fAtt.getId()),  
					String.valueOf(version.getModification().getDate().getTime()), String.valueOf(version.getVersionNumber()), 
					version.getFileItem().getName());
		}
		if (useVersionNumber) {
			return getFileUrl(webPath, action, entity.getId().toString(), entity.getEntityType().name(), String.valueOf(fAtt.getId()),  
					String.valueOf(fAtt.getModification().getDate().getTime()), fAtt.getLastVersion().toString(), 
					fAtt.getFileItem().getName());
		} else {
			return getFileUrl(webPath, action, entity.getId().toString(), entity.getEntityType().name(), String.valueOf(fAtt.getId()),  
					String.valueOf(fAtt.getModification().getDate().getTime()), null, 
					fAtt.getFileItem().getName(), download);
		}
	}
	public static String getFileUrl(PortletRequest req, String action, DefinableEntity entity, String fileName) {
		return getFileUrl(getServletRootURL(req), action, entity, fileName);
	}
	public static String getFileUrl(HttpServletRequest req, String action, DefinableEntity entity, String fileName) {
		return getFileUrl(getServletRootURL(req), action, entity, fileName);
	}
	public static String getFileZipUrl(HttpServletRequest req, String action, DefinableEntity entity) {
		return getFileZipUrl(getServletRootURL(req), action, entity);
	}
	public static String getFileZipUrl(HttpServletRequest req, String action, DefinableEntity entity, String fileId) {
		return getFileZipUrl(getServletRootURL(req), action, entity, fileId);
	}
	public static String getFileListZipUrl(HttpServletRequest req, Collection<FolderEntry> fileList, Collection<Folder> folderList, boolean recursive) {
		return getFileListZipUrl(getServletRootURL(req), fileList, folderList, recursive);
	}
	public static String getFolderZipUrl(HttpServletRequest req, Long folderId, boolean recursive) {
		return getFolderZipUrl(getServletRootURL(req), folderId, recursive);
	}
	public static String getFolderAsCSVFileUrl(HttpServletRequest req, Long folderId, String csvDelim) {
		return getFolderAsCSVFileUrl(getServletRootURL(req), folderId, csvDelim);
	}
	public static String getFolderAsCSVFileUrl(HttpServletRequest req, Long folderId) {
		return getFolderAsCSVFileUrl(req, folderId, ",");
	}
	public static String getFileEmailTemplateUrl(HttpServletRequest req, String action, String fName, boolean defaultEmailTemplate) {
		return getFileEmailTemplateUrl(getServletRootURL(req), action, fName, defaultEmailTemplate);
	}
	public static String getFileHtmlUrl(HttpServletRequest req, String action, DefinableEntity entity, String fileName) {
		if (entity == null) return "";
		FileAttachment fAtt = null;
		if (!fileName.equals("")) 
			try {
				fAtt = entity.getFileAttachment(fileName);
			} catch (Exception e) {}
		if (fAtt != null) {
			StringBuffer webUrl = new StringBuffer(getServletRootURL(req) + action + Constants.QUESTION);
			webUrl.append(WebKeys.URL_BINDER_ID + Constants.EQUAL + entity.getParentBinder().getId().toString());
			webUrl.append(Constants.AMPERSAND + WebKeys.URL_ENTRY_ID + Constants.EQUAL + entity.getId().toString());
			webUrl.append(Constants.AMPERSAND + WebKeys.URL_ENTITY_TYPE + Constants.EQUAL + entity.getEntityType().name());
			webUrl.append(Constants.AMPERSAND + WebKeys.URL_FILE_ID + Constants.EQUAL + fAtt.getId());
			webUrl.append(Constants.AMPERSAND + WebKeys.URL_VIEW_TYPE + Constants.EQUAL + "html");
			return webUrl.toString();
		} else {
			return null;
		}
	}

	public static String getFileUrl(String webPath, String action, DefinableEntity entity, String fileName) {
		if (entity == null) return "";
		FileAttachment fAtt = null;
		if (!fileName.equals("")) 
			try {
				fAtt = entity.getFileAttachment(fileName);
			} catch (Exception e) {}
		if (fAtt != null) {
			return getFileUrl(webPath, action, entity.getId().toString(), 
				entity.getEntityType().name(), String.valueOf(fAtt.getId()), String.valueOf(fAtt.getModification().getDate().getTime()), null, fileName);
		} else {
			return getFileUrl(webPath, action, entity.getId().toString(), 
					entity.getEntityType().name(), null, String.valueOf(new Date().getTime()) , null, fileName);
			
		}
	}
	public static String getFileZipUrl(String webPath, String action, DefinableEntity entity, String fileId) {
		if (entity == null) return "";
		return getFileZipUrl(webPath, action, entity.getId().toString(), 
					entity.getEntityType().name(), fileId);			
	}
	public static String getFileZipUrl(String webPath, String action, DefinableEntity entity) {
		if (entity == null) return "";
		return getFileZipUrl(webPath, action, entity.getId().toString(), 
					entity.getEntityType().name(), "");			
	}
	public static String getFileUrl(PortletRequest req, String path, Map searchResults) {
		return getFileUrl(getServletRootURL(req), path, 	searchResults);
	}
	public static String getFileUrl(HttpServletRequest req, String path, Map searchResults) {
		return getFileUrl(getServletRootURL(req), path, searchResults);
	}
	public static String getFileUrl(String webPath, String action, Map searchResults) {
		return getFileUrl(webPath, action, searchResults, null);
	}
	public static String getFileUrl(String webPath, String action, Map searchResults, String file) {
		EntityIdentifier.EntityType entityType = EntityIdentifier.EntityType.valueOf((String)searchResults.get(org.kablink.util.search.Constants.ENTITY_FIELD));
		String entityId = (String)searchResults.get(org.kablink.util.search.Constants.DOCID_FIELD);
		String fileTime=null,fileName=null,fileId=null;
		Object fileIdResult = searchResults.get(org.kablink.util.search.Constants.FILE_ID_FIELD);
		if (fileIdResult == null) fileIdResult = searchResults.get(org.kablink.util.search.Constants.PRIMARY_FILE_ID_FIELD);
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
		return getFileUrl(webPath, action, entityId, entityType, fileId, attDate, version, fileName, false);
	}
	public static String getFileUrl(String webPath, String action, String entityId, String entityType, String fileId, String attDate, String version, 
			String fileName, boolean download) {
		if (Validator.isNull(fileId)) fileId = "-";
		if (Validator.isNull(version)) {
			if (download) {
				version = WebKeys.READ_FILE_LAST_VIEW;
			} else {
				version = WebKeys.READ_FILE_LAST;
			}
		}
		if (Validator.isNull(webPath)) webPath = getServletRootURL();
		StringBuffer webUrl = new StringBuffer(webPath + action);
		webUrl.append(Constants.SLASH + entityType);
		webUrl.append(Constants.SLASH + entityId);
		webUrl.append(Constants.SLASH + fileId); //for fall back
		webUrl.append(Constants.SLASH + attDate); //for browser caching
		webUrl.append(Constants.SLASH + version);
		webUrl.append(Constants.SLASH + urlEncodeFilename(fileName));  
		return webUrl.toString();
	}

	public static String getFileZipUrl(String webPath, String action, String entityId, String entityType, String fileId) {
		if (Validator.isNull(webPath)) webPath = getServletRootURL();
		StringBuffer webUrl = new StringBuffer(webPath + action);
		webUrl.append(Constants.SLASH + entityType);
		webUrl.append(Constants.SLASH + entityId);
		webUrl.append(Constants.SLASH + "zip"); 
		if (!fileId.equals("")) webUrl.append(Constants.SLASH + fileId); 
		return webUrl.toString();
	}

	public static String getFileEmailTemplateUrl(String webPath, String action, String fileName, boolean defaultEmailTemplate) {
		StringBuffer webUrl = new StringBuffer(webPath + action);
		webUrl.append(Constants.SLASH + FILE_URL_EMAIL_TEMPLATE); 
		webUrl.append(Constants.SLASH + (defaultEmailTemplate ? FILE_URL_EMAIL_TEMPLATE_TYPE_DEFAULT : FILE_URL_EMAIL_TEMPLATE_TYPE_CUSTOMIZED)); 
		webUrl.append(Constants.SLASH + urlEncodeFilename(fileName));
		return webUrl.toString();
	}

	/**
	 * Returns the URL for downloading the primary files from a
	 * specific collection of entries in a zip.
	 * 
	 * Note:  The URL constructed must adhere to the count and indexes
	 * of the various FILE_URL_ZIPLIST_* definitions. 
	 * 
	 * @param webPath
	 * @param fileList
	 * @param folderList
	 * @param recursive
	 * 
	 * @return
	 */
	public static String getFileListZipUrl(String webPath, Collection<FolderEntry> fileList, Collection<Folder> folderList, boolean recursive) {
		// Construct a ':' separated list of the entry IDs.
		StringBuffer entryIdsBuf = new StringBuffer();
		boolean first = true;
		for (FolderEntry fe:  fileList) {
			if (!first) {
				entryIdsBuf.append(":");
			}
			first = false;
			entryIdsBuf.append(String.valueOf(fe.getId()));
		}
		String entryIds;
		if (first)
		     entryIds = "-";
		else entryIds = entryIdsBuf.toString();
		
		// Construct a ':' separated list of the folder IDs.
		StringBuffer folderIdsBuf = new StringBuffer();
		first = true;
		for (Folder folder:  folderList) {
			if (!first) {
				folderIdsBuf.append(":");
			}
			first = false;
			folderIdsBuf.append(String.valueOf(folder.getId()));
		}
		String folderIds;
		if (first)
		     folderIds = "-";
		else folderIds = folderIdsBuf.toString();

		// Construct and return the URL.
		if (Validator.isNull(webPath)) webPath = getServletRootURL();
		StringBuffer webUrl = new StringBuffer(webPath + WebKeys.ACTION_READ_FILE);
		webUrl.append(Constants.SLASH + WebKeys.URL_OPERATION);
		webUrl.append(Constants.SLASH + WebKeys.OPERATION_READ_FILE_LIST);
		webUrl.append(Constants.SLASH + WebKeys.URL_FOLDER_ENTRY_LIST);
		webUrl.append(Constants.SLASH + entryIds);
		webUrl.append(Constants.SLASH + WebKeys.URL_FOLDER_LIST);
		webUrl.append(Constants.SLASH + folderIds);
		webUrl.append(Constants.SLASH + "zip"); 
		webUrl.append(Constants.SLASH + WebKeys.URL_RECURSIVE);
		webUrl.append(Constants.SLASH + recursive);
		return webUrl.toString();
	}

	/**
	 * Returns the URL for downloading the primary files from a folder
	 * in a zip.
	 * 
	 * Note:  The URL constructed must adhere to the count and indexes
	 * of the various FILE_URL_ZIPFOLDER_* definitions. 
	 * 
	 * @param webPath
	 * @param folderId
	 * @param recursive
	 * 
	 * @return
	 */
	public static String getFolderZipUrl(String webPath, Long folderId, boolean recursive) {
		// Construct and return the URL.
		if (Validator.isNull(webPath)) webPath = getServletRootURL();
		StringBuffer webUrl = new StringBuffer(webPath + WebKeys.ACTION_READ_FILE);
		webUrl.append(Constants.SLASH + WebKeys.URL_OPERATION);
		webUrl.append(Constants.SLASH + WebKeys.OPERATION_READ_FOLDER);
		webUrl.append(Constants.SLASH + WebKeys.URL_FOLDER_ID);
		webUrl.append(Constants.SLASH + folderId);
		webUrl.append(Constants.SLASH + "zip");
		webUrl.append(Constants.SLASH + WebKeys.URL_RECURSIVE);
		webUrl.append(Constants.SLASH + recursive);
		return webUrl.toString();
	}

	/**
	 * Returns the URL for downloading a folder as a CSV file.
	 * 
	 * Note:  The URL constructed must adhere to the count and indexes
	 * of the various FILE_URL_CSVFOLDER_* definitions. 
	 * 
	 * @param webPath
	 * @param folderId
	 * 
	 * @return
	 */
	public static String getFolderAsCSVFileUrl(String webPath, Long folderId, String csvDelim) {
		// Construct and return the URL.
		if (Validator.isNull(webPath)) webPath = getServletRootURL();
		StringBuffer webUrl = new StringBuffer(webPath + WebKeys.ACTION_READ_FILE);
		webUrl.append(Constants.SLASH + WebKeys.URL_OPERATION);
		webUrl.append(Constants.SLASH + WebKeys.OPERATION_READ_FOLDER);
		webUrl.append(Constants.SLASH + WebKeys.URL_FOLDER_ID);
		webUrl.append(Constants.SLASH + folderId);
		webUrl.append(Constants.SLASH + WebKeys.URL_FOLDER_CSV_DELIM);
		webUrl.append(Constants.SLASH + urlEncodeCSVDelim(csvDelim));
		webUrl.append(Constants.SLASH + WebKeys.URL_FOLDER_CSV);
		return webUrl.toString();
	}
	
	public static String getFolderAsCSVFileUrl(String webPath, Long folderId) {
		return getFolderAsCSVFileUrl(webPath, folderId, ",");
	}

	/*
	 * This routine is used to get Public Link URLs.
	 *  
	 * Public Links result from creating a ShareItem with a
	 * recipientType of publicLink.  Those ShareItems contain a pass
	 * key that allows the file to be publicly read.  There are two
	 * supported operations:  publicLink and publicLinkHtml.  The
	 * publicLink operation downloads the file.  The publicLinkHtml
	 * operation displays the converted HTML of the file.
	 */
	private static String getSharedPublicFileUrlImpl(Long shareItemId, String passKey, String operation, String fileName) {
		// We build the base URL using an AdaptedPortletURL so that we
		// get the same protocol, domain and part as returned by
		// PermaLinkUtil.getPermalinkForEmail().
		Boolean oldUseRTContext = ZoneContextHolder.getUseRuntimeContext();
		ZoneContextHolder.setUseRuntimeContext(Boolean.FALSE);
		AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		url.setCrawler(false);
		String baseUrl = url.toString();
		ZoneContextHolder.setUseRuntimeContext(oldUseRTContext);
		int pos = baseUrl.indexOf("a/do?");
		baseUrl = (baseUrl.substring(0, pos) + "s/");
		
		StringBuffer webUrl = new StringBuffer(baseUrl + WebKeys.ACTION_READ_FILE);
		webUrl.append(Constants.SLASH + WebKeys.URL_ENTITY_TYPE_SHARE);
		webUrl.append(Constants.SLASH + String.valueOf(shareItemId));
		webUrl.append(Constants.SLASH + passKey); 
		webUrl.append(Constants.SLASH + operation); 
		webUrl.append(Constants.SLASH + urlEncodeFilename(fileName)); 
		String reply = webUrl.toString();
		if (PermaLinkUtil.forceSecureLinksInEmail()) {
			reply = PermaLinkUtil.forceHTTPSInUrl(reply);
		}
		return reply;
	}
	
	/**
	 * This routine is used to get Public Link URLs.
	 *  
	 * Public Links result from creating a ShareItem with a
	 * recipientType of publicLink.  Those ShareItems contain a pass
	 * key that allows the file to be publicly read.  There are two
	 * supported operations:  publicLink and publicLinkHtml.  The
	 * publicLink operation downloads the file.  The publicLinkHtml
	 * operation displays the converted HTML of the file.
	 * 
	 * @param hRequest
	 * @param shareItemId
	 * @param passKey
	 * @param operation
	 * @param fileName
	 * 
	 * @return
	 */
	public static String getSharedPublicFileUrl(HttpServletRequest hRequest, Long shareItemId, String passKey, String operation, String fileName) {
		// Always use the implementation form of the method.
		return getSharedPublicFileUrlImpl(shareItemId, passKey, operation, fileName);
	}

	/**
	 * This routine is used to get Public Link URLs.
	 *  
	 * Public Links result from creating a ShareItem with a
	 * recipientType of publicLink.  Those ShareItems contain a pass
	 * key that allows the file to be publicly read.  There are two
	 * supported operations:  publicLink and publicLinkHtml.  The
	 * publicLink operation downloads the file.  The publicLinkHtml
	 * operation displays the converted HTML of the file.
	 * 
	 * @param hRequest
	 * @param shareItemId
	 * @param passKey
	 * @param operation
	 * @param fileName
	 * 
	 * @return
	 */
	public static String getSharedPublicFileUrl(PortletRequest pRequest, Long shareItemId, String passKey, String operation, String fileName) {
		// Always use the implementation form of the method.
		return getSharedPublicFileUrlImpl(shareItemId, passKey, operation, fileName);
	}

	public static String getSSFContextRootURL(PortletRequest req) {
		boolean secure = ((req != null)? req.isSecure() : false);
		return getSSFContextRootURL(req, secure,
				secure?WEB_PROTOCOL_CONTEXT_HTTPS:WEB_PROTOCOL_CONTEXT_HTTP, UrlType.unspecified).toString();
	}
	private static StringBuffer getSSFContextRootURL(PortletRequest req, Boolean secure, int webProtocol, UrlType urlType) {
		StringBuffer sb = getHostAndPort(WebApp.SSF, req, secure, webProtocol, urlType, false);
				
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	private static StringBuffer getSSFContextRootURL(boolean secure, String hostname, int port) {
		StringBuffer sb = getHostAndPort(WebApp.SSF, hostname, port, secure, false);
		
		String ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");

		sb.append(ctx).append("/");
		
		return sb;
	}
	
	public static String getStaticFilesSSFContextRootURL() {
		String ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		ctx += "/" + ObjectKeys.STATIC_DIR;
		ctx += "/" + SPropsUtil.getString("release.static.dir", "xxx");
		return ctx + "/";
	}
	
	public static String getSSFContextRootURL(HttpServletRequest req) {
		boolean secure = ((req != null)? req.isSecure() : false);
		return getSSFContextRootURL(req, secure,
				secure?WEB_PROTOCOL_CONTEXT_HTTPS:WEB_PROTOCOL_CONTEXT_HTTP, UrlType.unspecified).toString();
	}
	private static StringBuffer getSSFContextRootURL(HttpServletRequest req, Boolean secure, int webProtocol, UrlType urlType) {
		StringBuffer sb = getHostAndPort(WebApp.SSF, req, secure, webProtocol, urlType, false);
				
		// Context path
		String ctx;
		if(req == null)
			ctx = SPropsUtil.getString(SPropsUtil.SSF_CTX, "/ssf");
		else
			ctx = req.getContextPath();
		sb.append(ctx).append("/");
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(WebApp webApp, HttpServletRequest req, Boolean isSecure, int webProtocol, UrlType urlType, boolean portRequired) {
		String host = null;
		int port = -1;
		boolean secure;
		
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
			else if(ZoneContextHolder.getServerName() != null && Boolean.TRUE.equals(ZoneContextHolder.getUseRuntimeContext())) {
				host = ZoneContextHolder.getServerName();
				if(port == -1)
					port = ZoneContextHolder.getServerPort();
				if(isSecure != null)
					secure = isSecure; // don't get it from the context
				else
					secure = ZoneContextHolder.isSecure();
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (http request) passed in. Getting values from properties file.");
				host = getStaticHostName(urlType);
				port = getStaticPort(urlType);
				secure = false;
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
			else if(ZoneContextHolder.getServerName() != null && Boolean.TRUE.equals(ZoneContextHolder.getUseRuntimeContext())) {
				host = ZoneContextHolder.getServerName();
				if(port == -1)
					port = ZoneContextHolder.getServerPort();
				if(isSecure != null)
					secure = isSecure; // don't get it from the context
				else
					secure = ZoneContextHolder.isSecure();
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (http request) passed in. Getting values from properties file.");
				host = getStaticHostName(urlType);
				port = getStaticSecurePort(urlType);
				secure = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = getStaticHostName(urlType);
			port = getStaticPort(urlType);
			secure = false;		
		}
		else { // WEB_PROTOCOL_HTTPS
			host = getStaticHostName(urlType);
			port = getStaticSecurePort(urlType);
			secure = true;			
		}
		
		StringBuffer sb = getHostAndPort(webApp, host, port, secure, portRequired);
		
		if(logger.isTraceEnabled())
			logger.trace("(s) Generated host and port is " + sb.toString());
		
		return sb;
	}
	
	private static StringBuffer getHostAndPort(WebApp webApp, PortletRequest req, Boolean isSecure, int webProtocol, UrlType urlType, boolean portRequired) {
		String host = null;
		int port = -1;
		boolean secure;
		
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
			else if(ZoneContextHolder.getServerName() != null && Boolean.TRUE.equals(ZoneContextHolder.getUseRuntimeContext())) {
				host = ZoneContextHolder.getServerName();
				if(port == -1)
					port = ZoneContextHolder.getServerPort();
				if(isSecure != null)
					secure = isSecure; // don't get it from the context
				else
					secure = ZoneContextHolder.isSecure();
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (portlet request) passed in. Getting values from properties file.");
				host = getStaticHostName(urlType);
				port = getStaticPort(urlType);
				// context override value isSecure is relevant only when req is non-null
				secure = false;
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
			else if(ZoneContextHolder.getServerName() != null && Boolean.TRUE.equals(ZoneContextHolder.getUseRuntimeContext())) {
				host = ZoneContextHolder.getServerName();
				if(port == -1)
					port = ZoneContextHolder.getServerPort();
				if(isSecure != null)
					secure = isSecure; // don't get it from the context
				else
					secure = ZoneContextHolder.isSecure();
			}
			else {
				if(logger.isTraceEnabled())
					logger.trace("No context (portlet request) passed in. Getting values from properties file.");
				host = getStaticHostName(urlType);
				port = getStaticSecurePort(urlType);
				// context override value isSecure is relevant only when req is non-null
				secure = true;
			}			
		}
		else if(webProtocol == WEB_PROTOCOL_HTTP) {
			host = getStaticHostName(urlType);
			port = getStaticPort(urlType);
			secure = false;		
		}
		else { // WEB_PROTOCOL_HTTPS
			host = getStaticHostName(urlType);
			port = getStaticSecurePort(urlType);
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
		if(webApp != null && webApp == WebApp.SSFS) {
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
		
		// Undo this change for now - See Bug 536305 for the reason.
		/*
		if(!getMultiHomingSubPath().equals(""))
			sb.append(Constants.SLASH).append(getMultiHomingSubPath());
		*/
		
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
			int webProtocolHttp;
			int webProtocolContextHttp;
			if (0 == SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT)) {
				webProtocolHttp        = WEB_PROTOCOL_HTTPS;
				webProtocolContextHttp = WEB_PROTOCOL_CONTEXT_HTTPS;
			}
			else {
				webProtocolHttp        = WEB_PROTOCOL_HTTP;
				webProtocolContextHttp = WEB_PROTOCOL_CONTEXT_HTTP;
			}
			
			String prot = SPropsUtil.getString("adapter.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				adapterWebProtocol = webProtocolHttp;
			else if(prot.equalsIgnoreCase("https"))
				adapterWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				adapterWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				adapterWebProtocol = webProtocolContextHttp;
			
			prot = SPropsUtil.getString("servlet.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				servletWebProtocol = webProtocolHttp;
			else if(prot.equalsIgnoreCase("https"))
				servletWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				servletWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				servletWebProtocol = webProtocolContextHttp;
			
			prot = SPropsUtil.getString("rss.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				rssWebProtocol = webProtocolHttp;
			else if(prot.equalsIgnoreCase("https"))
				rssWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				rssWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				rssWebProtocol = webProtocolContextHttp;
			
			prot = SPropsUtil.getString("atom.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				atomWebProtocol = webProtocolHttp;
			else if(prot.equalsIgnoreCase("https"))
				atomWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				atomWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				atomWebProtocol = webProtocolContextHttp;
			
			
			prot = SPropsUtil.getString("ical.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				icalWebProtocol = webProtocolHttp;
			else if(prot.equalsIgnoreCase("https"))
				icalWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				icalWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				icalWebProtocol = webProtocolContextHttp;

			prot = SPropsUtil.getString("ssfs.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				ssfsWebProtocol = webProtocolHttp;
			else if(prot.equalsIgnoreCase("https"))
				ssfsWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				ssfsWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				ssfsWebProtocol = webProtocolContextHttp;			

			prot = SPropsUtil.getString("simpleurl.web.protocol", "context");
			if(prot.equalsIgnoreCase("http"))
				simpleURLWebProtocol = webProtocolHttp;
			else if(prot.equalsIgnoreCase("https"))
				simpleURLWebProtocol = WEB_PROTOCOL_HTTPS;
			else if(prot.equalsIgnoreCase("context-https"))
				simpleURLWebProtocol = WEB_PROTOCOL_CONTEXT_HTTPS;
			else
				simpleURLWebProtocol = webProtocolContextHttp;			
		}
	}
	
	private static String getStaticHostName(UrlType urlType) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		if(zoneName.equals(SZoneConfig.getDefaultZoneName())) {
			// Default zone does not have virtual host name associated with it.
			// Even if it did, we use the value specified in the properties file.
			String host = SPropsUtil.getString(urlType.name() + "." + SPropsUtil.SSF_DEFAULT_HOST, null);
			if(Validator.isNull(host))
				host = SPropsUtil.getDefaultHost();
			return host;
		}
		else {
			return getZoneModule().getVirtualHost(zoneName);
		}
	}
	
	private static int getStaticPort(UrlType urlType) {
		String portStr = SPropsUtil.getString(urlType.name() + "." + SPropsUtil.SSF_PORT, null);
		if(Validator.isNull(portStr))
			return SPropsUtil.getInt(SPropsUtil.SSF_PORT, Http.HTTP_PORT);
		else
			return Integer.parseInt(portStr);
	}
	
	private static int getStaticSecurePort(UrlType urlType) {
		String portStr = SPropsUtil.getString(urlType.name() + "." + SPropsUtil.SSF_SECURE_PORT, null);
		if(Validator.isNull(portStr))
			return SPropsUtil.getInt(SPropsUtil.SSF_SECURE_PORT, Http.HTTPS_PORT);
		else
			return Integer.parseInt(portStr);
	}
	
	private static ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}

	public static String getMobileURL(boolean isSecure, String hostname, int port) {
		AdaptedPortletURL adapterUrl = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_mobile", false, isSecure, hostname, port);
		return adapterUrl.toString();
	}

	@SuppressWarnings("unused")
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
	
	public static String getSsoProxyLogoffUrl(HttpServletRequest req) {
		String url = SPropsUtil.getString("sso.proxy.logoff.url", "");
		if(url.length() > 0) {
			String lowerCaseUrl = url.toLowerCase();
			if(!lowerCaseUrl.startsWith("http") && !lowerCaseUrl.startsWith("https")) {
				if(url.startsWith("://")) { // scheme is dynamic
					String scheme = (req.isSecure())? "https" : "http";
					url = scheme + url;	
				}
				else {
					String host = req.getServerName().toLowerCase();
					int port = req.getServerPort();
					boolean secure = req.isSecure();
					StringBuffer sb = getHostAndPort(null, host, port, secure, false);
					if(!url.startsWith("/"))
						sb.append("/");
					sb.append(url);
					url = sb.toString();
				}
			}
		}
		return url;
	}
	
	/**
	 * If configured to do so, URL encodes fileNames for inclusion into a URL.
	 * 
	 * @param fileName
	 * 
	 * @return
	 */
	public static String urlEncodeFilename(String fileName) {
		// Do we have a filename that we're supposed to encode?
		String encodedFileName;
		if (MiscUtil.hasString(fileName) && SPropsUtil.getBoolean(FILE_URL_ENCODE_FILENAME, false)) {
			// Yes!  Encode it.
			encodedFileName = Http.encodeURL(fileName);
			encodedFileName = StringUtils.replace(encodedFileName, "+", "%20");
			logger.debug("WebUrlUtil.urlEncodeFilename( '" + fileName + "' encoded as '" + encodedFileName + "')");
		}
		else {
			// No, we don't have a filename or we're not supposed to
			// encode it!  Return what was passed in.
			encodedFileName = fileName;
			logger.debug("WebUrlUtil.urlEncodeFilename( '" + ((null == fileName) ? "<null>" : fileName) + "' was not encoded )");
		}
		
		// If we get here, encodedFileName refers to the encoded
		// version of the string received if encoding was configured.
		// Return it.
		return encodedFileName;
	}
	
	/*
	 * URL encodes a CSV delimiter for inclusion into a URL.
	 */
	private static String urlEncodeCSVDelim(String csvDelim) {
		String encodedCSVDelim;
		if (MiscUtil.hasString(csvDelim)) {
			encodedCSVDelim = Http.encodeURL(csvDelim);
			encodedCSVDelim = StringUtils.replace(encodedCSVDelim, "+", "%20");
			logger.debug("WebUrlUtil.urlEncodeCSVDelim( '" + csvDelim + "' encoded as '" + encodedCSVDelim + "')");
		}
		
		else {
			encodedCSVDelim = csvDelim;
			logger.debug("WebUrlUtil.urlEncodeCSVDelim( '" + ((null == csvDelim) ? "<null>" : csvDelim) + "' was not encoded )");
		}
		
		// If we get here, encodedCSVDelim refers to the encoded
		// version of the string received.  Return it.
		return encodedCSVDelim;
	}
	
	//Routine to see if this is a mobile device in Full UI mode
	public static boolean isMobileFullUI(HttpServletRequest req) {
		HttpSession session = WebHelper.getRequiredSession(req);
		Boolean isFullUI = (Boolean)session.getAttribute(WebKeys.MOBILE_FULL_UI);
		if (isFullUI == null || !isFullUI) {
			return false;
		} else {
			return true;
		}
	}
	
	public static HttpServletRequest getNormalizedRequest(HttpServletRequest req) {
		String pathInfo = req.getPathInfo();
		if(pathInfo.startsWith("/c/")) { // adapter url for crawler
			Map pathParams = getPathParams(pathInfo.substring(3));
			if(pathParams != null && pathParams.size() > 0) {
				pathParams.putAll(req.getParameterMap());
				return new ParamsWrappedHttpServletRequest(req, pathParams);
			}
		}
		return req;
	}
	
	protected static Map<String, String[]> getPathParams(String pathInfo) {
		if(pathInfo == null)
			return null;
		String[] pathElems = StringUtil.split(pathInfo, "/");
		if(pathElems == null || pathElems.length < 2)
			return null;
		Map map = new HashMap();
		int count = pathElems.length / 2;
		for(int i = 0; i < count; i++) {
			map.put(pathElems[i*2], new String[]{pathElems[i*2+1]});
		}
		return map;
	}

    public static String getLocalDesktopDeploymentURL() {
        StringBuffer url =  getHostAndPort(null, (HttpServletRequest) null, null, WEB_PROTOCOL_CONTEXT_HTTPS, UrlType.unspecified, false);
        url.append("/desktopapp");
        return url.toString();
    }

    /**
     * Returns the URL from the request reformatted as appropriate for
     * embedding in an email.
     * 
     * DRF (20150109):  This method was originally written to fix
     *     bug#912103 where in a clustered environment, emails using
     *     URLs from the request contain the 'domain:port' from the
     *     specific server the code is running on instead of the root
     *     of the cluster.
     * 
     * @param req
     * 
     * @return
     */
    public static String getCompleteURLFromRequestForEmail(HttpServletRequest req) {
    	String baseUrl = Http.getCompleteURL(req);
    	logger.debug("WebUrlUtil.getCompleteURLFromRequestForEmail( baseUrl ):  " + baseUrl);
    	
		Boolean oldUseRTContext = ZoneContextHolder.getUseRuntimeContext();
		ZoneContextHolder.setUseRuntimeContext(Boolean.FALSE);
		AdaptedPortletURL url = AdaptedPortletURL.createAdaptedPortletURLOutOfWebContext("ss_forum", true);
		url.setCrawler(false);
		String baseUrlForEmail = url.toString();
		ZoneContextHolder.setUseRuntimeContext(oldUseRTContext);
		int pos = baseUrlForEmail.indexOf("a/do?");
		baseUrlForEmail = baseUrlForEmail.substring(0, pos);
		if (PermaLinkUtil.forceSecureLinksInEmail()) {
			baseUrlForEmail = PermaLinkUtil.forceHTTPSInUrl(baseUrlForEmail);
		}
    	logger.debug("WebUrlUtil.getCompleteURLFromRequestForEmail( baseUrlForEmail ):  " + baseUrlForEmail);
    	
    	if (0 != baseUrl.indexOf(baseUrlForEmail)) {
    		baseUrl = (baseUrlForEmail + baseUrl.substring(baseUrl.indexOf("a/do?")));
        	logger.debug("WebUrlUtil.getCompleteURLFromRequestForEmail( patchedUrl ):  " + baseUrl);
    	}
    	
    	return baseUrl;
    }
}
