/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.context.request.RequestContextHolder;

import org.springframework.util.FileCopyUtils;

/**
 * ?
 * 
 * @author ?
 */
public class TempFileUtil {
	private final static String HTML5_UPLOADER_TEMP_SUBDIR	= "html5.uploader";	// Directory where HTML5 uploader temporary files are stored.
	
	private final static String OIT_TEMP_SUBDIR				= "oit";			// Must agree with the path injected by the installer.  See install.tcl for details.
	private final static String OIT_TEMPDIR_PROPERTY		= "tempdir";		// Property in the Oracle Outside-in configuration file that specifies the location of temporary files.
	
	public final static long	A_SECOND					= 1000l;			// One second, in milliseconds.
	public final static long	A_MINUTE					= (60l * A_SECOND);	// One minute, in milliseconds.
	public final static long	AN_HOUR						= (60l * A_MINUTE);	// One hour,   in milliseconds.
	public final static long	A_DAY						= (24l * AN_HOUR);	// One day,    in milliseconds.
	
	/**
	 * Create a temporary file. The created temporary file is set to be deleted
	 * when the virtual machine terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @return
	 */
	public static File createTempFile(String prefix) {
		return createTempFile(prefix, null, getTempFileDir(), true);
	}
	
	/**
	 * Create a temporary file for the HTML5 uploader. The created
	 * temporary file is set to be deleted when the virtual machine
	 * terminates.
	 * 
	 * @param	prefix	The prefix string to be used in generating the file's name; must be at least three characters long.
	 * 
	 * @return
	 */
	public static File createHtml5UploaderTempFile(String fName) throws UncheckedIOException {
		return createTempFile(fName, null, getHtml5UploaderTempDir(), true);
	}
	
	/**
	 * Create a temporary file and set its initial content. 
	 * The created temporary file is set to be deleted when the virtual machine 
	 * terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param content The initial content of the file 
	 * @return
	 */
	public static File createTempFileWithContent(String prefix, InputStream content) {
		return createTempFileWithContent(prefix, null, getTempFileDir(), true, content);
	}
	
	/**
	 * Create a temporary file. The created temporary file is set to be deleted
	 * when the virtual machine terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @return
	 */
	public static File createTempFile(String prefix, File fileDir) {
		return createTempFile(prefix, null, fileDir, true);
	}
	
	/**
	 * Create a temporary file and set its initial content. 
	 * The created temporary file is set to be deleted when the virtual machine 
	 * terminates.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @param content The initial content of the file 
	 * @return
	 */
	public static File createTempFileWithContent(String prefix, File fileDir, InputStream content) {
		return createTempFileWithContent(prefix, null, fileDir, true, content);
	}
	
	/**
	 * Create a temporary file.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; 
	 * may be <code>null</code>, in which case the suffix ".tmp" will be used
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @param deleteOnExit If <code>true</code>, the created file is set to be
	 * deleted when the virtual machine terminates. 
	 * @return
	 * @throws UncheckedIOException
	 */
	public static File createTempFile(String prefix, String suffix, File fileDir,
			boolean deleteOnExit)
		throws UncheckedIOException {
		try {
			if(fileDir != null && !fileDir.exists())
				FileHelper.mkdirs(fileDir);
			
			File file = doCreateTempFile(prefix, suffix, fileDir);
			
			if(deleteOnExit)
				file.deleteOnExit();
			
			return file;
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
	}
	
	/**
	 * Create a temporary file.
	 * 
	 * @param prefix The prefix string to be used in generating the file's name;
	 * must be at least three characters long
	 * @param suffix The suffix string to be used in generating the file's name; 
	 * may be <code>null</code>, in which case the suffix ".tmp" will be used
	 * @param fileDir The directory in which the file is to be created, or 
	 * <code>null</code> if the default temporary-file directory is to be used
	 * @param deleteOnExit If <code>true</code>, the created file is set to be
	 * deleted when the virtual machine terminates. 
	 * @param content The initial content of the file 
	 * @return
	 * @throws UncheckedIOException
	 */
	public static File createTempFileWithContent(String prefix, String suffix, File fileDir, 
			boolean deleteOnExit, InputStream content)
		throws UncheckedIOException {
		File tempFile = TempFileUtil.createTempFile(prefix, suffix, fileDir, deleteOnExit);
		
		try {
			FileCopyUtils.copy(content, new BufferedOutputStream(new FileOutputStream(tempFile)));
			
			return tempFile;
		}
		catch(IOException e) {
			tempFile.delete();
			throw new UncheckedIOException(e);
		}
	}

	/**
	 * Opens a previously created temporary file and returns a stream to it.
	 * 
	 * @param fileHandle The name of the file as returned by File.getName().
	 */
	public static InputStream openTempFile(String fileHandle) throws UncheckedIOException {
		try {
			return new FileInputStream(getTempFileByName(fileHandle));
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Returns a File object on a previously created temporary file.
	 * @param fileHandle The name of the file as returned by File.getName().
	 * @return
	 */
	public static File getTempFileByName(String fileHandle) {
		File junk = new File(fileHandle);
		fileHandle = junk.getName();

		return new File(getTempFileDirPath(), fileHandle);
	}

	/**
	 * Returns a File object on a previously created HTML5 uploader
	 * temporary file.
	 * 
	 * @param fileHandle The name of the file as returned by File.getName().
	 * 
	 * @return
	 */
	public static File getHtml5UploaderTempFileByName(String fileHandle) {
		File junk = new File(fileHandle);
		fileHandle = junk.getName();

		return new File(getHtml5UploaderTempDirPath(), fileHandle);
	}

	/**
	 * This method differs from the rest of the public methods in this class in that
	 * this method returns a sub-directory within the temp area rather than returning
	 * a temporary file. The purpose is to give the caller a temporary area to work in. 
	 * To avoid namespace collision among different parts of the application and to 
	 * keep them from stepping over each other, the caller must supply the name of the 
	 * sub-directory and be fully responsible for managing the resources within it. 
	 * As such, the sub-directory name must be unique within the application.
	 *  
	 * @param caller
	 * @return
	 */
	public static File getTempFileDir(String subDirName) {
		return new File(getTempFileDir(), subDirName);
	}

	public static void main(String[] args) {
		System.out.println(new File("").getAbsolutePath());
	}
	
	private static File getTempFileDir() {
		return new File(getTempFileDirPath());
	}
	
	private static String getTempFileDirPath() {
		return getTempFileDirPathPerUser(RequestContextHolder.getRequestContext().getUserId());
	}

	public static File getTempFileRootDir() {
		return new File(getTempFileDirRootPath());
	}
	
	private static String getTempFileDirRootPath() {
		String filePath = SPropsUtil.getString("temp.dir", "");
		if(filePath.equals(""))
			filePath = System.getProperty("java.io.tmpdir");
		return filePath;
	}
	
	private static String getTempFileDirPathPerUser(Long userId) {
		return getTempFileDirRootPath() + File.separator + userId.toString();
	}
	
	private static File doCreateTempFile(String prefix, String suffix, File fileDir) throws IOException {
		try {
			return File.createTempFile(prefix, suffix, fileDir);
		}
		catch(IOException e) {
			// Give it one more try after brief pause.
			try {
				Thread.sleep(1);
			} 
			catch (InterruptedException ignore) {}
			return File.createTempFile(prefix, suffix, fileDir);
		}
	}

	/**
	 * Returns the appropriate path to use for the HTML5 uploader
	 * temporary directory.
	 * 
	 * @return
	 */
	public static String getHtml5UploaderTempDirPath() {
		return (getTempFileDirRootPath() + File.separator + HTML5_UPLOADER_TEMP_SUBDIR);
	}
	
	/**
	 * Returns the appropriate Filr to use for the HTML5 uploader
	 * temporary directory.
	 *
	 * @param createIfNecessary
	 * 
	 * @return
	 */
	public static File getHtml5UploaderTempDir(boolean createIfNecessary) {
		File reply = new File(getHtml5UploaderTempDirPath());
		if ((!(reply.exists())) && createIfNecessary) {
			reply.mkdirs();
		}
		return reply;
	}
	
	public static File getHtml5UploaderTempDir() {
		// Always use the initial form of the method.
		return getHtml5UploaderTempDir(true);
	}
	
	/**
	 * Returns the appropriate path to use for the Oracle Outside-in
	 * temporary directory.
	 * 
	 * @return
	 */
	public static String getOITTempDirPath() {
		return (getTempFileDirRootPath() + File.separator + OIT_TEMP_SUBDIR);
	}
	
	/**
	 * Sets the property the Oracle Outside-in converters use for their
	 * temporary directory.
	 * 
	 * @param oitProps
	 */
	public static void setOITTempDirProperty(Properties oitProps) {
		oitProps.setProperty(OIT_TEMPDIR_PROPERTY, TempFileUtil.getOITTempDirPath());
	}
	
	/**
	 * Validates that the Oracle Outside-in temporary directory has
	 * been created.   Returns a File object that describes the
	 * temporary directory.
	 *
	 * @param createIfNecessary
	 * 
	 * @return
	 */
	public static File validateOITTempDir(boolean createIfNecessary) {
		String oitTempPath = getOITTempDirPath();
		File oitTempDir = new File(oitTempPath);
		if ((!(oitTempDir.exists())) && createIfNecessary) {
			oitTempDir.mkdirs();
		}
		return oitTempDir;
	}
	
	public static File validateOITTempDir() {
		// Always use the initial form of the method.
		return validateOITTempDir(true);	// true -> Create it if it's not there.
	}
}
