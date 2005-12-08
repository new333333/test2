package com.sitescape.ef.repository.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.FileHelper;

public class GenericWebdavRepositoryService extends AbstractWebdavResourceFactory implements RepositoryService {

	protected Log logger = LogFactory.getLog(getClass());
	
	public SWebdavResource openSession(String userName, String password) 
		throws IOException {
		HttpURL hrl = new HttpURL(httpUrl);
		hrl.setUserinfo(userName, password);
		SWebdavResource wdr = new SWebdavResource(hrl);
		
		//WebdavUtil.dump(wdr);
		
		return wdr;
	}

	public void write(Folder folder, FolderEntry entry, String relativeFilePath, 
			MultipartFile mf) throws RepositoryServiceException {
		/*
		boolean checkedOut = isCheckedOut(folder, entry, relativeFilePath);
		if(!checkedOut)
			checkout(folder, entry, relativeFilePath);
		checkedOut = isCheckedOut(folder, entry, relativeFilePath);
		*/

		try {
			SWebdavResource wdr = openSession();
			
			try {
				writeInternal(wdr, folder, entry, relativeFilePath, mf);
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
		
		//checkin(folder, entry, relativeFilePath);
	}

	public void read(Folder folder, FolderEntry entry, String relativeFilePath, 
			OutputStream out) throws RepositoryServiceException {	
		try {
			SWebdavResource wdr = openSession();
			
			try {
				readInternal(wdr, getResourcePath(folder, entry, relativeFilePath), out);
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	// obsolete
	public void readVersion(String fileVersionURI, OutputStream out) throws RepositoryServiceException {
		try {
			SWebdavResource wdr = openSession();
			
			try {
				readInternal(wdr, fileVersionURIToResourcePath(fileVersionURI), out);
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void readVersion(Folder folder, FolderEntry entry, String relativeFilePath, 
			String versionName, OutputStream out) throws RepositoryServiceException {
		try {
			SWebdavResource wdr = openSession();
			
			try {
				String versionResourcePath = getVersionResourcePath(wdr, folder, entry, 
						relativeFilePath, versionName);			
				
				readInternal(wdr, versionResourcePath, out);
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}
	
	// obsolete
	public List fileVersionsURIs(Folder folder, FolderEntry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		try {
			SWebdavResource wdr = openSession();
			
			try {
				String resourcePath = getResourcePath(folder, entry, relativeFilePath);
				
				String value = WebdavUtil.getSingleHrefValue(wdr, 
						resourcePath, "version-history");
				
				if(value == null || value.length() == 0)
					throw new RepositoryServiceException("Cannot find version-history property for " + resourcePath);
				
				return WebdavUtil.getHrefValues(wdr, value, "version-set");
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void checkout(Folder folder, FolderEntry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		try {
			SWebdavResource wdr = openSession();
			
			try {
				String resourcePath = getResourcePath(folder, entry, relativeFilePath);
				boolean result = wdr.checkoutMethod(resourcePath);
				if(!result)
					throw new RepositoryServiceException("Failed to checkout [" + resourcePath + "]");
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public void uncheckout(Folder folder, FolderEntry entry, String relativeFilePath) throws RepositoryServiceException {
		try {
			SWebdavResource wdr = openSession();
			
			try {
				String resourcePath = getResourcePath(folder, entry, relativeFilePath);
				boolean result = wdr.uncheckoutMethod(resourcePath);
				if(!result)
					throw new RepositoryServiceException("Failed to uncheckout [" + resourcePath + "]");
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}

	public String checkin(Folder folder, FolderEntry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		try {
			SWebdavResource wdr = openSession();
			
			try {
				String resourcePath = getResourcePath(folder, entry, relativeFilePath);
				String location = wdr.checkin(resourcePath);
				
				if(location == null || location.length() == 0)
					throw new RepositoryServiceException("Failed to checkin [" + resourcePath + "]");
				
				String versionResourcePath = locationURLToResourcePath(location);
				
				String versionName = getVersionName(wdr, versionResourcePath);
				
				if(versionName == null || versionName.length() == 0)
					throw new RepositoryServiceException("Failed to get version name for [" + versionResourcePath + "]");
				
				return versionName;
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}
	
	public boolean isCheckedOut(Folder folder, FolderEntry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		try {
			SWebdavResource wdr = openSession();
			
			try {
				String resourcePath = getResourcePath(folder, entry, relativeFilePath);
				
				String checkedInValue = WebdavUtil.getSingleStringValue(wdr, 
						resourcePath, "checked-in");
				
				if(checkedInValue == null || checkedInValue.length() == 0)
					return true;
				else
					return false; 
			}
			catch(IOException e) {
				logError(wdr);
				throw e;
			}
			finally {
				wdr.close();
			}
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}	
	}

	public boolean supportVersionDeletion() {
		// It appears that the Slide server we use does not allows this.
		// It doesn't appear to me to be a restriction by the DeltaV spec
		// itself, but some Slide specific misbehavior (or mis-configuration).
		// It requires more investigation...
		return false; // for now
	}
	
	/**
	 * Converts the value of the Location response header to a valid resource
	 * path. The DeltaV spec (section 4.4) shows an example of CHECKIN method
	 * and corresponding HTTP response containing Location header where the
	 * value is a full URL containing scheme, hostname and port number, etc.
	 * (eg. http://repo.webdav.org/his/23/ver/32). 
	 * On the other hand, it is observed that the Slide Webdav implementation
	 * only returns the resource path URI (eg. /slide/history/201/1.1).
	 * This method provides a hook so that the subclass can override it with
	 * different implementations as needed if the underlying Webdav server's
	 * behavior regarding handling of Location response header is different.
	 * 
	 * @param locationURL
	 * @return resource path
	 */
	protected String locationURLToResourcePath(String locationURL) {
		return locationURL; // This is default implementation that works well with Slide.
	}

	/**
	 * Translates the file version URI into a resource path that can be used for
	 * Webdav request to the current server. This hook is useful when the 
	 * backing Webdav server has been migrated and somehow the server paths for 
	 * the versioned resources have subsequently changed.
	 * This method provides a hook for subclass to override so that the URI can 
	 * be translated from old format (which users may still have around, for
	 * example, in email notifications, etc.) into new one without major effort.
	 * 
	 * @param fileVersionURI
	 * @return
	 */
	protected String fileVersionURIToResourcePath(String fileVersionURI) {
		return fileVersionURI; // this is default implementation
	}
	
	protected String getVersionResourcePath(SWebdavResource wdr, Folder folder, 
			FolderEntry entry, String relativeFilePath, String versionName) 
		throws RepositoryServiceException, HttpException, IOException {
		return getVersionResourcePath(wdr, getResourcePath(folder, entry, relativeFilePath),
				versionName);
	}
	
	protected String getVersionResourcePath(SWebdavResource wdr,
			String versionControlledResourcePath, String versionName) 
		throws RepositoryServiceException, HttpException, IOException {
		String versionHistoryResourcePath = getVersionHistoryResourcePath(wdr,
				versionControlledResourcePath);
		
		if(versionHistoryResourcePath == null || versionHistoryResourcePath.length() == 0)
			throw new RepositoryServiceException("Cannot find version history resource for " + 
					versionControlledResourcePath);

		return makeVersionResourcePath(versionHistoryResourcePath, versionName);
	}
	
	protected String makeVersionResourcePath(String versionHistoryResourcePath,
			String versionName) {
		return versionHistoryResourcePath + "/" + versionName;
	}
	
	private String getVersionName(SWebdavResource wdr, String versionResourcePath) 
		throws HttpException, IOException {
		return WebdavUtil.getSingleStringValue(wdr, versionResourcePath, "version-name");
	}

	/**
	 * Returns the path of the version history resource associated with the
	 * specified version controlled resource. Returns <code>null</code> if
	 * not found.  
	 * 
	 * @param wdr
	 * @param versionControlledResourcePath
	 * @return
	 * @throws IOException 
	 * @throws HttpException 
	 */
	private String getVersionHistoryResourcePath(SWebdavResource wdr,
			String versionControlledResourcePath) throws HttpException, IOException {
		return WebdavUtil.getSingleHrefValue(wdr, 
				versionControlledResourcePath, "version-history");
	}

	private void writeInternal(SWebdavResource wdr, Folder folder,
			FolderEntry entry, String relativeFilePath, MultipartFile mf)
			throws IOException {
		boolean result = false;

		// Get the path for the entry containing the file.
		String entryDirPath = getEntryDirPath(folder, entry);

		/*
		 * boolean b = wdr.headMethod("/slide/files");
		 * System.out.println("Status Code: " + wdr.getStatusCode());
		 * System.out.println("Status Message: " + wdr.getStatusMessage()); b =
		 * wdr.headMethod("/slide/files/sitescape"); System.out.println("Status
		 * Code: " + wdr.getStatusCode()); System.out.println("Status Message: " +
		 * wdr.getStatusMessage());
		 */

		// If necessary, create containing collections (recursively) before
		// writing the file itself.
		WebdavUtil.createCollectionIfNecessary(wdr, entryDirPath);

		// Get the path for the file resource.
		String resourcePath = getResourcePath(entryDirPath, relativeFilePath);

		if (wdr.headMethod(resourcePath)) {
			// The file resource already exists.
			// Since we always put file resource under version control as
			// soon as it is created, it's largely unnecessary to do it
			// again here. But we can think of a couple of scenarios where
			// the file may not have been put into version control propertly.
			// For example, the file was created successfully but the
			// subsequent command for turning it into a version controlled
			// resource failed. Another example would be that the file was
			// created through some other means (i.e., other than this
			// interface), although it's very unlikely.
			// The following command will be noop if the resource was already
			// version controlled.
			result = wdr.versionControlMethod(resourcePath);
			// Write the file creating a new version.
			result = wdr.putMethod(resourcePath, mf.getInputStream());
			
		} else {
			// The file resource does not exist.
			// We must write the file first.
			result = wdr.putMethod(resourcePath, mf.getInputStream());
			// Put the file under version control.
			result = wdr.versionControlMethod(resourcePath);
		}
	}

	private SWebdavResource openSession() throws IOException {
		// How do we get WebDAV username/password for individual users??
		return openSession("root", "root");
	}
	
	private void readInternal(SWebdavResource wdr, String resourcePath,
			OutputStream out) throws IOException {

		/*
		 * for debugging WebdavUtil.dumpProp(wdr, "/slide/history/101/1.2",
		 * "version-name"); WebdavUtil.dumpProp(wdr, "/slide/history/101/1.2",
		 * "no-version-delete"); WebdavUtil.dumpProp(wdr,
		 * "/slide/history/101/1.2", "predecessor-set");
		 * WebdavUtil.dumpProp(wdr,
		 * "/slide/files/sitescape/document/liferay.com/74/493/junk.txt",
		 * "no-version-delete");
		 * 
		 * if(wdr.headMethod("/slide/history/101/1.2")) { // failed result =
		 * wdr.deleteMethod("/slide/history/101/1.2"); }
		 * 
		 * if(wdr.headMethod("/slide/history/1")) { // successful result =
		 * wdr.deleteMethod("/slide/history/1"); }
		 * 
		 * if(wdr.headMethod("/slide/files/sitescape/document/liferay.com/74/489/license.html")) //
		 * successful result =
		 * wdr.deleteMethod("/slide/files/sitescape/document/liferay.com/74/489/license.html");
		 * 
		 * String value = WebdavUtil.getSinglePropertyValue(wdr,
		 * "/slide/files/sitescape/document/liferay.com/74/493/junk.txt",
		 * "version-history"); value = WebdavUtil.getHrefValue(value);
		 * WebdavUtil.dumpProp(wdr, "/slide/history/101", "version-set");
		 */

		InputStream is = wdr.getMethodData(resourcePath);

		FileHelper.copyContent(is, out);

		//WebdavUtil.dumpAllProps(wdr, resourcePath); 
		//WebdavUtil.dumpAllProps(wdr, "/slide/history/201"); 
		//WebdavUtil.dumpAllProps(wdr, "/slide/history/201/1.2"); 
	}
	
	private void logError(SWebdavResource wdr) {
		// Log the HTTP status code and error message.
		logger.error("status code=" + wdr.getStatusCode() + ", " +
				"status message=[" + wdr.getStatusMessage() + "]");
		// The exception object associated with the error will be logged higher up.		
	}

	private String getEntryDirPath(Folder folder, FolderEntry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(getDocRootDir()).
			append(zoneName).
			append(Constants.SLASH).
			append(folder.getId()).
			append(Constants.SLASH).
			append(entry.getId()).
			append(Constants.SLASH).
			toString();
	}
	
	private String getResourcePath(String entryDirPath, String relativeFilePath) {
		return entryDirPath + relativeFilePath;
	}
	
	private String getResourcePath(Folder folder, FolderEntry entry, 
			String relativeFilePath) {
		return getResourcePath(getEntryDirPath(folder, entry), relativeFilePath);
	}
}
