package com.sitescape.ef.web.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.portletadapter.MultipartFileSupport;
import com.sitescape.ef.util.SZoneConfig;
import com.sitescape.ef.util.SimpleMultipartFile;
import com.sitescape.ef.util.TempFileUtil;
import com.sitescape.ef.web.WebKeys;

public class WebHelper {

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
		
		String username = request.getRemoteUser();
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
		
		String username = request.getRemoteUser();
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
		String username = request.getRemoteUser();

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
		String username = request.getRemoteUser();

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
		
		File file = new File(TempFileUtil.getTempFileDir(), fileHandle);
		
		SimpleMultipartFile mf = new SimpleMultipartFile(fileName, file);
		
		return mf;
	}
	
	/**
	 * Clean up resources associated with the file handle. Must be called
	 * when the application is done with the file handle.
	 * 
	 * @param fileHandle
	 */
	public static void releaseFileHandle(String fileHandle) {
		File file = new File(TempFileUtil.getTempFileDir(), fileHandle);

		file.delete();
	}
}
