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
package org.kablink.teaming.remoting.rest.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFileByTheIdException;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.RemotingException;
import org.kablink.teaming.remoting.rest.util.Constant;
import org.kablink.teaming.rest.model.FileProperties;
import org.kablink.teaming.rest.model.FileVersionProperties;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

@Path("/file")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FileResource extends AbstractResource {

	@POST
	@Path("/name/{filename}/content")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public FileProperties writeFileByName(@PathParam("filename") String filename,
			@DefaultValue(Constants.ENTITY_TYPE_FOLDER_ENTRY) @QueryParam("entity_type") String entityType,
			@QueryParam("entity_id") long entityId,
			@QueryParam("data_item_name") String dataItemName,
			@QueryParam("mod_date") String modDateISO8601,
            @Context HttpServletRequest request) 
			throws RuntimeException, IOException {
		InputStream is;
        try {
            ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
            FileItemIterator fii = sfu.getItemIterator(request);
            if (fii.hasNext()) {
                FileItemStream item = fii.next();
                is = item.openStream();
            }
            else
            {
                throw new RuntimeException("Missing form data");
            }
        } catch (FileUploadException e) {
            throw new RuntimeException("Received bad multipart form data", e);
        }
		// write the file to vibe
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = getFolderModule().getEntry(null, entityId);
    		try {
    			Date modDate = null;
    			if(Validator.isNotNull(modDateISO8601)) {
    				//modDate = dateFromISO8601(modDateISO8601);
    			}
    			if (Validator.isNull(dataItemName) && entry.getParentFolder().isLibrary()) {
    				// The file is being created within a library folder and the client hasn't specified a data item name explicitly.
    				// This will attach the file to the most appropriate definition element (data item) of the entry type (which is by default "upload").
    				FolderUtils.modifyLibraryEntry(entry, filename, is, modDate, true);
    			}
    			else {
    				if (Validator.isNull(dataItemName)) 
    					dataItemName="ss_attachFile1";
    				getFolderModule().modifyEntry(null, entityId, dataItemName, filename, is, null);
    			}
    		}
    		catch(WriteFilesException e) {
    			throw new RemotingException(e);
    		}
    		catch(WriteEntryDataException e) {
    			throw new RemotingException(e);
    		}
    		FileAttachment fa = entry.getFileAttachment(filename);
    		return filePropertiesFromFileAttachment(fa);
        }
        else {
        	return null;
        }
	}
	
	@POST
	@Path("/name/{filename}/content")
	public FileProperties writeFileByName2(@PathParam("filename") String filename,
			@DefaultValue(Constants.ENTITY_TYPE_FOLDER_ENTRY) @QueryParam("entity_type") String entityType,
			@QueryParam("entity_id") long entityId,
			@QueryParam("data_item_name") String dataItemName,
			@QueryParam("mod_date") String modDateISO8601,
            @Context HttpServletRequest request) 
			throws RuntimeException, IOException {
		InputStream is = request.getInputStream();
		// write the file to vibe
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = getFolderModule().getEntry(null, entityId);
    		try {
    			Date modDate = null;
    			if(Validator.isNotNull(modDateISO8601)) {
    				//modDate = dateFromISO8601(modDateISO8601);
    			}
    			if (Validator.isNull(dataItemName) && entry.getParentFolder().isLibrary()) {
    				// The file is being created within a library folder and the client hasn't specified a data item name explicitly.
    				// This will attach the file to the most appropriate definition element (data item) of the entry type (which is by default "upload").
    				FolderUtils.modifyLibraryEntry(entry, filename, is, modDate, true);
    			}
    			else {
    				if (Validator.isNull(dataItemName)) 
    					dataItemName="ss_attachFile1";
    				getFolderModule().modifyEntry(null, entityId, dataItemName, filename, is, null);
    			}
    		}
    		catch(WriteFilesException e) {
    			throw new RemotingException(e);
    		}
    		catch(WriteEntryDataException e) {
    			throw new RemotingException(e);
    		}
    		FileAttachment fa = entry.getFileAttachment(filename);
    		return filePropertiesFromFileAttachment(fa);
        }
        else {
        	return null;
        }
	}

	@POST
	@Path("/id/{fileid}/content")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public FileProperties writeFileById(@PathParam("fileid") String fileId,
			@DefaultValue(Constants.ENTITY_TYPE_FOLDER_ENTRY) @QueryParam("entity_type") String entityType,
			@QueryParam("entity_id") long entityId,
			@QueryParam("data_item_name") String dataItemName,
			@QueryParam("mod_date") String modDateISO8601,
            @Context HttpServletRequest request) 
			throws RuntimeException, IOException {
		InputStream is;
        try {
            ServletFileUpload sfu = new ServletFileUpload(new DiskFileItemFactory());
            FileItemIterator fii = sfu.getItemIterator(request);
            if (fii.hasNext()) {
                FileItemStream item = fii.next();
                is = item.openStream();
            }
            else
            {
                throw new RuntimeException("Missing form data");
            }
        } catch (FileUploadException e) {
            throw new RuntimeException("Received bad multipart form data", e);
        }
		// write the file to vibe
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = getFolderModule().getEntry(null, entityId);
    		FileAttachment fa = getFileAttachment(entry, fileId);
    		try {
    			Date modDate = null;
    			if(Validator.isNotNull(modDateISO8601)) {
    				//modDate = dateFromISO8601(modDateISO8601);
    			}
    			if (Validator.isNull(dataItemName) && entry.getParentFolder().isLibrary()) {
    				// The file is being created within a library folder and the client hasn't specified a data item name explicitly.
    				// This will attach the file to the most appropriate definition element (data item) of the entry type (which is by default "upload").
    				FolderUtils.modifyLibraryEntry(entry, fa.getFileItem().getName(), is, modDate, true);
    			}
    			else {
    				if (Validator.isNull(dataItemName)) 
    					dataItemName="ss_attachFile1";
    				getFolderModule().modifyEntry(null, entityId, dataItemName, fa.getFileItem().getName(), is, null);
    			}
    		}
    		catch(WriteFilesException e) {
    			throw new RemotingException(e);
    		}
    		catch(WriteEntryDataException e) {
    			throw new RemotingException(e);
    		}
    		return filePropertiesFromFileAttachment(fa);
        }
        else {
        	return null;
        }
	}

	// Read file content
	@GET
	@Path("/name/{filename}/content")
	public Response readFileByName(@PathParam("filename") String filename,
			@DefaultValue(Constants.ENTITY_TYPE_FOLDER_ENTRY) @QueryParam("entity_type") String entityType,
			@QueryParam("entity_id") long entityId) {
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = getFolderModule().getEntry(null, entityId);
    		FileAttachment fa = entry.getFileAttachment(filename);
    		String mt = new MimetypesFileTypeMap().getContentType(filename);
    		return Response.ok(getFileModule().readFile(entry.getParentBinder(), entry, fa), mt).build();
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
	}
	
	// Read file content
	@GET
	@Path("/id/{fileid}/content")
	public Response readFileById(@PathParam("fileid") String fileId,
			@DefaultValue(Constants.ENTITY_TYPE_FOLDER_ENTRY) @QueryParam("entity_type") String entityType,
			@QueryParam("entity_id") long entityId) {
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = getFolderModule().getEntry(null, entityId);
    		FileAttachment fa = getFileAttachment(entry, fileId);
    		String mt = new MimetypesFileTypeMap().getContentType(fa.getFileItem().getName());
    		return Response.ok(getFileModule().readFile(entry.getParentBinder(), entry, fa)).build();
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
	}
	
	// Read file properties
	@GET
	@Path("/name/{filename}/properties")
	public FileProperties readFilePropertiesByName(@PathParam("filename") String filename,
			@DefaultValue(Constants.ENTITY_TYPE_FOLDER_ENTRY) @QueryParam("entity_type") String entityType,
			@QueryParam("entity_id") long entityId) {
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = getFolderModule().getEntry(null, entityId);
    		FileAttachment fa = entry.getFileAttachment(filename);
    		return filePropertiesFromFileAttachment(fa);
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
	}
	
	// Read file properties
	@GET
	@Path("/id/{fileid}/properties")
	public FileProperties readFilePropertiesById(@PathParam("fileid") String fileId,
			@DefaultValue(Constants.ENTITY_TYPE_FOLDER_ENTRY) @QueryParam("entity_type") String entityType,
			@QueryParam("entity_id") long entityId) {
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = getFolderModule().getEntry(null, entityId);
    		FileAttachment fa = getFileAttachment(entry, fileId);
    		return filePropertiesFromFileAttachment(fa);
        }
        throw new WebApplicationException(Response.Status.NOT_FOUND);
	}
	

	// There is no method for updating file properties (at least yet). 
	// File properties are modified only indirectly when file content is modified.

	// Delete file content as well as the properties associated with the file.
	@DELETE
	public void deleteFile(@PathParam("id") String id) {
		
	}
	
	@GET
	@Path("versions")
	public List<FileVersionProperties> getFileVersions(@PathParam("id") String id) {
		return null;
	}
	
	private FileProperties filePropertiesFromFileAttachment(FileAttachment fa) {
		FileProperties fp = new FileProperties();
		fp.setFilename(fa.getFileItem().getName());
		fp.setFile_length(fa.getFileItem().getLength());
		return fp;
	}
	private FileAttachment getFileAttachment(DefinableEntity entity, String attachmentId) {
		Attachment att = entity.getAttachment(attachmentId);
		if(att == null || !(att instanceof FileAttachment))
			throw new NoFileByTheIdException(attachmentId);
		return (FileAttachment) att;
	}
}
