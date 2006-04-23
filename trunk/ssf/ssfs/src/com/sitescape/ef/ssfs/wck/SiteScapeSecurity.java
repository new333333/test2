package com.sitescape.ef.ssfs.wck;

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
