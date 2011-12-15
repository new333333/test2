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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.DiscussionFolderView;
import org.kablink.teaming.gwt.client.binderviews.FileFolderView;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.binderviews.landingpage.LandingPage;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.GotoUrlEvent;
import org.kablink.teaming.gwt.client.event.ShowContentControlEvent;
import org.kablink.teaming.gwt.client.event.ShowDiscussionFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowFileFolderEvent;
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
		ContributorIdsRequestEvent.Handler,
		ChangeContextEvent.Handler,
		GotoUrlEvent.Handler,
		ShowDiscussionFolderEvent.Handler,
		ShowFileFolderEvent.Handler,
		ShowLandingPageEvent.Handler
{
	private boolean m_contentInGWT;
	private boolean m_isAdminContent;
	private boolean m_isDebugUI;
	private boolean m_isDebugLP;
	private boolean m_isGraniteGwtEnabled;
	private GwtMainPage m_mainPage;
	private NamedFrame m_frame;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[] {
		// Context events.
		TeamingEvents.CHANGE_CONTEXT,
		TeamingEvents.GOTO_URL,
		
		// Contributor events.
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		
		// Show events.
		TeamingEvents.SHOW_DISCUSSION_FOLDER,
		TeamingEvents.SHOW_FILE_FOLDER,
		TeamingEvents.SHOW_LANDING_PAGE,
	};

	// Maximum number of URLs tracked in the content history stack.
	public final static int CONTENT_HISTORY_MAXDEPTH	= 2;
	
	
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
		m_isDebugLP = ri.isDebugLP();
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
		
		// Initialize the JavaScript for tracking content history.
		initContentHistoryJS( this, CONTENT_HISTORY_MAXDEPTH );
		
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
	 * Returns a List<String> of the URLs currently store in the
	 * content history.
	 * 
	 * @return
	 */
	public List<String> getContentHistory()
	{
		// Scan the URLs from the content history...
		List<String> reply = new ArrayList<String>();
		for ( int i = 0; true; i += 1 )
		{
			String url = jsGetContentHistoryUrl( i );
			if ( null == url )
			{
				break;
			}
			
			// ...adding each to the List<String>.
			reply.add( url );
		}
		
		// If we get here, reply refers to a List<String> of the URLs
		// from the content history stack.  Return it.
		return reply;
	}// end getContentHistory()
	
	/**
	 * Returns the URL from the content history at the specified index.
	 * 
	 * If there aren't enough items being tracked to satisfy the
	 * request, null is returned.
	 * 
	 * @param index
	 * 
	 * @return
	 */
	public String getContentHistoryUrl( int index )
	{
		// Simply call the JavaScript implementation method.
		return jsGetContentHistoryUrl( index );
	}// end getContentHistoryUrl()

	/*
	 * Initializes the JavaScript for tracking content history.
	 */
	private native void initContentHistoryJS( ContentControl contentControl, int contentHistoryDepth ) /*-{
		// Have we defined the JavaScript elements for tracking content
		// history yet?
		if ( ! $wnd.top.ss_contentHistory )
		{
			// No!  Define them now.
			$wnd.top.ss_contentHistoryDepth = contentHistoryDepth;
			$wnd.top.ss_contentHistory = new Array();
			$wnd.top.ss_getUrlFromContentHistory = function( index )
			{
				return contentControl.@org.kablink.teaming.gwt.client.widgets.ContentControl::jsGetContentHistoryUrl(Ljava/lang/Integer;)( index );
			}//end ss_getUrlFromContentHistory()
		}
	}-*/;

	/*
	 * Returns the URL from the content history at the specified index.
	 * 
	 * If there aren't enough items being tracked to satisfy the
	 * request, null is returned.
	 */
	private native String jsGetContentHistoryUrl( Integer index ) /*-{
		// If we were given a negative index...
		if ( 0 > index )
		{
			// ...invert it.
			index = ( -index );
		}

		// If the request index is beyond what's in the history...
		if ( index >= $wnd.top.ss_contentHistory.length )
		{
			// ...return null.
			return null;
		}

		// Return the URL from the history at the requested index.
		return $wnd.top.ss_contentHistory[index];
	}-*/;

	/*
	 * Pushes a URL on the content history stack.
	 */
	private native void jsPushContentHistoryUrl( String url ) /*-{
		// Push the URL.
		$wnd.top.ss_contentHistory.unshift( url );
		
		// While the stack contains more items that we track...
		while ( $wnd.top.ss_contentHistory.length > $wnd.top.ss_contentHistoryDepth )
		{
			// ...remove the last one from the list.
			$wnd.top.ss_contentHistory.pop();
		}
	}-*/;

	/**
	 * Pushes a URL on the content history stack.
	 * 
	 * @param url
	 */
	private void pushContentHistoryUrl( String url )
	{
		// Simply call the JavaScript implementation method.
		jsPushContentHistoryUrl( url );
	}// end pushContentHistoryUrl()
	
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
			if (!m_isAdminContent) {
				// Adjust the width and height for proper spacing.
				width  += GwtConstants.CONTENT_WIDTH_ADJUST;
				height += GwtConstants.CONTENT_HEIGHT_ADJUST;
			}
			
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
		m_contentInGWT = false;
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
					// While writing the GWT based views, I've
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
				ViewReady viewReady = new ViewReady()
				{
					@Override
					public void viewReady()
					{
						GwtClientHelper.jsSetMainTitle(bi.getBinderTitle());
						GwtTeaming.fireEvent(
							new ContextChangedEvent(
								new OnSelectBinderInfo(
									bi.getBinderId(),
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
						GwtTeaming.fireEvent( new ShowDiscussionFolderEvent( bi, viewReady ) );
						m_contentInGWT = true;
						break;
						
						
					case FILE:
						GwtTeaming.fireEvent( new ShowFileFolderEvent( bi, viewReady ) );
						m_contentInGWT = true;
						break;
						
						
					case BLOG:
					case CALENDAR:
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
					{
						boolean showNew = true;
						
						if ( m_isDebugLP )
						{
							if ( !Window.confirm( "Show new landing page?" ) )
								showNew = false;
						}
						
						if ( showNew )
						{
							GwtTeaming.fireEvent( new ShowLandingPageEvent( bi, viewReady ) );
							m_contentInGWT = true;
						}
						break;
					}
						
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
				
			case ADD_FOLDER_ENTRY:
			case ADVANCED_SEARCH:
			case BUILD_FILTER:
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
		if ( !m_contentInGWT )
		{
			// No!  Load the URL instead and make sure the
			// ContentControl is showing.
			setUrl( url );
			m_mainPage.getMainContentLayoutPanel().showWidget( null );
			ShowContentControlEvent.fireOne();
		}

		// Finally, push the URL we just processed on the content
		// history stack.
		pushContentHistoryUrl( url );
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
	 * Handles ContributorIdsRequestEvent's received by this class.
	 * 
	 * Implements the ContributorIdsRequestEvent.Handler.onContributorIdsRequest() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContributorIdsRequest( ContributorIdsRequestEvent event )
	{
		// Is the content being handled by a GWT widget?
		if ( m_contentInGWT )
		{
			// Yes!  Bail as we only respond to requests for the
			// content IFRAME.
			return;
		}
		
		// Does the content IFRAME have a binder ID?
		String contentBinderId = GwtClientHelper.jsGetContentBinderId();
		if ( ! ( GwtClientHelper.hasString( contentBinderId ) ) )
		{
			// No!  Bail.
			return;
		}
		
		// Is this event targeted to that binder?
		final Long eventBinderId = event.getBinderId();
		if ( ! ( eventBinderId.equals( Long.parseLong( contentBinderId ) ) ) )
		{
			// No!  Bail.
			return;
		}

		// Does the content IFRAME have a list of contributor IDs?
		String contributorIdsString = GwtClientHelper.jsGetContentContributorIds();
		if ( ! ( GwtClientHelper.hasString( contributorIdsString ) ) )
		{
			// No!  Bail.
			return;
		}

		// Can we parse any contributor IDs from that list?
		String[] contributorIdsS = contributorIdsString.split( "," );
		final List<Long> contributorIds = new ArrayList<Long>();
		for ( String cId:  contributorIdsS )
		{
			try {contributorIds.add( Long.parseLong( cId.trim() ) );}
			catch (Exception ex) {}
		}
		if ( contributorIds.isEmpty() )
		{
			// No!  Bail.
			return;
		}

		// If we get here, the IFRAME contained a list of contributor
		// ID's targeted to the requested binder.  Asynchronously fire
		// a contributor ID's reply event with the contributor IDs.
		ScheduledCommand doReply = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				GwtTeaming.fireEvent(
					new ContributorIdsReplyEvent(
						eventBinderId,
						contributorIds ) );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( doReply );
	}// end onContributorIdsRequest()
	
	
	/**
	 * Handles the GotoUrlEvents received by this class
	 * 
	 * Implements the GotoUrlEvent.Handler.onGotoUrl() method.
	 */
	@Override
	public void onGotoUrl( GotoUrlEvent event )
	{
		setViewNow( null, event.getUrl() );
	}

	
	/**
	 * Handles ShowDiscussionFolderEvent's received by this class.
	 * 
	 * Implements the ShowDiscussionFolderEvent.Handler.onShowDiscussionFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowDiscussionFolder( final ShowDiscussionFolderEvent event )
	{
		// Create a DiscussionFolderView widget for the selected binder.
		DiscussionFolderView.createAsync(
				event.getBinderInfo(),
				event.getViewReady(),
				new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( ViewBase dfView )
			{
				dfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( dfView );
			}// end onSuccess()
		});
	}// end onShowDiscussionFolder()
	
	/**
	 * Handles ShowFileFolderEvent's received by this class.
	 * 
	 * Implements the ShowFileFolderEvent.Handler.onShowFileFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowFileFolder( final ShowFileFolderEvent event )
	{
		// Create a FileFolderView widget for the selected binder.
		FileFolderView.createAsync(
				event.getBinderInfo(),
				event.getViewReady(),
				new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( ViewBase dfView )
			{
				dfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( dfView );
			}// end onSuccess()
		});
	}// end onShowFileFolder()
	
	/**
	 * Handles ShowLandingPageEvent's received by this class.
	 * 
	 * Implements the ShowLandingPageEvent.Handler.onShowLandingPage() method.
	 */
	@Override
	public void onShowLandingPage( ShowLandingPageEvent event )
	{
		// Display a landing page for the given binder id.
		ViewClient vClient;
		
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase landingPage )
			{
				landingPage.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( landingPage );
			}// end onSuccess()
		};
		
		// Create a LandingPage widget for the selected binder.
		LandingPage.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
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
