package com.sitescape.team.repository.file;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.FileTypeMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.UncheckedIOException;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.repository.RepositoryServiceException;
import com.sitescape.team.repository.RepositorySession;
import com.sitescape.team.repository.RepositoryUtil;
import com.sitescape.team.util.FileHelper;

public class FileRepositorySession implements RepositorySession {

	protected static Log logger = LogFactory.getLog(FileRepositorySession.class);

	private static final String VERSION_NAME_PREFIX = "_ssfversionfile_";
	private static final String VERSION_NAME_SUFFIX = "_";
	private static final String TEMP_STRING = "_ssftempfile_";

	private String repositoryRootDir;
	
	public FileRepositorySession(String repositoryRootDir) {
		this.repositoryRootDir = repositoryRootDir;
	}
	
	public void close() throws RepositoryServiceException, UncheckedIOException {
	}
	
	public String createVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException, UncheckedIOException {
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
			throw new UncheckedIOException(e);
		}
	}

	public String createVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		File fileDir = getFileDir(binder, entry, relativeFilePath);
		
		try {
			FileHelper.mkdirsIfNecessary(fileDir);

			String versionName = newVersionName();
			
			File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
			
			copyData(in, versionFile);
			
			return versionName;
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public void createUnversioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
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
			throw new UncheckedIOException(e);
		}
	}

	public void update(Binder binder, DefinableEntity entry, 
			String relativeFilePath, MultipartFile mf) throws RepositoryServiceException, UncheckedIOException {
		
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
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
			throw new UncheckedIOException(e);
		}			
	}

	public void update(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in) throws RepositoryServiceException, UncheckedIOException {
		
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
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
			throw new UncheckedIOException(e);
		}			
	}
	
	public void delete(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
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
		
		try {
			FileHelper.delete(tempFile);
		}
		catch(IOException e) {
			logger.error("Error deleting file [" + tempFile.getAbsolutePath() + "]");
			throw new UncheckedIOException(e);
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
				throw new UncheckedIOException(e);
			}
		}
		
		// Delete unversioned file if exists
		File unversionedFile = getUnversionedFile(binder, entry, relativeFilePath);
		
		try {
			FileHelper.delete(unversionedFile);
		}
		catch(IOException e) {
			logger.error("Error deleting file [" + unversionedFile.getAbsolutePath() + "]");
			throw new UncheckedIOException(e);
		}			
	}

	public void read(Binder binder, DefinableEntity entry, 
			String relativeFilePath, OutputStream out) throws RepositoryServiceException, UncheckedIOException {
		File file = getFileForRead(binder, entry, relativeFilePath);
		
		readFile(file, out);
	}

	public InputStream read(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		File file = getFileForRead(binder, entry, relativeFilePath);
		
		try {
			return new BufferedInputStream(new FileInputStream(file));
		}
		catch(IOException e) {
			throw new UncheckedIOException(e);
		}	
	}
	
	public void readVersion(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName, OutputStream out) 
		throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
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

	public InputStream readVersion(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
			
			try {
				return new BufferedInputStream(new FileInputStream(versionFile));
			} catch (FileNotFoundException e) {
				throw new UncheckedIOException(e);
			}
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

	public int fileInfo(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
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
	
	public DataSource getDataSource(Binder binder, DefinableEntity entry, 
			String relativeFilePath, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException, UncheckedIOException {
		File latestFile = getLatestFile(binder, entry, relativeFilePath);
		FileDataSource fSource = new FileDataSource(latestFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}
	public DataSource getDataSourceVersion(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException, UncheckedIOException {
		File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
		FileDataSource fSource = new FileDataSource(versionFile);
		fSource.setFileTypeMap(fileTypeMap);
		return fSource;
	}	
	
	public void checkout(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
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
					throw new UncheckedIOException(e);
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

	public void uncheckout(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File tempFile = getTempFile(binder, entry, relativeFilePath);
			
			if(tempFile.exists()) { // It is checked out
				// Delete the temp file
				try {
					FileHelper.delete(tempFile);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
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

	public String checkin(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File tempFile = getTempFile(binder, entry, relativeFilePath);
			
			if(tempFile.exists()) { // It is checked out
				String versionName = newVersionName();
				
				File versionFile = getVersionFile(binder, entry, relativeFilePath, versionName);
				
			    try {
					FileHelper.move(tempFile, versionFile);
				} catch (IOException e) {
					throw new UncheckedIOException(e);
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

	/*
	public boolean isCheckedOut(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		File tempFile = getTempFile(binder, entry, relativeFilePath);
		
		return tempFile.exists();
	}*/

	/*
	public boolean exists(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		String[] versionFileNames = getVersionFileNames(binder, entry, relativeFilePath);
		if(versionFileNames == null || versionFileNames.length == 0)
			return false;
		else
			return true;
	}*/

	public long getContentLength(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
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
	
	public long getContentLength(Binder binder, DefinableEntity entry, 
			String relativeFilePath, String versionName) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
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
	
	public void move(Binder binder, DefinableEntity entity, 
			String relativeFilePath, Binder destBinder, 
			DefinableEntity destEntity, String destRelativeFilePath)
	throws RepositoryServiceException, UncheckedIOException {
		File tempFile = getTempFile(binder, entity, relativeFilePath);
		
		if(tempFile.exists()) {
			File newTempFile = getTempFile(destBinder, destEntity, destRelativeFilePath);			
			move(tempFile, newTempFile);
		}
		
		String[] versionFileNames = getVersionFileNames(binder, entity, relativeFilePath);
		
		String versionName;
		File versionFile, newVersionFile;
		for(int i = 0; i < versionFileNames.length; i++) {
			versionName = getVersionName(versionFileNames[i]);
			versionFile = getVersionFileFromVersionFileName(binder, entity, relativeFilePath, versionFileNames[i]);
			newVersionFile = getVersionFile(destBinder, destEntity, destRelativeFilePath, versionName);
			move(versionFile, newVersionFile);
		}
		
		File unversionedFile = getUnversionedFile(binder, entity, relativeFilePath);

		if(unversionedFile.exists()) {
			File newUnversionedFile = getUnversionedFile(destBinder, destEntity, destRelativeFilePath);
			move(unversionedFile, newUnversionedFile);
		}
	}

	public void deleteVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entity, relativeFilePath);
		
		if(fileInfo == VERSIONED_FILE) {
			File versionFile = getVersionFile(binder, entity, relativeFilePath, versionName);
			
			try {
				FileHelper.delete(versionFile);
			}
			catch(IOException e) {
				logger.error("Error deleting file [" + versionFile.getAbsolutePath() + "]");
				throw new UncheckedIOException(e);
			}			
		}
		else if(fileInfo == UNVERSIONED_FILE) {
			throw new RepositoryServiceException("Cannot delete a version from the file " + 
					relativeFilePath + " for entry " + entity.getTypedId() + ": It is not versioned"); 
		}
		else {
			throw new RepositoryServiceException("Cannot delete a version from the file " + 
					relativeFilePath + " for entry " + entity.getTypedId() + ": It does not exist"); 
		}	
	}

	// For internal use only
	public List<String> getVersionNames(Binder binder, DefinableEntity entity,
			String relativeFilePath) throws RepositoryServiceException {
		String[] versionFileNames = getVersionFileNames(binder, entity, relativeFilePath);
		List<String> list = new ArrayList<String>(versionFileNames.length);
		for(int i = 0; i < versionFileNames.length; i++) 
			list.add(versionFileNames[i]);
		return list;
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
		
		if(versionFileNames == null)
			versionFileNames = new String[0];
		
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
		return repositoryRootDir + RepositoryUtil.getEntityPath(binder, entry, File.separator);
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

	private void readFile(File file, OutputStream out) throws RepositoryServiceException, UncheckedIOException {
		try {
			FileCopyUtils.copy(new BufferedInputStream(new FileInputStream(file)), out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
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
	
	private File getFileForRead(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		int fileInfo = fileInfo(binder, entry, relativeFilePath);
		
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
	
	private void move(File source, File target) throws UncheckedIOException {
		try {
			FileHelper.move(source, target);
		} catch (IOException e) {
			logger.error("Error moving file [" + source.getAbsolutePath() + "] to [" + target.getAbsolutePath() + "]");			
			throw new UncheckedIOException(e);
		}
	}
	
}
