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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.FileCopyUtils;


public class FileStore {

	private String rootPath;
	
	public FileStore(String rootPath) { 
		this.rootPath = rootPath;
	}
	
	public FileStore(String rootPath, String subDir) { 
		String postFix = "";
		if (!subDir.equals("")) {
			postFix = "/" + subDir;
		}
		this.rootPath = rootPath + postFix;
	}
	
	public String getRootPath() {
		return rootPath;
	}
	
	/**
	 * Copy the source file to the target. 
	 * If target file already exists, it is overwritten.
	 * 
	 * @param sourceFileRelativePath relative path to the source file
	 * @param targetFileRelativePath relative path to the target file
	 * @throws IOException 
	 */
	public void copyFile(String sourceFileRelativePath, String targetFileRelativePath) throws IOException {
		File fromFile = getFile(sourceFileRelativePath);
		File toFile = getFileMkdir(targetFileRelativePath);
		
		FileCopyUtils.copy(fromFile, toFile);
	}
	
	/**
	 * Move the source file to the target.
	 * If target file already exists, it is deleted first.
	 * 
	 * @param sourceFileRelativePath
	 * @param targetFileRelativePath
	 * @throws IOException
	 */
	public void moveFile(String sourceFileRelativePath, String targetFileRelativePath) throws IOException {
		File fromFile = getFile(sourceFileRelativePath);
		File toFile = getFileMkdir(targetFileRelativePath);
		
		FileHelper.move(fromFile, toFile);
	}

	/**
	 * Write the data to the file. 
	 * If the file already exists, it is overwritten. Otherwise, a new file is created.
	 * 
	 * @param fileRelativePath
	 * @param input
	 * @throws IOException
	 */
	public void writeFile(String fileRelativePath, InputStream input) throws IOException {
		File file = getFileMkdir(fileRelativePath);
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

		FileCopyUtils.copy(input, bos);
	}
	
	public void writeFile(String fileRelativePath, byte[] input) throws IOException {
		File file = getFileMkdir(fileRelativePath);

		FileCopyUtils.copy(input, file);
	}
	
	/**
	 * Delete the file.
	 * 
	 * @param fileRelativePath
	 * @throws IOException
	 */
	public void deleteFile(String fileRelativePath) throws IOException {
		File file = getFile(fileRelativePath);
		
		FileHelper.delete(file);
	}
	
	public void deleteDirectory(String dirRelativePath) throws IOException {
		File dir = getFile(dirRelativePath);
		
		FileHelper.deleteRecursively(dir);
	}
	
	public boolean fileExists(String fileRelativePath) {
		return getFile(fileRelativePath).exists();
	}
	
	public void readFile(String fileRelativePath, OutputStream out) throws FileNotFoundException, IOException {
		FileCopyUtils.copy(new BufferedInputStream(new FileInputStream(getFile(fileRelativePath))), out);
	}
	
	/**
	 * Return a file object corresponding to the specified relative file path.
	 * This provides a hook for accessing the file directly.
	 * 
	 * @param fileRelativePath
	 * @return
	 */
	public File getFile(String fileRelativePath) {
		return new File(rootPath, fileRelativePath);
	}
	
	/**
	 * Return absolute path corresponding to the specified relative file path.
	 * This provides a hook for accessing the file directly.
	 * 
	 * @param fileRelativePath
	 * @return
	 */
	public String getAbsolutePath(String fileRelativePath) {
		return getFile(fileRelativePath).getAbsolutePath();
	}
	
	private File getFileMkdir(String fileRelativePath) {
		File file = getFile(fileRelativePath);
		
		File parentDir = file.getParentFile();
		if(!parentDir.exists())
			parentDir.mkdirs();
		
		return file;
	}
	
}
