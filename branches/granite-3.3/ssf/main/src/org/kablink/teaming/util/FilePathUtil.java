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
package org.kablink.teaming.util;

import java.io.File;

import org.kablink.teaming.InternalException;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;


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

	public static String getFilePath(Binder binder, DefinableEntity entity,
			FileAttachment fa, String fileName) {
		return getFilePathInternal(binder, entity, fa, fileName).toString();
	}

	public static String getFilePath(Binder binder, DefinableEntity entity,
			FileAttachment fa, String subdirName, String fileName) {
		return getFilePathInternal(binder, entity, fa, subdirName, fileName).toString();
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
	
	public static String getFileAttachmentDirPath(Binder binder, 
			DefinableEntity entity, FileAttachment fa) {
		return getFileAttachmentDirPathInternal(binder, entity, fa).toString();
	}
	
	private static StringBuilder getEntitySubdirPathInternal(Binder binder, 
			DefinableEntity entity, String subdirName) {
		return getEntityDirPathInternal(binder, entity).
		append(subdirName).
		append(File.separatorChar);
	}
	
	private static StringBuilder getFileAttachmentSubdirPathInternal(Binder binder, 
			DefinableEntity entity, FileAttachment fa, String subdirName) {
		return getFileAttachmentDirPathInternal(binder, entity, fa).
		append(subdirName).
		append(File.separatorChar);
	}
	
	private static StringBuilder getFileAttachmentDirPathInternal(Binder binder, 
			DefinableEntity entity, FileAttachment fa) {
		String faId = fa.getId();
		if(faId == null)
			throw new IllegalStateException("File attachment should have an id");
		
		return getEntityDirPathInternal(binder, entity)
		.append(faId)
		.append(File.separatorChar);
	}
	
	private static StringBuilder getBinderDirPathInternal(Binder binder) {
		Long binderId = binder.getId();
		if(binderId == null)
			throw new IllegalStateException("Binder must have an id");
		
		// For better scalability, each binder is represented as two-level 
		// directories on the file system, where the binders are grouped
		// into chunks of size 1000.
		return new StringBuilder(Utils.getZoneKey()).
			append(File.separator).
			append(binderId.longValue()/1000).		
			append(File.separator).
			append(binderId).
			append(File.separator);
	}
	
	private static StringBuilder getEntityDirPathInternal(Binder binder, DefinableEntity entity) {
		String id = entity.getEntityTypedId();
		if(id == null)
			throw new IllegalStateException("Entity must have an id");
		
		return getBinderDirPathInternal(binder).
			append(id).
			append(File.separator);
	}
	
	private static StringBuilder getFilePathInternal(Binder binder, DefinableEntity entity,
			String fileName) {
		return getEntityDirPathInternal(binder, entity).append(fileName);
	}
	
	private static StringBuilder getFilePathInternal(Binder binder, DefinableEntity entity,
			String subdirName, String fileName) {
		return getEntitySubdirPathInternal(binder, entity, subdirName).append(fileName);
	}
	
	private static StringBuilder getFilePathInternal(Binder binder, DefinableEntity entity,
			FileAttachment fa, String fileName) {
		return getFileAttachmentDirPathInternal(binder, entity, fa).append(fileName);
	}
	
	private static StringBuilder getFilePathInternal(Binder binder, DefinableEntity entity,
			FileAttachment fa, String subdirName, String fileName) {
		return getFileAttachmentSubdirPathInternal(binder, entity, fa, subdirName).append(fileName);
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
