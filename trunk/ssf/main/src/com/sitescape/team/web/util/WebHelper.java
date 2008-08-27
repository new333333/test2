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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.dao.ProfileDao;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.runas.RunasCallback;
import com.sitescape.team.runas.RunasTemplate;
import com.sitescape.team.security.accesstoken.AccessTokenManager;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.SpringContextUtil;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.web.WebKeys;
import com.sitescape.util.Html;
import com.sitescape.util.PortalDetector;
import com.sitescape.util.Validator;

public class WebHelper {
	protected static Log logger = LogFactory.getLog(WebHelper.class);
	private static String specialAmp = "___aMp___";

	public static boolean isUserLoggedIn(HttpServletRequest request) {
		try {
			getRequiredUserName(request);
			return true;
		}
		catch(IllegalStateException e) {
			return false;
		}
	}
	
	public static boolean isUserLoggedIn(PortletRequest request) {
		try {
			getRequiredUserName(request);
			return true;
		}
		catch(IllegalStateException e) {
			return false;
		}
	}
	
	public static boolean isGuestLoggedIn(HttpServletRequest request) {
		try {
			String username = getRequiredUserName(request);
			if(username.equals(SZoneConfig.getGuestUserName(getZoneNameByVirtualHost(request))))
				return true;
			else
				return false;
		}
		catch(IllegalStateException e) {
			return false;
		}	
	}
	
	public static boolean isGuestLoggedIn(PortletRequest request) {
		try {
			String username = getRequiredUserName(request);
			if(username.equals(SZoneConfig.getGuestUserName(getZoneNameByVirtualHost(request))))
				return true;
			else
				return false;
		}
		catch(IllegalStateException e) {
			return false;
		}	
	}
	
	public static String getRequiredUserName(HttpServletRequest request) 
	throws IllegalStateException {
		HttpSession ses = getRequiredSession(request);			
		return (String) ses.getAttribute(WebKeys.USER_NAME);
	}
	
	public static String getRequiredUserName(PortletRequest request) 
	throws IllegalStateException {
		PortletSession ses = getRequiredPortletSession(request);			
		return (String) ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE);		
	}
	
	public static Long getRequiredUserId(HttpServletRequest request) 
	throws IllegalStateException {
		HttpSession ses = getRequiredSession(request);			
		return (Long) ses.getAttribute(WebKeys.USER_ID);
	}
	
	public static Long getRequiredUserId(PortletRequest request) 
	throws IllegalStateException {
		PortletSession ses = getRequiredPortletSession(request);			
		return (Long) ses.getAttribute(WebKeys.USER_ID, PortletSession.APPLICATION_SCOPE);		
	}
	
	public static String getRequiredZoneName(HttpServletRequest request) 
	throws IllegalStateException {
		HttpSession ses = getRequiredSession(request);			
		return (String) ses.getAttribute(WebKeys.ZONE_NAME);
	}
	
	public static String getRequiredZoneName(PortletRequest request) 
	throws IllegalStateException {
		PortletSession ses = getRequiredPortletSession(request);			
		return (String) ses.getAttribute(WebKeys.ZONE_NAME, PortletSession.APPLICATION_SCOPE);		
	}
	
	public static Long getRequiredZoneId(HttpServletRequest request) 
	throws IllegalStateException {
		HttpSession ses = getRequiredSession(request);			
		return (Long) ses.getAttribute(WebKeys.ZONE_ID);
	}
	
	public static Long getRequiredZoneId(PortletRequest request) 
	throws IllegalStateException {
		PortletSession ses = getRequiredPortletSession(request);			
		return (Long) ses.getAttribute(WebKeys.ZONE_ID, PortletSession.APPLICATION_SCOPE);		
	}
	
	public static PortletSession getRequiredPortletSession(PortletRequest request) 
	throws IllegalStateException {
		PortletSession ses = request.getPortletSession(false);
		
		if(ses != null) { // session already exists
			if(ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE) == null) {
				String username = getRemoteUserName(request);
				if(username != null) {
					// put the context into the existing session
					putContext(ses, getProfileDao().findUserByName(username, getZoneIdByVirtualHost(request)));
				}
				else {
					// Neither the session nor the request contains username.
					// The session must have been created via some invalid means
					// (programming error) or side effect (portal/app server 
					// environment). In this case we shouldn't allow the caller 
					// to proceed normally.
					throw new IllegalStateException("No valid session - Illegal request sequence.");
				}
			}
		}
		else { // session doesn't exist
			String username = getRemoteUserName(request);
			if(username != null) {
				// we can create a new session and put the context into it
				ses = request.getPortletSession();
				putContext(ses, getProfileDao().findUserByName(username, getZoneIdByVirtualHost(request)));
			}
			else {
				// since we don't have username we must not create a new session here
				throw new IllegalStateException("No valid session - Illegal request sequence");
			}
		}
		
		return ses;
	}
	
	/*
	 * getRequiredSession relies on getting a brand-new session if an anonymous user authenticates,
	 *   so be sure that you set session-fixation-protection="newSession" in the spring security setup
	 */
	public static HttpSession getRequiredSession(HttpServletRequest request) 
	throws IllegalStateException {
		HttpSession ses = request.getSession();
		final String infoId = (String) ses.getAttribute(WebKeys.TOKEN_INFO_ID);
		if(infoId == null) { 
			String username = (String) ses.getAttribute(WebKeys.USER_NAME);
			if(username == null) {
				username = getRemoteUserName(request);
			}
			if(username == null) {
				username = SZoneConfig.getGuestUserName(getZoneNameByVirtualHost(request));
			}
			// put the context into the existing session
			final User user = getProfileDao().findUserByName(username, getZoneIdByVirtualHost(request));
			putContext(ses, user);

			if(!user.isShared() || 
					SPropsUtil.getBoolean("remoteapp.interactive.token.support.guest", true)) { // create a new info object
				final HttpSession session = ses;
				// Make sure to run it in the user's context.			
				RunasTemplate.runas(new RunasCallback() {
					public Object doAs() {
						String infoId = getAccessTokenManager().createTokenInfoSession(user.getId());
						session.setAttribute(WebKeys.TOKEN_INFO_ID, infoId);
						return null;
					}
				}, user);						
			}
		}
		
		return ses;
	}
	
	public static void putContext(HttpSession ses, User user) {
		// Do NOT store user object itself
		ses.setAttribute(WebKeys.ZONE_NAME, user.getParentBinder().getRoot().getName());
		ses.setAttribute(WebKeys.ZONE_ID, user.getZoneId());
		ses.setAttribute(WebKeys.USER_NAME, user.getName());
		ses.setAttribute(WebKeys.USER_ID, user.getId());
	}
	
	private static void putContext(PortletSession ses, User user) {
		// Do NOT store user object itself, only its attributes!
		ses.setAttribute(WebKeys.ZONE_NAME, user.getParentBinder().getRoot().getName(), PortletSession.APPLICATION_SCOPE);
		ses.setAttribute(WebKeys.ZONE_ID, user.getZoneId(), PortletSession.APPLICATION_SCOPE);
		ses.setAttribute(WebKeys.USER_NAME, user.getName(), PortletSession.APPLICATION_SCOPE);
		ses.setAttribute(WebKeys.USER_ID, user.getId(), PortletSession.APPLICATION_SCOPE);
	}
	
	/**
	 * Returns a handle on the uploaded file. This handle is guaranteed to be
	 * valid only during the current server session. In other words, the handle
	 * is not persistent and will be lost once the server shuts down.
	 * It returns <code>null</code> if there is no uploaded file.
	 * 
	 * @param request
	 * @return
	 */
	public static String getFileHandleOnUploadedFile(ActionRequest request)
		throws IOException {
		Map fileMap = null;
		if (request instanceof MultipartFileSupport)
			fileMap = ((MultipartFileSupport) request).getFileMap();
		if(fileMap == null || fileMap.size() == 0)
			return null;
		MultipartFile file = (MultipartFile) fileMap.values().iterator().next();
		String fileName = file.getOriginalFilename();
		// Encode the original file name into the prefix.
		String prefix = String.valueOf(fileName.length()) + "-" + fileName + "_";
		File destFile = TempFileUtil.createTempFile(prefix);
		file.transferTo(destFile);
		return destFile.getName();
	}
	
	/**
	 * Returns a handle on the uploaded iCal file. This handle is guaranteed to be
	 * valid only during the current server session. In other words, the handle
	 * is not persistent and will be lost once the server shuts down.
	 * It returns <code>null</code> if there is no uploaded file.
	 * 
	 * @param request
	 * @return
	 */
	public static String getFileHandleOnUploadedCalendarFile(ActionRequest request)
		throws IOException {
		Map fileMap = null;
		if (request instanceof MultipartFileSupport)
			fileMap = ((MultipartFileSupport) request).getFileMap();
		if(fileMap == null || fileMap.size() == 0)
			return null;
		MultipartFile mpfile = (MultipartFile) fileMap.values().iterator().next();
		String fileName = mpfile.getOriginalFilename();
		BufferedReader breader = new BufferedReader(new InputStreamReader (mpfile.getInputStream()));
		
		// Encode the original file name into the prefix.
		String prefix = String.valueOf(fileName.length()) + "-" + fileName + "_";
		
		File destFile = TempFileUtil.createTempFile(prefix);
		
		BufferedWriter bwriter = new BufferedWriter(new FileWriter (destFile));
		
		while(breader.ready()) {
			String line = breader.readLine();
			
			if(line.endsWith("=")) {
				while(line.endsWith("=") && breader.ready()) {
					String temp = line.substring(0, line.length() - 1);
					bwriter.write(temp);
					
					line = breader.readLine();
				}
				bwriter.write(line);
			}
			else {	
				bwriter.write(line);
			}
			bwriter.newLine();
		}
		
		breader.close();
		bwriter.close();
		
		return destFile.getName();
	}
	
	/**
	 * Wraps the file handle in a MultipartFile datastructure. This is used
	 * primarily to put the data in an argument format compatible with some
	 * of the module methods. 
	 * 
	 * @param fileHandle
	 * @return
	 * @throws IOException
	 */
	public static SimpleMultipartFile wrapFileHandleInMultipartFile(String fileHandle) 
		throws IOException {
		int idx = fileHandle.indexOf("-");
		int fileNameLength = Integer.parseInt(fileHandle.substring(0, idx));
		String fileName = fileHandle.substring(idx+1, idx+1+fileNameLength);
		
		File file = TempFileUtil.getTempFileByName(fileHandle);
		
		SimpleMultipartFile mf = new SimpleMultipartFile(fileName, file, true);
		
		return mf;
	}
	
	/**
	 * Returns the file name of the uploaded file
	 * 
	 * @param fileHandle
	 * @return fileName
	 */
	public static String getFileName(String fileHandle) {
		int idx = fileHandle.indexOf("-");
		if (idx < 0) return null;
		int fileNameLength = Integer.parseInt(fileHandle.substring(0, idx));
		return fileHandle.substring(idx+1, idx+1+fileNameLength);
	}
	
	public static void readFileHandleContent(String fileHandle, OutputStream out)
		throws IOException {

		FileCopyUtils.copy(new BufferedInputStream(TempFileUtil.openTempFile(fileHandle)), out);
	}
	
	/**
	 * Clean up resources associated with the file handle. Must be called
	 * when the application is done with the file handle.
	 * 
	 * @param fileHandle
	 */
	public static void releaseFileHandle(String fileHandle) {
		File file = TempFileUtil.getTempFileByName(fileHandle);

		file.delete();
	}
	
	/**
	 * Parse a description looking for uploaded file references
	 * 
	 * @param Description
	 * @param File
	 * @return 
	 */
	public static void scanDescriptionForUploadFiles(Description description, String fieldName, List fileData) {
    	String fileHandle = "";
    	Pattern pattern = Pattern.compile("(<img [^>]*src=\"[^\"]*viewType=ss_viewUploadFile[^>]*>)");
    	Matcher m = pattern.matcher(description.getText());
    	int loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup [1]: " + description.getText());
    			break;
    		}
    		String img = new String(m.group(0));
        	Pattern p2 = Pattern.compile("fileId=([^\\&\"]*)");
        	Matcher m2 = p2.matcher(img);
        	if (m2.find() && m2.groupCount() >= 1) fileHandle = m2.group(1);

	    	if (!fileHandle.equals("")) {
	    		MultipartFile myFile = null;
		    	try {
		    		myFile = WebHelper.wrapFileHandleInMultipartFile(fileHandle);
		    	} catch(IOException e) {
		    		return;
		    	}
		    	if (myFile != null) {
		    		String fileName = myFile.getOriginalFilename();
			    	if (fileName.equals("")) return;
			    	// Different repository can be specified for each file uploaded.
			    	// If not specified, use the statically selected one.  
			    	String repositoryName = RepositoryUtil.getDefaultRepositoryName();
			    	FileUploadItem fui = new FileUploadItem(FileUploadItem.TYPE_ATTACHMENT, null, myFile, repositoryName);
			    	//flag as used in markup, for further processing after files are saved
			    	fui.setMarkup(true);
			    	fui.setMarkupFieldName(fieldName);
			    	fileData.add(fui);
		    	}
	    	}
	    	//Now, replace the url with special markup version
	    	Pattern p3 = Pattern.compile("src *= *\"([^\"]*)\"");
	    	Matcher m3 = p3.matcher(img);
        	if (m3.find() && m3.groupCount() >= 1) {
        		img = new String(m3.replaceFirst("src=\"{{attachmentUrl: " + WebHelper.getFileName(fileHandle) + "}}\""));
        		description.setText(m.replaceFirst(img.replace("$", "\\$")));
        		m = pattern.matcher(description.getText());
        	}
    	}
	}
	
	public static void scanDescriptionForAttachmentUrls(Description description, DefinableEntity entity) {
		if(description == null) return;
		String entityType = entity.getEntityType().toString();
		String binderId = "";
		String entryId = "";
		if (entityType.equals(EntityIdentifier.EntityType.folder.name()) || 
				entityType.equals(EntityIdentifier.EntityType.workspace.name()) ||
				entityType.equals(EntityIdentifier.EntityType.profiles.name())) {
			binderId = entity.getId().toString();
			entryId = entity.getId().toString();
		} else if (entityType.equals(EntityIdentifier.EntityType.folderEntry.name())) {
			binderId = entity.getParentBinder().getId().toString();
			entryId = entity.getId().toString();
		}
		Pattern p1 = Pattern.compile("(\\{\\{attachmentUrl: ([^}]*)\\}\\})");
    	Matcher m1 = p1.matcher(description.getText());
    	int loopDetector = 0;
    	boolean changes = false;
    	while (m1.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup: " + description.getText());
    			return;
    		}
    		loopDetector++;
    		String url = m1.group(2);
			if (entity != null) {
	    		//Look for the attachment
	    		FileAttachment fa = entity.getFileAttachment(url.trim());
	    		if (fa != null) {
    		    	//Now, replace the url with special markup version
    	        		String newText = new String("{{attachmentFileId: fileId=" + fa.getId() 
    	        				+ specialAmp + "binderId=" + binderId + specialAmp + "entryId=" + entryId 
    	        				+ specialAmp + "entityType=" + entityType + "}}");
    	        		description.setText(m1.replaceFirst(newText.replace("$", "\\$")));
    	        		m1 = p1.matcher(description.getText());
    	        		changes = true;
	    		}
			}
    	}
    	//don't want to break "==" compare if not necesary, faster for long text
    	if (changes) description.setText(description.getText().replaceAll(specialAmp, "&"));
	}
	//converts back to markup.  Would happen if modify
	public static void scanDescriptionForAttachmentFileUrls(Description description) {
    	Pattern pattern = Pattern.compile("(<img [^>]*src=\"https?://[^>]*viewType=ss_viewAttachmentFile[^>]*>)");
    	Matcher m = pattern.matcher(description.getText());
    	int loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup [2]: " + description.getText());
    			break;
    		}
    		String fileId = "";
    		String img = new String(m.group(0));
        	Pattern p2 = Pattern.compile("fileId=([^\\&\"]*)");
        	Matcher m2 = p2.matcher(img);
        	if (m2.find() && m2.groupCount() >= 1) fileId = m2.group(1).trim();
    		
        	String binderId = "";
    		img = new String(m.group(0));
        	Pattern p3 = Pattern.compile("binderId=([^\\&\"]*)");
        	Matcher m3 = p3.matcher(img);
        	if (m3.find() && m3.groupCount() >= 1) binderId = m3.group(1).trim();
    		
        	String entryId = "";
    		img = new String(m.group(0));
        	Pattern p4 = Pattern.compile("entryId=([^\\&\"]*)");
        	Matcher m4 = p4.matcher(img);
        	if (m4.find() && m4.groupCount() >= 1) entryId = m4.group(1).trim();

        	String entityType = "";
    		img = new String(m.group(0));
        	Pattern p5 = Pattern.compile("entityType=([^\\&\"]*)");
        	Matcher m5 = p5.matcher(img);
        	if (m5.find() && m5.groupCount() >= 1) entityType = m5.group(1).trim();

	    	if (!fileId.equals("")) {
		    	//Now, replace the url with special markup version
		    	Pattern p1 = Pattern.compile("src *= *\"([^\"]*)\"");
		    	Matcher m1 = p1.matcher(img);
	        	if (m1.find() && m1.groupCount() >= 1) {
	        		img = new String(m1.replaceFirst("src=\"{{attachmentFileId: fileId=" + fileId 
	        				+ specialAmp + "binderId=" + binderId + specialAmp + "entryId=" + entryId
	        				+ specialAmp +"entityType=" + entityType + "}}\""));
	        		description.setText(m.replaceFirst(img.replace("$", "\\$")));
	        		m = pattern.matcher(description.getText());
	        	}
	    	}
    	}
    	description.setText(description.getText().replaceAll(specialAmp, "&"));
	}

	
	public static void scanDescriptionForICLinks(Description description) {
    	Pattern pattern = Pattern.compile("(<a [^>]*class=\"ss_icecore_link\"[^>]*>)([^<]*)</a>");
    	Matcher m = pattern.matcher(description.getText());
    	int loopDetector = 0;
    	while (m.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup [2a]: " + description.getText());
    			break;
    		}
    		String linkArgs = "";
    		String link = new String(m.group(0));
        	Pattern p2 = Pattern.compile("rel=\"([^\"]*)");
        	Matcher m2 = p2.matcher(link);
        	if (m2.find() && m2.groupCount() >= 1) linkArgs = m2.group(1).trim();
    		
        	String linkText = "" ;
        	if (m.groupCount() >= 2) { linkText = m.group(2).trim(); }

        	if (!linkArgs.equals("")) {
        		description.setText(m.replaceFirst("{{titleUrl: " + linkArgs + " text=" + Html.stripHtml(linkText) + "}}"));
        		m = pattern.matcher(description.getText());
	    	}
    	}
	}

	
	
	public static String markupStringReplacement(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes,
			DefinableEntity entity, String inputString, String type) {
		Long binderId = null;
		Long entryId = null;
		if (entity != null) {
			String entityType = entity.getEntityIdentifier().getEntityType().name();
			if (entityType.equals(EntityType.workspace.name()) ||
					entityType.equals(EntityType.folder.name()) ||
					entityType.equals(EntityType.profiles.name())) {
				binderId = entity.getId();
			} else if (entityType.equals(EntityType.folderEntry.name())) {
				binderId = entity.getParentBinder().getId();
				entryId = entity.getId();
			}
		}
		return markupStringReplacement(req, res, httpReq, httpRes,
				entity, inputString, type, binderId, entryId);
	}
	public static String markupStringReplacement(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes,
			DefinableEntity entity, String inputString, String type, 
			Long binderId, Long entryId) {
		String outputString = new String(inputString);
		outputString = outputString.replaceAll("%20", " ");
		outputString = outputString.replaceAll("%7B", "{");
		outputString = outputString.replaceAll("%7D", "}");
		int loopDetector;

		try {
	    	//Replace the markup urls with real urls {{attachmentUrl: tempFileHandle}}
	    	if (httpReq != null && binderId != null) {
	    		Pattern p1 = Pattern.compile("(\\{\\{attachmentUrl: ([^}]*)\\}\\})");
		    	Matcher m1 = p1.matcher(outputString);
		    	loopDetector = 0;
		    	while (m1.find()) {
		    		if (loopDetector > 2000) {
			        	logger.error("Error processing markup [3]: " + inputString);
		    			return outputString;
		    		}
		    		loopDetector++;
		    		String url = m1.group(2);
					String webUrl = WebUrlUtil.getServletRootURL(httpReq) + WebKeys.SERVLET_VIEW_FILE + "?";
					if (entity != null) {
			    		//Look for the attachment
			    		FileAttachment fa = entity.getFileAttachment(url.trim());
			    		if (fa != null) {
			    			webUrl += WebKeys.URL_FILE_ID + "=" + fa.getId().toString() + specialAmp;
			    			webUrl += WebKeys.URL_ENTITY_TYPE + "=" + 
			    				fa.getOwner().getEntity().getEntityType().toString() + specialAmp;
			    		} else {
			    			webUrl += WebKeys.URL_FILE_TITLE + "=" + url.trim() + specialAmp;
			    		}
					} else {
		    			webUrl += WebKeys.URL_FILE_TITLE + "=" + url.trim() + specialAmp;
					}
					webUrl += WebKeys.URL_FILE_VIEW_TYPE + "=" + WebKeys.FILE_VIEW_TYPE_ATTACHMENT_FILE + specialAmp;
					webUrl += WebKeys.URL_BINDER_ID + "=" + binderId.toString() + specialAmp;
					if (entryId != null) {
						webUrl += WebKeys.URL_ENTRY_ID + "=" + entryId.toString() + specialAmp;
					}
					outputString = m1.replaceFirst(webUrl);
					m1 = p1.matcher(outputString);
		    	}
	    	}
	    	
	    	//Replace the markup attachmentFileIds with real urls {{attachmentFileId: binderId=xxx entryId=xxx fileId=xxx entityType=xxx}}
	    	if (type.equals(WebKeys.MARKUP_VIEW) || type.equals(WebKeys.MARKUP_FORM)) {
		    	Pattern p2 = Pattern.compile("(\\{\\{attachmentFileId: ([^}]*)\\}\\})");
		    	Matcher m2 = p2.matcher(outputString);
		    	loopDetector = 0;
		    	while (m2.find()) {
		    		if (loopDetector > 2000) {
			        	logger.error("Error processing markup [4]: " + inputString);
		    			return outputString;
		    		}
		    		loopDetector++;
		    		String fileIds = m2.group(2).trim();
		    		//Look for the attachment
					String webUrl = WebUrlUtil.getServletRootURL(httpReq) + WebKeys.SERVLET_VIEW_FILE + "?";
					webUrl += WebKeys.URL_FILE_VIEW_TYPE + "=" + WebKeys.FILE_VIEW_TYPE_ATTACHMENT_FILE + specialAmp;
					webUrl += fileIds;
					outputString = m2.replaceFirst(webUrl);
					m2 = p2.matcher(outputString);
				}
	    	}
	    	
	    	//Replace the markup {{titleUrl}} with real urls {{titleUrl: binderId=xxx title=xxx}}
	    	if (type.equals(WebKeys.MARKUP_VIEW)) {
		    	Pattern p2 = Pattern.compile("(\\{\\{titleUrl: ([^\\}]*)\\}\\})");
		    	Matcher m2 = p2.matcher(outputString);
		    	loopDetector = 0;
		    	while (m2.find()) {
		    		if (loopDetector > 2000) {
			        	logger.error("Error processing markup [5]: " + inputString);
		    			return outputString;
		    		}
		    		loopDetector++;
		    		String urlParts = m2.group(2).trim();
		        	String s_binderId = "";
		        	Pattern p3 = Pattern.compile("binderId=([^ ]*)");
		        	Matcher m3 = p3.matcher(urlParts);
		        	if (m3.find() && m3.groupCount() >= 1) s_binderId = m3.group(1).trim();
		    		
		        	String normalizedTitle = "";
		        	Pattern p4 = Pattern.compile("title=([^ ]*)");
		        	Matcher m4 = p4.matcher(urlParts);
		        	if (m4.find() && m4.groupCount() >= 1) normalizedTitle = m4.group(1).trim();
		        	normalizedTitle = getNormalizedTitle(Html.stripHtml(normalizedTitle));
		        	if (normalizedTitle == null) normalizedTitle = "";
		        	
		        	String title = "";
		        	Pattern p5 = Pattern.compile("text=(.*)$");
		        	Matcher m5 = p5.matcher(urlParts);
		        	if (m5.find() && m5.groupCount() >= 1) title = m5.group(1).trim();
		        	title = Html.stripHtml(title);
		        	
		    		//build the link
		        	String titleLink = "";
	    			String action = WebKeys.ACTION_VIEW_FOLDER_ENTRY;
	    			Map params = new HashMap();
	    			params.put(WebKeys.URL_BINDER_ID, s_binderId.toString());
	    			params.put(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
	    			if (req == null && res == null && httpReq == null && httpRes == null) {
	    				action = WebKeys.ACTION_VIEW_PERMALINK;
	    				params.put(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
	    				params.put(WebKeys.URL_ENTITY_TYPE, ObjectKeys.FOLDER_ENTRY);
	    			}
	    			String webUrl = getPortletUrl(req, res, httpReq, httpRes, action, true, params);
	    			titleLink = "<a href=\"" + webUrl + "\" ";
	    			titleLink += "onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this);\">";
	    			titleLink += "<span class=\"ss_title_link\">";
	    			titleLink += title + "</span></a>";
	    			titleLink = titleLink.replaceAll("&", "&amp;");
	    			outputString = outputString.substring(0, m2.start(0)) + titleLink + outputString.substring(m2.end(), outputString.length());
					m2 = p2.matcher(outputString);
				}
	    	}
	    	
	    	//Replace the markup {{titleUrl}} with editing hyperlinks
	    	if (type.equals(WebKeys.MARKUP_FORM)) {
		    	Pattern p2 = Pattern.compile("(\\{\\{titleUrl: ([^\\}]*)\\}\\})");
		    	Matcher m2 = p2.matcher(outputString);
		    	loopDetector = 0;
		    	while (m2.find()) {
		    		if (loopDetector > 2000) {
			        	logger.error("Error processing markup [5a]: " + inputString);
		    			return outputString;
		    		}
		    		loopDetector++;
		    		String urlParts = m2.group(2).trim();
		        	String s_binderId = "";
		        	Pattern p3 = Pattern.compile("binderId=([^ ]*)");
		        	Matcher m3 = p3.matcher(urlParts);
		        	if (m3.find() && m3.groupCount() >= 1) s_binderId = m3.group(1).trim();
		    		
		        	String normalizedTitle = "";
		        	Pattern p4 = Pattern.compile("title=([^ ]*)");
		        	Matcher m4 = p4.matcher(urlParts);
		        	if (m4.find() && m4.groupCount() >= 1) normalizedTitle = m4.group(1).trim();
		        	normalizedTitle = Html.stripHtml(normalizedTitle);
		        	
		        	String title = "";
		        	Pattern p5 = Pattern.compile("text=(.*)$");
		        	Matcher m5 = p5.matcher(urlParts);
		        	if (m5.find() && m5.groupCount() >= 1) title = m5.group(1).trim();
		        	title = Html.stripHtml(title);
		        	
		    		//build the link
		        	String titleLink = "";
	    			titleLink = "<a class=\"ss_icecore_link\" ";
	    			titleLink += "rel=\"binderId=" + s_binderId + " title=" + normalizedTitle  + "\">";
	    			titleLink += title + "</a>";
	    			titleLink = titleLink.replaceAll("&", "&amp;");
	    			outputString = outputString.substring(0, m2.start(0)) + titleLink + outputString.substring(m2.end(), outputString.length());
					m2 = p2.matcher(outputString);
				}
	    	}

	    	//When viewing the string, replace the markup title links with real links    [[page title]]
			if (binderId != null && (type.equals(WebKeys.MARKUP_VIEW) || type.equals(WebKeys.MARKUP_FILE))) {
				String action = WebKeys.ACTION_VIEW_FOLDER_ENTRY;
		    	Pattern p3 = Pattern.compile("(\\[\\[([^\\]]*)\\]\\])");
		    	Matcher m3 = p3.matcher(outputString);
		    	loopDetector = 0;
		    	while (m3.find()) {
		    		if (loopDetector > 2000) {
			        	logger.error("Error processing markup [6]: " + inputString);
		    			return outputString;
		    		}
		    		loopDetector++;
		    		//Get the title
		    		String title = m3.group(2).trim();
		    		title = Html.stripHtml(title);
		    		String normalizedTitle = getNormalizedTitle(title);
		    		if (normalizedTitle != null && !normalizedTitle.equals("")) {
		    			//Build the url to that entry
		    			String titleLink = "";
		    			if (type.equals(WebKeys.MARKUP_VIEW)) {
			    			Map params = new HashMap();
			    			params.put(WebKeys.URL_BINDER_ID, binderId.toString());
			    			params.put(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
			    			if (req == null && res == null && httpReq == null && httpRes == null) {
			    				action = WebKeys.ACTION_VIEW_PERMALINK;
			    				params.put(WebKeys.URL_ENTRY_TITLE, normalizedTitle);
			    				params.put(WebKeys.URL_ENTITY_TYPE, ObjectKeys.FOLDER_ENTRY);
			    			}
			    			String webUrl = getPortletUrl(req, res, httpReq, httpRes, action, true, params);
			    			titleLink = "<a href=\"" + webUrl + "\" ";
			    			titleLink += "onClick=\"if (self.ss_openTitleUrl) return self.ss_openTitleUrl(this);\">";
			    			titleLink += "<span class=\"ss_title_link\">";
			    			titleLink += title + "</span></a>";
		    			} else {
			    			titleLink = "{{titleUrl: " + WebKeys.URL_BINDER_ID + "=" + binderId.toString();
			    			titleLink += " " + WebKeys.URL_NORMALIZED_TITLE + "=" + normalizedTitle;
			    			titleLink += " text=" + title + "}}";
		    			}
		    			outputString = outputString.substring(0, m3.start(0)) + titleLink + outputString.substring(m3.end(), outputString.length());
		    			m3 = p3.matcher(outputString);
		    		}
				}
			}
		} catch(Exception e) {
			logger.error("Error processing markup [7]: " + inputString, e);
			return inputString;
		}
     	return outputString.replaceAll(specialAmp, "&");
	}
	
	//Routine to split a body of text into sections
	public static List markupSplitBySection(String body) {
		List bodyParts = new ArrayList();
    	int loopDetector = 0;
    	Pattern p0 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
    	Matcher m0 = p0.matcher(body);
    	if (m0.find()) {
			Map part = new HashMap();
			part.put("prefix", body.substring(0, m0.start(0)));
			bodyParts.add(part);
			body = body.substring(m0.start(0), body.length());
    	}
    	
    	int sectionNumber = 0;
    	Pattern p1 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
    	Matcher m1 = p1.matcher(body);
    	while (m1.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup [6]: " + body);
    			return bodyParts;
    		}
    		loopDetector++;
			Map part = new HashMap();
    		//Get the section title
    		String title = m1.group(2).trim();
    		if (title == null) title = "";
			
    		part.put("sectionTitle", title);
    		part.put("sectionNumber", String.valueOf(sectionNumber));
    		
			String equalSigns = m1.group(1).trim();
			int sectionDepth = Integer.valueOf(equalSigns.length());
			if (sectionDepth > 4) sectionDepth = 4;
			sectionDepth--;
			part.put("sectionTitleClass", "ss_sectionHeader" + String.valueOf(sectionDepth));
			
			body = body.substring(m1.end(), body.length());
	    	Pattern p2 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
	    	Matcher m2 = p2.matcher(body);
	    	if (m2.find()) {
				part.put("sectionBody", body.substring(0, m2.start(0)));
				body = body.substring(m2.start(0), body.length());
	    	} else {
	    		part.put("sectionBody", body);
	    	}
	    	part.put("sectionText", m1.group(1) + m1.group(2) + m1.group(3) + part.get("sectionBody"));
			bodyParts.add(part);
			m1 = p1.matcher(body);
    		
			sectionNumber++;
		}
		return bodyParts;
	}
	
	//Routine to split a body of text into sections
	public static String markupSectionsReplacement(String body) {
		List<Map> bodyParts = new ArrayList();
    	int loopDetector = 0;
    	Pattern p0 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
    	Matcher m0 = p0.matcher(body);
    	if (m0.find()) {
			Map part = new HashMap();
			part.put("prefix", body.substring(0, m0.start(0)));
			bodyParts.add(part);
			body = body.substring(m0.start(0), body.length());
    	}
    	
    	int sectionNumber = 0;
    	Pattern p1 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
    	Matcher m1 = p1.matcher(body);
    	while (m1.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup [6]: " + body);
    			return body;
    		}
    		loopDetector++;
			Map part = new HashMap();
    		//Get the section title
    		String title = m1.group(2).trim();
    		if (title == null) title = "";
			
    		part.put("sectionTitle", title);
    		part.put("sectionNumber", String.valueOf(sectionNumber));
    		
			String equalSigns = m1.group(1).trim();
			int sectionDepth = Integer.valueOf(equalSigns.length());
			if (sectionDepth > 4) sectionDepth = 4;
			sectionDepth--;
			part.put("sectionTitleClass", "ss_sectionHeader" + String.valueOf(sectionDepth));
			
			body = body.substring(m1.end(), body.length());
	    	Pattern p2 = Pattern.compile("(==[=]*)([^=]*)(==[=]*)");
	    	Matcher m2 = p2.matcher(body);
	    	if (m2.find()) {
				part.put("sectionBody", body.substring(0, m2.start(0)));
				body = body.substring(m2.start(0), body.length());
	    	} else {
	    		part.put("sectionBody", body);
	    	}
	    	part.put("sectionText", m1.group(1) + m1.group(2) + m1.group(3) + part.get("sectionBody"));
			bodyParts.add(part);
			m1 = p1.matcher(body);
    		
			sectionNumber++;
		}
    	String result = "";
    	if (bodyParts.isEmpty()) return body;
    	for (Map bodyPart : bodyParts) {
    		result += "<div>";
    		if (bodyPart.containsKey("prefix")) result += bodyPart.get("prefix");
    		if (bodyPart.containsKey("sectionTitle")) {
	    		result += "<div><span ";
	    		if (bodyPart.containsKey("sectionTitleClass")) 
	    			result += "class=\"" + bodyPart.get("sectionTitleClass") + "\"";
	    		result += ">";
	    		result += bodyPart.get("sectionTitle");
	    		result += "</span></div>";
    		}
    		if (bodyPart.containsKey("sectionBody")) result += bodyPart.get("sectionBody");
    		result += "</div>\n";
    	}
		return result;
	}
	
	//Routine to compute a normalized title
	public static String getNormalizedTitle(String title) {
		if (title == null) return null;
		String titleTrimmed = Html.stripHtml(title.trim());
		if (titleTrimmed.equals("")) return null;
		
        //compute normalized title
		//Start by removing all quoted characters (e.g., &QUOT;)
		Pattern p1 = Pattern.compile("(\\&[^;]*;)");
    	Matcher m1 = p1.matcher(titleTrimmed);
    	int loopDetector = 0;
    	while (m1.find()) {
    		if (loopDetector > 2000) {
	        	logger.error("Error processing markup: " + titleTrimmed);
    			break;
    		}
			titleTrimmed = m1.replaceFirst(" ");
			m1 = p1.matcher(titleTrimmed);
    	}
        String normalTitle = titleTrimmed.replaceAll("[\\P{L}&&\\P{N}]", " ");
        normalTitle = normalTitle.replaceAll(" ++","_");
		normalTitle = normalTitle.toLowerCase();
		return normalTitle;
	}
	
	public static String getPortletUrl(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes,
			String action, boolean actionUrl, Map params) {
		return getPortletUrl(req, res, httpReq, httpRes, action, actionUrl, params, "ss_forum");
	}
	public static String getPortletUrl(RenderRequest req, RenderResponse res, 
			HttpServletRequest httpReq, HttpServletResponse httpRes,
			String action, boolean actionUrl, Map params, String portletName) {
		if (req == null || res == null) {
			//This call must have come from a servlet (e.g., rss)
			//  Build a permalink url
			if (!Validator.isNull(action)) {
				params.put("action", action);
			}
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(httpReq, portletName, actionUrl);
			Iterator it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				adapterUrl.setParameter((String) me.getKey(), (String)me.getValue());
			}
			return adapterUrl.toString();

		} else {
			PortletURL portletURL = null;
			if (actionUrl) {
				portletURL = res.createActionURL();
			} else {
				portletURL = res.createRenderURL();
			}
			try {
				portletURL.setWindowState(new WindowState(WindowState.MAXIMIZED.toString()));
			} catch(Exception e) {}
			
			Iterator it = params.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry me = (Map.Entry) it.next();
				portletURL.setParameter((String) me.getKey(), (String)me.getValue());
			}
			if (!Validator.isNull(action)) {
				portletURL.setParameter("action", new String[] {action});
			}
	
			return portletURL.toString();
		}
	}
	
	public static boolean isUnauthenticatedRequest(HttpServletRequest request) {
		// Check to see if this is a legally unauthenticated request.
		Boolean unathenticatedRequest = (Boolean) request.getAttribute
		(WebKeys.UNAUTHENTICATED_REQUEST);

		return Boolean.TRUE.equals(unathenticatedRequest);
	}
	
	public static boolean isUnauthenticatedRequest(PortletRequest request) {
		// Check to see if this is a legally unauthenticated request.
		Boolean unathenticatedRequest = (Boolean) request.getAttribute
		(WebKeys.UNAUTHENTICATED_REQUEST);

		return Boolean.TRUE.equals(unathenticatedRequest);
	}
	
	private static String getRemoteUserName(HttpServletRequest request) {
		return request.getRemoteUser();
	}
	
	private static String getRemoteUserName(PortletRequest request) {
		if(PortalDetector.isLiferay()) {
			// Important: If running under Liferay, do NOT rely on getRemoteUser!! 
			// Starting with Liferay 4.3.0, this returns the user's internal id (long surrogate 
			// value) rather than the user's login name (which they call screen name).
			// It seems bad practice to use surrogate key for this purpose, and it prevents us
			// to identify Aspen user based on the portal user name since Liferay's surrogate
			// keys are meaningless in Aspen. Sigh...
			return null;			
		}
		else {
			return request.getRemoteUser();			
		}
	}
	
	public static String getZoneNameByVirtualHost(ServletRequest request) {
		return getZoneModule().getZoneNameByVirtualHost(request.getServerName().toLowerCase());
	}

	public static Long getZoneIdByVirtualHost(ServletRequest request) {
		return getZoneModule().getZoneIdByVirtualHost(request.getServerName().toLowerCase());
	}

	public static String getZoneNameByVirtualHost(PortletRequest request) {
		return getZoneModule().getZoneNameByVirtualHost(request.getServerName().toLowerCase());
	}

	public static Long getZoneIdByVirtualHost(PortletRequest request) {
		return getZoneModule().getZoneIdByVirtualHost(request.getServerName().toLowerCase());
	}

	private static ZoneModule getZoneModule() {
		return (ZoneModule) SpringContextUtil.getBean("zoneModule");
	}
	private static ProfileDao getProfileDao() {
		return (ProfileDao) SpringContextUtil.getBean("profileDao");
	}
	private static AccessTokenManager getAccessTokenManager() {
		return (AccessTokenManager) SpringContextUtil.getBean("accessTokenManager");
	}

	public static String getTokenInfoId(HttpServletRequest request) {
		HttpSession ses = request.getSession(false);
		if(ses == null) return null;
		return (String) ses.getAttribute(WebKeys.TOKEN_INFO_ID);
	}

}
