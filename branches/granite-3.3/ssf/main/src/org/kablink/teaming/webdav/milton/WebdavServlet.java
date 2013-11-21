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
package org.kablink.teaming.webdav.milton;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;

import com.bradmcevoy.http.Handler;
import com.bradmcevoy.http.HttpExtension;
import com.bradmcevoy.http.MiltonServlet;
import com.bradmcevoy.http.ProtocolHandlers;
import com.bradmcevoy.http.webdav.CopyHandler;
import com.bradmcevoy.http.webdav.MoveHandler;
import com.bradmcevoy.http.webdav.WebDavProtocol;

/**
 * @author jong
 *
 */
public class WebdavServlet extends MiltonServlet {

	private Log logger = LogFactory.getLog(WebdavServlet.class);
	
	private static final boolean DELETE_EXISTING_BEFORE_COPY_DEFAULT = false;
	private static final boolean DELETE_EXISTING_BEFORE_MOVE_DEFAULT = false;
	
	private volatile boolean inited = false;
	
	@Override
    public void service( javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse ) throws ServletException, IOException {
		// Before servicing the request, make sure that initialization has been done.
		if(!inited)
			lazyInit();

		super.service(servletRequest, servletResponse);
    }
    
    private void lazyInit() throws ServletException {
    	// We can not do this initialization in the regular init() method which gets invoked when the servlet is initialized,
    	// because this method needs access to SPropsUtil class loaded by the master Spring context, which may not have 
    	// properly initialized by the time this servlet is loaded. So, we can do this only lazily at the time of use.
    	ProtocolHandlers protocolHandlers = httpManager.getHandlers();
    	for(HttpExtension protocolHandler:protocolHandlers) {
    		if(protocolHandler instanceof WebDavProtocol) {
    			WebDavProtocol webdavProtocol = (WebDavProtocol) protocolHandler;
    			for(Handler handler:webdavProtocol.getHandlers()) {
    				if(handler instanceof CopyHandler) {
    					logger.debug("Configuring copy handler");
    					CopyHandler copyHandler = (CopyHandler) handler;
    					copyHandler.setDeleteExistingBeforeCopy(SPropsUtil.getBoolean("", DELETE_EXISTING_BEFORE_COPY_DEFAULT));
    				}
    				else if(handler instanceof MoveHandler) {
    					logger.debug("Configuring move handler");
    					MoveHandler moveHandler = (MoveHandler) handler;
    					moveHandler.setDeleteExistingBeforeMove(SPropsUtil.getBoolean("", DELETE_EXISTING_BEFORE_MOVE_DEFAULT));
    				}
    			}
    		}
    	}
    	inited = true;
    }
    
}
