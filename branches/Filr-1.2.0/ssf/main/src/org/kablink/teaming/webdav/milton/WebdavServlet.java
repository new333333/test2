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
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.util.SPropsUtil;

import com.bradmcevoy.http.ApplicationConfig;
import com.bradmcevoy.http.AuthenticationHandler;
import com.bradmcevoy.http.AuthenticationService;
import com.bradmcevoy.http.Handler;
import com.bradmcevoy.http.HttpExtension;
import com.bradmcevoy.http.ProtocolHandlers;
import com.bradmcevoy.http.Request;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.ResourceFactoryFactory;
import com.bradmcevoy.http.Response;
import com.bradmcevoy.http.ServletHttpManager;
import com.bradmcevoy.http.ServletResponse;
import com.bradmcevoy.http.webdav.CopyHandler;
import com.bradmcevoy.http.webdav.MoveHandler;
import com.bradmcevoy.http.webdav.WebDavProtocol;
import com.bradmcevoy.http.webdav.WebDavResponseHandler;

/**
 * This class is created from com.bradmcevoy.http.MiltonServlet (1.7.3 snapshot)
 * and customized to meet the need of our product.
 * 
 * @author jong
 *
 */
public class WebdavServlet implements Servlet {

    private static final ThreadLocal<HttpServletRequest> originalRequest = new ThreadLocal<HttpServletRequest>();
    private static final ThreadLocal<HttpServletResponse> originalResponse = new ThreadLocal<HttpServletResponse>();
    private static final ThreadLocal<ServletConfig> tlServletConfig = new ThreadLocal<ServletConfig>();

	private Log logger = LogFactory.getLog(WebdavServlet.class);
	
	private static final boolean DELETE_EXISTING_BEFORE_COPY_DEFAULT = false;
	private static final boolean DELETE_EXISTING_BEFORE_MOVE_DEFAULT = false;
	
	private volatile boolean inited = false;
	
    public static HttpServletRequest request() {
        return originalRequest.get();
    }

    public static HttpServletResponse response() {
        return originalResponse.get();
    }

    /**
     * Make the servlet config available to any code on this thread.
     *
     * @return
     */
    public static ServletConfig servletConfig() {
        return tlServletConfig.get();
    }

    public static void forward( String url ) {
        try {
            request().getRequestDispatcher( url ).forward( originalRequest.get(), originalResponse.get() );
        } catch( IOException ex ) {
            throw new RuntimeException( ex );
        } catch( ServletException ex ) {
            throw new RuntimeException( ex );
        }
    }
    private ServletConfig config;
    protected ServletHttpManager httpManager;

	@Override
    public void init( ServletConfig config ) throws ServletException {
        try {
            this.config = config;
            // Note that the config variable may be null, in which case default handlers will be used
            // If present and blank, NO handlers will be configed
            List<String> authHandlers = loadAuthHandlersIfAny( config.getInitParameter( "authentication.handler.classes" ) );
            String resourceFactoryFactoryClassName = config.getInitParameter( "resource.factory.factory.class" );
            if( resourceFactoryFactoryClassName != null && resourceFactoryFactoryClassName.length() > 0 ) {
                initFromFactoryFactory( resourceFactoryFactoryClassName, authHandlers );
            } else {
                String resourceFactoryClassName = config.getInitParameter( "resource.factory.class" );
                String responseHandlerClassName = config.getInitParameter( "response.handler.class" );
                init( resourceFactoryClassName, responseHandlerClassName, authHandlers );
            }
            httpManager.init( new ApplicationConfig( config ), httpManager );
        } catch( ServletException ex ) {
            logger.error( "Exception starting milton servlet", ex );
            throw ex;
        } catch( Throwable ex ) {
            logger.error( "Exception starting milton servlet", ex );
            throw new RuntimeException( ex );
        }
    }

    protected void init( String resourceFactoryClassName, String responseHandlerClassName, List<String> authHandlers ) throws ServletException {
        logger.debug( "resourceFactoryClassName: " + resourceFactoryClassName );
        ResourceFactory rf = instantiate( resourceFactoryClassName );
        WebDavResponseHandler responseHandler;
        if( responseHandlerClassName == null ) {
            responseHandler = null; // allow default to be created
        } else {
            responseHandler = instantiate( responseHandlerClassName );
        }
        init( rf, responseHandler, authHandlers );
    }

    protected void initFromFactoryFactory( String resourceFactoryFactoryClassName, List<String> authHandlers ) throws ServletException {
        logger.debug( "resourceFactoryFactoryClassName: " + resourceFactoryFactoryClassName );
        ResourceFactoryFactory rff = instantiate( resourceFactoryFactoryClassName );
        rff.init();
        ResourceFactory rf = rff.createResourceFactory();
        WebDavResponseHandler responseHandler = rff.createResponseHandler();
        init( rf, responseHandler, authHandlers );
    }

    protected void init( ResourceFactory rf, WebDavResponseHandler responseHandler, List<String> authHandlers ) throws ServletException {
        AuthenticationService authService;
        if( authHandlers == null ) {
            authService = new AuthenticationService();
        } else {
            List<AuthenticationHandler> list = new ArrayList<AuthenticationHandler>();
            for( String authHandlerClassName : authHandlers ) {
                Object o = instantiate( authHandlerClassName );
                if( o instanceof AuthenticationHandler ) {
                    AuthenticationHandler auth = (AuthenticationHandler) o;
                    list.add( auth );
                } else {
                    throw new ServletException( "Class: " + authHandlerClassName + " is not a: " + AuthenticationHandler.class.getCanonicalName() );
                }
            }
            authService = new AuthenticationService( list );
        }

        // log the auth handler config
        logger.debug( "Configured authentication handlers: " + authService.getAuthenticationHandlers().size());
        if( authService.getAuthenticationHandlers().size() > 0 ) {
            for( AuthenticationHandler hnd : authService.getAuthenticationHandlers()) {
                logger.debug( " - " + hnd.getClass().getCanonicalName());
            }
        } else {
            logger.warn("No authentication handlers are configured! Any requests requiring authorisation will fail.");
        }


        if( responseHandler == null ) {
            httpManager = new ServletHttpManager( rf, authService );
        } else {
            httpManager = new ServletHttpManager( rf, responseHandler, authService );
        }
    }

    protected <T> T instantiate( String className ) throws ServletException {
        try {
            Class c = Class.forName( className );
            T rf = (T) c.newInstance();
            return rf;
        } catch( Throwable ex ) {
            throw new ServletException( "Failed to instantiate: " + className, ex );
        }
    }

	@Override
    public void destroy() {
        logger.debug( "destroy" );
        if( httpManager == null ) return;
        httpManager.destroy( httpManager );
    }

    protected void doService( javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse ) throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;
        try {
            setThreadlocals( req , resp);
            tlServletConfig.set( config );
            // 4/14/2015 JK (but #926653) - Use our own custom class WebdavServletRequest
            // instead of Milton's ServletRequest.
            Request request = new WebdavServletRequest( req );
            Response response = new ServletResponse( resp );
            httpManager.process( request, response );
        } finally {
            clearThreadlocals();
            tlServletConfig.remove();
            servletResponse.getOutputStream().flush();
            servletResponse.flushBuffer();
        }
    }

	public static  void clearThreadlocals() {
		originalRequest.remove();
		originalResponse.remove();
	}

	public static  void setThreadlocals(HttpServletRequest req, HttpServletResponse resp) {
		originalRequest.set( req );
		originalResponse.set( resp );
	}

	@Override
    public String getServletInfo() {
        return "MiltonServlet";
    }

	@Override
    public ServletConfig getServletConfig() {
        return config;
    }

    /**
     * Returns null, or a list of configured authentication handler class names
     *
     * @param initParameter - null, or the (possibly empty) list of comma seperated class names
     * @return - null, or a possibly empty list of class names
     */
    private List<String> loadAuthHandlersIfAny( String initParameter ) {
        if( initParameter == null ) return null;
        String[] arr = initParameter.split( "," );
        List<String> list = new ArrayList<String>();
        for( String s : arr ) {
            s = s.trim();
            if( s.length() > 0 ){
                list.add( s );
            }
        }
        return list;
    }

    @Override
    public void service( javax.servlet.ServletRequest servletRequest, javax.servlet.ServletResponse servletResponse ) throws ServletException, IOException {
		// Before servicing the request, make sure that initialization has been done.
		if(!inited)
			lazyInit();

		doService(servletRequest, servletResponse);
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
