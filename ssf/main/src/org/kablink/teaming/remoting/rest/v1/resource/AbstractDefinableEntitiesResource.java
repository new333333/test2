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
package org.kablink.teaming.remoting.rest.v1.resource;

import org.kablink.teaming.NoObjectByTheIdException;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.Permission;
import org.kablink.teaming.rest.v1.model.SearchResultList;

import java.util.List;

/**
 * User: david
 * Date: 6/1/12
 * Time: 11:11 AM
 */
public abstract class AbstractDefinableEntitiesResource extends AbstractResource {
    protected SearchResultList<Permission> testBinderPermissions(EntityIdentifier.EntityType entityType, BinderModule.BinderOperation operation, List<Long> binderIds) {
        SearchResultList<Permission> permissions = new SearchResultList<Permission>();
        for(Long binderId : binderIds) {
            Permission permission = new Permission(ResourceUtil.buildEntityId(entityType, binderId), null);
            try {
                // Do not use BinderModule.getBinder() method to load the folder, since it will
                // fail if the caller doesn't already have the appropriate right to load it.
                Binder binder = getBinderModule().getBinderWithoutAccessCheck(binderId);
                if (entityType==binder.getEntityType()) {
                    permission.setPermission(getBinderModule().testAccess(binder, operation));
                } else {
                    permission.setPermission(false);
                    permission.setFound(false);
                }
            } catch(NoBinderByTheIdException e) {
                // The specified folder does not exist. Instead of throwing an exception (and
                // aborting this operation all together), simply set the result to false for
                // this folder, and move on to the next folder.
                permission.setPermission(false);
                permission.setFound(false);
            }
            permissions.append(permission);
        }
        return permissions;
    }

    protected SearchResultList<Permission> testFolderPermissions(EntityIdentifier.EntityType entityType, FolderModule.FolderOperation operation, List<Long> folderIds) {
        SearchResultList<Permission> permissions = new SearchResultList<Permission>();
        for(Long folderId : folderIds) {
            Permission permission = new Permission(ResourceUtil.buildEntityId(entityType, folderId), null);
            try {
                // Do not use FolderModule.getFolder() method to load the folder, since it will
                // fail if the caller doesn't already have the appropriate right to load it.
                if (entityType==EntityIdentifier.EntityType.folder) {
                    Folder folder = getFolderModule().getFolderWithoutAccessCheck(folderId);
                    permission.setPermission(getFolderModule().testAccess(folder, operation));
                } else if (entityType==EntityIdentifier.EntityType.folderEntry) {
                    FolderEntry folderEntry = getFolderModule().getEntryWithoutAccessCheck(null, folderId);
                    permission.setPermission(getFolderModule().testAccess(folderEntry, operation));
                }
            } catch(NoObjectByTheIdException e) {
                // The specified folder does not exist. Instead of throwing an exception (and
                // aborting this operation all together), simply set the result to false for
                // this folder, and move on to the next folder.
                permission.setPermission(false);
                permission.setFound(false);
            }
            permissions.append(permission);
        }
        return permissions;
    }

    protected BinderModule.BinderOperation getBinderOperation(String name) {
        try {
            return BinderModule.BinderOperation.valueOf(name);
        } catch (IllegalArgumentException e) {
        }
        return null;
    }

    protected FolderModule.FolderOperation getFolderOperation(String name) {
        try {
            return FolderModule.FolderOperation.valueOf(name);
        } catch (IllegalArgumentException e) {
        }
        return null;
    }
}
