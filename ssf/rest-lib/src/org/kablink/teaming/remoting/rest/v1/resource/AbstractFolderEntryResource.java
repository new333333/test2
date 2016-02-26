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

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.*;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.rest.v1.annotations.Undocumented;
import org.kablink.teaming.rest.v1.model.Reply;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.rest.v1.model.SearchResultTree;
import org.kablink.teaming.rest.v1.model.SearchResultTreeNode;
import org.kablink.teaming.rest.v1.model.Tag;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.TrashHelper;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: david
 * Date: 9/13/12
 * Time: 3:57 PM
 */
abstract public class AbstractFolderEntryResource  extends AbstractDefinableEntityResource {

	@DELETE
    @Path("{id}")
	public void deleteFolderEntry(@PathParam("id") long id,
                                  @QueryParam("purge") @DefaultValue("false") boolean purge,
                                  @QueryParam("version") Integer lastVersionNumber
                                  ) throws Exception {
        org.kablink.teaming.domain.FolderEntry folderEntry = _getFolderEntry(id);
        if (lastVersionNumber!=null) {
            FileAttachment fa = folderEntry.getPrimaryFileAttachment();
            if (fa!=null && !isFileVersionCorrect(fa, lastVersionNumber, null, null)) {
                throw new ConflictException(ApiErrorCode.FILE_VERSION_CONFLICT, "Specified version number does not reflect the current state of the file",
                        ResourceUtil.buildFileProperties(fa));
            }
        }
        if (purge || folderEntry.getParentBinder().isMirrored()) {
            Map options = new HashMap();
            options.put(ObjectKeys.INPUT_OPTION_PROPAGATE_ERRORS, true);
            getFolderModule().deleteEntry(folderEntry.getParentBinder().getId(), id, true, options);
            getCoreDao().clear();
            try {
                folderEntry = _getFolderEntry(id);
                logger.warn("Uh-oh, delete entry " + id + " seemed to succeed but the folder entry still exists in the DB.");
            } catch (NoFolderEntryByTheIdException e) {
                logger.info("Successfully deleted entry " + id + " from the DB.");
            }
        } else {
            TrashHelper.preDeleteEntry(this, folderEntry.getParentBinder().getId(), id);
        }
	}

    @GET
    @Path("{id}/reply_tree")
    public SearchResultTree<Reply> getReplyTree(@PathParam("id") Long id,
                                                @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        SearchResultTree<Reply> tree = new SearchResultTree<Reply>();
        populateReplies(entry, tree, toDomainFormat(descriptionFormatStr));
        return tree;
    }

    @GET
    @Path("{id}/replies")
    public SearchResultList<Reply> getReplies(@PathParam("id") Long id,
                                              @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        List replies = entry.getReplies();
        SearchResultList<Reply> results = new SearchResultList<Reply>();
        for (Object o : replies) {
            org.kablink.teaming.domain.FolderEntry reply = (org.kablink.teaming.domain.FolderEntry) o;
            if (!reply.isPreDeleted()) {
                results.append(ResourceUtil.buildReply(reply, false, toDomainFormat(descriptionFormatStr)));
            }
        }
        return results;
    }

    @POST
    @Path("{id}/replies")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Reply addReply(@PathParam("id") Long id,
                          Reply entry,
                          @QueryParam("description_format") @DefaultValue("text") String descriptionFormatStr)
            throws WriteFilesException, WriteEntryDataException {
        org.kablink.teaming.domain.FolderEntry parent = _getFolderEntry(id);
        if (entry==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The request body must contain a 'Reply' object.");
        }
        if (entry.getDescription()==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'description' value");
        }
        if (entry.getDescription().getText()==null || entry.getDescription().getText().length()==0) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "Missing 'description.text' value");
        }
        if (entry.getTitle()==null) {
            String title = NLT.get("reply.re.title", new String[]{getFolderEntryTitle(parent)});
            entry.setTitle(title);
        }
        String defId = null;
        if (entry.getDefinition()!=null) {
            defId = entry.getDefinition().getId();
        }
        Map options = new HashMap();
      	populateTimestamps(options, entry);
        org.kablink.teaming.domain.FolderEntry newEntry = getFolderModule().addReply(null, id, defId, new RestModelInputData(entry), null, options);
        return ResourceUtil.buildReply(newEntry, true, toDomainFormat(descriptionFormatStr));
    }


    @GET
    @Path("{id}/tags")
    @Undocumented
    public SearchResultList<Tag> getTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        return getEntryTags(entry, false);
    }

    @POST
    @Path("{id}/tags")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Undocumented
    public SearchResultList<Tag> addTag(@PathParam("id") Long id, Tag tag) {
        _getFolderEntry(id);
        org.kablink.teaming.domain.Tag[] tags = getFolderModule().setTag(null, id, tag.getName(), tag.isPublic());
        SearchResultList<Tag> results = new SearchResultList<Tag>();
        for (org.kablink.teaming.domain.Tag tg : tags) {
            results.append(ResourceUtil.buildTag(tg));
        }
        return results;
    }

    @DELETE
    @Path("{id}/tags")
    @Undocumented
    public void deleteTags(@PathParam("id") Long id) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getFolderModule().getTags(entry);
        for (org.kablink.teaming.domain.Tag tag : tags) {
            getFolderModule().deleteTag(null, id, tag.getId());
        }
    }

    @GET
    @Path("{id}/tags/{tagId}")
    @Undocumented
    public Tag getTag(@PathParam("id") Long id, @PathParam("tagId") String tagId) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);
        Collection<org.kablink.teaming.domain.Tag> tags = getFolderModule().getTags(entry);
        for (org.kablink.teaming.domain.Tag tag : tags) {
            if (tag.getId().equals(tagId)) {
                return ResourceUtil.buildTag(tag);
            }
        }
        throw new NoTagByTheIdException(tagId);
    }

    @DELETE
    @Path("{id}/tags/{tagId}")
    @Undocumented
    public void deleteTag(@PathParam("id") Long id, @PathParam("tagId") String tagId) {
        getFolderModule().deleteTag(null, id, tagId);
    }

    private void populateReplies(org.kablink.teaming.domain.FolderEntry entry, SearchResultTreeNode<Reply> node, int descriptionFormat) {
        List replies = entry.getReplies();
        for (Object o : replies) {
            org.kablink.teaming.domain.FolderEntry reply = (org.kablink.teaming.domain.FolderEntry) o;
            if (!reply.isPreDeleted()) {
                SearchResultTreeNode<Reply> childNode = node.addChild(ResourceUtil.buildReply(reply, false, descriptionFormat));
                populateReplies(reply, childNode, descriptionFormat);
            }
        }
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.folderEntry;
    }

    /**
     * Returns the string to use as the title of a folder entry.
     *
     * @param fe
     *
     * @return
     */
    public static String getFolderEntryTitle(FolderEntry fe) {
        String reply = fe.getTitle();
        if (!(MiscUtil.hasString(reply))) {
            FolderEntry feParent = fe.getParentEntry();
            if (null == feParent)
                reply = ("--" + NLT.get("entry.noTitle") + "--");
            else reply = NLT.get("reply.re.title", new String[]{getFolderEntryTitle(feParent)});
        }
        return reply;
    }


}
