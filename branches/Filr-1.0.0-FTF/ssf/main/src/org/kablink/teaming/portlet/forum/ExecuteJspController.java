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
package org.kablink.teaming.portlet.forum;

import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.portletadapter.portlet.HttpServletRequestReachable;
import org.kablink.teaming.portletadapter.portlet.HttpServletResponseReachable;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.kablink.teaming.web.util.WorkspaceTreeHelper;
import org.kablink.util.servlet.StringServletResponse;
import org.springframework.web.portlet.ModelAndView;


/**
 * This class handles the requests to execute a jsp
 * 
 * @author Jay Wootton
 *
 */
public class ExecuteJspController extends SAbstractController
{
	/**
	 * 
	 */
	public void handleActionRequestAfterValidation(
			ActionRequest	request,
			ActionResponse	response ) throws Exception
	{
		String	jspName;
		String binderId;
		String configStr;
		
		// Make the jsp name passed in the request visible to handleRenderRequestAfterValidation() 
		jspName = PortletRequestUtils.getStringParameter( request, WebKeys.JSP_NAME, "" );
		response.setRenderParameter( WebKeys.JSP_NAME, jspName );
		
		// Make the binder id passed into the request visible to handleRenderRequestAfterValidation()
		binderId = PortletRequestUtils.getStringParameter( request, WebKeys.URL_BINDER_ID, "" );
		response.setRenderParameter( WebKeys.URL_BINDER_ID, binderId );
		
		// Make the config string passed into the request visible to handleRenderRequestAfterValidation()
		configStr = PortletRequestUtils.getStringParameter( request, WebKeys.CONFIG_STRING );
		response.setRenderParameter( WebKeys.CONFIG_STRING, configStr );
	}
	
	
	/**
	 * 
	 */
	public ModelAndView handleRenderRequestAfterValidation(
			RenderRequest	request, 
			RenderResponse	response ) throws Exception
	{
		String jspName;
		String configStr;
		String results;
		Long binderIdL;
		HttpServletRequest httpReq;
		HttpServletResponse httpResp;		
		RequestDispatcher reqDispatcher;
		ServletOutputStream outputStream;
 		
		results = "";
		
		httpReq = ((HttpServletRequestReachable) request).getHttpServletRequest();
		httpResp = ((HttpServletResponseReachable)response).getHttpServletResponse();
 			
 		// Get the name of the jsp to be executed
 		jspName = PortletRequestUtils.getStringParameter( request, WebKeys.JSP_NAME, "" );
 		
 		// Get the id of the binder we are working with.
		binderIdL = PortletRequestUtils.getLongParameter( request, WebKeys.URL_BINDER_ID );
		
		// Get the configuration string for the landing page element.
		configStr = PortletRequestUtils.getStringParameter( request, WebKeys.CONFIG_STRING, "" );
 		
 		if ( jspName != null && jspName.length() > 0 && binderIdL != null && configStr != null && configStr.length() > 0 )
 		{
 			try
 			{
	 			String path;
				Map<String, Object> model;
				StringServletResponse ssResponse;
	 			
				// Create the beans needed by the jsp
				model = new HashMap<String, Object>();
	 			WorkspaceTreeHelper.setupWorkspaceBeans( this, binderIdL, request, response, model, false );
	
				// Put the data that setupWorkspaceBeans() put in model into the request.
				for (String key: model.keySet())
				{
					Object value;
					
					value = model.get( key );
					httpReq.setAttribute( key, value );
				}
				
				// Add the data that normally would have been added by mashup_canvas_view.jsp
				{
					Map map1;
					Map map2;
					
					map1 = new HashMap();
					map2 = new HashMap();
					map1.put( 0, "" );
					map2.put( 0, Long.valueOf( 0 ) );
	
					httpReq.setAttribute( "ss_mashupTableDepth", Long.valueOf( 0 ) );
					httpReq.setAttribute( "ss_mashupTableNumber", Long.valueOf( 0 ) );
					httpReq.setAttribute( "ss_mashupTableItemCount", map1 );
					httpReq.setAttribute( "ss_mashupTableItemCount2", map2 );
					httpReq.setAttribute( "ss_mashupListDepth", Long.valueOf( 0 ) );
				}
				
				// Add the data that normally would have been added by MashupTag.java
				if ( configStr != null )
				{
					String[] mashupItemValues;
					
					mashupItemValues = configStr.split(",");
					if ( mashupItemValues.length > 0 )
					{
						Map<String, String> mashupItemAttributes;
	
						//Build a map of attributes
						mashupItemAttributes = new HashMap<String, String>();
						for (int i = 1; i < mashupItemValues.length; i++)
						{
							int k = mashupItemValues[i].indexOf("=");
							if ( k > 0 )
							{
								String a = mashupItemValues[i].substring(0, k);
								String v = mashupItemValues[i].substring(k+1, mashupItemValues[i].length());
								String value1 = v;
								try
								{
									value1 = URLDecoder.decode(v.replaceAll("\\+", "%2B"), "UTF-8");
								}
								catch(Exception e)
								{
								}
								
								if ( a != null && !a.equalsIgnoreCase( "width" ) && !a.equalsIgnoreCase( "height" ) && !a.equalsIgnoreCase( "overflow" ) )
									mashupItemAttributes.put(a, value1);
							}
						}
	
						httpReq.setAttribute( "mashup_id", 0 );
						httpReq.setAttribute( "mashup_type", "enhancedView" );
						httpReq.setAttribute( "mashup_values", mashupItemValues );
						httpReq.setAttribute( "mashup_attributes", mashupItemAttributes );
						httpReq.setAttribute( "mashup_view", "view" );
					}
				}
	
				// Construct the full path to the jsp
	 			path = "/WEB-INF/jsp/landing_page_enhanced_views/" + jspName;
	
	 			reqDispatcher = httpReq.getRequestDispatcher( path );
	 			ssResponse = new StringServletResponse( httpResp );
	
				// Execute the jsp
				reqDispatcher.include( httpReq, ssResponse );
				results = ssResponse.getString().trim();
 			} 		
			catch ( Exception e )
			{
				String[] errorArgs;
				String errorTag = "errorcode.unexpectedError";
				
				errorArgs = new String[] { e.getLocalizedMessage() };
				results = NLT.get( errorTag, errorArgs );
			}
 		}
 		else
 		{
 			// We should never get here.
 			if ( jspName == null || jspName.length() == 0 )
 			{
 				results = "jsp name is empty";
 			}
 			else if ( binderIdL == null )
 			{
 				results = "binder id is empty";
 			}
 			else if ( configStr == null || configStr.length() == 0 )
 			{
 				results = "configuration string is empty";
 			}
 		}
 		
		// Send the results to the browser.
		outputStream = httpResp.getOutputStream();
		httpResp.setContentType( "text/html" );
		outputStream.print( results );
		outputStream.close();

		return null;
	}
}
