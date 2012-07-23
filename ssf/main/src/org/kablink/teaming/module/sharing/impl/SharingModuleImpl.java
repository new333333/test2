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
package org.kablink.teaming.module.sharing.impl;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContext;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Dashboard;
import org.kablink.teaming.domain.DashboardPortlet;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.EntityDashboard;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.ShareItemMember;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserDashboard;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.dashboard.DashboardModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.impl.CommonDependencyInjection;
import org.kablink.teaming.module.shared.AccessUtils;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.security.AccessControlException;
import org.kablink.teaming.security.function.OperationAccessControlExceptionNoName;
import org.kablink.teaming.security.function.WorkAreaOperation;
import org.kablink.teaming.util.InvokeUtil;
import org.kablink.teaming.util.ObjectPropertyNotFoundException;
import org.kablink.util.Validator;

/**
 * This module gives us the transaction semantics to deal with the "Shared with Me" features.  
 *
 * @author Peter Hurley
 *
 */
public class SharingModuleImpl extends CommonDependencyInjection implements SharingModule {

	private FolderModule folderModule;
	private BinderModule binderModule;
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#addShareItem(org.kablink.teaming.domain.ShareItem)
	 */
    //RW transaction
	@Override
	public void addShareItem(ShareItem shareItem) {
		// Access check?
		Collection<ShareItemMember> members = shareItem.getMembers();
		for(ShareItemMember member:members) {
			member.setShareItem(shareItem);
		}
		getCoreDao().save(shareItem);
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#modifyShareItem(org.kablink.teaming.domain.ShareItem)
	 */
    //RW transaction
	@Override
	public void modifyShareItem(ShareItem shareItem) {
		// Access check?
		// This should handle both persistent and detached instance.
		Collection<ShareItemMember> members = shareItem.getMembers();
		for(ShareItemMember member:members) {
			member.setShareItem(shareItem);
		}
		getCoreDao().update(shareItem);
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#deleteShareItem(org.kablink.teaming.domain.ShareItem)
	 */
    //RW transaction
	@Override
	public void deleteShareItem(Long shareItemId) {
		// Access check?
		try {
			ShareItem shareItem = getProfileDao().loadShareItem(shareItemId);
			Collection<ShareItemMember> members = shareItem.getMembers();
			for(ShareItemMember member:members) {
				member.setShareItem(null);
			}
			getCoreDao().delete(shareItem);
		}
		catch(NoShareItemByTheIdException e) {
			// already gone, ok
		}
	}
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItem(java.lang.Long)
	 */
	@Override
	public ShareItem getShareItem(Long shareItemId)
			throws NoShareItemByTheIdException {
		// Access check?
		return getProfileDao().loadShareItem(shareItemId);
	}
	
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.profile.ProfileModule#getShareItems(java.util.Collection)
	 */
	@Override
	public List<ShareItem> getShareItems(Collection<Long> shareItemIds) {
		// Access check?
		return getProfileDao().loadShareItems(shareItemIds);
	}
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getShareItems(org.kablink.teaming.dao.util.ShareItemSelectSpec)
	 */
	@Override
	public List<ShareItem> getShareItems(ShareItemSelectSpec selectSpec) {
		// Access check?
		return getProfileDao().findShareItems(selectSpec);
	}
    
	/* (non-Javadoc)
	 * @see org.kablink.teaming.module.sharing.SharingModule#getSharedEntity(org.kablink.teaming.domain.ShareItem)
	 */
	@Override
	public DefinableEntity getSharedEntity(ShareItem shareItem) {
		EntityIdentifier.EntityType entityType = shareItem.getSharedEntityIdentifier().getEntityType();
		if(entityType == EntityIdentifier.EntityType.folderEntry) {
			return getFolderModule().getEntry(null, shareItem.getSharedEntityIdentifier().getEntityId());
		}
		else if(entityType == EntityIdentifier.EntityType.folder || entityType == EntityIdentifier.EntityType.workspace) {
			return getBinderModule().getBinder(shareItem.getSharedEntityIdentifier().getEntityId());
		}
		else {
			throw new IllegalArgumentException("Unsupported entity type '" + entityType.name() + "' for sharing");
		}
	}

	protected FolderModule getFolderModule() {
		return folderModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}

	protected BinderModule getBinderModule() {
		return binderModule;
	}

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}

}
