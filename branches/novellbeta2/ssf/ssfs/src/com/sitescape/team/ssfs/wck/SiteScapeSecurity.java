/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.ssfs.wck;

import org.apache.slide.common.ServiceAccessException;
import org.apache.slide.common.SlideToken;
import org.apache.slide.security.AccessDeniedException;
import org.apache.slide.security.SecurityImpl;
import org.apache.slide.structure.ActionNode;
import org.apache.slide.structure.ObjectNode;

public class SiteScapeSecurity extends SecurityImpl {

    public void checkCredentials(SlideToken token, ObjectNode object,
            ActionNode action) 
    throws ServiceAccessException, AccessDeniedException {
    	// Do not let Slide be responsible for ACL checking.
    	// It will be performed entirely by SSF. 
    	
    	// Noop
    }
}
