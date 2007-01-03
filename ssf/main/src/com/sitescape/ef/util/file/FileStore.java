package com.sitescape.ef.util.file;

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

import com.sitescape.ef.util.FileHelper;

public class FileStore {

	private String rootPath;
	
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
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
		File toFile = getFile(targetFileRelativePath);
		
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
		File toFile = getFile(targetFileRelativePath);
		
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
		File file = getFile(fileRelativePath);
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

		FileCopyUtils.copy(input, bos);
	}
	
	public void writeFile(String fileRelativePath, byte[] input) throws IOException {
		File file = getFile(fileRelativePath);

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
		File file = new File(rootPath, fileRelativePath);
		
		File parentDir = file.getParentFile();
		if(!parentDir.exists())
			parentDir.mkdirs();
		
		return file;
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
}
