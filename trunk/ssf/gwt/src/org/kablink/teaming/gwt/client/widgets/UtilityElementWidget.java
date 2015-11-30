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
package org.kablink.teaming.gwt.client.widgets;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.AdministrationEvent;
import org.kablink.teaming.gwt.client.event.GotoMyWorkspaceEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareBinderEvent;
import org.kablink.teaming.gwt.client.event.LoginEvent;
import org.kablink.teaming.gwt.client.event.TrackCurrentBinderEvent;
import org.kablink.teaming.gwt.client.lpe.UtilityElement;
import org.kablink.teaming.gwt.client.lpe.UtilityElementConfig;
import org.kablink.teaming.gwt.client.lpe.UtilityElementProperties;
import org.kablink.teaming.gwt.client.rpc.shared.GetSiteAdminUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.InlineLabel;

/**
 * ?
 *  
 * @author jwootton
 */
public class UtilityElementWidget extends VibeWidget
{
	private UtilityElementProperties m_properties;
	private String m_style;
	private InlineLabel m_link;

	/**
	 * 
	 */
	public UtilityElementWidget( UtilityElementConfig config, WidgetStyles widgetStyles )
	{
		VibeFlowPanel mainPanel;
		
		mainPanel = init( config, widgetStyles );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}
	
	/**
	 * Issue an ajax request to see if the user has rights to run the "site administration" page.
	 * If they do we will show the "Vibe Administration Page" link.
	 */
	private void checkAdminRights()
	{
		String binderId;
		AsyncCallback<VibeRpcResponse> rpcCallback;

		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			@Override
			public void onFailure( Throwable t )
			{
				// Note:  We don't pass a string here such as
				//   rpcFailure_GetSiteAdminUrl() because it would
				//   get displayed for guest, and all other
				//   non-admin users.  Not passing a string here
				//   allows the proper exception handling to occur
				//   but will NOT display an error to the user.
				GwtClientHelper.handleGwtRPCFailure( t );
				
				// The user does not have the rights to run the "site administration" page.
				// Remove the admin link.
				m_link.removeFromParent();
				m_link = null;
			}
	
			@Override
			public void onSuccess( VibeRpcResponse response )
			{
				String url;
				StringRpcResponseData responseData;
				
				responseData = (StringRpcResponseData) response.getResponseData();
				url = responseData.getStringValue();
				
				// Did we get a url for the "site administration" action?
				if ( url != null && url.length() > 0 )
				{
					// Yes
					// Show the "Vibe administration Page" link.
					m_link.setVisible( true );
				}
			}
		};
		
		// Issue an ajax request to see if the user had administration rights.
		binderId = m_properties.getBinderId();
		if ( binderId != null && binderId.length() > 0 )
		{
			GetSiteAdminUrlCmd cmd;

			cmd = new GetSiteAdminUrlCmd( binderId );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * 
	 */
	private void handleClickOnLink()
	{
		UtilityElement type;
		
		type = m_properties.getType();
		if ( type == UtilityElement.LINK_TO_MYWORKSPACE )
		{
			// Fire the "go to my workspace" event.
			GotoMyWorkspaceEvent.fireOne();
		}
		else if ( type == UtilityElement.LINK_TO_TRACK_FOLDER_OR_WORKSPACE )
		{
			TrackCurrentBinderEvent tcbEvent;
			
			// Fire the event to track this workspace.
			tcbEvent = new TrackCurrentBinderEvent( false );
			GwtTeaming.fireEvent( tcbEvent );
			
			// Tell the user they are now following this workspace.
			Window.alert( GwtTeaming.getMessages().nowFollowingBinder() );
		}
		else if ( type == UtilityElement.LINK_TO_SHARE_FOLDER_OR_WORKSPACE )
		{
			InvokeShareBinderEvent isbEvent;
			
			// Fire the "invoke share binder" event.
			isbEvent = new InvokeShareBinderEvent( m_properties.getBinderId() );
			GwtTeaming.fireEvent( isbEvent );
		}
		else if ( type == UtilityElement.LINK_TO_ADMIN_PAGE )
		{
			// If we get here we know the user has admin rights.
			// Open the administration page.
			AdministrationEvent.fireOne();
		}
		else if ( type == UtilityElement.SIGNIN_FORM )
		{
			// Bugzilla 956854:
			//    Invoke the login dialog, sending the user to the base
			//    URL.  Prior to this fix, it simply fired a LoginEvent
			//    without a refererUrl.  I copied doing it this way
			//    from the 'Sign In' link provided by MastHead.java.
			LoginEvent loginEvent = new LoginEvent();
			String refererUrl = (Window.Location.getProtocol() + "//" + Window.Location.getHost());
			loginEvent.setRefererUrl(refererUrl);
			GwtTeaming.fireEvent(loginEvent);
		}
	}

	/**
	 * 
	 */
	private VibeFlowPanel init( UtilityElementConfig config, WidgetStyles widgetStyles )
	{
		UtilityElementProperties properties;
		VibeFlowPanel mainPanel;
		String title;
		boolean isValid;
		
		m_properties = new UtilityElementProperties();
		properties = config.getProperties();
		m_properties.copy( properties );
		
		m_style = config.getLandingPageStyle();
		
		mainPanel = new VibeFlowPanel();
		mainPanel.addStyleName( "landingPageWidgetMainPanel" + m_style );
		mainPanel.addStyleName( "utilityElementWidgetMainPanel" + m_style );
		
		m_link = new InlineLabel( "" );
		m_link.addStyleName( "utilityElementWidgetLink" + m_style );
		
		// Get the appropriate title
		{
			UtilityElement type;
			
			isValid = true;
			type = m_properties.getType();
			
			if ( type == UtilityElement.LINK_TO_MYWORKSPACE )
				title = GwtTeaming.getMessages().utilityElementMyWorkspace();
			else if ( type == UtilityElement.LINK_TO_TRACK_FOLDER_OR_WORKSPACE )
			{
				title = GwtTeaming.getMessages().utilityElementFollowWorkspace();
				isValid = GwtTeaming.m_requestInfo.isUserLoggedIn();
			}
			else if ( type == UtilityElement.LINK_TO_SHARE_FOLDER_OR_WORKSPACE )
			{
				title = GwtTeaming.getMessages().utilityElementShareWorkspace();
				isValid = GwtTeaming.m_requestInfo.isUserLoggedIn();
			}
			else if ( type == UtilityElement.LINK_TO_ADMIN_PAGE )
			{
				title = GwtTeaming.getMessages().utilityElementAdminPage();
				isValid = true;
				
				// Hide the link.  We will show the link if the user has administration rights.
				m_link.setVisible( false );
				
				// Issue an ajax request to see if the user has administration rights.
				checkAdminRights();
			}
			else if ( type == UtilityElement.SIGNIN_FORM )
			{
				title = GwtTeaming.getMessages().utilityElementLogIn();
				isValid = !GwtTeaming.m_requestInfo.isUserLoggedIn();
			}
			else
			{
				title = "Unknown utility element";
				isValid = false;
			}
		}
		
		// Is it valid to add this utility element?
		if ( isValid )
		{
			m_link.setText( title );
			
			m_link.addClickHandler( new ClickHandler()
			{
				@Override
				public void onClick( ClickEvent event )
				{
					handleClickOnLink();
				}
			} );

			// Set the text color for the content.
			GwtClientHelper.setElementTextColor( m_link.getElement(), widgetStyles.getContentTextColor() );
			
			mainPanel.add( m_link );
		}
		
		return mainPanel;
	}
}
