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
package org.kablink.teaming.ssfs.util;
import java.net.URLEncoder;
import java.util.Iterator;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.util.UserAppConfig;
import org.kablink.teaming.web.util.WebUrlUtil;


public class SsfsUtil {
	
	private static String[] editInPlaceFileExtensionsIE;
	private static String[] editInPlaceFileExtensionsNonIE;
	
	public static String getEntryUrl(PortletRequest req, Binder binder, 
			DefinableEntity entity, String strRepositoryName) {
		StringBuffer sb = getInternalCommonPart(req, binder, entity);
		
		return sb.append("attach/").
		append(strRepositoryName).
		append("/").toString();
	}
		
	public static String getInternalAttachmentUrl(HttpServletRequest req, 
			Binder binder, DefinableEntity entity, FileAttachment fa) {
		StringBuffer sb = getInternalCommonPart(req, binder, entity);
		
		if (fa == null) {
			sb.append("attach/unknown/");
		} else {
			sb.append("attach/").
			append(fa.getRepositoryName()).
			append("/").
			append(fa.getFileItem().getName()).toString();
		}
		return sb.toString();
	}

	public static String getInternalAttachmentUrlEncoded(PortletRequest req, Binder binder, 
			DefinableEntity entity, FileAttachment fa) throws Exception {
		StringBuffer sb = getInternalCommonPart(req, binder, entity);
		String strFileName = fa.getFileItem().getName();
		String strUrlEncoded = URLEncoder.encode(strFileName, "UTF-8");
		
		if (fa == null) {
			sb.append("attach/unknown/");
		} else {
			sb.append("attach/").
			append(fa.getRepositoryName()).
			append("/");
			sb.append(strUrlEncoded);
		}
		return sb.toString();
	}
	
	public static String getInternalFileUrl(HttpServletRequest req, Binder binder, 
			DefinableEntity entity, String elemName, FileAttachment fa) {
		StringBuffer sb = getInternalCommonPart(req, binder, entity);
		
		return sb.append("file").
		append("/").		
		append(elemName).
		append("/").
		append(fa.getFileItem().getName()).toString();
	}
	
	public static String getInternalTitleFileUrl(HttpServletRequest req, 
			Binder binder, DefinableEntity entity, FileAttachment fa) {
		StringBuffer sb = getInternalCommonPart(req, binder, entity);
		
		// Library type element is singleton (ie, at most one instance),
		// and therefore we do not need to encode element name in url.
		
		return sb.append("library").
		append("/").		
		append(fa.getFileItem().getName()).toString();
	}
	
	public static String getLibraryBinderUrl(HttpServletRequest req, Binder binder) {
		StringBuffer sb = WebUrlUtil.getSSFSContextRootURL(req);
		
		return sb.append("files/library"). // follow Slide's convention
		append(binder.getPathName()).toString();
	}
	
	public static String getLibraryBinderUrl(PortletRequest req, Binder binder) {
		StringBuffer sb = WebUrlUtil.getSSFSContextRootURL(req);
		
		return sb.append("files/library"). // follow Slide's convention
		append(binder.getPathName()).toString();
	}
	
	public static String getLibraryBinderUrl(Binder binder) {
		return getLibraryBinderUrl((HttpServletRequest)null, binder);
	}
	
	private static StringBuffer getInternalCommonPart(HttpServletRequest req, Binder binder, 
			DefinableEntity entity) {
		StringBuffer sb = WebUrlUtil.getSSFSContextRootURL(req);
		
		return sb.append("files/internal/"). // follow Slide's convention
		append(binder.getId()).
		append("/").
		append(entity.getId()).
		append("/");	
	}
	
	private static StringBuffer getInternalCommonPart(PortletRequest req, Binder binder, 
			DefinableEntity entity) {
		StringBuffer sb = WebUrlUtil.getSSFSContextRootURL(req);
		
		return sb.append("files/internal/"). // follow Slide's convention
		append(binder.getId()).
		append("/").
		append(entity.getId()).
		append("/");	
	}
	
	public static String[] getEditInPlaceExtensions(boolean isIE) { 
		String [] editInPlaceFileExtensions;
		String strEditType = "";
		
		if (isIE) {
			if (editInPlaceFileExtensionsIE == null) {
				strEditType = SPropsUtil.getString("edit.in.place.for.ie", "");
				String[] s = SPropsUtil.getStringArray("edit.in.place.file." + strEditType + ".extensions", ",");
				for(int i = 0; i < s.length; i++) {
					s[i] = s[i].toLowerCase();
				}		
				editInPlaceFileExtensionsIE = s;
			}
			editInPlaceFileExtensions = editInPlaceFileExtensionsIE;
		} else {
			if (editInPlaceFileExtensionsNonIE == null) {
				strEditType = SPropsUtil.getString("edit.in.place.for.nonie", "");

				String[] s = SPropsUtil.getStringArray("edit.in.place.file." + strEditType + ".extensions", ",");
				for(int i = 0; i < s.length; i++) {
					s[i] = s[i].toLowerCase();
				}		
				editInPlaceFileExtensionsNonIE = s;
			}
			editInPlaceFileExtensions = editInPlaceFileExtensionsNonIE;
		}
		
		return editInPlaceFileExtensions;
	}
	
	public static boolean supportsEditInPlace(String relativeFilePath, String browserType) { 
		String extension = null;
		int index = relativeFilePath.lastIndexOf(".");
		if(index < 0)
			return false; // No extension. can not support edit-in-place
		else
			extension = relativeFilePath.substring(index).toLowerCase();
		
		String [] editInPlaceFileExtensions;
		
		editInPlaceFileExtensions = getEditInPlaceExtensions(browserType != null && browserType.equalsIgnoreCase("ie"));
		for(int i = 0; i < editInPlaceFileExtensions.length; i++) {
			if(extension.endsWith(editInPlaceFileExtensions[i]))
				return true;
		}
		
		return false;
	}
	
	public static boolean supportsViewAsHtml(String relativeFilePath, String browserType) { 
		String extension = null;
		
		int index = relativeFilePath.lastIndexOf(".");
		if(index < 0)
			return false; // No extension. can not support edit-in-place
		else
			extension = relativeFilePath.substring(index).toLowerCase();
		
		String[] s = SPropsUtil.getStringArray("view.as.html.file.stellent.extensions", ",");
		for(int i = 0; i < s.length; i++)
			s[i] = s[i].toLowerCase();

		for(int i = 0; i < s.length; i++)
		{
			if(extension.endsWith(s[i]))
				return true;
		}
		
		return false;
	}
	
	public static String openInEditor(String relativeFilePath, String operatingSystem) {
		return openInEditor(relativeFilePath, operatingSystem, null);
	}
	public static String openInEditor(String relativeFilePath, String operatingSystem, UserProperties userProperties) {
		if (operatingSystem == null || operatingSystem.equals("")) return "";
		String extension = null;
		int index = relativeFilePath.lastIndexOf(".");
		if(index < 0)
			return ""; // No extension. can not support edit-in-place
		else {
			extension = relativeFilePath.substring(index).toLowerCase();
			String strEditor = getAppFromUserAppConfig(userProperties, extension);
			if (null == strEditor) {
				strEditor = SPropsUtil.getString("edit.in.place."+operatingSystem+".editor"+extension, "");
			}
			return strEditor;
		}
	}
	
	@SuppressWarnings("unchecked")
	private static String getAppFromUserAppConfig(UserProperties userProperties, String extension) {
		String	reply = null;
		if ((null != userProperties) && (null != extension)) {
			UserAppConfig	uac				= new UserAppConfig((String)userProperties.getProperty(ObjectKeys.USER_PROPERTY_APPCONFIGS));
			Document		appConfig		= uac.getUserAppConfig();
	    	Element			appConfigsRoot	= appConfig.getRootElement();
	       	Iterator		itAppConfigs	= appConfigsRoot.selectNodes("app-configs|app-config").iterator();
	       	
	       	extension = extension.trim();
	       	int pPos  = extension.indexOf('.');
	       	if (0 == pPos) {
	       		extension = extension.substring(pPos + 1);
	       	}
	       	extension = extension.toLowerCase();
	       	while (itAppConfigs.hasNext()) {
	       		Element e	= ((Element) itAppConfigs.next());
	       		String	ext	= e.attributeValue("extension");
	       		if (null != ext) {
	       			ext  = ext.trim();
	    	       	pPos = ext.indexOf('.');
	    	       	if (0 <= pPos) {
	    	       		ext = ext.substring(pPos + 1);
	    	       	}
	    	       	ext = ext.toLowerCase();
	    	       	if (ext.equals(extension)) {
	    	       		reply = e.attributeValue("application").trim();
	    	       		break;
	    	       	}
	       		}
	       	}
		}
		return reply;
	}
	
	public static boolean supportAttachmentEdit() {
		return SPropsUtil.getBoolean("edit.in.place");
	}	

	public static String attachmentEditTypeForIE() {
		return SPropsUtil.getString("edit.in.place.for.ie");
	}	

	public static String attachmentEditTypeForNonIE() {
		return SPropsUtil.getString("edit.in.place.for.nonie");
	}
	
	public static boolean supportApplets() {
		return SPropsUtil.getBoolean("applet.support.in.application", false);
	}
}
