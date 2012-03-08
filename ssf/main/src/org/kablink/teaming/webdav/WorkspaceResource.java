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
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.NoWorkspaceByTheIdException;
import org.kablink.teaming.domain.Workspace;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.security.AccessControlException;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.exceptions.BadRequestException;
import com.bradmcevoy.http.exceptions.NotAuthorizedException;

/**
 * @author jong
 *
 */
public class WorkspaceResource extends BinderResource  implements PropFindableResource, GetableResource, CollectionResource {
	
	private static final Log logger = LogFactory.getLog(WorkspaceResource.class);

	// lazy resolved for efficiency, so may be null initially
	private Workspace ws;
	
	public WorkspaceResource(WebdavResourceFactory factory, Workspace ws) {
		super(factory, ws);
		this.ws = ws; // already resolved
	}

	public WorkspaceResource(WebdavResourceFactory factory, BinderIndexData bid) {
		super(factory, bid);
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#child(java.lang.String)
	 */
	@Override
	public Resource child(String childName) throws NotAuthorizedException,
			BadRequestException {
		try {
			Binder child = getBinderModule().getBinderByPathName(path + "/" + childName);
			return makeResourceFromBinder(child);
		}
		catch(AccessControlException e) {
			// The specified child physically exists, but the user has no read access to it.
			// In this case, we treat it as if the child didn't exist in the first place
			// as opposed to throwing an error.
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.CollectionResource#getChildren()
	 */
	@Override
	public List<? extends Resource> getChildren()
			throws NotAuthorizedException, BadRequestException {
		// A workspace can have other workspaces and/or folders as children
		Map<String,BinderIndexData> childrenMap = getBinderModule().getChildrenBinderDataFromIndex(id);
		List<Resource> childrenResources = new ArrayList<Resource>(childrenMap.size());
		Resource resource;
		for(BinderIndexData bid:childrenMap.values()) {
			resource = makeResourceFromBinder(bid);
			if(resource != null)
				childrenResources.add(resource);
		}
		return childrenResources;
	}

	private Workspace resolveWorkspace() throws NoWorkspaceByTheIdException {
		if(ws == null) {
			// Load it directly from DAO without further access check, since access check
			// was already performed at the time this instance was created. Resource object
			// is created only after the system determines by looking up the database or
			// Lucene index that the user making request has read access to the resource.
			//ws = getWorkspaceModule().getWorkspace(entityIdentifier.getEntityId());
			ws = (Workspace) getCoreDao().loadBinder(id,  RequestContextHolder.getRequestContext().getZoneId());
	        if(ws == null || ws.isDeleted() || ws.isPreDeleted())
	        	throw new NoWorkspaceByTheIdException(id);
		}
		return ws;
	}
	
}
