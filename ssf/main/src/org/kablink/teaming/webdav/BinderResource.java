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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.module.binder.BinderIndexData;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.PropFindableResource;

/**
 * @author jong
 *
 */
public abstract class BinderResource extends WebdavCollectionResource  implements PropFindableResource, CollectionResource, GetableResource {

	private static final Log logger = LogFactory.getLog(BinderResource.class);
	
	// Required properties
	protected EntityIdentifier entityIdentifier;
	protected String title;
	protected String path;
	protected Date createdDate;
	protected Date modifiedDate;
	protected boolean library;
	protected boolean mirrored;
	
	private BinderResource(WebdavResourceFactory factory, EntityIdentifier entityIdentifier, String title, String path, Date createdDate, Date modifiedDate,
			boolean library, boolean mirrored) {
		super(factory);
		this.entityIdentifier = entityIdentifier;
		this.title = title;
		this.path = path;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.library = library;
		this.mirrored = mirrored;
	}
	
	public BinderResource(WebdavResourceFactory factory, Binder binder) {
		this(factory,
				binder.getEntityIdentifier(),
				binder.getTitle(),
				binder.getPathName(),
				binder.getCreation().getDate(),
				binder.getModification().getDate(),
				binder.isLibrary(),
				binder.isMirrored());
	}
	
	public BinderResource(WebdavResourceFactory factory, BinderIndexData bid) {
		this(factory,
				new EntityIdentifier(bid.getId(), bid.getEntityType()),
				bid.getTitle(),
				bid.getPath(),
				bid.getCreatedDate(),
				bid.getModifiedDate(),
				bid.isLibrary(),
				bid.isMirrored());
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getUniqueId()
	 */
	@Override
	public String getUniqueId() {
		return entityIdentifier.toString();
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getName()
	 */
	@Override
	public String getName() {
		return title;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Resource#getModifiedDate()
	 */
	@Override
	public Date getModifiedDate() {
		return modifiedDate;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.PropFindableResource#getCreateDate()
	 */
	@Override
	public Date getCreateDate() {
		return createdDate;
	}
}
