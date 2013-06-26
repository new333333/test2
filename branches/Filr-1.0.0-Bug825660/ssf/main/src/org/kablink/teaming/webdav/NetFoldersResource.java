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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.file.FileIndexData;
import org.kablink.teaming.search.SearchUtils;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.util.search.Criteria;

import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MakeCollectionableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.PutableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.ConflictException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author jong
 * 
 */
public class NetFoldersResource extends ContainerResource implements
		PropFindableResource, GetableResource, CollectionResource {

	public NetFoldersResource(WebdavResourceFactory factory) {
		super(factory, "/" + factory.getNetFoldersPrefix(), factory.getNetFoldersPrefix());
	}

	@Override
	public String getUniqueId() {
		return this.factory.getNetFoldersPrefix();
	}

	@Override
	public Date getModifiedDate() {
		// $$$We don't have this information for Net Folders container. So, let's
		// just return
		// current time to force WebDAV client to always come and get the latest
		// view.
		// return new Date();

		return getCreateDate();
	}

	@Override
	public Long getMaxAgeSeconds(Auth auth) {
		// Share the setting with regular folders.
		return factory.getMaxAgeSecondsFolder();
	}

	@Override
	public Date getCreateDate() {
		return getMiltonSafeDate(ReleaseInfo.getBuildDate()); // This is as good as any other random date
	}

	@Override
	public Resource child(String childName) throws NotAuthorizedException,
			BadRequestException {
		Criteria netFoldersCrit = SearchUtils.getNetFoldersSearchCriteria(this, false);

		List<BinderIndexData> bidList = getBinderDataFromIndex(netFoldersCrit, true, SearchUtils.getNetFoldersRootBinder());
		//Remove any net folder that the user does not have direct access to
		SearchUtils.removeNetFoldersWithNoRootAccess2(bidList);
		
		for (BinderIndexData bid : bidList) {
			if (bid.getTitle().equals(childName))
				return makeResourceFromBinder(bid);
		}

		return null;
	}

	@Override
	public List<? extends Resource> getChildren()
			throws NotAuthorizedException, BadRequestException {
		List<Resource> childrenResources = new ArrayList<Resource>();
		Resource resource;
		
		Criteria netFoldersCrit = SearchUtils.getNetFoldersSearchCriteria(this, false);

		List<BinderIndexData> bidList = getBinderDataFromIndex(netFoldersCrit, true, SearchUtils.getNetFoldersRootBinder());
		//Remove any net folder that the user does not have direct access to
		SearchUtils.removeNetFoldersWithNoRootAccess2(bidList);
		
		for (BinderIndexData bid : bidList) {
			resource = makeResourceFromBinder(bid);
			if (resource != null)
				childrenResources.add(resource);
		}

		return childrenResources;
	}

}
