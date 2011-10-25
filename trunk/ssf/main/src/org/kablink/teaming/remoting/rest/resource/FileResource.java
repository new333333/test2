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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.kablink.teaming.UncheckedIOException;
import org.kablink.teaming.dao.CoreDao;
import org.kablink.teaming.domain.AnyOwner;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.NoFileByTheIdException;
import org.kablink.teaming.domain.EntityIdentifier.EntityType;
import org.kablink.teaming.domain.NoFileVersionByTheIdException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.VersionAttachment;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.shared.FolderUtils;
import org.kablink.teaming.remoting.rest.exc.BadRequestException;
import org.kablink.teaming.remoting.rest.exc.InternalServerErrorException;
import org.kablink.teaming.remoting.rest.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.util.ServiceUtil;
import org.kablink.teaming.rest.model.FileProperties;
import org.kablink.teaming.rest.model.FileVersionProperties;
import org.kablink.util.Validator;
import org.kablink.util.search.Constants;

import com.sun.jersey.api.core.InjectParam;

@Path("/file")
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class FileResource {

	private static Log logger = LogFactory.getLog(FileResource.class);
	
	@InjectParam("folderModule") private FolderModule folderModule;
    @InjectParam("fileModule") private FileModule fileModule;
    @InjectParam("profileModule") private ProfileModule profileModule;
    @InjectParam("binderModule") private BinderModule binderModule;
    @InjectParam("coreDao") private CoreDao coreDao;
	
	@POST
	@Path("/name/{entityType}/{entityId}/{filename}/content")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public FileProperties writeFileContentByName_MultipartFormData(
			@PathParam("entityType") String entityType,
			@PathParam("entityId") long entityId,
			@PathParam("filename") String filename,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
            @Context HttpServletRequest request) {
		InputStream is = getInputStreamFromMultipartFormdata(request);
		try {
			return writeFileContentByName(entityType, entityId, filename, dataName, modDateISO8601, request, is);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException ignore) {}
		}
	}
	
	@POST
	@Path("/name/{entityType}/{entityId}/{filename}/content")
	public FileProperties writeFileContentByName_Raw(
			@PathParam("entityType") String entityType,
			@PathParam("entityId") long entityId,
			@PathParam("filename") String filename,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
            @Context HttpServletRequest request) {
		InputStream is = getRawInputStream(request);
		try {
			return writeFileContentByName(entityType, entityId, filename, dataName, modDateISO8601, request, is);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException ignore) {}
		}
	}

	@POST
	@Path("/name/{entityType}/{entityId}/{filename}/content")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public FileProperties writeFileContentByName_ApplicationFormUrlencoded(
			@PathParam("entityType") String entityType,
			@PathParam("entityId") long entityId,
			@PathParam("filename") String filename,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
            @Context HttpServletRequest request) {
		throw new UnsupportedMediaTypeException("'" + MediaType.APPLICATION_FORM_URLENCODED + "' format is not supported by this method. Use '" + MediaType.MULTIPART_FORM_DATA + "' or raw type");
	}
	
	@POST
	@Path("/id/{fileid}/content")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public FileProperties writeFileContentById_MultipartFormData(@PathParam("fileid") String fileId,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
            @Context HttpServletRequest request) {
		FileAttachment fa = getFileAttachment(fileId);
		DefinableEntity entity = fa.getOwner().getEntity();
		InputStream is = getInputStreamFromMultipartFormdata(request);
		try {
			return writeFileContentByName(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), dataName, modDateISO8601, request, is);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException ignore) {}
		}
	}

	@POST
	@Path("/id/{fileid}/content")
	public FileProperties writeFileContentById_Raw(@PathParam("fileid") String fileId,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
            @Context HttpServletRequest request) {
		FileAttachment fa = getFileAttachment(fileId);
		DefinableEntity entity = fa.getOwner().getEntity();
		InputStream is = getRawInputStream(request);
		try {
			return writeFileContentByName(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), dataName, modDateISO8601, request, is);
		}
		finally {
			try {
				is.close();
			}
			catch(IOException ignore) {}
		}
	}

	@POST
	@Path("/id/{fileid}/content")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public FileProperties writeFileContentById_ApplicationFormUrlencoded(@PathParam("fileid") String fileId,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
            @Context HttpServletRequest request) {
		throw new UnsupportedMediaTypeException("'" + MediaType.APPLICATION_FORM_URLENCODED + "' format is not supported by this method. Use '" + MediaType.MULTIPART_FORM_DATA + "' or raw type");
	}

	// Read file content
	@GET
	@Path("/name/{entityType}/{entityId}/{filename}/content")
	public Response readFileContentByName(
			@PathParam("entityType") String entityType,
			@PathParam("entityId") long entityId,
			@PathParam("filename") String filename) {
		return readFileContent(entityType, entityId, filename);
	}
	
	// Read file content
	@GET
	@Path("/id/{fileid}/content")
	public Response readFileContentById(@PathParam("fileid") String fileId) {
		FileAttachment fa = getFileAttachment(fileId);
		DefinableEntity entity = fa.getOwner().getEntity();
		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName());
	}
	
	// Read file properties
	@GET
	@Path("/name/{entityType}/{entityId}/{filename}/properties")
	public FileProperties readFilePropertiesByName(
	@PathParam("entityType") String entityType,
	@PathParam("entityId") long entityId,
	@PathParam("filename") String filename) {
		return readFileProperties(entityType, entityId, filename);
	}
	
	// Read file properties
	@GET
	@Path("/id/{fileid}/properties")
	public FileProperties readFilePropertiesById(@PathParam("fileid") String fileId) {
		FileAttachment fa = getFileAttachment(fileId);
		DefinableEntity entity = fa.getOwner().getEntity();
		return readFileProperties(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName());
	}
	
	// There is no method for updating file properties (at least yet). 
	// File properties are modified only indirectly when file content is modified.

	// Delete file content as well as the properties associated with the file.
	@DELETE
	public void deleteFile(@PathParam("id") String id) {
		// TODO jong
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

	private InputStream getRawInputStream(HttpServletRequest request) 
	throws UncheckedIOException {
		try {
			return request.getInputStream();
		} catch (IOException e) {
        	logger.error("Error reading data", e);
        	throw new UncheckedIOException(e);
		}
	}
	
	private InputStream getInputStreamFromMultipartFormdata(HttpServletRequest request) 
	throws WebApplicationException, UncheckedIOException {
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
            	throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Missing form data").build());
            }
        } catch (FileUploadException e) {
        	logger.warn("Received bad multipart form data", e);
        	throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).entity("Received bad multipart form data").build());
        } catch (IOException e) {
        	logger.error("Error reading multipart form data", e);
        	throw new UncheckedIOException(e);
		}
        return is;
	}
	
	private Date dateFromISO8601(String modDateISO8601) {
		if(Validator.isNotNull(modDateISO8601)) {
			DateTime dateTime = ISODateTimeFormat.basicDateTime().parseDateTime(modDateISO8601);
			return dateTime.toDate();
		}
		else {
			return null;
		}
	}
	
	private FileProperties writeFileContentByName(
			String entityType,
			long entityId,
			String filename,
			String dataName,
			String modDateISO8601,
            HttpServletRequest request,
            InputStream is) {
        EntityType et = EntityType.valueOf(entityType);
        if(et == EntityType.folderEntry) {
    		FolderEntry entry = folderModule.getEntry(null, entityId);
    		try {
    			Date modDate = dateFromISO8601(modDateISO8601);
    			ServiceUtil.modifyFolderEntryWithFile(entry, dataName, filename, is, modDate);
    		}
    		catch(WriteFilesException e) {
    			throw new InternalServerErrorException(e.getMessage());
    		}
    		catch(WriteEntryDataException e) {
    			throw new InternalServerErrorException(e.getMessage());
    		}
    		FileAttachment fa = entry.getFileAttachment(filename);
    		return filePropertiesFromFileAttachment(fa);
        }
        else if(et == EntityType.user) {
        	Principal user = profileModule.getEntry(entityId);
        	if(!(user instanceof User))
        		throw new BadRequestException("entityId '" + entityId + "' does not represent a user");
        	try {
    			Date modDate = dateFromISO8601(modDateISO8601);
    			ServiceUtil.modifyUserWithFile(user, dataName, filename, is, modDate);
        	}
    		catch(WriteFilesException e) {
    			throw new InternalServerErrorException(e.getMessage());
    		}
    		catch(WriteEntryDataException e) {
    			throw new InternalServerErrorException(e.getMessage());
    		}
    		FileAttachment fa = user.getFileAttachment(filename);
    		return filePropertiesFromFileAttachment(fa);
        }
        else if(et == EntityType.workspace || et == EntityType.folder) {
    		Binder binder = binderModule.getBinder(entityId);
    		try {
    			// Ignore modDate param, since it isn't applicable in this case.
    			ServiceUtil.modifyBinderWithFile(binder, dataName, filename, is);
    		}
    		catch(WriteFilesException e) {
    			throw new InternalServerErrorException(e.getMessage());
    		}
    		catch(WriteEntryDataException e) {
    			throw new InternalServerErrorException(e.getMessage());
    		}
    		FileAttachment fa = binder.getFileAttachment(filename);
    		return filePropertiesFromFileAttachment(fa);
        }
        else {
        	throw new BadRequestException("entityType '" + entityType + "' is unknown or not supported by this method");
        }
	}

	private FileAttachment getFileAttachment(String fileId) 
	throws NoFileByTheIdException {
		// Don't use FileModule.getFileAttachmentById for this because -  
		// We don't want access control check on the owning entity to account
		// for such weird boundary case where the user has no right to read the
		// entry but has the right to modify the entry, although it's unclear
		// if this condition can actually occur...
		FileAttachment fa = (FileAttachment) coreDao.load(FileAttachment.class, fileId);
		if(fa == null)
			throw new NoFileByTheIdException(fileId);
		else if(fa instanceof VersionAttachment)
			throw new NoFileByTheIdException(fileId, "The specified file ID represents a file version rather than a file");
		else
			return fa;
	}

	private VersionAttachment getVersionAttachment(String fileId) 
	throws NoFileByTheIdException {
		// Don't use FileModule.getFileAttachmentById for this because -  
		// We don't want access control check on the owning entity to account
		// for such weird boundary case where the user has no right to read the
		// entry but has the right to modify the entry, although it's unclear
		// if this condition can actually occur...
		FileAttachment fa = (FileAttachment) coreDao.load(FileAttachment.class, fileId);
		if(fa == null)
			throw new NoFileVersionByTheIdException(fileId);
		else if(!(fa instanceof VersionAttachment))
			throw new NoFileVersionByTheIdException(fileId, "The specified file version ID represents a file rather than a file version");
		else
			return (VersionAttachment) fa;
	}
	
	private Response readFileContent(String entityType, long entityId, String filename) 
	throws BadRequestException {
        EntityType et = EntityType.valueOf(entityType);
        Binder binder;
        DefinableEntity entity;
        FileAttachment fa;
        if(et == EntityType.folderEntry) {
    		entity = folderModule.getEntry(null, entityId);
    		binder = entity.getParentBinder();
        }
        else if(et == EntityType.user) {
        	entity = profileModule.getEntry(entityId);
        	if(!(entity instanceof User))
        		throw new BadRequestException("entityId '" + entityId + "' does not represent a user");
        	binder = entity.getParentBinder();
        }
        else if(et == EntityType.workspace || et == EntityType.folder) {
    		entity = binderModule.getBinder(entityId);
    		binder = (Binder) entity;
        }
        else {
        	throw new BadRequestException("entityType '" + entityType + "' is unknown or not supported by this method");
        }
		fa = entity.getFileAttachment(filename);
		String mt = new MimetypesFileTypeMap().getContentType(filename);
		return Response.ok(fileModule.readFile(binder, entity, fa), mt).build();
	}
	
	private FileProperties readFileProperties(String entityType, long entityId, String filename) 
	throws BadRequestException {
        EntityType et = EntityType.valueOf(entityType);
        DefinableEntity entity;
        FileAttachment fa;
        if(et == EntityType.folderEntry) {
    		entity = folderModule.getEntry(null, entityId);
        }
        else if(et == EntityType.user) {
        	entity = profileModule.getEntry(entityId);
        	if(!(entity instanceof User))
        		throw new BadRequestException("entityId '" + entityId + "' does not represent a user");
        }
        else if(et == EntityType.workspace || et == EntityType.folder) {
    		entity = binderModule.getBinder(entityId);
        }
        else {
        	throw new BadRequestException("entityType '" + entityType + "' is unknown or not supported by this method");
        }
		fa = entity.getFileAttachment(filename);
		return filePropertiesFromFileAttachment(fa);
	}
}
