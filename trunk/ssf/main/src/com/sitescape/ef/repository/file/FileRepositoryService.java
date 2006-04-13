package com.sitescape.ef.repository.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
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
import com.sitescape.ef.domain.DefinableEntity;
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
	
	public String createVersioned(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		File fileDir = getFileDir(binder, entry, relativeFilePath);
		
        try {
    		FileHelper.mkdirsIfNecessary(fileDir);

    		String versionName = newVersionName();
    		
    		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
    		
        	mf.transferTo(versionFile);
        	
        	return versionName;
		} catch (IllegalStateException e) {
			throw new RepositoryServiceException(e);

		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public String createVersioned(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException {
		File fileDir = getFileDir(binder, entry, relativeFilePath);
		
		try {
			FileHelper.mkdirsIfNecessary(fileDir);

			String versionName = newVersionName();
			
			File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
			
			copyData(in, versionFile);
			
			return versionName;
		}
		catch(IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void createUnversioned(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException {
		File fileDir = getFileDir(binder, entry, relativeFilePath);
		
		try {
			FileHelper.mkdirsIfNecessary(fileDir);
			
			File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
			
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unversionedFile));
			
			try {
				FileCopyUtils.copy(in, bos);
			} 
			finally {
				try {
					bos.close();
				} catch (IOException e) {
					logger.warn(e); // Log and eat up.
				}
			}
		}
		catch(IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void update(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException {
		
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		try {
			if(fileInfo == VERSIONED_FILE) {
				File tempFile = getTempFile(binder, entry, relativeFilePath);
				
				if(!tempFile.exists())
					throw new RepositoryServiceException("Cannot update file " + 
							relativeFilePath + " for entry " + entry.getTypedId() + 
							": It must be checked out first"); 
	
				mf.transferTo(tempFile);
			}
			else if(fileInfo == UNVERSIONED_FILE) {
				File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
				
				mf.transferTo(unversionedFile);
			}
			else {
				throw new RepositoryServiceException("Cannot update file " + relativeFilePath + 
						" for entry " + entry.getTypedId() + ": It does not exist"); 
			}
		}
		catch (IllegalStateException e) {
			throw new RepositoryServiceException(e);
		} 
		catch (IOException e) {
			throw new RepositoryServiceException(e);
		}			
	}

	public void update(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException {
		
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		try {
			if(fileInfo == VERSIONED_FILE) {
				File tempFile = getTempFile(binder, entry, relativeFilePath);
				
				if(!tempFile.exists())
					throw new RepositoryServiceException("Cannot update file " + 
							relativeFilePath + " for entry " + entry.getTypedId() + 
							": It must be checked out first"); 
	
				copyData(in, tempFile);
			}
			else if(fileInfo == UNVERSIONED_FILE) {
				File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
				
				copyData(in, unversionedFile);
			}
			else {
				throw new RepositoryServiceException("Cannot update file " + relativeFilePath + 
						" for entry " + entry.getTypedId() + ": It does not exist"); 
			}
		}
		catch (IOException e) {
			throw new RepositoryServiceException(e);
		}			
	}
	
	public void delete(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		// Since this operation may involve deleting multiple files, it is 
		// tricky to deal with error conditions precisely. For now, I'll 
		// take simplistic approach of throwing an exception on the first
		// I/O error. An alternative would be to stay in the method until
		// it tries all files, accumulate all errors that may arise during
		// the operation, and throw at the end one big exception that
		// summarizes all the errors. 
		
		// Unlike other methods that take different actions based on the
		// return value from fileInfo method, this method tries to account
		// for all circumstances inclusive. Under some rare error situations,
		// it is potentially possible that both unversioned and versioned
		// files exist on disk (in which case fileInfo's return indicates
		// that the file is unversioned, but that is rather implementation
		// dependent and not well defined at the API level). In such case,
		// we want delete method to be able to clean up the entire mess
		// so that the next invocation of fileInfo returns NON_EXISTING_FILE. 
		// At least this much is semantically clear and hence should be
		// enforced. 
		
		// Delete temp file if exists
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		if(tempFile.exists()) {
			try {
				FileHelper.delete(tempFile);
			}
			catch(IOException e) {
				logger.error("Error deleting file [" + tempFile.getAbsolutePath() + "]");
				throw new RepositoryServiceException(e);
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
				logger.error("Error deleting file [" + versionFile.getAbsolutePath() + "]");			
				throw new RepositoryServiceException(e);
			}
		}
		
		// Delete unversioned file if exists
		File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
		
		try {
			FileHelper.delete(unversionedFile);
		}
		catch(IOException e) {
			logger.error("Error deleting file [" + unversionedFile.getAbsolutePath() + "]");
			throw new RepositoryServiceException(e);
		}			
	}

	public void read(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, OutputStream out) throws RepositoryServiceException {
		File file = getFileForRead(session, binder, entry, relativeFilePath);
		
		readFile(file, out);
	}

	public InputStream read(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File file = getFileForRead(session, binder, entry, relativeFilePath);
		
		try {
			return new FileInputStream(file);
		}
		catch(IOException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public void readVersion(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName, OutputStream out) throws RepositoryServiceException {
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
			
			readFile(versionFile, out);
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 
		}
		else {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public int fileInfo(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
		
		if(unversionedFile.exists()) {
			return UNVERSIONED_FILE;
		}
		else {
			String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
			if(versionFileNames == null || versionFileNames.length == 0)
				return NON_EXISTING_FILE;
			else
				return VERSIONED_FILE;
		}
	}

	public boolean supportVersioning() {
		return true;
	}
	
	public DataSource getDataSource(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		File latestFile = getLatestFile(binder, entry, relativeFilePath);
		FileDataSource fSource = new FileDataSource(latestFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}
	public DataSource getDataSourceVersion(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
		FileDataSource fSource = new FileDataSource(versionFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}	
	
	public void checkout(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
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
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot checkout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot checkout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public void uncheckout(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
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
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot uncheckout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot uncheckout file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}

	public String checkin(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
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
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot checkin file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot checkin file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}		
	}

	public boolean isCheckedOut(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		return tempFile.exists();
	}
	
	public boolean supportVersionDeletion() {
		// Actually we can easily support this, but...
		return false;
	}

	/*
	public boolean exists(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		if(versionFileNames == null || versionFileNames.length == 0)
			return false;
		else
			return true;
	}*/

	public long getContentLength(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File latestFile = getLatestFile(binder, entry, relativeFilePath);
			
			return latestFile.length();
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
			
			return unversionedFile.length();
		}
		else {
			throw new RepositoryServiceException("Cannot get length of file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}
	
	public long getContentLength(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName) throws RepositoryServiceException {
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
			
			return versionFile.length();
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot get length of file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It is not versioned"); 			
		}
		else {
			throw new RepositoryServiceException("Cannot get length of file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}
	}
	
	/**
	 * Returns latest snapshot of the file (which is either the latest version
	 * of the file or the working copy in progress which is created when the 
	 * file is checked out). It is assumed that the file is versioned.
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private File getLatestFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		if(tempFile.exists()) {
			return tempFile;
		}
		else {
			File latestVersionFile = getLatestVersionFile(binder, entry, relativeFilePath);
			if(latestVersionFile != null)
				return latestVersionFile;
			else
				throw new RepositoryServiceException("The specified file does not exist");
		}
	}
	
	/**
	 * Returns an array of file names representing each version of the specified file. 
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private String[] getVersionFileNames(Binder binder, DefinableEntity entry, String relativeFilePath) {
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
	
	/**
	 * Returns latest version name or <code>null</code> if no version exists.
	 * 
	 * @param binder
	 * @param entry
	 * @param relativeFilePath
	 * @return
	 */
	private String getLatestVersionName(Binder binder, DefinableEntity entry, String relativeFilePath) {
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		String latestVersionName = null;
		if(versionFileNames != null) {
			for(int i = 0; i < versionFileNames.length; i++) {
				String versionName = getVersionName(versionFileNames[i]);
				latestVersionName = getLaterVersionName(latestVersionName, versionName);
			}
		}
		return latestVersionName;
	}
	
	/**
	 * Return version name that is later of two.
	 * 
	 * @param versionName1
	 * @param versionName2
	 * @return
	 */
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
	
	/**
	 * Given a name of version file, return its version name portion. 
	 * 
	 * @param versionFileName
	 * @return
	 */
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
	private File getLatestVersionFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		String latestVersionName = getLatestVersionName(binder, entry, relativeFilePath);
		if(latestVersionName != null)
			return getVersionFile(binder, entry, relativeFilePath, latestVersionName);
		else
			return null;
	}

	private File getFileDir(Binder binder, DefinableEntity entry, String relativeFilePath) {
		File file = getFile(binder, entry, relativeFilePath);
		
		return file.getParentFile();
	}
	
	private String getEntityDirPath(Binder binder, DefinableEntity entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(dataRootDir).append(zoneName).append(File.separator).append(subDirName).append(File.separator).append(binder.getId()).append(File.separator).append(entry.getTypedId()).append(File.separator).toString();
	}
	
	private File getFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		return new File(getEntityDirPath(binder, entry), relativeFilePath);
	}
	
	private String newVersionName() {
		return String.valueOf(new Date().getTime());
	}
	
	private File getVersionFileFromVersionFileName(Binder binder, DefinableEntity entry, String relativeFilePath, String versionFileName) {
		File file = getFile(binder, entry, relativeFilePath);
		
		return new File(file.getParent(), versionFileName);
	}
	
	private File getVersionFile(Binder binder, DefinableEntity entry, 
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
	
	private File getUnversionedFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
		return getFile(binder, entry, relativeFilePath);
	}
	
	private File getTempFile(Binder binder, DefinableEntity entry, String relativeFilePath) {
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
		try {
			FileCopyUtils.copy(new BufferedInputStream(new FileInputStream(file)), out);
		} 
		catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	private void copyData(InputStream in, File outFile) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outFile));
		
		try {
			FileCopyUtils.copy(in, bos);
		} 
		finally {
			try {
				bos.close();
			} catch (IOException e) {
				logger.warn(e); // Log and return normally.
			}
		}
	}
	
	private File getFileForRead(Object session, Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException {
		int fileInfo = fileInfo(session, binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			return getLatestFile(binder, entry, relativeFilePath);
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			return getUnversionedFile(binder, entry, relativeFilePath);
		}
		else {
			throw new RepositoryServiceException("Cannot read file " + relativeFilePath + 
					" for entry " + entry.getTypedId() + ": It does not exist"); 
		}					
	}
}
