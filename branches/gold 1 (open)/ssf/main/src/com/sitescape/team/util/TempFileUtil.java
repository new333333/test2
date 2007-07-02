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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.util.FileCopyUtils;

import com.sitescape.team.UncheckedIOException;

public class TempFileUtil {
	
	/**
	 * Create a temporary file. The created temporary file is set to be deleted
	 * when the virtual machine terminates.
	 * 
	 * @param caller The class object of the caller
	 * @return
	 */
	//public static File createTempFile(Class caller) {
	//	return createTempFile(getPrefix(caller));
	//}
	
	/**
	 * Create a temporary file and set its initial content. 
	 * The created temporary file is set to be deleted when the virtual machine 
	 * terminates.
	 * 
	 * @param caller The class object of the caller
	 * @param content The initial content of the file 
	 * @return
	 */
	//public static File createTempFileWithContent(Class caller, InputStream content) {
	//	return createTempFileWithContent(getPrefix(caller), content);
	//}
	
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
	
	public static File getTempFileDir() {
		return new File(getTempFileDirPath());
	}
	
	public static String getTempFileDirPath() {
		String filePath = SPropsUtil.getString("temp.dir", "");
		if(filePath.equals(""))
			filePath = System.getProperty("java.io.tmpdir");
		return filePath;
	}
	
	private static String getPrefix(Class caller) {
		String name = caller.getSimpleName();
		if(name.length() < 3) // very unlikely scenario
			name = name + "_" + name;
		return name;
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
	private static File createTempFile(String prefix, String suffix, File fileDir,
			boolean deleteOnExit)
		throws UncheckedIOException {
		try {
			if(fileDir != null && !fileDir.exists())
				FileHelper.mkdirs(fileDir);
			
			File file = File.createTempFile(prefix, suffix, fileDir);
			
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
	private static File createTempFileWithContent(String prefix, String suffix, File fileDir, 
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

	/*
	 * Opens a previously created temporary file and returns a stream to it.
	 * 
	 * @param fileHandle The name of the file as returned by File.getName().
	 */
	public static InputStream openTempFile(String fileHandle) throws UncheckedIOException {
		try {
			return new FileInputStream(new File(getTempFileDirPath(), fileHandle));
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static void main(String[] args) {
		System.out.println(new File("").getAbsolutePath());
	}
}
