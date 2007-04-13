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
package com.sitescape.team.util;

import java.io.File;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;

public class FilePathUtil {
	
	/**
	 * Returns relative file system path representing the specified file.
	 * For example, liferay.com/294/folderEntry_654/report.doc
	 * 
	 * @param binder
	 * @param entity
	 * @param fileName
	 * @return
	 */
	public static String getFilePath(Binder binder, DefinableEntity entity,
			String fileName) {
		return getFilePathInternal(binder, entity, fileName).toString();
	}
	
	/**
	 * Returns relative file system path representing the specified file.
	 * For example, liferay.com/294/folderEntry_654/thumb/report.doc
	 * 
	 * @param binder
	 * @param entity
	 * @param subdirName
	 * @param fileName
	 * @return
	 */
	public static String getFilePath(Binder binder, DefinableEntity entity,
			String subdirName, String fileName) {
		return getFilePathInternal(binder, entity, subdirName, fileName).toString();
	}
	
	/**
	 * Returns relative file system path representing the specified binder.
	 * The returned directory path does not begin with a file separator 
	 * character, but ends with a file separator. 
	 * (eg. liferay.com/294/)
	 *
	 * @param binder
	 * @return
	 */
	public static String getBinderDirPath(Binder binder) {
		return getBinderDirPathInternal(binder).toString();
	}
	
	/**
	 * Returns relative file system path representing the specified entity.
	 * The returned directory path does not begin with a file separator 
	 * character, but ends with a file separator. 
	 * (eg. liferay.com/294/folderEntry_654/)
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
	 * The returned directory path does not begin with a file separator character, 
	 * but ends with a file separator. (eg. liferay.com/294/folderEntry_654/thumb/)
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
	
	private static StringBuffer getBinderDirPathInternal(Binder binder) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		// For better scalability, each binder is represented as two-level 
		// directories on the file system, where the binders are grouped
		// into chunks of size 1000.
		return new StringBuffer(zoneName).
			append(File.separator).
			append(binder.getId()/1000).		
			append(File.separator).
			append(binder.getId()).
			append(File.separator);
	}
	
	private static StringBuffer getEntityDirPathInternal(Binder binder, DefinableEntity entity) {
		return getBinderDirPathInternal(binder).
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
	
	
	public static void main(String[] args) {
		System.out.println("-1001: " + (-1001/1000));
		System.out.println("-1000: " + (-1000/1000));
		System.out.println("-999: " + (-999/1000));
		System.out.println("-1: " + (-1/1000));
		System.out.println("0: " + (0/1000));
		System.out.println("1: " + (1/1000));
		System.out.println("2: " + (2/1000));
		System.out.println("999: " + (999/1000));
		System.out.println("1000: " + (1000/1000));
		System.out.println("1001: " + (1001/1000));
		System.out.println("1999: " + (1999/1000));
		System.out.println("2000: " + (2000/1000));
		System.out.println("2001: " + (2001/1000));
	}
}
