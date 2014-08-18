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

import static org.kablink.util.search.Restrictions.conjunction;
import static org.kablink.util.search.Restrictions.eq;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.BinderIndexData;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.MapInputData;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.util.search.Constants;
import org.kablink.util.search.Criteria;

import com.bradmcevoy.http.CollectionResource;
import com.bradmcevoy.http.CopyableResource;
import com.bradmcevoy.http.DeletableCollectionResource;
import com.bradmcevoy.http.DeletableResource;
import com.bradmcevoy.http.GetableResource;
import com.bradmcevoy.http.MakeCollectionableResource;
import com.bradmcevoy.http.MoveableResource;
import com.bradmcevoy.http.PropFindableResource;
import com.bradmcevoy.http.Request;

/**
 * @author jong
 *
 */
public abstract class BinderResource extends ContainerResource  
implements PropFindableResource, GetableResource, CollectionResource, MakeCollectionableResource, DeletableResource, CopyableResource, MoveableResource, DeletableCollectionResource {

	// Required properties
	protected Long id;
	protected EntityType entityType;
	protected String path;
	protected Date createdDate;
	protected Date modifiedDate;
	protected boolean library;
	protected boolean mirrored;

	private EntityIdentifier entityIdentifier;

	private BinderResource(WebdavResourceFactory factory, String webdavPath, EntityIdentifier entityIdentifier, String title, String path, Date createdDate, Date modifiedDate,
			boolean library, boolean mirrored) {
		super(factory, webdavPath, title);
		this.id = entityIdentifier.getEntityId();
		this.entityType = entityIdentifier.getEntityType();
		this.entityIdentifier = entityIdentifier;
		this.path = path;
		this.createdDate = getMiltonSafeDate(createdDate);
		this.modifiedDate = getMiltonSafeDate(modifiedDate);
		this.library = library;
		this.mirrored = mirrored;
	}
	
	public BinderResource(WebdavResourceFactory factory, String webdavPath, Binder binder) {
		this(factory,
				webdavPath,
				binder.getEntityIdentifier(),
				binder.getTitle(),
				binder.getPathName(),
				binder.getCreation().getDate(),
				binder.getModification().getDate(),
				binder.isLibrary(),
				binder.isMirrored());
	}
	
	public BinderResource(WebdavResourceFactory factory, String webdavPath, BinderIndexData bid) {
		this(factory,
				webdavPath,
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

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.DeletableCollectionResource#isLockedOutRecursive(com.bradmcevoy.http.Request)
	 */
	@Override
	public boolean isLockedOutRecursive(Request request) {
		// For now, always return false without checking actual locks on individual files
		// so that efficient version of delete can proceed.
		// In the current system this is expensive to implement and execute, but it's no 
		// worse than the browser interface since both interface proceed with binder
		// deletion without first checking any locks that might exist.
		return false;
	}

	// Return Vibe path
	public String getPath() {
		return path;
	}
	
	public Long getBinderId() {
		return id;
	}
	
	public EntityIdentifier getEntityIdentifier() {
		return entityIdentifier;
	}
	
	protected void renameBinder(Binder binder, String newTitle) 
			throws AccessControlException, WriteFilesException, WriteEntryDataException {
		// Do this only if the new title is actually different from the current value.
		if(binder != null && !newTitle.equals(binder.getTitle())) {
			Map data = new HashMap();
			data.put("title", newTitle);
			getBinderModule().modifyBinder(binder.getId(), new MapInputData(data), null, null, null);
		}
	}
	
	/**
	 * Returns a map of titles of the sub-binders contained in the specified binder 
	 * to its <code>BinderIndexData</code> objects encapsulating more detailed information
	 * about sub-binders obtained from the Lucene index.
	 * It is important for the efficiency reason that the requested data be obtainable
	 * entirely from the Lucene index without querying the database.
	 * 
	 * @param binderId
	 * @return
	 */
	protected Map<String,BinderIndexData> getChildrenBinderDataFromIndex(Long binderId) {
    	Criteria crit = new Criteria()
    	    .add(conjunction()	
    			.add(eq(Constants.BINDERS_PARENT_ID_FIELD, binderId.toString()))
   				.add(eq(Constants.DOC_TYPE_FIELD,Constants.DOC_TYPE_BINDER))
     		);

    	List<BinderIndexData> results = getBinderDataFromIndex(crit, true, loadBinder(binderId));
    	Map<String,BinderIndexData> resultsMap = new HashMap<String,BinderIndexData>();
    	for(BinderIndexData data : results) {
    		resultsMap.put(data.getTitle(), data);
    	}
    	
    	return resultsMap;
	}

	private Binder loadBinder(Long binderId) {
		return getCoreDao().loadBinder(binderId, RequestContextHolder.getRequestContext().getZoneId());
	}
}
