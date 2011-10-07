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

import org.kablink.teaming.gwt.client.binderviews.DiscussionFolderView;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.landingpage.LandingPage;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.ShowContentControlEvent;
import org.kablink.teaming.gwt.client.event.ShowDiscussionFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowLandingPageEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.ViewInfo;
import org.kablink.teaming.gwt.client.util.ViewType;
import org.kablink.teaming.gwt.client.util.WorkspaceType;
import org.kablink.teaming.gwt.client.GwtConstants;
import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.RequestInfo;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
		ChangeContextEvent.Handler,
		ShowDiscussionFolderEvent.Handler,
		ShowLandingPageEvent.Handler
{
	private boolean m_isAdminContent;
	private boolean m_isDebugUI;
	private boolean m_isGraniteGwtEnabled;
	private GwtMainPage m_mainPage;
	private NamedFrame m_frame;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Context events.
		TeamingEvents.CHANGE_CONTEXT,
		
		// Show events.
		TeamingEvents.SHOW_DISCUSSION_FOLDER,
		TeamingEvents.SHOW_LANDING_PAGE,
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

		// Extract some commonly used flags from the RequestInfo.
		RequestInfo ri = GwtClientHelper.getRequestInfo();
		m_isDebugUI           = ri.isDebugUI();
		m_isGraniteGwtEnabled = ri.isGraniteGwtEnabled();

		// Is this other than the admin control's content panel?
		m_isAdminContent = ( name.equals( "adminContentControl" ));
		if ( !m_isAdminContent )
		{
			// Yes!  Register the events to be handled by this class.
			EventHelper.registerEventHandlers(
				GwtTeaming.getEventBus(),
				m_registeredEvents,
				this );
		}
		
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.addStyleName( "contentControl" );

		// Give the IFRAME a name so that view_workarea_navbar.jsp,
		// doesn't set the URL of the browser.
		m_frame = new NamedFrame( name );
		m_frame.setPixelSize( 700, 500 );
		m_frame.getElement().setId( m_isAdminContent ?  "adminContentControl" : "contentControl" );
		m_frame.setUrl( "" );
		mainPanel.add( m_frame );
		
		// All composites must call initWidget() in their constructors.
		initWidget( mainPanel );
	}// end ContentControl()
	
	
	/**
	 * Clear the contents of the IFRAME.
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
		// Clear the IFRAME content.
		clear();
		
		// Remember the current URL.
		String url = m_frame.getUrl();

		// Reload the URL.
		setUrl(         ""  );
		setViewFromUrl( url );
	}// end reload()
	
	
	/**
	 * Set the width and height of this control.
	 */
	public void setDimensions( int width, int height )
	{
		if ( isVisible() )
		{
			// Adjust the width and height for proper spacing.
			width  += GwtConstants.CONTENT_WIDTH_ADJUST;
			height += GwtConstants.CONTENT_HEIGHT_ADJUST;
			
			// Set the width and height of the frame.
			setSize( String.valueOf( width ) + "px", String.valueOf( height ) + "px" );
			m_frame.setPixelSize( width, height );
	
			// Does the content panel contain a task listing?
			FrameElement fe = getContentFrame();
			if ( null != fe ) 
			{
				Document doc;

				try
				{
					doc = fe.getContentDocument();
					if ( doc != null && null != doc.getElementById( "gwtTasks" ) )
					{
						// Yes!  Let it resize if it needs to.
						jsResizeTaskListing();
					}
				}
				catch (Exception ex)
				{
					// Nothing to do.
				}
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
	 * This method will set the URL used by the IFRAME.
	 * 
	 * @param url
	 */
	public void setUrl( String url )
	{
		m_frame.setUrl( url );
	}// end setUrl()

	/*
	 * Asynchronously loads a view based on a ViewInfo.
	 */
	private void setViewAsync( final ViewInfo vi, final String url ) {
		ScheduledCommand doSetView = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				setViewNow( vi, url );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( doSetView );
	}// end setViewAsync()

	/*
	 * If the Granite GWT UI is enabled, sets the view based on the
	 * URL.  Otherwise, simply loads the URL in the content frame the
	 * way it has always been done.
	 */
	private void setViewFromUrl( final String url )
	{
		// Are we running the Granite GWT extensions?
		if ( m_isGraniteGwtEnabled && (!m_isAdminContent) )
		{			
			// Yes!  Use the URL to get a ViewInfo for the new
			// context.
			GetViewInfoCmd cmd = new GetViewInfoCmd( url );
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>()
			{
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetViewInfo(),
						url );
				}// end onFailure()
				
				public void onSuccess( VibeRpcResponse response )
				{				
					// Show the context asynchronously so that we can
					// release the AJAX request ASAP.
					ViewInfo vi = ((ViewInfo) response.getResponseData());
					setViewAsync( vi, url );
				}//end onSuccess()
			});
		}
			
		else
		{
			// No, we aren't running the Granite GWT extensions!
			// Put the change into affect the old way via the URL.
			setUrl( url );
		}
	}// end setViewFromUrl()
	
	/*
	 * Synchronously loads a view based on a ViewInfo.
	 * 
	 * If a view cannot be determined (or no ViewInfo was provided),
	 * the URL is loaded into the IFRAME instead.
	 */
	private void setViewNow( final ViewInfo vi, final String url ) {
		// Do we have a ViewInfo?
		boolean viHandled = false;
		if ( null != vi )
		{
			// What type of view is it?
			ViewType vt = vi.getViewType();
			switch ( vt )
			{
			case BINDER:
				// Are we in UI debug mode?
				final BinderInfo bi = vi.getBinderInfo();
				if ( m_isDebugUI )
				{
					// Regardless of what's implemented or not, should
					// we force this binder through its old, JSP flow?
					//
					// While write the GWT based views, I've
					// continually wanted to go back and look at the
					// JSP version of what I'm implementing.  This lets
					// us force a binder, regardless of type, to ALWAYS
					// go through the JSP display flow.
					String binderTitle = bi.getBinderTitle().trim().toLowerCase();
					if (binderTitle.startsWith("jsp-") && binderTitle.endsWith("-jsp")) {
						// Yes!  Simply break out of the switch.  That
						// will let it take the default flow.
						break;
					}
				}
				
				// Regardless of the type, we'll need an ViewReady to
				// clean up things after the view is loaded.  Create
				// one now.
				final String binderIdS = bi.getBinderId();
				final Long   binderId  = Long.parseLong( binderIdS );
				ViewReady viewReady = new ViewReady()
				{
					@Override
					public void viewReady()
					{
						GwtTeaming.fireEvent(
							new ContextChangedEvent(
								new OnSelectBinderInfo(
									binderIdS,
									url,
									bi.isBinderTrash(),
									Instigator.CONTENT_AREA_CHANGED ) ) );
					}//end viewReady()
				};
				
				// What type of binder is it?
				BinderType bt = bi.getBinderType();
				switch ( bt )
				{
				case FOLDER:
					// What type of folder is it?
					FolderType ft = bi.getFolderType();
					switch ( ft )
					{
					case DISCUSSION:
						GwtTeaming.fireEvent( new ShowDiscussionFolderEvent( binderId, viewReady ) );
						viHandled = true;
						break;
						
						
					case BLOG:
					case CALENDAR:
					case FILE:
					case GUESTBOOK:
					case MILESTONE:
					case MINIBLOG:
					case MIRROREDFILE:
					case PHOTOALBUM:
					case SURVEY:
					case TASK:
					case TRASH:
					case WIKI:
						// These aren't handled!  Let things take the
						// default flow.
						break;
						
					default:
						// Something we don't know how to handle!
						if ( m_isDebugUI )
						{
							Window.alert( "ContentControl.setViewNow( Unhandled FolderType:  " + ft.name() + " )" );
						}
						break;
					}
					break;
					
				case WORKSPACE:
					// What type of workspace is it?
					WorkspaceType wt = bi.getWorkspaceType(); 
					switch ( wt )
					{
					case LANDING_PAGE:
						GwtTeaming.fireEvent( new ShowLandingPageEvent( binderIdS, viewReady ) );
						viHandled = true;
						break;
						
					case DISCUSSIONS:
					case GLOBAL_ROOT:
					case PROFILE_ROOT:
					case PROJECT_MANAGEMENT:
					case TEAM:
					case TEAM_ROOT:
					case TOP:
					case TRASH:
					case USER:
					case WORKSPACE:
						// These aren't handled!  Let things take the 
						// default flow.
						break;
					
					default:
						// Something we don't know how to handle!  
						if ( m_isDebugUI )
						{
							Window.alert( "ContentControl.setViewNow( Unhandled WorkspaceType:  " + wt.name() + " )" );
						}
						break;
					}
					break;
				
				default:
					// Something we don't know how to handle!
					if ( m_isDebugUI )
					{
						Window.alert( "ContentControl.setViewNow( Unhandled BinderType:  " + bt.name() + " )" );
					}
					break;
				}
				break;
				
			case ADVANCED_SEARCH:
				// These aren't handled!  Let things take the default
				// flow.
				break;
				
			default:
				// Something we don't know how to handle!
				if ( m_isDebugUI )
				{
					Window.alert( "ContentControl.setViewNow( Unhandled ViewType:  " + vt.name() + " )" );
				}
				break;
			}			
		}

		// Did we handle the ViewInfo as a view?
		if ( !viHandled )
		{
			// No!  Load the URL instead and make sure the
			// ContentControl is showing.
			setUrl( url );			
			ShowContentControlEvent.fireOne();
		}
	}// end setViewNow()
	
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
			// Yes!  Tell everybody the context is about to be changed
			// and change it.
			ContextChangingEvent.fireOne();						
			setViewFromUrl( osbInfo.getBinderUrl() );
		}
	}// end onChangeContext()
	
	/**
	 * Handles ShowDiscussionFolderEvent's received by this class.
	 * 
	 * Implements the ShowDiscussionFolderEvent.Handler.onShowDiscussionFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowDiscussionFolder( ShowDiscussionFolderEvent event )
	{
		// Create a DiscussionFolderView widget for the selected binder.
		DiscussionFolderView.createAsync(
				event.getBinderId(),
				event.getViewReady(),
				new DiscussionFolderView.DiscussionFolderViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( DiscussionFolderView dfView )
			{
				dfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( dfView );
			}// end onSuccess()
		});
	}// end onShowDiscussionFolder()
	
	/**
	 * Handles ShowLandingPageEvent's received by this class.
	 * 
	 * Implements the ShowLandingPageEvent.Handler.onShowLandingPage() method.
	 */
	@Override
	public void onShowLandingPage( ShowLandingPageEvent event )
	{
		// Display a landing page for the given binder id.
		LandingPage.LandingPageClient lpClient;
		
		lpClient = new LandingPage.LandingPageClient()
		{
			/**
			 * 
			 */
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			/**
			 * 
			 */
			@Override
			public void onSuccess( LandingPage landingPage )
			{
				landingPage.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( landingPage );
			}// end onSuccess()
		};
		
		// Create a LandingPage widget for the selected binder.
		LandingPage.createAsync( event.getBinderId(), event.getViewReady(), lpClient );
	}// end onShowLandingPage()
	
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
