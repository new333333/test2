/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.AdministrationEvent;
import org.kablink.teaming.gwt.client.event.AdministrationUpgradeCheckEvent;
import org.kablink.teaming.gwt.client.event.EditPersonalPreferencesEvent;
import org.kablink.teaming.gwt.client.event.InvokeHelpEvent;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewResourceLibraryEvent;
import org.kablink.teaming.gwt.client.event.ViewTeamingFeedEvent;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.rpc.shared.GetSiteAdminUrlCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * This popup menu is used to display the actions in the masthead, such as help, personal prefs, resource library.
 * @author jwootton
 *
 */
public class MastheadPopupMenu extends PopupMenu
{
	PopupMenuItem m_adminMenuItem;
	
	/**
	 * 
	 */
	public MastheadPopupMenu( String mastheadBinderId, boolean isUserLoggedIn, boolean autoHide, boolean modal )
	{
		super( autoHide, modal );
		
		// Add all the possible menu items.
		{
			GwtTeamingMessages messages;
			Image img;
			
			messages = GwtTeaming.getMessages();
			
			if ( isUserLoggedIn )
			{
				// Create the "Administration" menu item.
				{
					img = new Image( GwtTeaming.getImageBundle().adminMenuImg() );
					m_adminMenuItem = addMenuItem( new AdministrationEvent(), img, messages.adminMenuItem() );
					
					// Issue an ajax request to see if the user has rights to run the "site adminitration" page.
					checkAdminRights( mastheadBinderId );
				}
				
				// Create the "Personal preferences" menu item.
				img = new Image( GwtTeaming.getImageBundle().personalPrefsMenuImg() );
				addMenuItem( new EditPersonalPreferencesEvent(), img, messages.personalPrefsMenuItem() );
			
				// Create the "Open News Feed" menu item.
				img = new Image( GwtTeaming.getImageBundle().newsFeedMenuImg() );
				addMenuItem( new ViewTeamingFeedEvent(), img, messages.newsFeedMenuItem() );
				
				// Add a separator
				addSeparator();
			}
			
			// Create the "Vibe Resource Library" menu item.
			{
				img = new Image( GwtTeaming.getImageBundle().resourceLibMenuImg() );
				addMenuItem( new ViewResourceLibraryEvent(), img, messages.resourceLibMenuItem() );
			}

			// Create the "Help" menu item.
			{
				img = new Image( GwtTeaming.getImageBundle().helpMenuImg() );
				addMenuItem( new InvokeHelpEvent(), img, messages.helpMenuItem() );
			}
		}
	}
	

	/**
	 * 
	 * @param event
	 * @param img
	 * @param text
	 * 
	 * @return
	 */
	@Override
	public PopupMenuItem addMenuItem( VibeEventBase<?> event, Image img, String text )
	{
		PopupMenuItem menuItem;

	    menuItem = super.addMenuItem( event, img, text );

		return menuItem;
	}
	
	/**
	 * Issue an ajax request to see if the user has rights to run the "site administration" page.
	 * If they don't we will remove the "administration" menu item from the menu.
	 */
	private void checkAdminRights( String mastheadBinderId )
	{
		AsyncCallback<VibeRpcResponse> rpcCallback;

		rpcCallback = new AsyncCallback<VibeRpcResponse>()
		{
			/**
			 * 
			 */
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
				m_adminMenuItem.removeFromParent();
				m_adminMenuItem = null;
			}
	
			/**
			 * 
			 * @param result
			 */
			public void onSuccess( VibeRpcResponse response )
			{
				String url;
				StringRpcResponseData responseData;
				
				responseData = (StringRpcResponseData) response.getResponseData();
				url = responseData.getStringValue();
				
				// Did we get a url for the "site administration" action?
				if ( url != null && url.length() > 0 )
				{
					Scheduler.ScheduledCommand cmd;
					
					// Yes
					cmd = new Scheduler.ScheduledCommand()
					{
						/**
						 * 
						 */
						public void execute()
						{
							// Since the user has administration rights, show them a list of
							// upgrade tasks that still need to be performed.
							// Sent event to check for tasks
							AdministrationUpgradeCheckEvent.fireOne();
						}
					};
					Scheduler.get().scheduleDeferred( cmd );
				}
			}
		};
		
		// Issue an ajax request to get the url for the "site administration" action.
		if ( mastheadBinderId != null && mastheadBinderId.length() > 0 )
		{
			GetSiteAdminUrlCmd cmd;

			cmd = new GetSiteAdminUrlCmd( mastheadBinderId );
			GwtClientHelper.executeCommand( cmd, rpcCallback );
		}
	}
	
	/**
	 * Show this popup menu.
	 */
	public void showMenu( final int x, final int y )
	{
		PopupPanel.PositionCallback posCallback;

		posCallback = new PopupPanel.PositionCallback()
		{
			/**
			 * 
			 */
			public void setPosition( int offsetWidth, int offsetHeight )
			{
				setPopupPosition( x - offsetWidth, y );
			}
		};
		setPopupPositionAndShow( posCallback );
	}
}
