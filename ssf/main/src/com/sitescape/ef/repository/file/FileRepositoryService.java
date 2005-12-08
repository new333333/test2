package com.sitescape.ef.repository.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;

import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.FileHelper;

/**
 * An implementation of file-based repository which supports neither versioning
 * nor checkout/checkin features. This class should never be used for production
 * system. Also this implementation will NOT work if the pathname for file 
 * represents anything other than a flat file name.  
 * 
 * @author jong
 *
 */
public class FileRepositoryService implements RepositoryService {

	private static final String VERSION_NAME_PREFIX = "_ssfversionfile_";
	private static final String VERSION_NAME_SUFFIX = "_";
	private static final String TEMP_STRING = "_ssftempfile_";
	
	private String rootDirPath;
	
	public String getRootDirPath() {
		return rootDirPath;
	}

	public void setRootDirPath(String rootPath) throws IOException {
		this.rootDirPath = new File(rootPath).getCanonicalPath();
		
		if(!rootDirPath.endsWith(File.separator))
			rootDirPath += File.separator;
		
		FileHelper.mkdirsIfNecessary(rootDirPath);
	}

	public Object openRepositorySession() throws RepositoryServiceException {
		// This file repository service maintains no connection or session.
		return null;
	}

	public void closeRepositorySession(Object session) throws RepositoryServiceException {
	}
	
	public String create(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		File fileDir = getFileDir(folder, entry, relativeFilePath);
		
		if(!fileDir.exists())
			fileDir.mkdirs();

		String versionName = newVersionName();
		
		File versionFile = getVersionFile(folder, entry, relativeFilePath, versionName);
		
        try {
        	mf.transferTo(versionFile);
		} catch (IllegalStateException e) {
			throw new RepositoryServiceException(e);

		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	
		return versionName;
	}

	public void update(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		
		File tempFile = getTempFile(folder, entry, relativeFilePath);
		
		if(!tempFile.exists())
			throw new RepositoryServiceException("Cannot update [" + entry.getId() 
					+ "," + relativeFilePath + "]: It must be checked out first");

    	try {
			mf.transferTo(tempFile);
		} catch (IllegalStateException e) {
			throw new RepositoryServiceException(e);

		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void read(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, OutputStream out) throws RepositoryServiceException {
		File latestFile = getLatestFile(folder, entry, relativeFilePath);
		
		readFile(latestFile, out);
	}

	public void readVersion(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, String versionName, OutputStream out) throws RepositoryServiceException {
		File versionFile = getVersionFile(folder, entry, relativeFilePath, versionName);
		
		readFile(versionFile, out);
	}

	public DataSource getDataSource(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		File latestFile = getLatestFile(folder, entry, relativeFilePath);
		FileDataSource fSource = new FileDataSource(latestFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}
	public DataSource getDataSourceVersion(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		File versionFile = getVersionFile(folder, entry, relativeFilePath, versionName);
		FileDataSource fSource = new FileDataSource(versionFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}	
	
	public void checkout(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(folder, entry, relativeFilePath);
		
		if(!tempFile.exists()) { // It is not checked out
			// Check it out by coping the content of the latest version of the file
			try {
				FileHelper.copyContent(getLatestVersionFile(folder, entry, relativeFilePath), tempFile);
			} catch (IOException e) {
				throw new RepositoryServiceException(e);
			}
		}
	}

	public void uncheckout(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(folder, entry, relativeFilePath);
		
		if(tempFile.exists()) { // It is checked out
			// Delete the temp file
			try {
				FileHelper.delete(tempFile);
			} catch (IOException e) {
				throw new RepositoryServiceException(e);
			}
		}
	}

	public String checkin(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(folder, entry, relativeFilePath);
		
		if(tempFile.exists()) { // It is checked out
			String versionName = newVersionName();
			
			File versionFile = getVersionFile(folder, entry, relativeFilePath, versionName);
			
		    try {
				FileHelper.move(tempFile, versionFile);
			} catch (IOException e) {
				throw new RepositoryServiceException(e);
			}
			
			return versionName;
		}	
		else { // It is already checked in
			return getLatestVersionName(folder, entry, relativeFilePath);
		}
	}

	public boolean isCheckedOut(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(folder, entry, relativeFilePath);
		
		return tempFile.exists();
	}
	
	public boolean supportVersionDeletion() {
		return false;
	}

	public boolean exists(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		String[] versionFileNames = getVersionFileNames(folder, entry, relativeFilePath);
		if(versionFileNames == null || versionFileNames.length == 0)
			return false;
		else
			return true;
	}

	public long getContentLength(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File latestFile = getLatestFile(folder, entry, relativeFilePath);
		
		return latestFile.length();
	}
	
	public long getContentLength(Object session, Folder folder, FolderEntry entry, 
			String relativeFilePath, String versionName) throws RepositoryServiceException {
		File versionFile = getVersionFile(folder, entry, relativeFilePath, versionName);
		
		return versionFile.length();
	}
	
	private File getLatestFile(Folder folder, FolderEntry entry, String relativeFilePath) {
		File tempFile = getTempFile(folder, entry, relativeFilePath);
		
		if(tempFile.exists()) {
			return tempFile;
		}
		else {
			return getLatestVersionFile(folder, entry, relativeFilePath);
		}
	}
	
	private String[] getVersionFileNames(Folder folder, FolderEntry entry, String relativeFilePath) {
		File file = getFile(folder, entry, relativeFilePath);
		File fileDir = file.getParentFile();
		String fileName = file.getName();
		final String versionFileNamePrefix;
		final String versionFileNameSuffix;
		
		int index = fileName.lastIndexOf(".");
		if(index == -1) {
			// The file name doesn't contain extension 
			versionFileNamePrefix = fileName + VERSION_NAME_PREFIX;
			versionFileNameSuffix = null;
		}
		else {
			versionFileNamePrefix = fileName.substring(0, index) + VERSION_NAME_PREFIX;
			versionFileNameSuffix = fileName.substring(index);
		}
		
		String[] versionFileNames = fileDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(name.startsWith(versionFileNamePrefix) &&
						(versionFileNameSuffix == null || (name.endsWith(versionFileNameSuffix))))
					return true;
				else
					return false;	
			}
		});
		
		return versionFileNames;
	}
	
	private String getLatestVersionName(Folder folder, FolderEntry entry, String relativeFilePath) {
		String[] versionFileNames = getVersionFileNames(folder, entry, relativeFilePath);
		String latestVersionName = null;
		for(int i = 0; i < versionFileNames.length; i++) {
			String versionName = getVersionName(versionFileNames[i]);
			latestVersionName = getLaterVersionName(latestVersionName, versionName);
		}
		return latestVersionName;
	}
	
	private String getLaterVersionName(String versionName1, String versionName2) {
		if(versionName1 == null) {
			if(versionName2 == null)
				return null;
			else
				return versionName2;
		}
		else {
			if(versionName2 == null)
				return versionName1;
			else {
				long value1 = Long.valueOf(versionName1).longValue();
				long value2 = Long.valueOf(versionName2).longValue();
				return ((value1 > value2)? versionName1 : versionName2);
			}
		}
	}
	
	private String getVersionName(String versionFileName) {
		int versionNameBeginIndex = versionFileName.indexOf(VERSION_NAME_PREFIX) + VERSION_NAME_PREFIX.length();
		int versionNameEndIndex = versionFileName.indexOf(VERSION_NAME_SUFFIX, versionNameBeginIndex);
		
		return versionFileName.substring(versionNameBeginIndex, versionNameEndIndex);
	}
	
	private File getLatestVersionFile(Folder folder, FolderEntry entry, String relativeFilePath) {
		String latestVersionName = getLatestVersionName(folder, entry, relativeFilePath);
		return getVersionFile(folder, entry, relativeFilePath, latestVersionName);
	}

	private File getFileDir(Folder folder, FolderEntry entry, String relativeFilePath) {
		File file = getFile(folder, entry, relativeFilePath);
		
		return file.getParentFile();
	}
	
	private String getEntryDirPath(Folder folder, FolderEntry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(rootDirPath).append(zoneName).append(File.separator).append(folder.getId()).append(File.separator).append(entry.getId()).append(File.separator).toString();
	}
	
	private File getFile(Folder folder, FolderEntry entry, String relativeFilePath) {
		return new File(getEntryDirPath(folder, entry), relativeFilePath);
	}
	
	private String newVersionName() {
		return String.valueOf(new Date().getTime());
	}
	
	private File getVersionFile(Folder folder, FolderEntry entry, 
			String relativeFilePath, String versionName) {
		File file = getFile(folder, entry, relativeFilePath);
		
		String fileName = file.getName();
		String versionFileName;
		int index = fileName.lastIndexOf(".");
		if(index == -1) {
			// The file name doesn't contain extension 
			versionFileName = fileName + VERSION_NAME_PREFIX + versionName + 
				VERSION_NAME_SUFFIX;
		}
		else {
			versionFileName = fileName.substring(0, index) + VERSION_NAME_PREFIX + 
				versionName + VERSION_NAME_SUFFIX + "." + fileName.substring(index+1);
		}
		return new File(file.getParent(), versionFileName);		
	}
	
	private File getTempFile(Folder folder, FolderEntry entry, String relativeFilePath) {
		File file = getFile(folder, entry, relativeFilePath);
		
		String fileName = file.getName();
		String tempFileName;
		int index = fileName.lastIndexOf(".");
		if(index == -1) {
			// The file name doesn't contain extension 
			tempFileName = fileName + TEMP_STRING;
		}
		else {
			tempFileName = fileName.substring(0, index) + TEMP_STRING + 
				"." + fileName.substring(index+1);
		}
		return new File(file.getParent(), tempFileName);		
	}

	private void readFile(File file, OutputStream out) throws RepositoryServiceException {
		FileInputStream in = null;
		
		try {
			in = new FileInputStream(file);
		
			FileHelper.copyContent(in, out);
		}
		catch(IOException e) {
			throw new RepositoryServiceException(e);
		}
		finally {
			if(in != null) {
				try {
					in.close();
				} 
				catch (IOException e) {}
			}
		}			
	}
}
