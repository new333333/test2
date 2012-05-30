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

import javax.servlet.http.HttpServletRequest;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.EntityIdentifier;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.NoFolderEntryByTheIdException;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.BaseFileProperties;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.FolderEntry;

import org.kablink.teaming.rest.v1.model.SearchResultList;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

@Path("/v1/folder_entry/{id}")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FolderEntryResource extends AbstractFileResource {
		
	@Context UriInfo uriInfo;
	
	// Read folder entry
	@GET
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public FolderEntry getFolderEntry(
			@PathParam("id") long id,
            @QueryParam("include_attachments") @DefaultValue("true") boolean includeAttachments) {
        org.kablink.teaming.domain.FolderEntry hEntry = _getFolderEntry(id);
		return ResourceUtil.buildFolderEntry(hEntry, includeAttachments);
	}

    // Update folder entry
	@PUT
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
	public Response putFolderEntry(@PathParam("id") long id) {
		return null;
	}

	// Delete folder entry
	@DELETE
	public void deleteFolderEntry(@PathParam("id") long id) {
        org.kablink.teaming.domain.FolderEntry folderEntry = _getFolderEntry(id);
        getFolderModule().preDeleteEntry(folderEntry.getParentBinder().getId(), id, getLoggedInUserId());
	}

    @GET
    @Path("files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public SearchResultList<BaseFileProperties> getAttachments(@PathParam("id") long id) {
        org.kablink.teaming.domain.FolderEntry entry = _getFolderEntry(id);

        Set<Attachment> attachments = entry.getAttachments();
        SearchResultList<BaseFileProperties> props = new SearchResultList<BaseFileProperties>();
        for (Attachment attachment : attachments) {
            if (attachment instanceof VersionAttachment) {
                props.append(ResourceUtil.fileVersionFromFileAttachment((VersionAttachment) attachment));
            } else if (attachment instanceof FileAttachment) {
                props.append(ResourceUtil.buildFileProperties((FileAttachment) attachment));
            }
        }
		return props;
    }

    @POST
    @Path("files")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties postAttachment_Multipart(@PathParam("id") long id,
                               @QueryParam("file_name") String fileName,
                               @QueryParam("data_name") String dataName,
       			               @QueryParam("mod_date") String modDateISO8601,
                               @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        //TODO: make sure a file with that name doesn't exist
        InputStream is = getInputStreamFromMultipartFormdata(request);
        try {
            return writeNewFileContent(EntityIdentifier.EntityType.folderEntry, id, fileName, dataName, modDateISO8601, is);
        }
        finally {
            try {
                is.close();
            }
            catch(IOException ignore) {}
        }
    }

    @POST
    @Path("files")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public FileProperties postAttachment_Raw(@PathParam("id") long id,
                               @QueryParam("file_name") String fileName,
                               @QueryParam("data_name") String dataName,
       			               @QueryParam("mod_date") String modDateISO8601,
                               @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        //TODO: make sure a file with that name doesn't exist
        InputStream is = getRawInputStream(request);
        try {
            return writeNewFileContent(EntityIdentifier.EntityType.folderEntry, id, fileName, dataName, modDateISO8601, is);
        }
        finally {
            try {
                is.close();
            }
            catch(IOException ignore) {}
        }
    }

    @POST
   	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
   	public FileProperties postAttachment_ApplicationFormUrlencoded(@PathParam("id") String id) {
   		throw new UnsupportedMediaTypeException("'" + MediaType.APPLICATION_FORM_URLENCODED + "' format is not supported by this method. Use '" + MediaType.MULTIPART_FORM_DATA + "' or raw type");
   	}

    private org.kablink.teaming.domain.FolderEntry _getFolderEntry(long id) {
        org.kablink.teaming.domain.FolderEntry hEntry = getFolderModule().getEntry(null, id);
        if (hEntry.isPreDeleted()) {
            throw new NoFolderEntryByTheIdException(id);
        }
        return hEntry;
    }

}
