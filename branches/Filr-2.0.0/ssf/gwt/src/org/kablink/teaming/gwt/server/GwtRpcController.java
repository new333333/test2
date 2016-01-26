/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.server;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcCmd;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.server.util.GwtServerHelper;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.web.util.WebHelper;

import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RpcTokenException;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.client.rpc.XsrfProtectedService;

/**
 * Servlet controller for GWT RPC request.
 *  
 * @author drfoster@novell.com
 */
@SuppressWarnings({"serial", "unchecked"})
public class GwtRpcController extends VibeXsrfProtectedServiceServlet implements Controller, ServletContextAware {
	private Log 					m_logger = LogFactory.getLog(GwtRpcController.class);
	
    private ServletContext			m_servletContext;				//
	private Class					m_xsrfProtectedServiceClass;	//
    private XsrfProtectedService	m_xsrfProtectedService;			//

    /*
     * Write information about how long an operation took to the log if
     * debug is enabled.
     */
    private void debugLogOperationTime(String cmdName, String operation, long opBegin) {
		if (m_logger.isDebugEnabled()) {
			double diff = ((System.nanoTime() - opBegin) / 1000000.0);
			m_logger.info("..." + cmdName + ":" + operation + " took " + diff + " ms to complete.");
		}
    }
    
    /**
     * Traces any unexpected failures and bubbles up the exception.
     * 
     * @param t
     */
    @Override
	protected void doUnexpectedFailure(Throwable t) {
    	m_logger.debug("GwtRpcController.doUnexpectedFailure(EXCEPTION):  ", t);
    	super.doUnexpectedFailure(t);
    }

    /**
     * ?
     * 
     * @return
     */
    @Override
    public ServletContext getServletContext() {
        return m_servletContext;
    }
    
    /**
     * ?
     * 
     * @param request
     * @param response
     * 
     * @return
     * 
     * @throws Exception
     */
    @Override
	public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.doPost(request, response);
        return null;
    }

    /**
     * ?
     * 
     * @param payload
     * 
     * @return
     * 
     * @throws SerializationException
     */
    @Override
    public String processCall(String payload) throws SerializationException {
		boolean isRetry = false;
        try {
            RPCRequest rpcRequest = RPC.decodeRequest(payload, m_xsrfProtectedServiceClass);
            Object[]   parameters = rpcRequest.getParameters();
            
    		String cmdName = "Unknown";
            if (m_logger.isDebugEnabled()) {
                String methodName = rpcRequest.getMethod().getName();
            	if (methodName.equalsIgnoreCase("executeCommand")) {
            		// Get the name of the command we are trying to
            		// execute.
            		if (parameters != null && parameters.length > 1 && parameters[1] instanceof VibeRpcCmd) {
            			cmdName = ((VibeRpcCmd)parameters[1]).getClass().getSimpleName();
            		}
        			m_logger.debug("Executing GWT RPC command:  " + cmdName);
            	}
            	
            	else {
            		m_logger.debug("Executing old style GWT RPC request:  " + methodName);
            		cmdName = methodName;
            	}
            }

    		long opBegin = System.nanoTime();
    		try {
            	// Run the parameters through the XSS checker.
    			performXSSChecks(parameters);
    		}
    		finally {
    			debugLogOperationTime(cmdName, "performXSSChecks()", opBegin);
    		}
            
            // Is the first parameter to the method is an
    		// HttpRequestInfo object?
            if ((null != parameters) && (0 < parameters.length) && (parameters[0] instanceof HttpRequestInfo)) {
            	// Yes!  Get the HttpServletRequest we are working
            	// with.
            	HttpServletRequest req = getThreadLocalRequest();
            	
            	// Get the HttpServletResponse.
            	HttpServletResponse resp = getThreadLocalResponse();
            	
            	// Get the HttpRequestInfo object that is the first
            	// parameter and drop in the current
            	// HttpServletRequest.
            	HttpRequestInfo ri = ((HttpRequestInfo) parameters[0]);                
            	ri.setRequestObj( req );
            	ri.setResponseObj(resp);
            	ri.setServletContext(getServletContext());
            	isRetry = ri.isRetry();
            	
            	// Is the user logged in?
            	if (WebHelper.isGuestLoggedIn(req)) {
            		// No!  Get the id of the user the client thinks it
            		// is dealing with.
                	String clientUserId = ri.getUserLoginId();
                	
            		// Does the client think they are working with the
                	// guest user?
            		String guestId = SZoneConfig.getGuestUserName(WebHelper.getZoneNameByVirtualHost(req));
            		if (clientUserId != null && (!(clientUserId.equalsIgnoreCase(guestId)))) {
            			// No, pass back a 'not logged in' exception.
                		GwtTeamingException ex = new GwtTeamingException();
                		ex.setExceptionType(GwtTeamingException.ExceptionType.USER_NOT_LOGGED_IN);
                        return RPC.encodeResponseForFailure(null, ex);
            		}
            	}
            }
            
        	String results;
    		opBegin = System.nanoTime();
    		try {
                // Validate the token from the RPC request and delegate
    			// the work to the Spring injected service.
    			Method rpcMethod = rpcRequest.getMethod();
    			if (!(rpcMethod.getName().equals("getNewXsrfToken"))) {
    				validateXsrfToken(rpcRequest.getRpcToken(), rpcMethod);
    			}
    			results = RPC.invokeAndEncodeResponse(m_xsrfProtectedService, rpcMethod, parameters);
    		}
    		finally {
    			debugLogOperationTime(cmdName, "RPC.invokeAndEncodeResponse()", opBegin);
    		}
            	
            return results;
        }
        
        catch (RpcTokenException ex) {
        	// If this call is a retry of the GWT RPC request...
        	if (isRetry) {
        		// ...log the error.
        		m_logger.error("An RpcTokenException was thrown while processing this call.", ex);
        	}
            return RPC.encodeResponseForFailure(null, ex);
        }
        
        catch (IncompatibleRemoteServiceException ex) {
        	m_logger.error("An IncompatibleRemoteServiceException was thrown while processing this call.", ex);
            return RPC.encodeResponseForFailure(null, ex);
        }
    }
    
    /*
     * Runs the XSS checker on the parameters that require checking.
     */
    private void performXSSChecks(Object[] parameters) {
    	// Scan the parameters...
    	int pCount = ((null == parameters) ? 0 : parameters.length );
		for(int i = 0; i < pCount; i += 1) {
			// ...and for any that are VibeRpcCmd's...
			if (parameters[i] instanceof VibeRpcCmd) {
				// ...perform XSS checking on the command.
				GwtServerHelper.performXSSCheckOnRpcCmd((VibeRpcCmd) parameters[i]); 
			}
		}
    }
    
    /**
     * ?
     *  
     * @param remoteService
     */
    public void setRemoteService(XsrfProtectedService remoteService) {
        m_xsrfProtectedService      = remoteService;
        m_xsrfProtectedServiceClass = m_xsrfProtectedService.getClass();
    }
    
    /**
     * ?
     * 
     * @param servletContext
     */
    @Override
	public void setServletContext(ServletContext servletContext) {
        m_servletContext = servletContext;
    }
}
