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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.Description;
import com.sitescape.team.domain.EntityIdentifier;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.EntityIdentifier.EntityType;
import com.sitescape.team.portletadapter.AdaptedPortletURL;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.FileUploadItem;
import com.sitescape.team.util.SZoneConfig;
import com.sitescape.team.util.SimpleMultipartFile;
import com.sitescape.team.util.TempFileUtil;
import com.sitescape.team.web.WebKeys;
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
	
	public static String getRequiredUserName(HttpServletRequest request) 
	throws IllegalStateException {
		//System.out.println("*** Servlet: USER NAME = " + request.getRemoteUser()); // TODO TBR
		//System.out.println("*** Servlet: USER PRINCIPAL = " + request.getUserPrincipal()); // TODO TBR
		
		String username = getRemoteUserName(request);
			
		if(username != null)
			return username;
		
		HttpSession ses = request.getSession(false);
		
		if(ses == null)
			throw new IllegalStateException("No user session");
		
		if(ses.getAttribute(WebKeys.USER_NAME) == null)
			throw new IllegalStateException("No user name in the session");
			
		return (String) ses.getAttribute(WebKeys.USER_NAME);
	}
	
	public static String getRequiredUserName(PortletRequest request) 
	throws IllegalStateException {
		//System.out.println("*** Portlet: USERNAME = " + request.getRemoteUser()); // TODO TBR
		//System.out.println("*** Portlet: USER PRINCIPAL = " + request.getUserPrincipal()); // TODO TBR
		
		String username = getRemoteUserName(request);
		if(username != null)
			return username;
		
		PortletSession ses = request.getPortletSession(false);
		
		if(ses == null)
			throw new IllegalStateException("No user session");
		
    	// Due to bugs in some portlet containers (eg. Liferay) as well as in 
    	// our own portlet adapter, when a user's session is invalidated or
    	// expired, the associated PortletSession if any may not be properly 
    	// notified of the change in the state of the underlying HttpSession.
    	// When this occurs, application may still be able to call getAttribute
    	// method on the PortletSession without incurring an exception. 
    	// To work around that problem, I had to add the following checking 
    	// (hack) as a way of indirectly detecting whether or not the user 
    	// session has indeed expired. Yuck, but it works. 
		if(ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE) == null)
			throw new IllegalStateException("No user name in the session");
			
		return (String) ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE);		
	}
	
	public static String getRequiredZoneName(HttpServletRequest request) 
	throws IllegalStateException {
		String zoneName = null;
		
		HttpSession ses = request.getSession(false);
		
		if(ses != null)
			zoneName = (String) ses.getAttribute(WebKeys.ZONE_NAME);
		
		if(zoneName == null)
			zoneName = SZoneConfig.getDefaultZoneName();
		
		return zoneName;
	}
	
	public static String getRequiredZoneName(PortletRequest request) 
	throws IllegalStateException {
		String zoneName = null;
		
		PortletSession ses = request.getPortletSession(false);
		
		if(ses != null)
			zoneName = (String) ses.getAttribute(WebKeys.ZONE_NAME, PortletSession.APPLICATION_SCOPE);
		
		if(zoneName == null)
			zoneName = SZoneConfig.getDefaultZoneName();
			
		return zoneName;
	}
	
	public static PortletSession getRequiredPortletSession(PortletRequest request) 
	throws IllegalStateException {
		String username = getRemoteUserName(request);

		PortletSession ses = request.getPortletSession(false);
		
		if(ses != null) { // session already exists
			if(ses.getAttribute(WebKeys.USER_NAME, PortletSession.APPLICATION_SCOPE) == null) {
				if(username != null) {
					// store the username into the existing session
					ses.setAttribute(WebKeys.USER_NAME, username, PortletSession.APPLICATION_SCOPE);
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
			if(username != null) {
				// we can create a new session and put the username into it
				ses = request.getPortletSession();
				ses.setAttribute(WebKeys.USER_NAME, username, PortletSession.APPLICATION_SCOPE);
			}
			else {
				// since we don't have username we must not create a new session here
				throw new IllegalStateException("No valid session - Illegal request sequence");
			}
		}
		
		return ses;
	}
	
	public static HttpSession getRequiredSession(HttpServletRequest request) 
	throws IllegalStateException {
		String username = getRemoteUserName(request);

		HttpSession ses = request.getSession(false);
		
		if(ses != null) { // session already exists
			if(ses.getAttribute(WebKeys.USER_NAME) == null) {
				if(username != null) {
					// store the username into the existing session
					ses.setAttribute(WebKeys.USER_NAME, username);
				}
				else {
					// Neither the session nor the request contains username.
					// The session must have been created via some invalid means
					// (programmic error) or side effect (portal/app server 
					// environment). In this case we shouldn't allow the caller 
					// to proceed normally.
					throw new IllegalStateException("No valid session - Illegal request sequence.");
				}
			}
		}
		else { // session doesn't exist
			if(username != null) {
				// we can create a new session and put the username into it
				ses = request.getSession();
				ses.setAttribute(WebKeys.USER_NAME, username);
			}
			else {
				// since we don't have username we must not create a new session here
				throw new IllegalStateException("No valid session - Illegal request sequence");
			}
		}
		
		return ses;
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
	public static MultipartFile wrapFileHandleInMultipartFile(String fileHandle) 
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
	public static void scanDescriptionForUploadFiles(Description description, List fileData) {
    	String fileHandle = "";
    	Pattern pattern = Pattern.compile("(<img [^>]*src=\"https?://[^>]*viewType=ss_viewUploadFile[^>]*>)");
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
	    		}
			}
    	}
    	description.setText(description.getText().replaceAll(specialAmp, "&"));
	}
	
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
        		description.setText(m.replaceFirst("{{titleUrl: " + linkArgs + " text=" + linkText + "}}"));
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
	    	if (type.equals(WebKeys.MARKUP_VIEW) && ((req != null && res != null) || (httpReq != null && httpRes != null))) {
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
		        	
		        	String title = "";
		        	Pattern p5 = Pattern.compile("text=(.*)$");
		        	Matcher m5 = p5.matcher(urlParts);
		        	if (m5.find() && m5.groupCount() >= 1) title = m5.group(1).trim();
		        	
		    		//build the link
		        	String titleLink = "";
	    			String action = WebKeys.ACTION_VIEW_FOLDER_ENTRY;
	    			Map params = new HashMap();
	    			params.put(WebKeys.URL_BINDER_ID, s_binderId.toString());
	    			params.put(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
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
		        	
		        	String title = "";
		        	Pattern p5 = Pattern.compile("text=(.*)$");
		        	Matcher m5 = p5.matcher(urlParts);
		        	if (m5.find() && m5.groupCount() >= 1) title = m5.group(1).trim();
		        	
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
			if (binderId != null && (type.equals(WebKeys.MARKUP_VIEW) || type.equals(WebKeys.MARKUP_FILE)) && 
	    			((req != null && res != null) || (httpReq != null && httpRes != null))) {
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
		    		String normalizedTitle = getNormalizedTitle(title);
		    		if (!normalizedTitle.equals("")) {
		    			//Build the url to that entry
		    			String titleLink = "";
		    			if (type.equals(WebKeys.MARKUP_VIEW)) {
			    			Map params = new HashMap();
			    			params.put(WebKeys.URL_BINDER_ID, binderId.toString());
			    			params.put(WebKeys.URL_NORMALIZED_TITLE, normalizedTitle);
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
	
	//Routine to compute a normalized title
	public static String getNormalizedTitle(String title) {
		if (title == null) return null;
		String titleTrimmed = title.trim();
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
}
