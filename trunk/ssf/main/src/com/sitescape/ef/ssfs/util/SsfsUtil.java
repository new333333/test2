package com.sitescape.ef.ssfs.util;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.util.SPropsUtil;
import com.sitescape.ef.web.util.WebUrlUtil;

public class SsfsUtil {

	private static String[] editInPlaceFileExtensions;
	
	public static String getAttachmentUrl(Binder binder, 
			DefinableEntity entity, FileAttachment fa) {
		StringBuffer sb = getCommonPart(binder, entity);
		
		return sb.append("attach/").
		append(fa.getRepositoryServiceName()).
		append("/").
		append(fa.getFileItem().getName()).toString();
	}
	
	public static String getFileUrl(Binder binder, DefinableEntity entity,
			String elemName, FileAttachment fa) {
		StringBuffer sb = getCommonPart(binder, entity);
		
		return sb.append("file").
		append("/").		
		append(elemName).
		append("/").
		append(fa.getFileItem().getName()).toString();
	}
	
	public static String getLibraryFileUrl(Binder binder, 
			DefinableEntity entity, FileAttachment fa) {
		StringBuffer sb = getCommonPart(binder, entity);
		
		// Library type element is singleton (ie, at most one instance),
		// and therefore we do not need to encode element name in url.
		
		return sb.append("library").
		append("/").		
		append(fa.getFileItem().getName()).toString();
	}
	
	private static StringBuffer getCommonPart(Binder binder, 
			DefinableEntity entity) {
		StringBuffer sb = WebUrlUtil.getSSFSContextRootURL();
		
		return sb.append("files/"). // follow Slide's convention
		append(RequestContextHolder.getRequestContext().getZoneName()). // zone name 
		append("/internal/"). // All urls generated this way are internal ones
		append(binder.getId()).
		append("/").
		append(entity.getId()).
		append("/");	
	}

	public static boolean supportsEditInPlace(String relativeFilePath) {
		String extension = null;
		int index = relativeFilePath.lastIndexOf(".");
		if(index < 0)
			return false; // No extension. can not support edit-in-place
		else
			extension = relativeFilePath.substring(index).toLowerCase();
		
		if(editInPlaceFileExtensions == null) {
			String[] s = SPropsUtil.getStringArray("edit.in.place.file.extensions", ",");
			for(int i = 0; i < s.length; i++) {
				s[i] = s[i].toLowerCase();
			}		
			editInPlaceFileExtensions = s;
		}
		
		for(int i = 0; i < editInPlaceFileExtensions.length; i++) {
			if(extension.endsWith(editInPlaceFileExtensions[i]))
				return true;
		}
		
		return false;
	}
}
