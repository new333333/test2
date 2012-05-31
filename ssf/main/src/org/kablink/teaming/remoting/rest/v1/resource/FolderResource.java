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
package org.kablink.teaming.remoting.rest.v1.resource;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.NoBinderByTheIdException;
import org.kablink.teaming.domain.TemplateBinder;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.v1.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.v1.exc.NotFoundException;
import org.kablink.teaming.remoting.rest.v1.util.FolderEntryBriefBuilder;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.remoting.rest.v1.util.RestModelInputData;
import org.kablink.teaming.remoting.rest.v1.util.SearchResultBuilderUtil;
import org.kablink.teaming.rest.v1.model.BinderBrief;
import org.kablink.teaming.rest.v1.model.FolderEntry;
import org.kablink.teaming.rest.v1.model.FolderEntryBrief;
import org.kablink.teaming.rest.v1.model.SearchResultList;
import org.kablink.teaming.search.filter.SearchFilter;
import org.kablink.teaming.util.SimpleProfiler;
import org.kablink.util.api.ApiErrorCode;

@Path("/v1/folder/{id}")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FolderResource extends AbstractBinderResource {

	// Read sub-folders
	@GET
	@Path("folders")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<BinderBrief> getSubFolders(@PathParam("id") long id) {
        SearchFilter filter = new SearchFilter();
        filter.addFolderFilter("");
        return getSubBinders(id, filter);
	}
	
    @POST
   	@Path("folders")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public org.kablink.teaming.rest.v1.model.Binder createSubFolder(@PathParam("id") long id, org.kablink.teaming.rest.v1.model.Binder binder, @QueryParam("template") Long templateId)
               throws WriteFilesException, WriteEntryDataException {
        if (templateId!=null) {
            TemplateBinder template = getTemplateModule().getTemplate(templateId);
            if (EntityIdentifier.EntityType.folder != template.getEntityType()) {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "The specified 'template' parameter must be a folder template.");
            }
        }
        return createBinder(id, binder, templateId);
    }

	// Read entries
	@GET
	@Path("entries")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public SearchResultList<FolderEntryBrief> getFolderEntries(@PathParam("id") long id,
                                                            @QueryParam("first") Integer offset,
                                                            @QueryParam("count") Integer maxCount,
                                                            @QueryParam("file_name") String fileName) {
        SearchResultList<FolderEntryBrief> results = new SearchResultList<FolderEntryBrief>();
        Folder folder = _getFolder(id);
        if (fileName!=null) {
            if (folder.isLibrary()) {
                org.kablink.teaming.domain.FolderEntry folderEntry = getFolderModule().getLibraryFolderEntryByFileName(folder, fileName);
                if (folderEntry!=null) {
                    results.append(ResourceUtil.buildFolderEntryBrief(folderEntry));
                }
            } else {
                throw new InternalServerErrorException(ApiErrorCode.NOT_SUPPORTED, "Searching for folder entries with file names is only supported on library folders.");
            }
        } else {
            Map<String, Object> options = new HashMap<String, Object>();
            if (offset!=null) {
                options.put(ObjectKeys.SEARCH_OFFSET, offset);
            } else {
                offset = 0;
            }
            if (maxCount!=null) {
                options.put(ObjectKeys.SEARCH_MAX_HITS, maxCount);
            }
            Map resultMap = getFolderModule().getEntries(id, options);
            results.setFirst(offset);
            SearchResultBuilderUtil.buildSearchResults(results, new FolderEntryBriefBuilder(), resultMap, "/folder/" + id + "/entries", offset);
        }
		return results;
	}

    @POST
   	@Path("entries")
    @Consumes({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
   	public FolderEntry createFolderEntry(@PathParam("id") long id,
                                         @QueryParam("file_entry") @DefaultValue("false") boolean fileEntry,
                                         FolderEntry entry) throws WriteFilesException, WriteEntryDataException {
        Folder folder = _getFolder(id);

        String defId;
        if (entry.getDefinition()==null) {
            Definition def;
            if (fileEntry) {
                def = folder.getDefaultFileEntryDef();
            } else {
                def = folder.getDefaultEntryDef();
            }
            if  (def!=null) {
                defId = def.getId();
            } else {
                throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No definition was supplied in the POST data.");
            }
        } else {
            defId = entry.getDefinition().getId();
        }
        if (defId==null) {
            throw new BadRequestException(ApiErrorCode.BAD_INPUT, "No definition id was supplied in the POST data.");
        }
        SimpleProfiler.start("REST_folder_createFolderEntry");
        HashMap options = new HashMap();
        populateTimestamps(options, entry);
        org.kablink.teaming.domain.FolderEntry result = getFolderModule().addEntry(id, defId, new RestModelInputData(entry), null, options);
        SimpleProfiler.stop("REST_folder_createFolderEntry");
        return ResourceUtil.buildFolderEntry(result, true);
    }

    @Override
    protected Binder _getBinder(long id) {
        return _getFolder(id);
    }

    private org.kablink.teaming.domain.Folder _getFolder(long id) {
        try{
            org.kablink.teaming.domain.Binder binder = getBinderModule().getBinder(id);
            if (binder instanceof org.kablink.teaming.domain.Folder) {
                Folder folder = (Folder) binder;
                if (!folder.isPreDeleted()) {
                    return folder;
                }
            }
        } catch (NoBinderByTheIdException e) {
            // Throw exception below.
        }
        throw new NotFoundException(ApiErrorCode.FOLDER_NOT_FOUND, "NOT FOUND");
    }

    @Override
    EntityIdentifier.EntityType _getEntityType() {
        return EntityIdentifier.EntityType.folder;
    }
}
