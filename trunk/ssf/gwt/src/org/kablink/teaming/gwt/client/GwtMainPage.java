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

package org.kablink.teaming.gwt.client;


import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.widgets.ContentControl;
import org.kablink.teaming.gwt.client.widgets.MainMenuControl;
import org.kablink.teaming.gwt.client.widgets.MastHead;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl;
import org.kablink.teaming.gwt.client.widgets.WorkspaceTreeControl.TreeMode;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;


/**
 * This widget will display the main Teaming page
 */
public class GwtMainPage extends Composite
	implements ActionHandler, ResizeHandler
{
	private MastHead m_mastHead;
	private MainMenuControl m_mainMenuCtrl;
	private WorkspaceTreeControl m_wsTreeCtrl;
	private ContentControl m_contentCtrl;
	private FlowPanel m_contentPanel;
	private RequestInfo m_requestInfo;


	/**
	 * 
	 */
	public GwtMainPage()
	{
		FlowPanel mainPanel;
		Element bodyElement;

		// Set the class name on the <body> element to "mainGwtTeamingPage"
		bodyElement = RootPanel.getBodyElement();
		bodyElement.setClassName( "mainTeamingPage" );
		
		mainPanel = new FlowPanel();
		mainPanel.addStyleName( "mainTeamingPagePanel" );
		
		// Get information about the request we are dealing with.
		m_requestInfo = getRequestInfo();
		
		// Add the MastHead to the page.
		m_mastHead = new MastHead( m_requestInfo );
		m_mastHead.addActionHandler( this );
		mainPanel.add( m_mastHead );
		
		// Add the main menu to the page.
		m_mainMenuCtrl = new MainMenuControl();
		mainPanel.add( m_mainMenuCtrl );
		
		// Create a panel to hold the WorkspaceTree control and the content control
		m_contentPanel = new FlowPanel();
		m_contentPanel.addStyleName( "mainContentPanel" );
		
		// Create the WorkspaceTree control.
		m_wsTreeCtrl = new WorkspaceTreeControl( m_requestInfo, TreeMode.VERTICAL );
		m_wsTreeCtrl.addStyleName( "mainWorkspaceTreeControl" );
		m_wsTreeCtrl.addActionHandler( this );
		m_contentPanel.add( m_wsTreeCtrl );
		
		// Create the content control.
		m_contentCtrl = new ContentControl();
		m_contentCtrl.addStyleName( "mainContentControl" );
		m_contentCtrl.setUrl( m_requestInfo.getAdaptedUrl() + "&captive=true" );
		m_contentPanel.add( m_contentCtrl );
		
		mainPanel.add( m_contentPanel );
		
		// Add a ResizeHandler to the browser so we'll know when the user resizes the browser.
		Window.addResizeHandler( this );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );

	}// end GwtMainPage()


	/**
	 * Use JSNI to grab the JavaScript object that holds the information about the request dealing with.
	 */
	private native RequestInfo getRequestInfo() /*-{
		// Return a reference to the JavaScript variable called, m_requestInfo.
		return $wnd.m_requestInfo;
	}-*/;
	
	
	/**
	 * Handle the action that was requested by the user somewhere in the main page.
	 * For example, the user clicked on "My Workspace" in the masthead.
	 */
	public void handleAction( TeamingAction action, Object obj )
	{
		switch (action)
		{
		case ADMINISTRATION:
			Window.alert( "Administration is not implemented yet." );
			break;
			
		case HELP:
			Window.alert( "Help is not implemented yet." );
			break;

		case LOGOUT:
			Window.alert( "Logout is not implemented yet." );
			break;
			
		case MY_WORKSPACE:
			// Change the browser's url.
			Window.Location.replace( m_requestInfo.getMyWorkspaceUrl() );
			break;
			
		case SELECTION_CHANGED:
			selectionChanged( obj );
			break;
		
		case SIZE_CHANGED:
			sizeChanged( obj );
			break;
		
		default:
			Window.alert( "Unknown action selected: " + action.getUnlocalizedDesc() );
			break;
		}
	}// end handleAction()
	
	
	/**
	 * This method will be called when the user selects a binder from the workspace tree control.
	 */
	private void selectionChanged( Object obj )
	{
		if ( obj instanceof OnSelectBinderInfo )
		{
			OnSelectBinderInfo binderInfo;
			String binderId;

			// Tell the masthead to update the branding for the newly selected binder.
			binderInfo = (OnSelectBinderInfo) obj;
			binderId = binderInfo.getBinderId().toString();
			m_mastHead.setBinderId( binderId );
			
			// Tell the content panel to view the binder.
			m_contentCtrl.setUrl( binderInfo.getBinderUrl() );
		}
		else
			Window.alert( "in onSelect() and obj is not an OnSelectBinderInfo object" );
	}// end selectionChanged()

	
	/**
	 * This method gets called when the browser gets resized.
	 */
	public void onResize( ResizeEvent event )
	{
		// Adjust the height and width of the controls on this page.
		relayoutPage( true );
	}// end onResize()
	
	
	/**
	 * This method will be called when one of the controls on this page changes size.
	 */
	private void sizeChanged( Object obj )
	{
		// Adjust the height and width of the controls on this page.
		relayoutPage( false );
	}// end sizeChanged()

	
	/**
	 * Adjust the height and width of the controls on this page.  Currently the only
	 * control we adjust is the ContentControl.
	 */
	public void relayoutPage( boolean layoutImmediately )
	{
		int width;
		int height;

		// Are we supposed to relayout now?
		if ( layoutImmediately == true )
		{
			// Yes
			// Calculate how wide the ContentControl should be.
			{
				int clientWidth;
				
				// Get the width of the browser window's client area.
				clientWidth = Window.getClientWidth();
				
				width = clientWidth - m_contentCtrl.getAbsoluteLeft() - 10; 
			}
			
			// Calculate how high the ContentControl should be.
			{
				int clientHeight;
				
				// Get the height of the browser window's client area.
				clientHeight = Window.getClientHeight();
				
				height = clientHeight - m_contentPanel.getAbsoluteTop() - 20;
			}
			
			m_contentCtrl.setDimensions( width, height );
		}
		else
		{
			Timer timer;

			// No, set a timer and then relayout.
			timer = new Timer()
			{
				/**
				 * 
				 */
				@Override
				public void run()
				{
					relayoutPage( true );
				}// end run()
			};
			
			timer.schedule( 250 );
		}
	}// end relayoutPage()

}// end GwtMainPage
