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

import org.kablink.teaming.gwt.client.event.AdministrationExitEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.NamedFrame;

/**
 * This widget will display the Teaming content for a given
 * folder/workspace.
 * 
 * @author jwootton@novell.com
 */
public class ContentControl extends Composite
	implements
	// Event handlers implemented by this class.
		AdministrationExitEvent.Handler,
		ChangeContextEvent.Handler,
		SidebarHideEvent.Handler,
		SidebarShowEvent.Handler
{
	private GwtMainPage m_mainPage;
	private NamedFrame m_frame;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Administration events.
		TeamingEvents.ADMINISTRATION_EXIT,

		// Context events.
		TeamingEvents.CHANGE_CONTEXT,
		
		// Sidebar events.
		TeamingEvents.SIDEBAR_HIDE,
		TeamingEvents.SIDEBAR_SHOW,
	};
	
	/*
	 * Constructor method.
	 * 
	 * Note that the class constructor is private to facilitate code
	 * splitting.  All instantiations of this object must be done
	 * through its createAsync().
	 */
	private ContentControl( GwtMainPage mainPage, String name )
	{		
		// Store the parameters.
		m_mainPage = mainPage;

		// Is this other than the admin control's content panel?
		boolean isAdminContent = ( name.equals( "adminContentControl" ));
		if ( !isAdminContent )
		{
			// Yes!  Register the events to be handled by this class.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this );
		}
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName( "contentControl" );

		// Give the iframe a name so that view_workarea_navbar.jsp, doesn't set the url of the browser.
		m_frame = new NamedFrame( name );
		m_frame.setPixelSize( 700, 500 );
		m_frame.getElement().setId( isAdminContent ?  "adminContentControl" : "contentControl" );
		m_frame.setUrl( "" );
		mainPanel.add( m_frame );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end ContentControl()
	
	
	/**
	 * Clear the contents of the iframe.
	 */
	public void clear()
	{
		FrameElement frameElement;
		
		frameElement = getContentFrame();
		if ( null != frameElement )
		{
			String html;
			
			html = "<body><div style=\"text-align: center\">" + GwtTeaming.getMessages().oneMomentPlease() + "</div></body>";
			frameElement.getContentDocument().getBody().setInnerHTML( html );
		}
	}
	
	/**
	 * 
	 */
	public void empty()
	{
		FrameElement frameElement;
		
		frameElement = getContentFrame();
		if ( null != frameElement )
		{
			String html;
			
			html = "<body><div/></body>";
			frameElement.getContentDocument().getBody().setInnerHTML( html );
		}
	}

	/*
	 * Returns the FrameElement encompassing this ContentControl.
	 */
	private FrameElement getContentFrame()
	{
		FrameElement reply;
		Element e = m_frame.getElement();
		if ( e instanceof FrameElement )
		     reply = ((FrameElement) e);
		else reply = null;
		return reply;
	}// end getContentFrame()

	/**
	 * Reload the page that is currently being displayed.
	 */
	public void reload()
	{
		String url;
		
		// Clear the iframe content.
		clear();
		
		// Remember the current url.
		url = m_frame.getUrl();

		// Reload the url.
		setUrl( "" );
		setUrl( url );
	}// end reload()
	
	
	/**
	 * Set the width and height of this control.
	 */
	public void setDimensions( int width, int height )
	{
		if ( isVisible() )
		{
			// Set the width and height of the frame.
			setSize( String.valueOf( width ) + "px", String.valueOf( height ) + "px" );
			m_frame.setPixelSize( width, height );
	
			try
			{
				// Does the content panel contain a task listing?
				FrameElement fe = getContentFrame();
				if ( null != fe )
				{
					Document doc;
					
					doc = fe.getContentDocument();
					if ( doc != null )
					{
						if (  null != doc.getElementById( "gwtTasks" ) )
						{
							// Yes!  Let it resize if it needs to.
							jsResizeTaskListing();
						}
					}
				}
			}
			catch (Exception ex)
			{
				// Nothing we need to do.  This generally happens when the iframe has
				// been loaded with a url that is not a Vibe url.  Because of the
				// "Same origin" policy we can't get the frame's content document.
				// See bug 743444
				// Window.alert( "in setDimensions() got an exception: " + ex.toString() );
			}
		}
	}// end setDimensions()

	/*
	 * Uses JSNI to tell the task listing that it may need to be
	 * resized.
	 */
	private static native void jsResizeTaskListing() /*-{
		// If the method to resize the task listing is defined...
		if ( $wnd.top.gwtContentIframe.ss_resizeTasks )
		{
			// ...call it.
			$wnd.top.gwtContentIframe.ss_resizeTasks();
		}
	}-*/;	
	
	/**
	 * This method will set the url used by the iframe.
	 */
	public void setUrl( String url )
	{
		m_frame.setUrl( url );
	}// end setUrl()

	/**
	 * Handles AdministrationExitEvent's received by this class.
	 * 
	 * Implements the AdministrationExitEvent.Handler.onAdministrationExit() method.
	 * 
	 * @param event
	 */
	@Override
	public void onAdministrationExit( AdministrationExitEvent event )
	{
		if ( ! ( isVisible() ) )
		{
			setVisible( true );
		}
	}// end onAdministrationExit()
	
	/**
	 * Handles ChangeContextEvent's received by this class.
	 * 
	 * Implements the ChangeContextEvent.Handler.onChangeContext() method.
	 * 
	 * @param event
	 */
	@Override
	public void onChangeContext( final ChangeContextEvent event )
	{		
		// Is the event data is valid?
		OnSelectBinderInfo osbInfo = event.getOnSelectBinderInfo();
		if ( GwtClientHelper.validateOSBI( osbInfo, false ))
		{
			// Yes!  Tell everybody the context is about to be
			// changed and put the change into affect.
			ContextChangingEvent.fireOne();			
			setUrl( osbInfo.getBinderUrl() );
		}
	}// end onChangeContext()
	
	/**
	 * Handles SidebarHideEvent's received by this class.
	 * 
	 * Implements the SidebarHideEvent.Handler.onSidebarHide() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarHide( SidebarHideEvent event )
	{
		if ( !m_mainPage.isAdminActive() )
		{
			addStyleName( "mainWorkspaceTreeControl" );
		}
	}// end onSidebarHide()
	
	/**
	 * Handles SidebarShowEvent's received by this class.
	 * 
	 * Implements the SidebarShowEvent.Handler.onSidebarShow() method.
	 * 
	 * @param event
	 */
	@Override
	public void onSidebarShow( SidebarShowEvent event )
	{
		if ( !m_mainPage.isAdminActive() )
		{
			removeStyleName( "mainWorkspaceTreeControl" );
		}
	}// end onSidebarShow()
	
	/**
	 * Callback interface to interact with the content control
	 * asynchronously after it loads. 
	 */
	public interface ContentControlClient {
		void onSuccess(ContentControl contentCtrl);
		void onUnavailable();
	}

	/**
	 * Loads the ContentControl split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param mainPage
	 * @param name
	 * @param contentCtrlClient
	 */
	public static void createAsync( final GwtMainPage mainPage, final String name, final ContentControlClient contentCtrlClient )
	{
		GWT.runAsync( ContentControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onSuccess()
			{
				ContentControl contentCtrl = new ContentControl( mainPage, name );
				contentCtrlClient.onSuccess( contentCtrl );
			}// end onSuccess()
			
			@Override
			public void onFailure( Throwable reason )
			{
				Window.alert( GwtTeaming.getMessages().codeSplitFailure_ContentControl() );
				contentCtrlClient.onUnavailable();
			}// end onFailure()
		} );
	}// end createAsync()
}// end ContentControl
