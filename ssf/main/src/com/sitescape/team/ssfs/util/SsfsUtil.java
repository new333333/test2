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
package com.sitescape.team.ssfs.util;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.util.WebUrlUtil;

public class SsfsUtil {

	private static String[] editInPlaceFileExtensionsIE;
	private static String[] editInPlaceFileExtensionsNonIE;
	
	public static String getEntryUrl(Binder binder, 
			DefinableEntity entity, String strRepositoryName) {
		StringBuffer sb = getInternalCommonPart(binder, entity);
		
		return sb.append("attach/").
		append(strRepositoryName).
		append("/").toString();
	}
	

	
	public static String getInternalAttachmentUrl(Binder binder, 
			DefinableEntity entity, FileAttachment fa) {
		StringBuffer sb = getInternalCommonPart(binder, entity);
		
		return sb.append("attach/").
		append(fa.getRepositoryName()).
		append("/").
		append(fa.getFileItem().getName()).toString();
	}
	
	public static String getInternalFileUrl(Binder binder, DefinableEntity entity,
			String elemName, FileAttachment fa) {
		StringBuffer sb = getInternalCommonPart(binder, entity);
		
		return sb.append("file").
		append("/").		
		append(elemName).
		append("/").
		append(fa.getFileItem().getName()).toString();
	}
	
	public static String getInternalTitleFileUrl(Binder binder, 
			DefinableEntity entity, FileAttachment fa) {
		StringBuffer sb = getInternalCommonPart(binder, entity);
		
		// Library type element is singleton (ie, at most one instance),
		// and therefore we do not need to encode element name in url.
		
		return sb.append("library").
		append("/").		
		append(fa.getFileItem().getName()).toString();
	}
	
	public static String getLibraryBinderUrl(Binder binder) {
		StringBuffer sb = WebUrlUtil.getSSFSContextRootURL();
		
		return sb.append("files/library/"). // follow Slide's convention
		append(RequestContextHolder.getRequestContext().getZoneName()). // zone name
		append(binder.getPathName()).toString();
	}
	
	private static StringBuffer getInternalCommonPart(Binder binder, 
			DefinableEntity entity) {
		StringBuffer sb = WebUrlUtil.getSSFSContextRootURL();
		
		return sb.append("files/internal/"). // follow Slide's convention
		append(RequestContextHolder.getRequestContext().getZoneName()). // zone name 
		append("/").
		append(binder.getId()).
		append("/").
		append(entity.getId()).
		append("/");	
	}
	
	public static boolean supportsEditInPlace(String relativeFilePath, String browserType) { 
		String extension = null;
		int index = relativeFilePath.lastIndexOf(".");
		if(index < 0)
			return false; // No extension. can not support edit-in-place
		else
			extension = relativeFilePath.substring(index).toLowerCase();
		
		String [] editInPlaceFileExtensions;
		String strEditType = "";
		
		if (browserType != null && browserType.equalsIgnoreCase("ie")) {
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
		if (operatingSystem == null || operatingSystem.equals("")) return "";
		String extension = null;
		int index = relativeFilePath.lastIndexOf(".");
		if(index < 0)
			return ""; // No extension. can not support edit-in-place
		else {
			extension = relativeFilePath.substring(index).toLowerCase();
			String strEditor = SPropsUtil.getString("edit.in.place."+operatingSystem+".editor"+extension, "");
			return strEditor;
		}
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
