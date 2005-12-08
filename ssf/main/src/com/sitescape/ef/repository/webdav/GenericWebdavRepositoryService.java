package com.sitescape.ef.repository.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;
import com.sitescape.ef.repository.RepositoryService;
import com.sitescape.ef.repository.RepositoryServiceException;
import com.sitescape.ef.util.Constants;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.domain.Entry;
import com.sitescape.ef.domain.Binder;
public class GenericWebdavRepositoryService extends AbstractWebdavResourceFactory implements RepositoryService {

	protected Log logger = LogFactory.getLog(getClass());

	protected String docRootDir;

	public String getDocRootDir() {
		return docRootDir;
	}

	public void setDocRootDir(String docRootDir) {
		this.docRootDir = docRootDir;
	}
	
	public Object openRepositorySession() throws RepositoryServiceException{
		try {
			return openResource();
		} catch (IOException e) {
			throw new RepositoryServiceException(e);
		}
	}
	
	public void closeRepositorySession(Object session) throws RepositoryServiceException{
		try {
			((SWebdavResource) session).close();
		} catch (IOException e) {
			throw new RepositoryServiceException(e);			
		}
	}

	public String create(Object session, Binder binder, Entry entry, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			return createResource(wdr, binder, entry, relativeFilePath, mf);
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public void update(Object session, Binder binder, Entry entry, 
			String relativeFilePath, MultipartFile mf) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			updateResource(wdr, binder, entry, relativeFilePath, mf);
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public void read(Object session, Binder binder, Entry entry, String relativeFilePath, 
			OutputStream out) throws RepositoryServiceException {	
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			readResource(wdr, getResourcePath(binder, entry, relativeFilePath), out);
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	// obsolete
	public void readVersion(Object session, String fileVersionURI, OutputStream out) throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			readResource(wdr, fileVersionURIToResourcePath(fileVersionURI), out);
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public void readVersion(Object session, Binder binder, Entry entry, String relativeFilePath, 
			String versionName, OutputStream out) throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String versionResourcePath = getVersionResourcePath(wdr, binder, entry, 
					relativeFilePath, versionName);			
			
			readResource(wdr, versionResourcePath, out);
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public List getVersionNames(Object session, Binder binder, Entry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			return WebdavUtil.getVersionNames(wdr, getResourcePath(binder, entry, relativeFilePath));
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public DataSource getDataSource(Object session, Binder binder, Entry entry, 
			String relativeFilePath, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;		
		return new WebDavDataSource(wdr, getResourcePath(binder, entry, relativeFilePath), relativeFilePath, fileTypeMap);
	}
	public DataSource getDataSourceVersion(Object session, Binder binder, Entry entry, 
			String relativeFilePath, String versionName, FileTypeMap fileTypeMap)		
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String versionResourcePath = getVersionResourcePath(wdr, binder, entry, 
					relativeFilePath, versionName);			
			return new WebDavDataSource(wdr, versionResourcePath, relativeFilePath, fileTypeMap);
			
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}	
	// obsolete
	public List fileVersionsURIs(Object session, Binder binder, Entry entry, 
			String relativeFilePath) throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String resourcePath = getResourcePath(binder, entry, relativeFilePath);
			
			String value = WebdavUtil.getSingleHrefValue(wdr, 
					resourcePath, "version-history");
			
			if(value == null || value.length() == 0)
				throw new RepositoryServiceException("Cannot find version-history property for " + resourcePath);
			
			return WebdavUtil.getHrefValues(wdr, value, "version-set");

		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public void checkout(Object session, Binder binder, Entry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String resourcePath = getResourcePath(binder, entry, relativeFilePath);

			// Since we always put file resource under version control as
			// soon as it is created, it's largely unnecessary to do it
			// again here. But for the unlikely scenario where the file was
			// created successfully but not put under version control 
			// properly, we will re-execute the version-control command
			// here. If the resource is already version controlled, this
			// operation is noop. 
			boolean result = wdr.versionControlMethod(resourcePath);

			if(!isCheckedOut(wdr, resourcePath)) {
				result = wdr.checkoutMethod(resourcePath);
				if(!result)
					throw new RepositoryServiceException("Failed to checkout [" + resourcePath + "]");
			}
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public void uncheckout(Object session, Binder binder, Entry entry, String relativeFilePath) throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String resourcePath = getResourcePath(binder, entry, relativeFilePath);
			if(isCheckedOut(wdr, resourcePath)) {
				boolean result = wdr.uncheckoutMethod(resourcePath);
				if(!result)
					throw new RepositoryServiceException("Failed to uncheckout [" + resourcePath + "]");
			}
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public String checkin(Object session, Binder binder, Entry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String resourcePath = getResourcePath(binder, entry, relativeFilePath);
			String checkedInVersionResourcePath = getCheckedInVersionResourcePath(wdr, resourcePath);
			String versionResourcePath = null;
			String versionName = null;
			if(checkedInVersionResourcePath == null || checkedInVersionResourcePath.length() == 0) {
				// The resource is currently checked out. 
				String location = wdr.checkin(resourcePath);
				
				if(location == null || location.length() == 0)
					throw new RepositoryServiceException("Failed to checkin [" + resourcePath + "]");
				
				String newVersionResourcePath = locationURLToResourcePath(location);
				
				versionResourcePath = newVersionResourcePath;
			}
			else {
				// The resource is currently checked in. 
				versionResourcePath = checkedInVersionResourcePath;
			}			
			versionName = getVersionName(wdr, versionResourcePath);
			
			if(versionName == null || versionName.length() == 0)
				throw new RepositoryServiceException("Failed to get version name for [" + versionResourcePath + "]");
			
			return versionName;
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}
	
	public boolean isCheckedOut(Object session, Binder binder, Entry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String resourcePath = getResourcePath(binder, entry, relativeFilePath);
			
			return isCheckedOut(wdr, resourcePath);
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}
	
	public boolean exists(Object session, Binder binder, Entry entry, String relativeFilePath) throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String resourcePath = getResourcePath(binder, entry, relativeFilePath);
			
			return WebdavUtil.exists(wdr, resourcePath);
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}

	public long getContentLength(Object session, Binder binder, 
			Entry entry, String relativeFilePath) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String resourcePath = getResourcePath(binder, entry, relativeFilePath);
		
			wdr.setPath(resourcePath);
			
			return wdr.getGetContentLength();
		} catch (IOException e) {
			logError(wdr);
			throw new RepositoryServiceException(e);
		}
	}
	
	public long getContentLength(Object session, Binder binder, 
			Entry entry, String relativeFilePath, String versionName) 
		throws RepositoryServiceException {
		SWebdavResource wdr = (SWebdavResource) session;
		
		try {
			String versionResourcePath = getVersionResourcePath(wdr, binder, entry, 
					relativeFilePath, versionName);			
			
			wdr.setPath(versionResourcePath);
			
			return wdr.getGetContentLength();
		} catch (IOException e) {
			logError(wdr);
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
	
	protected String getVersionResourcePath(SWebdavResource wdr, Binder binder, 
			Entry entry, String relativeFilePath, String versionName) 
		throws RepositoryServiceException, HttpException, IOException {
		return getVersionResourcePath(wdr, getResourcePath(binder, entry, relativeFilePath),
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
	
	private boolean isCheckedOut(SWebdavResource wdr, String resourcePath) 
		throws HttpException, IOException {		
		String checkedInValue = getCheckedInVersionResourcePath(wdr, resourcePath);
		
		if(checkedInValue == null || checkedInValue.length() == 0)
			return true;
		else
			return false; 
	}

	private String getCheckedInVersionResourcePath(SWebdavResource wdr, String resourcePath) 
		throws HttpException, IOException {		
		return WebdavUtil.getSingleStringValue(wdr, resourcePath, "checked-in");		
	}
	
	private String getVersionName(SWebdavResource wdr, String versionResourcePath) 
		throws HttpException, IOException {
		return WebdavUtil.getSingleStringValue(wdr, versionResourcePath, "version-name");
	}
	
	private String getCheckedInVersionName(SWebdavResource wdr, String resourcePath) 
		throws HttpException, IOException {
		return getVersionName(wdr, getCheckedInVersionResourcePath(wdr, resourcePath));
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

	private String createResource(SWebdavResource wdr, Binder binder,
			Entry entry, String relativeFilePath, MultipartFile mf)
			throws IOException {
		boolean result = false;

		// Get the path for the entry containing the file.
		String entryDirPath = getEntryDirPath(binder, entry);

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

		// Write the file.
		result = wdr.putMethod(resourcePath, mf.getInputStream());
		
		// Put the file under version control.
		result = wdr.versionControlMethod(resourcePath);
		
		return getCheckedInVersionName(wdr, resourcePath);
	}
	
	private void updateResource(SWebdavResource wdr, Binder binder,
			Entry entry, String relativeFilePath, MultipartFile mf)
			throws IOException {
		boolean result = false;

		/*
		 * boolean b = wdr.headMethod("/slide/files");
		 * System.out.println("Status Code: " + wdr.getStatusCode());
		 * System.out.println("Status Message: " + wdr.getStatusMessage()); b =
		 * wdr.headMethod("/slide/files/sitescape"); System.out.println("Status
		 * Code: " + wdr.getStatusCode()); System.out.println("Status Message: " +
		 * wdr.getStatusMessage());
		 */

		// Get the path for the file resource.
		String resourcePath = getResourcePath(binder, entry, relativeFilePath);

		// Write the file.
		result = wdr.putMethod(resourcePath, mf.getInputStream());
	}
	
	private void readResource(SWebdavResource wdr, String resourcePath,
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

		FileCopyUtils.copy(is, out);

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

	private String getEntryDirPath(Binder binder, Entry entry) {
		String zoneName = RequestContextHolder.getRequestContext().getZoneName();
		
		return new StringBuffer(getDocRootDir()).
			append(zoneName).
			append(Constants.SLASH).
			append(binder.getId()).
			append(Constants.SLASH).
			append(entry.getId()).
			append(Constants.SLASH).
			toString();
	}
	
	private String getResourcePath(String entryDirPath, String relativeFilePath) {
		return entryDirPath + relativeFilePath;
	}
	
	private String getResourcePath(Binder binder, Entry entry, 
			String relativeFilePath) {
		return getResourcePath(getEntryDirPath(binder, entry), relativeFilePath);
	}
	public class WebDavDataSource implements DataSource {
		protected SWebdavResource wdr;
		protected String resourcePath, name;
		protected FileTypeMap fileMap;
		public WebDavDataSource(SWebdavResource wdr, String resourcePath, String name, FileTypeMap fileMap) {
			this.wdr = wdr;
			this.resourcePath = resourcePath;
			this.fileMap = fileMap;
			this.name = name;
		}
		public java.io.InputStream getInputStream() throws java.io.IOException {
			return wdr.getMethodData(resourcePath);
		}
		
		public java.io.OutputStream getOutputStream() throws java.io.IOException {
			return null;
		}
		public java.lang.String getContentType() {
			return fileMap.getContentType(resourcePath);
		}
		public java.lang.String getName() {
			return name;
			
		}


	}
}
