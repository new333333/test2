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

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.util.SZoneConfig;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.kablink.teaming.web.util.WebHelper;
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
        try
        {
            RPCRequest	rpcRequest;
        	String results;

            rpcRequest = RPC.decodeRequest( payload, m_remoteServiceClass );

            // If the first parameter to the method is an
            // HttpRequestInfo object...
            Object[] parameters = rpcRequest.getParameters();
            
        	//Run the data through the XSS checker
            parameters = performStringCheck(parameters);
            
            if ( ( null != parameters ) && ( 0 < parameters.length ) && ( parameters[0] instanceof HttpRequestInfo ) )
            {
            	HttpServletRequest req;
            	HttpRequestInfo ri;
            	
            	// Get the HttpServletRequest we are working with.
            	req = getThreadLocalRequest();
            	
            	// Get the HttpRequestInfo object that is the first parameter.
            	ri = (HttpRequestInfo) parameters[0];
                
            	// ...drop in the current HttpServletRequest.
            	ri.setRequestObj( req );
            	
            	// Is the user logged in?
            	if ( WebHelper.isGuestLoggedIn( req ) )
            	{
            		String guestId;
                	String clientUserId = null;
                	
            		// No
            		// Get the id of the user the client thinks it is dealing with.
            		clientUserId = ri.getUserLoginId();
                	
            		// Does the client think they are working with the guest user?
            		guestId = SZoneConfig.getGuestUserName( WebHelper.getZoneNameByVirtualHost( req) );
            		if ( clientUserId != null && clientUserId.equalsIgnoreCase( guestId ) == false )
            		{
                		GwtTeamingException ex;
                		
            			// No, pass back a "not logged in" exception
                		ex = new GwtTeamingException();
                		ex.setExceptionType( GwtTeamingException.ExceptionType.USER_NOT_LOGGED_IN );

                        return RPC.encodeResponseForFailure( null, ex );
            		}
            	}
            }
            
            // Delegate work to the spring injected service.
            results = RPC.invokeAndEncodeResponse( m_remoteService, rpcRequest.getMethod(), parameters );
            	
            return results;
        }
        catch (IncompatibleRemoteServiceException ex)
        {
            getServletContext().log(
                            "An IncompatibleRemoteServiceException was thrown while processing this call.",
                            ex );
            return RPC.encodeResponseForFailure( null, ex );
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
    
    Object[] performStringCheck(Object[] input) {
    	if(input == null)
    		return null;
    	else if(input.length == 0)
    		return input;
    	else {
    		Object[] output = new Object[input.length];
    		for(int i = 0; i < input.length; i++) {
    			if(input[i] instanceof String) {
    				output[i] = StringCheckUtil.check((String) input[i]); 
    			}
    			else if(input[i] instanceof String[]) {
    				output[i] = StringCheckUtil.check((String[]) input[i]);     				
    			}
    			else {
    				output[i] = input[i];
    			}
    		}
    		return output;
    	}
    }
    
}// end GwtRpcController
