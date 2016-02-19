/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
package org.kablink.teaming.remoting.rest.provider;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.module.binder.impl.EntryDataErrors;
import org.kablink.teaming.module.binder.impl.WriteEntryDataException;
import org.kablink.teaming.remoting.rest.jersey.filter.ContainerFilter;
import org.kablink.teaming.rest.v1.model.ErrorInfo;
import org.kablink.util.api.ApiErrorCode;
import org.kablink.util.api.ApiErrorCodeSupport;

import java.lang.invoke.MethodHandles;
import java.util.List;

/**
 * @author jong
 *
 */
@Provider
public class WriteEntryDataMapper implements ExceptionMapper<WriteEntryDataException> {
	protected static Log logger = LogFactory.getLog(MethodHandles.lookup().lookupClass());

	public Response toResponse(WriteEntryDataException ex) {
        Throwable root = ex;
        EntryDataErrors errors = ex.getErrors();
        if (errors!=null) {
            List<EntryDataErrors.Problem> problems = errors.getProblems();
            if (problems!=null && problems.size()==1) {
                EntryDataErrors.Problem problem = problems.get(0);
                root = problem.getException();
                Throwable cause = root;
                while (cause.getCause()!=null) {
                    cause = cause.getCause();
                    if (cause instanceof ApiErrorCodeSupport || cause.getCause()==null) {
                        root = cause;
                    }
                }
            }
        }
        ApiErrorCode errorCode = ApiErrorCode.SERVER_ERROR;
        if (root instanceof ApiErrorCodeSupport) {
            errorCode = ((ApiErrorCodeSupport)root).getApiErrorCode();
        }
		int httpStatusCode = ex.getHttpStatusCode();
		if(httpStatusCode == Response.Status.NOT_FOUND.getStatusCode())
			logger.warn("An error occurred while processing a REST request (" + ContainerFilter.getCurrentEndpoint() + "): " + ex.toString());
		else
			logger.error("An error occurred while processing a REST request (" + ContainerFilter.getCurrentEndpoint() + "): " + ex.toString());
        return Response.status(httpStatusCode).entity(new ErrorInfo(errorCode.name(), root.getMessage())).build();
	}
}
