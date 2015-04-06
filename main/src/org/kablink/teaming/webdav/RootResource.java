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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.module.license.LicenseChecker;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.webdav.util.WebdavUtils;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.Request.Method;
import com.bradmcevoy.http.Resource;

/**
 * @author jong
 *
 */
public class RootResource extends WebdavCollectionResource implements PropFindableResource, GetableResource, CollectionResource {

	private static final String ID = "root";
	
	public RootResource(WebdavResourceFactory factory) {
		super(factory, "/", "");
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		return getCreateDate();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		return getMiltonSafeDate(ReleaseInfo.getBuildDate()); // This is as good as any other random date
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.GetableResource#getMaxAgeSeconds(com.bradmcevoy.http.Auth)
	 */
	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		return factory.getMaxAgeSecondsStatic();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#authenticate(java.lang.String, java.lang.String)
	 */
	@Override
	public Object authenticate(String user, String password) {
		return "";
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#authorise(com.bradmcevoy.http.Request, com.bradmcevoy.http.Request.Method, com.bradmcevoy.http.Auth)
	 */
	@Override
	public boolean authorise(Request request, Method method, Auth auth) {
		return true;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getRealm()
	 */
	@Override
	public String getRealm() {
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#child(java.lang.String)
	 */
	@Override
	public Resource child(String childName) {
		// Allow navigation built on physical path only if the product is a pure Vibe
		if(DavResource.ID.equals(childName) && Utils.checkIfVibe())
			return new DavResource(factory);
		else if(this.factory.getMyFilesPrefix().equals(childName) && WebdavUtils.userCanAccessMyFiles(this))
			return new MyFilesResource(factory);
		else if(this.factory.getNetFoldersPrefix().equals(childName) && WebdavUtils.userCanAccessNetFolders())
			return new NetFoldersResource(factory);
		else if(this.factory.getSharedWithMePrefix().equals(childName))
			return new SharedWithMeResource(factory);
		else
			return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#getChildren()
	 */
	@Override
	public List<? extends Resource> getChildren() {
		List<Resource> list = new ArrayList<Resource>();
		// Enable navigation built on physical path only if the product is a pure Vibe
		if(Utils.checkIfVibe())
			list.add(new DavResource(factory));
		if(WebdavUtils.userCanAccessMyFiles(this))
			list.add(new MyFilesResource(factory));
		if(LicenseChecker.isAuthorizedByLicense(ObjectKeys.LICENSE_OPTION_FILR, true) && WebdavUtils.userCanAccessNetFolders())
			list.add(new NetFoldersResource(factory));
		list.add(new SharedWithMeResource(factory));
		return list;
	}

}
