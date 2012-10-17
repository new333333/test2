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
import org.kablink.teaming.domain.DefinableEntity;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.module.file.WriteFilesException;
import org.kablink.teaming.remoting.rest.v1.exc.UnsupportedMediaTypeException;
import org.kablink.teaming.remoting.rest.v1.util.ResourceUtil;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.teaming.rest.v1.model.LegacyFileProperties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;

@Path("/v1/file/id/{fileid}")
@Singleton
@Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
public class LegacyFileByIdResource extends AbstractFileResource {
	@POST
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	public LegacyFileProperties writeFileContentById_MultipartFormData(@PathParam("fileid") String fileId,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
			@QueryParam("lastVersionNumber") Integer lastVersionNumber,
			@QueryParam("lastMajorVersionNumber") Integer lastMajorVersionNumber,
			@QueryParam("lastMinorVersionNumber") Integer lastMinorVersionNumber,
            @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        boolean forceOverwrite = (lastVersionNumber==null && lastMajorVersionNumber==null && lastMinorVersionNumber==null);
		FileAttachment fa = findFileAttachment(fileId);
		DefinableEntity entity = fa.getOwner().getEntity();
		InputStream is = getInputStreamFromMultipartFormdata(request);
		try {
			return new LegacyFileProperties(
                    updateExistingFileContent(entity, fa, dataName, modDateISO8601, null, forceOverwrite, lastVersionNumber,
                            lastMajorVersionNumber, lastMinorVersionNumber, is));
		}
		finally {
			try {
				is.close();
			}
			catch(IOException ignore) {}
		}
	}

	@POST
	public LegacyFileProperties writeFileContentById_Raw(@PathParam("fileid") String fileId,
			@QueryParam("dataName") String dataName,
			@QueryParam("modDate") String modDateISO8601,
			@QueryParam("lastVersionNumber") Integer lastVersionNumber,
			@QueryParam("lastMajorVersionNumber") Integer lastMajorVersionNumber,
			@QueryParam("lastMinorVersionNumber") Integer lastMinorVersionNumber,
            @Context HttpServletRequest request) throws WriteFilesException, WriteEntryDataException {
        boolean forceOverwrite = (lastVersionNumber==null && lastMajorVersionNumber==null && lastMinorVersionNumber==null);
		FileAttachment fa = findFileAttachment(fileId);
		DefinableEntity entity = fa.getOwner().getEntity();
		InputStream is = getRawInputStream(request);
		try {
			return new LegacyFileProperties(
                    updateExistingFileContent(entity, fa, dataName, modDateISO8601, null, forceOverwrite, lastVersionNumber,
                            lastMajorVersionNumber, lastMinorVersionNumber, is));
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
	public LegacyFileProperties writeFileContentById_ApplicationFormUrlencoded(@PathParam("fileid") String fileId) {
		throw new UnsupportedMediaTypeException("'" + MediaType.APPLICATION_FORM_URLENCODED + "' format is not supported by this method. Use '" + MediaType.MULTIPART_FORM_DATA + "' or raw type");
	}

	@GET
	public Response readFileContentById(@PathParam("fileid") String fileId,
			@Context HttpServletRequest request) {
		FileAttachment fa = findFileAttachment(fileId);
		DefinableEntity entity = fa.getOwner().getEntity();
		return readFileContent(entity.getEntityType().name(), entity.getId(), fa.getFileItem().getName(), getIfModifiedSinceDate(request));
	}

    @GET
    @Path("/metadata")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public LegacyFileProperties getMetaData(@PathParam("fileid") String fileId) {
        FileAttachment fa = findFileAttachment(fileId);
        return new LegacyFileProperties(ResourceUtil.buildFileProperties(fa));
    }

}
