/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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

package org.kablink.teaming.webdav;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.SimpleName;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.util.AbstractAllModulesInjected;
import org.kablink.teaming.util.ReflectHelper;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.webdav.util.WebdavUtils;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.SecurityManager;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;
import com.ettrema.http.fs.LockManager;

/**
 * @author jong
 *
 */
public class WebdavResourceFactory extends AbstractAllModulesInjected implements ResourceFactory {

	private static final Log logger = LogFactory.getLog(WebdavResourceFactory.class);
	
	private static final String DEFAULT_LOCK_MANAGER_CLASS_NAME = "org.kablink.teaming.webdav.WebdavLockManager";
	private static final String DEFAULT_SECURITY_MANAGER_CLASS_NAME = "org.kablink.teaming.webdav.WebdavSecurityManager";
	
	private volatile boolean inited = false;
	
	private static final Long DEFAULT_MAX_AGE_SECONDS_STATIC = 3600L;
	private static final Long DEFAULT_MAX_AGE_SECONDS_WORKSPACE = 20L;
	private static final Long DEFAULT_MAX_AGE_SECONDS_FOLDER = 10L;
	private static final Long DEFAULT_MAX_AGE_SECONDS_FILE = 10L;
	private static final Long DEFAULT_MAX_AGE_SECONDS_EIP_FILE = null;
	
	private static final String DEFAULT_MY_FILES_PREFIX = "my_files";
	private static final String DEFAULT_NET_FOLDERS_PREFIX = "net_folders";
	private static final String DEFAULT_SHARED_WITH_ME_PREFIX = "shared_with_me";
	
	private boolean allowDirectoryBrowsing = true;
	
	private Long maxAgeSecondsStatic = DEFAULT_MAX_AGE_SECONDS_STATIC;
	private Long maxAgeSecondsWorkspace = DEFAULT_MAX_AGE_SECONDS_WORKSPACE;
	private Long maxAgeSecondsFolder = DEFAULT_MAX_AGE_SECONDS_FOLDER;
	private Long maxAgeSecondsFile = DEFAULT_MAX_AGE_SECONDS_FILE;
	private Long maxAgeSecondsEipFile = DEFAULT_MAX_AGE_SECONDS_EIP_FILE;
	
	private String myFilesPrefix = DEFAULT_MY_FILES_PREFIX;
	private String netFoldersPrefix = DEFAULT_NET_FOLDERS_PREFIX;
	private String sharedWithMePrefix = DEFAULT_SHARED_WITH_ME_PREFIX;
	
	private LockManager lockManager;
	private SecurityManager securityManager;
	
	public WebdavResourceFactory() {}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResourceFactory#getResource(java.lang.String, java.lang.String)
	 */
	@Override
	public Resource getResource(String host, String path) {
		logger.debug("getResource: " + path);
		
		if(!inited)
			init();
		
		Path p = Path.path(path);
		
		if(p.isRoot()) {
			return new RootResource(this);
		}
		else if(p.getFirst().equals("dav") && Utils.checkIfVibe()) { // Allow navigation into "dav" only if running a pure Vibe
			if(p.getLength() == 1) {
				return new DavResource(this);
			}
			else {
				Object obj = resolvePath(p.getStripFirst());
				return vibeObjectToResource(path, obj);
			}
		}
		else if(p.getFirst().equals("dave")) { // edit-in-place
			String[] parts = p.getParts();
			if(parts.length == 1) {
				return new EipResource(this);
			}
			else if(parts.length == 2 || parts.length == 3) {
				String fileId = parts[1];
				FileAttachment fa = getFileModule().getFileAttachmentById(fileId);
				if(fa == null)
					return null; // No such file exists
				if(parts.length == 2) {
					return new EipFileIdResource(this, fa);
				}
				else {
					if(fa.getFileItem().getName().equalsIgnoreCase(parts[2])) // should this check be case sensitive?
						return new EipFileNameResource(this, fa);
					else
						return null; // invalid URL
				}
			}
			else {
				return null; // invalid URL
			}
		}
		else if(p.getFirst().equals("davs")) { // user-defined simple name
			if(p.getLength() == 1) {
				return new SimpleNameResource(this);
			}
			else {
				p = p.getStripFirst(); // remove "/davs"
				String simpleName = p.getFirst();
				SimpleName sn = getBinderModule().getSimpleNameByEmailAddress(simpleName);
				if(sn == null)
					return null; // the simple path is not recognized
				Binder binder;
				try {
					// We don't need access check on this binder. The call to resolvePath() below will do
					// the check on the actual leaf resource the client is trying to access.
					binder = getCoreDao().loadBinder(sn.getBinderId(), RequestContextHolder.getRequestContext().getZoneId());
				}
				catch(NoBinderByTheIdException e) {
					logger.error("Can not load binder associated with simple name '" + simpleName + "'", e); // This shouldn't occur.
					return null;
				}
				Path vibePathForSimpleNamedBinder = Path.path(binder.getPathName());
				Path vibeFullPath = vibePathForSimpleNamedBinder.add(p.getStripFirst());
				Object obj = resolvePath(vibeFullPath);
				return vibeObjectToResource(path, obj);
			}
		}
		else if(p.getFirst().equals(this.getMyFilesPrefix())) { // My Files
			if(!WebdavUtils.userCanAccessMyFiles(this)) {
				return null; // Personal storage is not allowed.
			}
			if(p.getLength() == 1) {
				return new MyFilesResource(this);
			}
			p = p.getStripFirst();
			Resource mfcr = getMyFilesChildResource(p.getFirst());
			if(mfcr == null) {
				return null;
			}
			if(p.getLength() == 1) {
				return mfcr;
			}	
			if(mfcr instanceof FileResource) {
				// File can not have sub-path (i.e., children). This is not a valid situation.
				return null;
			}
			else if(mfcr instanceof BinderResource) {
				String vibePath = ((BinderResource)mfcr).getPath() + p.getStripFirst().toPath();
				Object obj = resolvePath(Path.path(vibePath));
				return vibeObjectToResource(path, obj);
			}
			else {
				return null;
			}
		}
		else if(p.getFirst().equals(this.getNetFoldersPrefix())) { // Net Folders
			if(!WebdavUtils.userCanAccessNetFolders()) {
				return null;
			}
			if(p.getLength() == 1) {
				return new NetFoldersResource(this);
			}
			p = p.getStripFirst();
			Resource mfcr = getNetFoldersChildResource(p.getFirst());
			if(mfcr == null) {
				return null;
			}
			if(p.getLength() == 1) {
				return mfcr;
			}	
			if(mfcr instanceof FileResource) {
				// File can not have sub-path (i.e., children). This is not a valid situation.
				return null;
			}
			else if(mfcr instanceof BinderResource) {
				String vibePath = ((BinderResource)mfcr).getPath() + p.getStripFirst().toPath();
				Object obj = resolvePath(Path.path(vibePath));
				return vibeObjectToResource(path, obj);
			}
			else {
				return null;
			}
		}
		else if(p.getFirst().equals(this.getSharedWithMePrefix())) { // Shared With Me
			if(p.getLength() == 1) {
				return new SharedWithMeResource(this);
			}
			p = p.getStripFirst();
			Resource swmcr = getSharedWithMeChildResource(p.getFirst());
			if(swmcr == null) {
				return null;
			}
			if(p.getLength() == 1) {
				return swmcr;
			}
			if(swmcr instanceof FileResource) {
				// File can not have sub-path (i.e., children). This is not a valid situation.
				return null;
			}
			else if(swmcr instanceof BinderResource) {
				String vibePath = ((BinderResource)swmcr).getPath() + p.getStripFirst().toPath();
				Object obj = resolvePath(Path.path(vibePath));
				return vibeObjectToResource(path, obj);
			}
			else {
				return null;
			}			
		}
		else {
			return null;
		}
	}
	
	private Resource vibeObjectToResource(String path, Object obj) {
		if(obj instanceof FileAttachment) {
			return new FileResource(this, path, (FileAttachment)obj);
		}
		else if(obj instanceof Folder) {
			return new FolderResource(this, path, (Folder)obj);
		}
		else if(obj instanceof Workspace) {
			return new WorkspaceResource(this, path, (Workspace)obj);
		}
		else {
			return null;
		}
	}
	
	private Resource getMyFilesChildResource(String childName) {
		try {
			return new MyFilesResource(this).child(childName);
		} catch (NotAuthorizedException e) {
			return null;
		} catch (BadRequestException e) {
			return null;
		}
	}
	
	private Resource getNetFoldersChildResource(String childName) {
		try {
			return new NetFoldersResource(this).child(childName);
		} catch (NotAuthorizedException e) {
			return null;
		} catch (BadRequestException e) {
			return null;
		}
	}
	
	private Resource getSharedWithMeChildResource(String childName) {
		try {
			return new SharedWithMeResource(this).child(childName);
		} catch (NotAuthorizedException e) {
			return null;
		} catch (BadRequestException e) {
			return null;
		}
	}
	
	/**
	 * Whether to allow generation of a listing of the contents of a binder via GET.
	 * If allowed, user can easily browse the contents of a binder using a browser.
	 *
	 * @return
	 */
	public boolean isAllowDirectoryBrowsing() {
		if(!inited)
			init();
		return allowDirectoryBrowsing;
	}

	public Long getMaxAgeSecondsStatic() {
		if(!inited)
			init();
		return maxAgeSecondsStatic;
	}

	public Long getMaxAgeSecondsWorkspace() {
		if(!inited)
			init();
		return maxAgeSecondsWorkspace;
	}

	public Long getMaxAgeSecondsFolder() {
		if(!inited)
			init();
		return maxAgeSecondsFolder;
	}

	public Long getMaxAgeSecondsFile() {
		if(!inited)
			init();
		return maxAgeSecondsFile;
	}

	public Long getMaxAgeSecondsEipFile() {
		if(!inited)
			init();
		return maxAgeSecondsEipFile;
	}

	public String getMyFilesPrefix() {
		if(!inited)
			init();
		return myFilesPrefix;
	}

	public String getNetFoldersPrefix() {
		if(!inited)
			init();
		return netFoldersPrefix;
	}

	public String getSharedWithMePrefix() {
		if(!inited)
			init();
		return sharedWithMePrefix;
	}

	public LockManager getLockManager() {
		if(!inited)
			init();
		return lockManager;
	}

	public SecurityManager getSecurityManager() {
		if(!inited)
			init();
		return securityManager;
	}
	
	protected Object resolvePath(Path vibePath) {
		String vibePathStr = vibePath.toPath(); // This is effective data path in Vibe
		
		try {
			// Step 1 - Check if this path represents a binder. This also involves ACL checking on the binder.
			Binder binder = getBinderModule().getBinderByPathName(vibePathStr);
			if(binder != null)
				return binder;
			
			// Step 2 - Check if this path represents a file. This also involves ACL checking on the enclosing entry.
			if(vibePath.getLength() == 1)
				return null; // The first element in path can not represent a file, since a file can not exist without a container.
			String leafName = vibePath.getName();
			String internalPath = vibePathStr.substring(0, vibePathStr.lastIndexOf('/'));
			Binder parentBinder = getBinderModule().getBinderByPathName(internalPath); // This performs ACL checking
			if(parentBinder == null)
				return null; // Matching parent binder doesn't exist
			if(!(parentBinder instanceof Folder) || !parentBinder.isLibrary())
				return null; // The parent binder is not a folder or a library folder.
			// See if there is an entry within the parent folder that has the file as a attachment. This also performs ACL checking on the entry if exists.
			FolderEntry entry = getFolderModule().getLibraryFolderEntryByFileName((Folder)parentBinder, leafName);
			if(entry == null)
				return null; // No such entry found
			return entry.getFileAttachment(leafName);
		}
		catch(AccessControlException e) {
			return null; // Don't throw an exception. Treat this as identical to non-existing resource
		}

	}
	
	protected synchronized void init() {
		// Initialize object state here rather than in the constructor, because SPropsUtil bean which 
		// is loaded by the primary Spring context is not yet initialized at the time this object is
		// instantiated.
		if(inited)
			return;
		
		String lockManagerClassName = SPropsUtil.getString("wd.lock.manager.class", DEFAULT_LOCK_MANAGER_CLASS_NAME);
		this.lockManager = (LockManager) ReflectHelper.getInstance(lockManagerClassName);
		
		String securityManagerClassName = SPropsUtil.getString("wd.security.manager.class", DEFAULT_SECURITY_MANAGER_CLASS_NAME);
		this.securityManager = (SecurityManager) ReflectHelper.getInstance(securityManagerClassName);

		allowDirectoryBrowsing = SPropsUtil.getBoolean("wd.allow.directory.browsing", true);
		
		maxAgeSecondsStatic = SPropsUtil.getLongObject("wd.max.age.seconds.static", DEFAULT_MAX_AGE_SECONDS_STATIC);
		maxAgeSecondsWorkspace = SPropsUtil.getLongObject("wd.max.age.seconds.workspace", DEFAULT_MAX_AGE_SECONDS_WORKSPACE);
		maxAgeSecondsFolder = SPropsUtil.getLongObject("wd.max.age.seconds.folder", DEFAULT_MAX_AGE_SECONDS_FOLDER);
		maxAgeSecondsFile = SPropsUtil.getLongObject("wd.max.age.seconds.file", DEFAULT_MAX_AGE_SECONDS_FILE);
		maxAgeSecondsEipFile = SPropsUtil.getLongObject("wd.max.age.seconds.eip.file", DEFAULT_MAX_AGE_SECONDS_EIP_FILE);
		
		myFilesPrefix = SPropsUtil.getString("wd.myfiles.prefix", DEFAULT_MY_FILES_PREFIX);
		netFoldersPrefix = SPropsUtil.getString("wd.netfolders.prefix", DEFAULT_NET_FOLDERS_PREFIX);
		sharedWithMePrefix = SPropsUtil.getString("wd.sharedwithme.prefix", DEFAULT_SHARED_WITH_ME_PREFIX);
		
		logger.info("allowDirectoryBrowsing:" + allowDirectoryBrowsing + 
				" maxAgeSecondsStatic:" + maxAgeSecondsStatic +
				" maxAgeSecondsWorkspace:" + maxAgeSecondsWorkspace +
				" maxAgeSecondsFolder:" + maxAgeSecondsFolder +
				" maxAgeSecondsFile:" + maxAgeSecondsFile +
				" maxAgeSecondsEipFile:" + maxAgeSecondsEipFile + 
				" myFilesPrefix:" + myFilesPrefix +
				" netFoldersPrefix:" + netFoldersPrefix +
				" sharedWithMePrefix:" + sharedWithMePrefix);
		
		inited = true;
	}
	
	protected CoreDao getCoreDao() {
		return (CoreDao) SpringContextUtil.getBean("coreDao");
	}
}
