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
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.util.SPropsUtil;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;

/**
 * @author jong
 *
 */
public class WebdavResourceFactory implements ResourceFactory {

	private Log log = LogFactory.getLog(getClass());
	
	private boolean allowDirectoryBrowsing;
	private long maxAgeSecondsRoot;
	private long maxAgeSecondsDav;
	private long maxAgeSecondsWorkspace;
	private long maxAgeSecondsFolder;
	private long maxAgeSecondsFile;
	
	public WebdavResourceFactory() {
		allowDirectoryBrowsing = SPropsUtil.getBoolean("wd.allow.directory.browsing", true);
		maxAgeSecondsRoot = SPropsUtil.getLong("wd.max.age.seconds.root", 31536000);
		maxAgeSecondsDav = SPropsUtil.getLong("wd.max.age.seconds.root", 86400);
		maxAgeSecondsWorkspace = SPropsUtil.getLong("wd.max.age.seconds.root", 10);
		maxAgeSecondsFolder = SPropsUtil.getLong("wd.max.age.seconds.root", 10);
		maxAgeSecondsFile = SPropsUtil.getLong("wd.max.age.seconds.root", 10);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.ResourceFactory#getResource(java.lang.String, java.lang.String)
	 */
	@Override
	public Resource getResource(String host, String path) {
		log.debug("getResource: " + path);
		
		Path p = Path.path(path);
		
		if(p.isRoot()) {
			return new RootResource(this);
		}
		else if(p.getFirst().equals("dav")) {
			if(p.getLength() == 1) {
				return new DavResource(this);
			}
			else {
				Object obj = resolvePath(p);
				if(obj instanceof FileAttachment) {
					return new FileResource(this, (FileAttachment)obj);
				}
				else if(obj instanceof Folder) {
					return new FolderResource(this, (Folder)obj);
				}
				else if(obj instanceof Workspace) {
					return new WorkspaceResource(this, (Workspace)obj);
				}
				else {
					return null;
				}
			}
		}
		else {
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
		return allowDirectoryBrowsing;
	}

	public long getMaxAgeSecondsRoot() {
		return maxAgeSecondsRoot;
	}

	public long getMaxAgeSecondsDav() {
		return maxAgeSecondsDav;
	}

	public long getMaxAgeSecondsWorkspace() {
		return maxAgeSecondsWorkspace;
	}

	public long getMaxAgeSecondsFolder() {
		return maxAgeSecondsFolder;
	}

	public long getMaxAgeSecondsFile() {
		return maxAgeSecondsFile;
	}

	protected Object resolvePath(Path path) {
		return null; // $$$
	}
}
