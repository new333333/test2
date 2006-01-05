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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.ConfigPropertyNotFoundException;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.SPropsUtil;

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

	protected Log logger = LogFactory.getLog(getClass());

	private static final String VERSION_NAME_PREFIX = "_ssfversionfile_";
	private static final String VERSION_NAME_SUFFIX = "_";
	private static final String TEMP_STRING = "_ssftempfile_";
	
	private String dataRootDir;
	private String subDirName;

	public void setDataRootDirProperty(String dataRootDirProperty)
			throws ConfigPropertyNotFoundException, IOException {
		this.dataRootDir = SPropsUtil.getDirPath(dataRootDirProperty);
	}

	public void setSubDirName(String subDirName) {
		this.subDirName = subDirName;
	}

	public Object openRepositorySession() throws RepositoryServiceException {
		// This file repository service maintains no connection or session.
		return null;
	}

	public void closeRepositorySession(Object session) throws RepositoryServiceException {
	}
	
	public String create(Object session, Binder binder, Entry entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		File fileDir = getFileDir(binder, entry, relativeFilePath);
		
		if(!fileDir.exists())
			fileDir.mkdirs();

		String versionName = newVersionName();
		
		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
		
        try {
        	mf.transferTo(versionFile);
		} catch (IllegalStateException e) {
			throw new RepositoryServiceException(e);

		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	
		return versionName;
	}

	public void update(Object session, Binder binder, Entry entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
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

	public void delete(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		// Delete temp file if exists
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		if(tempFile.exists()) {
			try {
				FileHelper.delete(tempFile);
			}
			catch(IOException e) {
				logger.error("Error deleting file [" + tempFile.getAbsolutePath() + "]\n"
						+ e.toString());
			}
		}
		
		// Delete all existing version files
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		
		File versionFile;
		for(int i = 0; i < versionFileNames.length; i++) {
			versionFile = getVersionFileFromVersionFileName(binder, entry, 
				relativeFilePath, versionFileNames[i]);
			try {
				FileHelper.delete(versionFile);
			}
			catch(IOException e) {
				logger.error("Error deleting file [" + versionFile.getAbsolutePath() + "]\n"
						+ e.toString());				
			}
		}
		
		// TODO We should aggregate the IOException errors occured during this
		// call and throws something that represents it. 
	}

	public void read(Object session, Binder binder, Entry entry, 
			String relativeFilePath, OutputStream out) throws RepositoryServiceException {
		File latestFile = getLatestFile(binder, entry, relativeFilePath);
		
		readFile(latestFile, out);
	}

	public void readVersion(Object session, Binder binder, Entry entry, 
			String relativeFilePath, String versionName, OutputStream out) throws RepositoryServiceException {
		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
		
		readFile(versionFile, out);
	}

	public DataSource getDataSource(Object session, Binder binder, Entry entry, 
			String relativeFilePath, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		File latestFile = getLatestFile(binder, entry, relativeFilePath);
		FileDataSource fSource = new FileDataSource(latestFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}
	public DataSource getDataSourceVersion(Object session, Binder binder, Entry entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
		FileDataSource fSource = new FileDataSource(versionFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}	
	
	public void checkout(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		if(!tempFile.exists()) { // It is not checked out
			try {
				File latestVersionFile = getLatestVersionFile(binder, entry, relativeFilePath);
				
				if(latestVersionFile != null) {
					// Check it out by coping the content of the latest version of the file
					FileCopyUtils.copy(latestVersionFile, tempFile);
				}
				else {
					// This shouldn't occur.
					throw new RepositoryServiceException("No version file is found");
				}
			} catch (IOException e) {
				throw new RepositoryServiceException(e);
			}
		}
	}

	public void uncheckout(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		if(tempFile.exists()) { // It is checked out
			// Delete the temp file
			try {
				FileHelper.delete(tempFile);
			} catch (IOException e) {
				throw new RepositoryServiceException(e);
			}
		}
	}

	public String checkin(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		if(tempFile.exists()) { // It is checked out
			String versionName = newVersionName();
			
			File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
			
		    try {
				FileHelper.move(tempFile, versionFile);
			} catch (IOException e) {
				throw new RepositoryServiceException(e);
			}
			
			return versionName;
		}	
		else { // It is already checked in
			return getLatestVersionName(binder, entry, relativeFilePath);
		}
	}

	public boolean isCheckedOut(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		return tempFile.exists();
	}
	
	public boolean supportVersionDeletion() {
		return false;
	}

	public boolean exists(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		if(versionFileNames == null || versionFileNames.length == 0)
			return false;
		else
			return true;
	}

	public long getContentLength(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File latestFile = getLatestFile(binder, entry, relativeFilePath);
		
		return latestFile.length();
	}
	
	public long getContentLength(Object session, Binder binder, Entry entry, 
			String relativeFilePath, String versionName) throws RepositoryServiceException {
		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
		
		return versionFile.length();
	}
	
	private File getLatestFile(Binder binder, Entry entry, String relativeFilePath) {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		if(tempFile.exists()) {
			return tempFile;
		}
		else {
			return getLatestVersionFile(binder, entry, relativeFilePath);
		}
	}
	
	private String[] getVersionFileNames(Binder binder, Entry entry, String relativeFilePath) {
		File file = getFile(binder, entry, relativeFilePath);
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
	
	private String getLatestVersionName(Binder binder, Entry entry, String relativeFilePath) {
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
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
	
	/**
	 * Returns latest version file or <code>null</code> if no version file exists.
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private File getLatestVersionFile(Binder binder, Entry entry, String relativeFilePath) {
		String latestVersionName = getLatestVersionName(binder, entry, relativeFilePath);
		if(latestVersionName != null)
			return getVersionFile(binder, entry, relativeFilePath, latestVersionName);
		else
			return null;
	}

	private File getFileDir(Binder binder, Entry entry, String relativeFilePath) {
		File file = getFile(binder, entry, relativeFilePath);
		
		return file.getParentFile();
	}
	
	private String getEntryDirPath(Binder binder, Entry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(dataRootDir).append(zoneName).append(File.separator).append(subDirName).append(File.separator).append(binder.getId()).append(File.separator).append(entry.getId()).append(File.separator).toString();
	}
	
	private File getFile(Binder binder, Entry entry, String relativeFilePath) {
		return new File(getEntryDirPath(binder, entry), relativeFilePath);
	}
	
	private String newVersionName() {
		return String.valueOf(new Date().getTime());
	}
	
	private File getVersionFileFromVersionFileName(Binder binder, Entry entry, String relativeFilePath, String versionFileName) {
		File file = getFile(binder, entry, relativeFilePath);
		
		return new File(file.getParent(), versionFileName);
	}
	
	
	private File getVersionFile(Binder binder, Entry entry, 
			String relativeFilePath, String versionName) {
		File file = getFile(binder, entry, relativeFilePath);
		
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
	
	private File getTempFile(Binder binder, Entry entry, String relativeFilePath) {
		File file = getFile(binder, entry, relativeFilePath);
		
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
		
			FileCopyUtils.copy(in, out);
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
