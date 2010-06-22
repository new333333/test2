/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.gwt.user.client.rpc.IncompatibleRemoteServiceException;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.server.rpc.RPC;
import com.google.gwt.user.server.rpc.RPCRequest;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * 
 * @author jwootton
 *
 */
@SuppressWarnings("serial")
public class GwtRpcController extends RemoteServiceServlet
	implements
        Controller, ServletContextAware
{
	private Log 				m_logger = LogFactory.getLog(GwtRpcController.class);
    private ServletContext		m_servletContext;
    private RemoteService		m_remoteService;
	@SuppressWarnings("unchecked")
	private Class				m_remoteServiceClass;

    /**
     * 
     */
    public ModelAndView handleRequest(
    	HttpServletRequest request,
        HttpServletResponse response) throws Exception
    {
        super.doPost( request, response );
        
        return null;
    }// end handleRequest()
    

    /**
     * 
     */
    @Override
    public String processCall( String payload ) throws SerializationException
    {
		HttpSession session = null;
        String methodName = null;
        
        try
        {
            RPCRequest	rpcRequest;
            Method method;
        	String results;

            rpcRequest = RPC.decodeRequest( payload, m_remoteServiceClass );

            // Add the HttpServletRequest object we are dealing with to the session cache
            // using the method name as the key.  This allows any method in GwtRpcServiceImpl.java
            // to have access to the HttpServletRequest if they need it.
            method = rpcRequest.getMethod();
            if ( method != null )
            {
                methodName = method.getName();
            	if ( methodName != null )
            	{
            		HttpServletRequest request;
            		
            		request = getThreadLocalRequest();
            		
            		// Store the HttpServletRequest in the session cache using the method name as the key.
            		session = request.getSession();
            		HttpServletRequest cachedRequest = null;
            		try
            		{
            			cachedRequest = ((HttpServletRequest) session.getAttribute( methodName ));
            		}
            		catch ( Exception e )
            		{
            			cachedRequest = null;
            		}
            		if (null != cachedRequest) {
            			m_logger.error( "GwtRpcController.processCall( HttpServletRequest for method '" + methodName + "' is already in the cache.  Overwritting it. )" );
            		}
            		session.setAttribute( methodName, request );
            	}
            }

            // delegate work to the spring injected service
            results = RPC.invokeAndEncodeResponse( m_remoteService, rpcRequest.getMethod(), rpcRequest.getParameters() );
            	
            return results;
        }
        catch (IncompatibleRemoteServiceException ex)
        {
            getServletContext().log(
                            "An IncompatibleRemoteServiceException was thrown while processing this call.",
                            ex );
            return RPC.encodeResponseForFailure( null, ex );
        }
        finally
        {
            // Remove the HttpServletRequest object from the session cache.
            if ( session != null && methodName != null )
            	session.removeAttribute( methodName );
        }
    }// end processCall()
    

    /**
     * 
     */
    @Override
    public ServletContext getServletContext()
    {
        return m_servletContext;
    }// end getServletContext()

    
    /**
     * 
     */
    public void setServletContext( ServletContext servletContext )
    {
        m_servletContext = servletContext;
    }// end setServletContext()

    
    /**
     * 
     * @param remoteService
     */
    public void setRemoteService( RemoteService remoteService )
    {
        m_remoteService = remoteService;
        m_remoteServiceClass = m_remoteService.getClass();
    }// end setRemoteService()

    /**
     * Traces any unexpected failures and bubbles up the exception.
     */
    protected void doUnexpectedFailure(Throwable t) {
    	m_logger.debug("GwtRpcController.doUnexpectedFailure(EXCEPTION):  ", t);
    	super.doUnexpectedFailure(t);
    }
}// end GwtRpcController
