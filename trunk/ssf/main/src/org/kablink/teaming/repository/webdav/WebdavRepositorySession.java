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
package org.kablink.teaming.repository.webdav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.commons.httpclient.HttpException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.repository.RepositoryServiceException;
import org.kablink.teaming.repository.RepositorySession;
import org.kablink.teaming.repository.RepositorySessionFactory;
import org.kablink.teaming.repository.RepositoryUtil;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.SWebdavResource;
import org.kablink.teaming.util.WebdavUtil;
import org.springframework.util.FileCopyUtils;


public class WebdavRepositorySession implements RepositorySession {

	protected static Log logger = LogFactory.getLog(WebdavRepositorySession.class);

	private WebdavRepositorySessionFactory factory;
	private SWebdavResource wdr;
	private String docRootPath; // This includes context path as well.
	
	public WebdavRepositorySession(WebdavRepositorySessionFactory factory, 
			SWebdavResource wdr, String docRootDir) {
		this.factory = factory;
		this.wdr = wdr;
		this.docRootPath = docRootDir;
	}
	
	public void close() throws RepositoryServiceException, UncheckedIOException{
		if(wdr != null) {
			try {
				wdr.close();
				wdr = null;
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}

	public String createVersioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in, long size, Long lastModTime) throws RepositoryServiceException, UncheckedIOException {
		try {
			return createResource(wdr, binder, entry, relativeFilePath, in, true);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}	
	}

	public void createUnversioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in, long size, Long lastModTime) throws RepositoryServiceException, UncheckedIOException {
		try {
			createResource(wdr, binder, entry, relativeFilePath, in, false);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}	
	}

	public void update(Binder binder, DefinableEntity entry, 
			String relativeFilePath, InputStream in, long size, Long lastModTime) throws RepositoryServiceException, UncheckedIOException {
		try {
			updateResource(wdr, binder, entry, relativeFilePath, in);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}	
	}
	
	public void delete(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		try {
			deleteResource(wdr, binder, entry, relativeFilePath);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}		
	}

	public void delete(Binder binder, DefinableEntity entity) 
	throws RepositoryServiceException, UncheckedIOException {
		try {
			deleteResource(wdr, binder, entity);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}		
	}

	public void delete(Binder binder) 
	throws RepositoryServiceException, UncheckedIOException {
		try {
			deleteResource(wdr, binder);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}		
	}

	public void readUnversioned(Binder binder, DefinableEntity entry, String relativeFilePath, 
			OutputStream out) throws RepositoryServiceException, UncheckedIOException {	
		InputStream is = readUnversioned(binder, entry, relativeFilePath);
		
		try {
			FileCopyUtils.copy(is, out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	public InputStream readUnversioned(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {	
		try {
			return wdr.getMethodData(getFileResourcePath(binder, entry, relativeFilePath));
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	public void readVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, 
			String latestVersionName, OutputStream out) 
	throws RepositoryServiceException, UncheckedIOException {
		InputStream is = readVersioned(binder, entity, relativeFilePath, versionName, latestVersionName);
		
		try {
			FileCopyUtils.copy(is, out);
		} 
		catch (IOException e) {
			throw new UncheckedIOException(e);
		}		
	}
	
	public InputStream readVersioned(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName, String latestVersionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath;
			if(versionName != null)			
				resourcePath = getVersionResourcePath(wdr, binder, entity, relativeFilePath, versionName);
			else
				resourcePath = getFileResourcePath(binder, entity, relativeFilePath);
			
			return wdr.getMethodData(resourcePath);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	// For internal use only
	public List<String> getVersionNames(Binder binder, DefinableEntity entry, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			return WebdavUtil.getVersionNames(wdr, getFileResourcePath(binder, entry, relativeFilePath));
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	// obsolete
	public List fileVersionsURIs(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);
			
			String value = WebdavUtil.getSingleHrefValue(wdr, 
					resourcePath, "version-history");
			
			if(value == null || value.length() == 0)
				throw new RepositoryServiceException("Cannot find version-history property for " + resourcePath);
			
			return WebdavUtil.getHrefValues(wdr, value, "version-set");

		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	public void checkout(Binder binder, DefinableEntity entry, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);

			// Since we always put file resource under version control as
			// soon as it is created, it's largely unnecessary to do it
			// again here. But for the unlikely scenario where the file was
			// created successfully but not put under version control 
			// properly, we will re-execute the version-control command
			// here. If the resource is already version controlled, this
			// operation is noop. 
			boolean result = wdr.versionControlMethod(resourcePath);

			if(!result)
				throw new RepositoryServiceException("Failed to put [" + resourcePath + "] under version control");
			
			if(!isCheckedOut(wdr, resourcePath)) {
				result = wdr.checkoutMethod(resourcePath);
				if(!result)
					throw new RepositoryServiceException("Failed to checkout [" + resourcePath + "]");
			}
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	public void uncheckout(Binder binder, DefinableEntity entry, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);
			if(isCheckedOut(wdr, resourcePath)) {
				boolean result = wdr.uncheckoutMethod(resourcePath);
				if(!result)
					throw new RepositoryServiceException("Failed to uncheckout [" + resourcePath + "]");
			}
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	public String checkin(Binder binder, DefinableEntity entry, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);
			String checkedInVersionResourcePath = getCheckedInVersionResourcePath(wdr, resourcePath);
			if(checkedInVersionResourcePath == null || checkedInVersionResourcePath.length() == 0) {
				// The resource is currently checked out. 
				String location = wdr.checkin(resourcePath);
				
				if(location == null || location.length() == 0)
					throw new RepositoryServiceException("Failed to checkin [" + resourcePath + "]");
				
				String newVersionResourcePath = locationURLToResourcePath(location);
				
				String versionName = getVersionName(wdr, newVersionResourcePath);
				
				if(versionName == null || versionName.length() == 0)
					throw new RepositoryServiceException("Failed to get version name for [" + newVersionResourcePath + "]");
				
				return versionName;
			}
			else {
				// The resource is currently checked in. 
				return null;
			}			
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	public long getContentLengthUnversioned(Binder binder, 
			DefinableEntity entry, String relativeFilePath) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);
		
			wdr.setPath(resourcePath);
			
			return wdr.getGetContentLength();
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}
	
	public long getContentLengthVersioned(Binder binder, 
			DefinableEntity entry, String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			String versionResourcePath = getVersionResourcePath(wdr, binder, entry, 
					relativeFilePath, versionName);			
			
			wdr.setPath(versionResourcePath);
			
			return wdr.getGetContentLength();
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	public int fileInfo(Binder binder, DefinableEntity entry, 
			String relativeFilePath) throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);
		
			if(WebdavUtil.exists(wdr, resourcePath)) {
				String checkedInVersionResourcePath = getCheckedInVersionResourcePath(wdr,resourcePath);
				
				if(checkedInVersionResourcePath == null || checkedInVersionResourcePath.length() == 0)
					return UNVERSIONED_FILE;
				else
					return VERSIONED_FILE;				
			}
			else {
				return NON_EXISTING_FILE;
			}
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}	
	}
	
	public void move(Binder binder, DefinableEntity entity, 
			String relativeFilePath, Binder destBinder, 
			DefinableEntity destEntity, String destRelativeFilePath)
	throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entity, relativeFilePath);
			String newResourcePath = getFileResourcePath(destBinder, destEntity, destRelativeFilePath);
			
			moveResource(wdr, resourcePath, newResourcePath);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
	}

	public void copy(Binder binder, DefinableEntity entity, String relativeFilePath, 
			Binder destBinder, DefinableEntity destEntity, String destRelativeFilePath) 
	throws RepositoryServiceException, UncheckedIOException {
		try {
			String resourcePath = getFileResourcePath(binder, entity, relativeFilePath);
			String newResourcePath = getFileResourcePath(destBinder, destEntity, destRelativeFilePath);
			
			copyResource(wdr, resourcePath, newResourcePath);
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}		
	}

	public void deleteVersion(Binder binder, DefinableEntity entity, 
			String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException {
		try {
			String versionResourcePath = getVersionResourcePath(wdr, binder, entity, 
					relativeFilePath, versionName);			
			
			boolean result = wdr.deleteMethod(versionResourcePath);
			
			if(!result)
				throw new RepositoryServiceException("Failed to delete [" + versionResourcePath + "]");
		} catch (IOException e) {
			wdr.logError(logger);
			throw new UncheckedIOException(e);
		}
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
			DefinableEntity entry, String relativeFilePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException, HttpException, IOException {
		return getVersionResourcePath(wdr, getFileResourcePath(binder, entry, relativeFilePath),
				versionName);
	}
	
	protected String getVersionResourcePath(SWebdavResource wdr,
			String versionControlledResourcePath, String versionName) 
		throws RepositoryServiceException, UncheckedIOException, HttpException, IOException {
		String versionHistoryResourcePath = getVersionHistoryResourcePath(wdr,
				versionControlledResourcePath);
		
		if(versionHistoryResourcePath == null || versionHistoryResourcePath.length() == 0)
			throw new RepositoryServiceException("Cannot find version history resource for " + 
					versionControlledResourcePath);

		return makeVersionResourcePath(versionHistoryResourcePath, versionName);
	}
	
	protected String makeVersionResourcePath(String versionHistoryResourcePath,
			String versionName) {
		return versionHistoryResourcePath + Constants.SLASH + versionName;
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
			DefinableEntity entry, String relativeFilePath, InputStream is,
			boolean versioned)
			throws RepositoryServiceException, IOException {
		boolean result = false;

		// Get the path for the file resource.
		String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);

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
		WebdavUtil.createCollectionIfNecessary(wdr, getFileResourceParentPath(resourcePath));

		// Write the file.
		result = wdr.putMethod(resourcePath, is);
		
		if(!result)
			throw new RepositoryServiceException("Failed to write [" + resourcePath + "]");
		
		if(versioned) {
			// Put the file under version control.
			result = wdr.versionControlMethod(resourcePath);
			
			if(!result)
				throw new RepositoryServiceException("Failed to put [" + resourcePath + "] under version control");
		
			return getCheckedInVersionName(wdr, resourcePath);
		}
		else {
			return null;
		}
	}
	
	private void moveResource(SWebdavResource wdr, String resourcePath, 
			String newResourcePath) throws RepositoryServiceException, 
			IOException {
		boolean result = wdr.moveMethod(resourcePath, newResourcePath);
		
		if(!result)
			throw new RepositoryServiceException("Failed to move [" + resourcePath + "] to [" + newResourcePath + "]");
	}

	private void copyResource(SWebdavResource wdr, String resourcePath, 
			String newResourcePath) throws RepositoryServiceException, 
			IOException {
		boolean result = wdr.copyMethod(resourcePath, newResourcePath);
		
		if(!result)
			throw new RepositoryServiceException("Failed to copy [" + resourcePath + "] to [" + newResourcePath + "]");
	}
	
	private void updateResource(SWebdavResource wdr, Binder binder,
			DefinableEntity entry, String relativeFilePath, InputStream in)
			throws RepositoryServiceException, IOException {
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
		String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);

		// Write the file.
		result = wdr.putMethod(resourcePath, in);
		
		if(!result)
			throw new RepositoryServiceException("Failed to write [" + resourcePath + "]");
	}

	private void deleteResource(SWebdavResource wdr, Binder binder,
			DefinableEntity entry, String relativeFilePath)
			throws RepositoryServiceException, IOException {
		// Get the path for the file resource.
		String resourcePath = getFileResourcePath(binder, entry, relativeFilePath);

		// Delete the file.
		boolean result = wdr.deleteMethod(resourcePath);
		
		if(!result)
			throw new RepositoryServiceException("Failed to delete [" + resourcePath + "]");
	}
	
	private void deleteResource(SWebdavResource wdr, Binder binder,
			DefinableEntity entry)
			throws RepositoryServiceException, IOException {
		// Get the path for the file resource.
		String resourcePath = getEntityResourcePath(binder, entry);

		// Delete the directory.
		boolean result = wdr.deleteMethod(resourcePath);
		
		if(!result)
			throw new RepositoryServiceException("Failed to delete [" + resourcePath + "]");
	}
	
	private void deleteResource(SWebdavResource wdr, Binder binder)
			throws RepositoryServiceException, IOException {
		// Get the path for the file resource.
		String resourcePath = getBinderResourcePath(binder);

		// Delete the directory.
		boolean result = wdr.deleteMethod(resourcePath);
		
		if(!result)
			throw new RepositoryServiceException("Failed to delete [" + resourcePath + "]");
	}

	private String getEntityResourcePath(Binder binder, DefinableEntity entry) {
		return docRootPath + RepositoryUtil.getEntityPath(binder, entry, Constants.SLASH);
	}
	
	private String getBinderResourcePath(Binder binder) {
		return docRootPath + RepositoryUtil.getBinderPath(binder, Constants.SLASH);
	}
	
	private String getFileResourcePath(String entryDirPath, String relativeFilePath) {
		// Because entryDirPath always ends with slash, we must ensure that
		// no extra slash is put in between. 
		if(relativeFilePath.startsWith(Constants.SLASH))
			relativeFilePath = relativeFilePath.substring(1);
		
		return entryDirPath + relativeFilePath;
	}
	
	private String getFileResourcePath(Binder binder, DefinableEntity entry, 
			String relativeFilePath) {
		return getFileResourcePath(getEntityResourcePath(binder, entry), relativeFilePath);
	}
	
	private String getFileResourceParentPath(String fileResourcePath) {
		return fileResourcePath.substring(0, fileResourcePath.lastIndexOf(Constants.SLASH) + 1);
	}
	
	public RepositorySessionFactory getFactory() {
		return factory;
	}
}
