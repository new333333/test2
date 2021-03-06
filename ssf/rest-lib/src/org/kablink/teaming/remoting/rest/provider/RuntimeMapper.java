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
package org.kablink.teaming.remoting.rest.provider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.UnresolvableObjectException;
import org.kablink.teaming.remoting.rest.jersey.filter.ContainerFilter;
import org.kablink.teaming.rest.v1.model.ErrorInfo;
import org.kablink.util.api.ApiErrorCode;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * User: david
 * Date: 6/14/12
 * Time: 8:29 AM
 */
@Provider
public class RuntimeMapper implements ExceptionMapper<RuntimeException> {
    protected static Log logger = LogFactory.getLog(RuntimeMapper.class);

	public Response toResponse(RuntimeException ex) {
		if(ex instanceof ObjectNotFoundException || ex instanceof UnresolvableObjectException) {
	        logger.warn("An error occurred while processing a REST request (" + ContainerFilter.getCurrentEndpoint() + ")", ex);
			return Response.status(Response.Status.NOT_FOUND).entity(new ErrorInfo(ApiErrorCode.NOT_FOUND.name(), ex.getMessage())).build();
		}
		else {
	        logger.error("An error occurred while processing a REST request (" + ContainerFilter.getCurrentEndpoint() + ")", ex);
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ErrorInfo(ApiErrorCode.SERVER_ERROR.name(), ex.getMessage())).build();
		}
	}
}
