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

import com.sun.jersey.spi.resource.Singleton;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.NoFileVersionByTheIdException;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.shared.FileUtils;
import org.kablink.teaming.remoting.rest.v1.exc.ConflictException;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.FileVersionProperties;
import org.kablink.util.api.ApiErrorCode;

import javax.activation.MimetypesFileTypeMap;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/file_versions/{id}")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FileVersionResource extends AbstractFileResource {

	// Read file version content
	@GET
	public Response readFileVersionContentById(@PathParam("id") String fileVersionId) {
		VersionAttachment va = FileUtils.findVersionAttachment(fileVersionId);
		DefinableEntity entity = va.getOwner().getEntity();
		Binder binder;
		if(entity instanceof Binder) 
			binder = (Binder) entity;
		else
			binder = entity.getParentBinder();
		String mt = new MimetypesFileTypeMap().getContentType(va.getFileItem().getName());
		return Response.ok(getFileModule().readFile(binder, entity, va), mt).build();
	}
	
	// Read file version properties
	@GET
	@Path("/metadata")
	public FileVersionProperties readFileVersionPropertiesById(@PathParam("id") String fileVersionId) {
		VersionAttachment va = FileUtils.findVersionAttachment(fileVersionId);
		return ResourceUtil.fileVersionFromFileAttachment(va);
	}
	
	// Update file version properties
	@POST
	@Path("/current")
	public FileVersionProperties updateFileVersionPropertiesById(
			@PathParam("id") String fileVersionId) {
		VersionAttachment va = FileUtils.findVersionAttachment(fileVersionId);
        try {
            FileUtils.promoteFileVersionCurrent(va);
        }
        catch(UnsupportedOperationException e) {
            throw new ConflictException(ApiErrorCode.NOT_SUPPORTED, e.getLocalizedMessage());
        }

        FileAttachment fa = getFileAttachment(va.getParentAttachment().getId());
		return ResourceUtil.fileVersionFromFileAttachment(fa.getHighestVersion());
	}
	
    @GET
   	@Path("/note")
    public FileVersionProperties getNote(@PathParam("id") String fileVersionId) {
   		VersionAttachment va = FileUtils.findVersionAttachment(fileVersionId);
        FileVersionProperties props = new FileVersionProperties();
        props.setNote(va.getFileItem().getDescription().getText());
   		return props;
   	}

	// Update file version properties
	@POST
	@Path("/note")
	public FileVersionProperties updateNote(
			@PathParam("id") String fileVersionId,
			FileVersionProperties fileVersionProperties) {
		VersionAttachment va = FileUtils.findVersionAttachment(fileVersionId);
		// Set note/comment for the file version
		String note = fileVersionProperties.getNote();
		if(note != null &&
				!note.equals(va.getFileItem().getDescription().getText())) {
			FileUtils.setFileVersionNote(va, note);
		}
		return ResourceUtil.fileVersionFromFileAttachment(va);
	}

    @GET
   	@Path("/status")
    public FileVersionProperties getStatus(@PathParam("id") String fileVersionId) {
   		VersionAttachment va = FileUtils.findVersionAttachment(fileVersionId);
        FileVersionProperties props = new FileVersionProperties();
        props.setStatus(va.getFileStatus());
   		return props;
   	}

	// Update file version properties
	@POST
	@Path("/status")
	public FileVersionProperties updateStatus(
			@PathParam("id") String fileVersionId,
			FileVersionProperties fileVersionProperties) {
		VersionAttachment va = FileUtils.findVersionAttachment(fileVersionId);

		// Set status for a file version.
		Integer status = fileVersionProperties.getStatus();
		if(status != null && !status.equals(va.getFileStatus())) {
			FileUtils.setFileVersionStatus(va, status);
		}

		return ResourceUtil.fileVersionFromFileAttachment(va);
	}

	// Delete file version. This deletes both the content and the properties associated with the version.
	@DELETE
	public void deleteFileVersionById(@PathParam("id") String fileVersionId) {
		VersionAttachment va;
		try {
			va = FileUtils.findVersionAttachment(fileVersionId);
		}
		catch(NoFileVersionByTheIdException e) {
			// The version isn't found. Since post-action condition is still met, 
			// do not throw an exception. Return normally.
			return;
		}
		FileUtils.deleteFileVersion(va);
	}

}
