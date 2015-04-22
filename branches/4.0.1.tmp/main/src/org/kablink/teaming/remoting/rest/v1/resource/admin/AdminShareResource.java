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
package org.kablink.teaming.remoting.rest.v1.resource.admin;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.dao.util.NetFolderSelectSpec;
import org.kablink.teaming.dao.util.ShareItemSelectSpec;
import org.kablink.teaming.domain.BinderState;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.NoShareItemByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ResourceDriverConfig;
import org.kablink.teaming.domain.ShareItem;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.util.AdminResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.Share;
import org.kablink.teaming.rest.v1.model.admin.NetFolder;
import org.kablink.teaming.rest.v1.model.admin.NetFolderSyncStatus;
import org.kablink.teaming.web.util.NetFolderHelper;
import org.kablink.util.Pair;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("/admin/shares")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class AdminShareResource extends AbstractAdminResource {

    @GET
    public SearchResultList<Share> getShares(@QueryParam("shared_by") Long sharedById,
                                             @QueryParam("shared_with") Long sharedWithId,
                                             @QueryParam("include_expired") @DefaultValue("false") Boolean includeExpired) {
        ShareItemSelectSpec spec = new ShareItemSelectSpec();
        if (sharedById!=null) {
            spec.setSharerId(sharedById);
        }
        if (sharedWithId!=null) {
            Principal entry = getProfileModule().getEntry(sharedWithId, true);
            if (entry instanceof User) {
                spec.setRecipients(sharedWithId, null, null);
            } else if (entry instanceof Group) {
                spec.setRecipients(null, sharedWithId, null);
            } else {
                // Assume team?
                spec.setRecipients(null, null, sharedWithId);
            }
        }
        spec.setLatest(true);
        return getShareSearchResultList(spec, includeExpired, true, true);
    }

    @GET
    @Path("/public")
    public SearchResultList<Share> getAllPublicShares() {
        User guest = getProfileModule().getGuestUser();
        if (null != guest) {
            // Yes!  Read the shares that have been made with Guest.
            ShareItemSelectSpec	spec = new ShareItemSelectSpec();
            spec.setRecipients(guest.getId(), null, null);
            spec.setLatest(true);
            return getShareSearchResultList(spec, false, true, false);
        }
        return new SearchResultList<Share>();
    }

    @GET
    @Path("/{id}")
    public Share getShare(@PathParam("id") Long id) {
        ShareItem share = _getShareItem(id);
        DefinableEntity definableEntity = findDefinableEntity(share.getSharedEntityIdentifier());
        if (definableEntity!=null) {
            throw new NoShareItemByTheIdException(id);
        }
        return AdminResourceUtil.buildShare(share, definableEntity, buildShareRecipient(share), isGuestAccessEnabled());
    }

    @DELETE
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public void deleteShare(@PathParam("id") Long id) {
        List<Pair<ShareItem, DefinableEntity>> origItems;
        ShareItem item = _getShareItem(id);
        if (item.getIsPartOfPublicShare()) {
            origItems = getAllPublicShareParts(item);
        } else {
            origItems = new ArrayList<Pair<ShareItem, DefinableEntity>>(1);
            origItems.add(new Pair<ShareItem, DefinableEntity>(item, null));
        }
        for (Pair<ShareItem, DefinableEntity> origItem : origItems) {
            getSharingModule().deleteShareItem(origItem.getA().getId());
        }
    }

    private ShareItem _getShareItem(Long id) {
        ShareItem share = getSharingModule().getShareItem(id);
        if (share.isDeleted() || !share.isLatest()) {
            // Don't allow the user to modify a share that is not the latest version of the share, or that was shared
            // by someone else.
            throw new NoShareItemByTheIdException(id);
        }
        return share;
    }

    private SearchResultList<Share> getShareSearchResultList(ShareItemSelectSpec spec, boolean includeExpired, boolean includePublic, boolean includeNonPublic) {
        List<Pair<ShareItem, DefinableEntity>> shareItems = getShareItems(spec, null, includeExpired, includePublic, includeNonPublic);
        SearchResultList<Share> results = new SearchResultList<Share>();
        boolean guestAccessEnabled = isGuestAccessEnabled();
        for (Pair<ShareItem, DefinableEntity> pair : shareItems) {
            results.append(AdminResourceUtil.buildShare(pair.getA(), pair.getB(), buildShareRecipient(pair.getA()), guestAccessEnabled));
        }
        return results;
    }

}
