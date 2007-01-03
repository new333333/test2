package com.sitescape.ef.util;

import java.io.File;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.domain.DefinableEntity;

public class FilePathUtil {

	/*
	public static String getFilePath(Binder binder, DefinableEntity entity,
			FileAttachment fAtt) {
		return getFilePathInternal(binder, entity, fAtt.getFileItem().getName()).toString();
	}
	
	public static String getFilePath(Binder binder, DefinableEntity entity,
			String subdirName, FileAttachment fAtt) {
		return getFilePathInternal(binder, entity, subdirName, fAtt.getFileItem().getName()).toString();
	}*/
	
	public static String getFilePath(Binder binder, DefinableEntity entity,
			String fileName) {
		return getFilePathInternal(binder, entity, fileName).toString();
	}
	
	public static String getFilePath(Binder binder, DefinableEntity entity,
			String subdirName, String fileName) {
		return getFilePathInternal(binder, entity, subdirName, fileName).toString();
	}
	
	/**
	 * Returns relative file system path representing the specified entity.
	 * The returned path does not begin with a file separator character, but 
	 * ends with a file separator. (eg. liferay.com/294/folderEntry_654/)
	 * 
	 * @param binder
	 * @param entity
	 * @return
	 */
	public static String getEntityDirPath(Binder binder, DefinableEntity entity) {
		return getEntityDirPathInternal(binder, entity).toString();
	}
	
	/**
	 * Returns relative file system path representing a subdirectory under the
	 * specified entity. 
	 * The returned path does not begin with a file separator character, but 
	 * ends with a file separator. (eg. liferay.com/294/folderEntry_654/thumb/)
	 * 
	 * @param binder
	 * @param entity
	 * @param subdirName
	 * @return
	 */
	public static String getEntitySubdirPath(Binder binder, DefinableEntity entity,
			String subdirName) {
		return getEntitySubdirPathInternal(binder, entity, subdirName).toString();
	}
	
	
	private static StringBuffer getEntitySubdirPathInternal(Binder binder, 
			DefinableEntity entity, String subdirName) {
		return getEntityDirPathInternal(binder, entity).
		append(subdirName).
		append(File.separatorChar);
	}
	
	private static StringBuffer getEntityDirPathInternal(Binder binder, DefinableEntity entity) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(zoneName).
			append(File.separator).
			append(binder.getId()).
			append(File.separator).
			append(entity.getTypedId()).
			append(File.separator);
	}
	
	private static StringBuffer getFilePathInternal(Binder binder, DefinableEntity entity,
			String fileName) {
		return getEntityDirPathInternal(binder, entity).append(fileName);
	}
	
	private static StringBuffer getFilePathInternal(Binder binder, DefinableEntity entity,
			String subdirName, String fileName) {
		return getEntitySubdirPathInternal(binder, entity, subdirName).append(fileName);
	}
	
}
