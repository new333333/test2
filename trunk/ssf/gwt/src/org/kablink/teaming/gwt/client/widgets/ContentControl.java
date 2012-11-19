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
package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.BlogFolderView;
import org.kablink.teaming.gwt.client.binderviews.CalendarFolderView;
import org.kablink.teaming.gwt.client.binderviews.CollectionView;
import org.kablink.teaming.gwt.client.binderviews.DiscussionFolderView;
import org.kablink.teaming.gwt.client.binderviews.DiscussionWSView;
import org.kablink.teaming.gwt.client.binderviews.FileFolderView;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryComposite;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryDlg;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryDlg.FolderEntryDlgClient;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryView;
import org.kablink.teaming.gwt.client.binderviews.GenericWSView;
import org.kablink.teaming.gwt.client.binderviews.GuestbookFolderView;
import org.kablink.teaming.gwt.client.binderviews.HomeWSView;
import org.kablink.teaming.gwt.client.binderviews.LandingPageView;
import org.kablink.teaming.gwt.client.binderviews.MicroBlogFolderView;
import org.kablink.teaming.gwt.client.binderviews.MilestoneFolderView;
import org.kablink.teaming.gwt.client.binderviews.MirroredFileFolderView;
import org.kablink.teaming.gwt.client.binderviews.NetFoldersWSView;
import org.kablink.teaming.gwt.client.binderviews.PersonalWorkspacesView;
import org.kablink.teaming.gwt.client.binderviews.ProjectManagementWSView;
import org.kablink.teaming.gwt.client.binderviews.SurveyFolderView;
import org.kablink.teaming.gwt.client.binderviews.TaskFolderView;
import org.kablink.teaming.gwt.client.binderviews.TeamWSView;
import org.kablink.teaming.gwt.client.binderviews.TrashView;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeletePurgeEntriesHelper.DeletePurgeEntriesCallback;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.EventsHandledBySourceMarker;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetCurrentViewInfoEvent;
import org.kablink.teaming.gwt.client.event.GotoUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareBinderEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.PurgeSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntriesEvent;
import org.kablink.teaming.gwt.client.event.ShowBlogFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowCalendarFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowCollectionViewEvent;
import org.kablink.teaming.gwt.client.event.ShowContentControlEvent;
import org.kablink.teaming.gwt.client.event.ShowDiscussionFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowDiscussionWSEvent;
import org.kablink.teaming.gwt.client.event.ShowFileFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowFolderEntryEvent;
import org.kablink.teaming.gwt.client.event.ShowGenericWSEvent;
import org.kablink.teaming.gwt.client.event.ShowGlobalWSEvent;
import org.kablink.teaming.gwt.client.event.ShowGuestbookFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowHomeWSEvent;
import org.kablink.teaming.gwt.client.event.ShowLandingPageEvent;
import org.kablink.teaming.gwt.client.event.ShowMicroBlogFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowMilestoneFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowMirroredFileFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowNetFoldersWSEvent;
import org.kablink.teaming.gwt.client.event.ShowPersonalWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.ShowProjectManagementWSEvent;
import org.kablink.teaming.gwt.client.event.ShowSurveyFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowTaskFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowTeamRootWSEvent;
import org.kablink.teaming.gwt.client.event.ShowTrashEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.ShowTeamWSEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.VibeEventBase;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetParentBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.util.ViewFolderEntryInfo;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.NamedFrame;

/**
 * This widget will display the Vibe content for a given
 * folder/workspace.
 * 
 * @author jwootton@novell.com
 */
@SuppressWarnings("unused")
public class ContentControl extends Composite
	implements
	// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler,
		ChangeContextEvent.Handler,
		CopySelectedEntriesEvent.Handler,
		DeleteSelectedEntriesEvent.Handler,
		GetCurrentViewInfoEvent.Handler,
		GotoUrlEvent.Handler,
		MoveSelectedEntriesEvent.Handler,
		PurgeSelectedEntriesEvent.Handler,
		ShowBlogFolderEvent.Handler,
		ShowCalendarFolderEvent.Handler,
		ShowCollectionViewEvent.Handler,
		ShowDiscussionFolderEvent.Handler,
		ShowDiscussionWSEvent.Handler,
		ShowFileFolderEvent.Handler,
		ShowFolderEntryEvent.Handler,
		ShowGenericWSEvent.Handler,
		ShowGlobalWSEvent.Handler,
		ShowGuestbookFolderEvent.Handler,
		ShowHomeWSEvent.Handler,
		ShowLandingPageEvent.Handler,
		ShowMicroBlogFolderEvent.Handler,
		ShowMilestoneFolderEvent.Handler,
		ShowMirroredFileFolderEvent.Handler,
		ShowNetFoldersWSEvent.Handler,
		ShowPersonalWorkspacesEvent.Handler,
		ShowProjectManagementWSEvent.Handler,
		ShowSurveyFolderEvent.Handler,
		ShowTaskFolderEvent.Handler,
		ShowTeamRootWSEvent.Handler,
		ShowTeamWSEvent.Handler,
		ShowTrashEvent.Handler,
		ViewForumEntryEvent.Handler
{
	private boolean			m_isAdminContent;							//
	private boolean			m_isDebugUI;								//
	private boolean			m_isDebugLP;								//
	private FolderEntryDlg	m_folderEntryDlg;							//
	private GwtMainPage		m_mainPage;									//
	private Instigator		m_contentInstigator = Instigator.UNKNOWN;	//
	private NamedFrame		m_frame;									//
	private ViewInfo		m_currentView;								//
	private ViewMode		m_viewMode = ViewMode.JSP_CONTENT_VIEW;		//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
	{
		// Context events.
		TeamingEvents.CHANGE_CONTEXT,
		TeamingEvents.GOTO_URL,
		
		// Contributor events.
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		
		// Selected entries events.
		TeamingEvents.COPY_SELECTED_ENTRIES,
		TeamingEvents.DELETE_SELECTED_ENTRIES,
		TeamingEvents.MOVE_SELECTED_ENTRIES,
		TeamingEvents.PURGE_SELECTED_ENTRIES,
		
		// Show events.
		TeamingEvents.SHOW_BLOG_FOLDER,
		TeamingEvents.SHOW_CALENDAR_FOLDER,
		TeamingEvents.SHOW_COLLECTION_VIEW,
		TeamingEvents.SHOW_DISCUSSION_FOLDER,
		TeamingEvents.SHOW_DISCUSSION_WORKSPACE,
		TeamingEvents.SHOW_FILE_FOLDER,
		TeamingEvents.SHOW_FOLDER_ENTRY,
		TeamingEvents.SHOW_GENERIC_WORKSPACE,
		TeamingEvents.SHOW_GLOBAL_WORKSPACE,
		TeamingEvents.SHOW_GUESTBOOK_FOLDER,
		TeamingEvents.SHOW_HOME_WORKSPACE,
		TeamingEvents.SHOW_LANDING_PAGE,
		TeamingEvents.SHOW_MICRO_BLOG_FOLDER,
		TeamingEvents.SHOW_MILESTONE_FOLDER,
		TeamingEvents.SHOW_MIRRORED_FILE_FOLDER,
		TeamingEvents.SHOW_NET_FOLDERS_WORKSPACE,
		TeamingEvents.SHOW_PERSONAL_WORKSPACES,
		TeamingEvents.SHOW_PROJECT_MANAGEMENT_WORKSPACE,
		TeamingEvents.SHOW_SURVEY_FOLDER,
		TeamingEvents.SHOW_TASK_FOLDER,
		TeamingEvents.SHOW_TEAM_ROOT_WORKSPACE,
		TeamingEvents.SHOW_TEAM_WORKSPACE,
		TeamingEvents.SHOW_TRASH,
		
		// View events.
		TeamingEvents.GET_CURRENT_VIEW_INFO,
		TeamingEvents.VIEW_FORUM_ENTRY,
	};

	// Maximum number of URLs tracked in the content history stack.
	public final static int CONTENT_HISTORY_MAXDEPTH	= 2;

	// The following enumeration is used in setViewNow() to specify how
	// a particular view is being handled. 
	private enum ViewMode {
		GWT_CONTENT_VIEW,	// One of our GWT based views that lives in the content area.
		JSP_CONTENT_VIEW,	// A legacy JSP view that lives in the content IFRAME.
		JSP_ENTRY_VIEW,		// A legacy JSP view that lives in the entry   IFRAME.
		POPUP_VIEW,			// A popup dialog.
	}
	
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
		m_isDebugUI = ri.isDebugUI();
		m_isDebugLP = ri.isDebugLP();

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
		initContentHistoryJS( CONTENT_HISTORY_MAXDEPTH );
		
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
			frameElement.setSrc( "/ssf/html/empty.html" );
		}
	}
	
	/**
	 * Write the given HTML to the IFRAME.
	 */
	public static native void setFrameHtml( String frameName, String html )
	/*-{
		var frame;
		
		// Can we find a frame with the given name?
		frame = $wnd.top.frames[frameName];
		if ( frame )
		{
			// Yes
			frame.document.open();

			// Write the given HTML to the document.
			frame.document.write( html );

			frame.document.close();
		}
	}-*/;
	

	/**
	 * 
	 */
	public void empty()
	{
		clear();
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
	public static List<String> getContentHistory()
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
	public static String getContentHistoryUrl( int index )
	{
		// Simply call the JavaScript implementation method.
		return jsGetContentHistoryUrl( index );
	}// end getContentHistoryUrl()

	/**
	 * Returns the instigator of the current content.
	 * 
	 * @return
	 */
	public Instigator getContentInstigator()
	{
		return m_contentInstigator;
	}// end getContentInstigator()

	/*
	 * Returns the BinderInfo of what's currently loaded in the content
	 * area, if it can be determined.  If it can't, null is returned.
	 */
	private BinderInfo getCurrentBinderInfo()
	{
		// Do we have a current view?
		BinderInfo reply;
		if ( null == m_currentView )
		{
			// No!  Then we can't get its binder.
			reply = null;
		}
		
		else
		{
			// Yes, we have a current view!  What is it of?
			reply = null;
			switch( m_currentView.getViewType() )
			{
			case BINDER:
			case BINDER_WITH_ENTRY_VIEW:
				// A binder!  Simply return its BinderInfo.
				reply = m_currentView.getBinderInfo();
				break;
				
			case FOLDER_ENTRY:
				// A folder entry!  Construct a BinderInfo from the
				// ViewFolderEntryInfo.
				reply = new BinderInfo();
				reply.setBinderId(  m_currentView.getFolderEntryInfo().getEntityId().getBinderId());
				reply.setBinderType(BinderType.FOLDER                                             );
				reply.setFolderType(FolderType.OTHER                                              );
				break;
			}

			// Did we determine a binder ID?
			if ( null == reply )
			{
				// No!  If we have a base binder ID stored in the
				// ViewInfo...
				Long binderId = m_currentView.getBaseBinderId();
				if ( null != binderId )
				{
					// ...create a BinderInfo from it and return it.
					reply = new BinderInfo();
					reply.setBinderId(  binderId        );
					reply.setBinderType(BinderType.OTHER);
				}
			}
		}
		
		// If we get here, reply is null or refers to the BinderInfo of
		// what's currently loaded in the content area.  Return it.
		return reply;
	}

	/*
	 * Initializes the JavaScript for tracking content history.
	 */
	private static native void initContentHistoryJS( int contentHistoryDepth )
	/*-{
		// Have we defined the JavaScript elements for tracking content
		// history yet?
		if ( ! $wnd.top.ss_contentHistory )
		{
			// No!  Define them now.
			$wnd.top.ss_contentHistoryDepth = contentHistoryDepth;
			$wnd.top.ss_contentHistory = new Array();
			$wnd.top.ss_getUrlFromContentHistory = function( index )
			{
				return @org.kablink.teaming.gwt.client.widgets.ContentControl::jsGetContentHistoryUrl(Ljava/lang/Integer;)( index );
			}//end ss_getUrlFromContentHistory()
		}
	}-*/;

	/*
	 * Returns the URL from the content history at the specified index.
	 * 
	 * If there aren't enough items being tracked to satisfy the
	 * request, null is returned.
	 */
	private static native String jsGetContentHistoryUrl( Integer index )
	/*-{
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
	private static native void jsPushContentHistoryUrl( String url )
	/*-{
		// Push the URL.
		$wnd.top.ss_contentHistory.unshift( url );
		
		// While the stack contains more items that we track...
		while ( $wnd.top.ss_contentHistory.length > $wnd.top.ss_contentHistoryDepth )
		{
			// ...remove the last one from the list.
			$wnd.top.ss_contentHistory.pop();
		}
	}-*/;

	/*
	 * Runs the given URL in the JSP entry 
	 */
	private native void jsViewFolderEntry( String url, String isDashboard )
	/*-{
		if ( $wnd.ss_showForumEntryJSP !== undefined )
			$wnd.ss_showForumEntryJSP( url, isDashboard );
		else
			alert( 'ss_showForumEntryJSP() is undefined' );
	}-*/;
	
	/*
	 * Pushes a URL on the content history stack.
	 */
	private static void pushContentHistoryUrl( String url )
	{
		// Simply call the JavaScript implementation method on the
		// sanitized URL.
		jsPushContentHistoryUrl( sanitizeHistoryUrl( url ) );
	}// end pushContentHistoryUrl()
	
	/**
	 * Reload the page that is currently being displayed.
	 */
	public void reload()
	{
		// Clear the IFRAME content.
		clear();
		
		// Remember the current URL.
		String url = getContentHistoryUrl( 0 );

		// Reload the URL.
		ContextChangingEvent.fireOne();						
		setUrl(         "",  Instigator.FORCE_FULL_RELOAD );
		setViewFromUrl( url, Instigator.FORCE_FULL_RELOAD );
	}// end reload()

	/*
	 * Does whatever's necessary to sanitize a URL before it gets
	 * pushed on the history URL stack.
	 */
	private static String sanitizeHistoryUrl( String url )
	{
		url = sanitizeHistoryUrlImpl(url, "/invokeShare/1");
		url = sanitizeHistoryUrlImpl(url, "&invokeShare=1");
		return url;
	}
	
	private static String sanitizeHistoryUrlImpl( String url, String removeThis )
	{
		int pos = url.indexOf(removeThis);
		if (0 < pos) {
			url = GwtClientHelper.replace(url, removeThis, "");
		}
		return url;
	}
	
	/**
	 * Set the width and height of this control.
	 */
	public void setDimensions( int width, int height )
	{
		if ( isVisible() )
		{
			if ( !m_isAdminContent )
			{
				// Adjust the width and height for proper spacing.
				width  += GwtConstants.CONTENT_WIDTH_ADJUST;
				height += GwtConstants.CONTENT_HEIGHT_ADJUST;
			}
			
			// Set the width and height of the frame.
			setSize( (width + "px"), (height + "px") );
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
	private static native void jsResizeTaskListing()
	/*-{
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
	public void setUrl( String url, Instigator instigator )
	{
		m_contentInstigator = instigator;
		m_frame.setUrl( url );
	}// end setUrl()
	
	/*
	 * Asynchronously loads a view based on a ViewInfo.
	 */
	private void setViewAsync( final ViewInfo vi, final String url, final Instigator instigator )
	{
		ScheduledCommand doSetView = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				setViewNow( vi, url, instigator );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( doSetView );
	}// end setViewAsync()

	/*
	 * Sets the view based on the URL.
	 */
	private void setViewFromUrl( final String url, final Instigator instigator )
	{
		// Are we running the admin console?
		if ( m_isAdminContent )
		{
			// Yes!  Simply activate the URL.
			setUrl( url, Instigator.ADMINISTRATION_CONSOLE );
		}
		else
		{
			// No, we aren't running the admin console!  Use the URL to
			// get a ViewInfo for the new context.
			GetViewInfoCmd cmd = new GetViewInfoCmd( url );
			GwtClientHelper.executeCommand(cmd, new AsyncCallback<VibeRpcResponse>()
			{
				@Override
				public void onFailure( Throwable t )
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetViewInfo(),
						url );
				}// end onFailure()
				
				@Override
				public void onSuccess( VibeRpcResponse response )
				{				
					// Show the context asynchronously so that we can
					// release the AJAX request ASAP.
					ViewInfo vi = ((ViewInfo) response.getResponseData());
					setViewAsync( vi, url, instigator );
				}//end onSuccess()
			});
		}
	}// end setViewFromUrl()
	
	/*
	 * Synchronously loads a view based on a ViewInfo.
	 * 
	 * If a view cannot be determined (or no ViewInfo was provided),
	 * the URL is loaded into the IFRAME instead.
	 */
	private void setViewNow( final ViewInfo vi, final String url, final Instigator instigator )
	{
		m_currentView       = vi;
		m_contentInstigator = instigator;
		try
		{
			// Do we have a ViewInfo?
			m_viewMode = ViewMode.JSP_CONTENT_VIEW;
			if ( null != vi )
			{
				// What type of view is it?
				final ViewType vt = vi.getViewType();
				switch ( vt )
				{
				case BINDER:
				case BINDER_WITH_ENTRY_VIEW:
					// Are we in UI debug mode?
					final BinderInfo bi = vi.getBinderInfo();
					if ( m_isDebugUI )
					{
						// Regardless of what's implemented or not,
						// should we force this binder through its old,
						// JSP flow?
						//
						// While writing the GWT based views, I've
						// continually wanted to go back and look at
						// the JSP version of what I'm implementing.
						// This lets us force a binder, regardless of
						// type, to ALWAYS go through the JSP display
						// flow.
						String binderTitle = bi.getBinderTitle().trim().toLowerCase();
						if ( binderTitle.startsWith( "jsp-" ) && binderTitle.endsWith( "-jsp" ) )
						{
							// Yes!  Simply break out of the switch.
							// That will let it take the default flow.
							break;
						}
					}
					
					// Regardless of the type, we'll need an ViewReady
					// to clean up things after the view is loaded.
					// Create one now.
					ViewReady viewReady = new ViewReady()
					{
						@Override
						public void viewReady()
						{
							GwtClientHelper.jsSetMainTitle(bi.getBinderTitle());
							ScheduledCommand doViewReady = new ScheduledCommand()
							{
								@Override
								public void execute()
								{
									GwtTeaming.fireEvent(
										new ContextChangedEvent(
											new OnSelectBinderInfo(
												bi,
												url,
												instigator )));
									
									if ( ViewType.BINDER_WITH_ENTRY_VIEW.equals( vt ) )
									{
										GwtTeaming.fireEventAsync(
											new ViewForumEntryEvent(
												vi.getEntryViewUrl() ) );
									}
									
									else if ( vi.isInvokeShare() )
									{
										GwtTeaming.fireEventAsync(
											new InvokeShareBinderEvent(
												bi.getBinderId() ) );
									}
								}// end execute()
							};
							Scheduler.get().scheduleDeferred( doViewReady );
						}//end viewReady()
					};
					
					// What type of binder is it?
					BinderType bt = bi.getBinderType();
					switch ( bt )
					{
					case COLLECTION:
						GwtTeaming.fireEvent( new ShowCollectionViewEvent( bi, viewReady ) );
						m_viewMode = ViewMode.GWT_CONTENT_VIEW;
						break;
						
						
					case FOLDER:
						// What type of folder is it?
						FolderType ft = bi.getFolderType();
						switch ( ft )
						{
						case CALENDAR:
							GwtTeaming.fireEvent( new ShowCalendarFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
	
							
						case BLOG:
						{
							GwtTeaming.fireEvent( new ShowBlogFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case DISCUSSION:
							GwtTeaming.fireEvent( new ShowDiscussionFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
							
							
						case FILE:
							GwtTeaming.fireEvent( new ShowFileFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
							
							
						case GUESTBOOK:
							GwtTeaming.fireEvent( new ShowGuestbookFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
							
							
						case MILESTONE:
							GwtTeaming.fireEvent( new ShowMilestoneFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
	
							
						case MINIBLOG:
							GwtTeaming.fireEvent( new ShowMicroBlogFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
	
							
						case MIRROREDFILE:
							GwtTeaming.fireEvent( new ShowMirroredFileFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
							
							
						case SURVEY:
							GwtTeaming.fireEvent( new ShowSurveyFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
	
							
						case TASK:
							GwtTeaming.fireEvent( new ShowTaskFolderEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
	
							
						case TRASH:
							GwtTeaming.fireEvent( new ShowTrashEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
							
	
						case PHOTOALBUM:
						case WIKI:
							// These aren't handled!  Let things take
							// the default flow.
							break;
							
						default:
							// Something we don't know how to handle!
							GwtClientHelper.debugAlert( "ContentControl.setViewNow( Unhandled FolderType:  " + ft.name() + " )" );
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
							// Fire the event that will display the landing page.
							GwtTeaming.fireEvent( new ShowLandingPageEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case DISCUSSIONS:
						{
							// Fire the event that will display the Discussion workspace.
							GwtTeaming.fireEvent( new ShowDiscussionWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case TEAM:
						{
							// Fire the event that will display the Team workspace.
							GwtTeaming.fireEvent( new ShowTeamWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case WORKSPACE:
						{
							// Fire the event that will display the generic workspace.
							GwtTeaming.fireEvent( new ShowGenericWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case TRASH:
							GwtTeaming.fireEvent( new ShowTrashEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
	
							
						case GLOBAL_ROOT:
						{
							// Fire the event that will display the Global workspace.
							GwtTeaming.fireEvent( new ShowGlobalWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case TEAM_ROOT:
						{
							// Fire the event that will display the Team root workspace.
							GwtTeaming.fireEvent( new ShowTeamRootWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}

						case TOP:
						{
							// Fire the event that will display the home (top) workspace.
							GwtTeaming.fireEvent( new ShowHomeWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case PROJECT_MANAGEMENT:
						{
							// Fire the event that will display the project management workspace.
							GwtTeaming.fireEvent( new ShowProjectManagementWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case PROFILE_ROOT:
						case PROFILE_ROOT_MANAGEMENT:
						{
							// Fire the event that will display the profile root workspace.
							GwtTeaming.fireEvent( new ShowPersonalWorkspacesEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}

						case NET_FOLDERS_ROOT:
						{
							// Fire the event that will display the
							// root Net Folders workspace.
							GwtTeaming.fireEvent( new ShowNetFoldersWSEvent( bi, viewReady ) );
							m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							break;
						}
							
						case USER:
							// These aren't handled!  Let things take 
							// the default flow.
							break;
						
						default:
							// Something we don't know how to handle!  
							GwtClientHelper.debugAlert( "ContentControl.setViewNow( Unhandled WorkspaceType:  " + wt.name() + " )" );
							break;
						}
						break;
					
					default:
						// If we're viewing an entry and don't have
						// access to the binder, we don't want to
						// display an error.  Otherwise...
						boolean noError = ( ViewType.BINDER_WITH_ENTRY_VIEW.equals( vt ) && ( ! ( bi.isBinderAccessible() ) ) );
						if ( ! noError )
						{
							// ...it's something we don't know how to
							// ...handle!
							GwtClientHelper.debugAlert( "ContentControl.setViewNow( Unhandled BinderType:  " + bt.name() + " )" );
						}
						break;
					}
					break;
				
				case FOLDER_ENTRY:
					// Are we in Filr mode?
					if ( GwtClientHelper.isLicenseFilr() )
					{
						// Yes!  Fire the event that will display the
						// folder entry viewer.
						final ViewFolderEntryInfo vfei = vi.getFolderEntryInfo();
						GwtTeaming.fireEvent( new ShowFolderEntryEvent( vfei, new ViewReady()
						{
							@Override
							public void viewReady()
							{
								if (vi.isInvokeShare()) {
									EntityId eid = vfei.getEntityId();
									GwtTeaming.fireEventAsync(
										new ShareSelectedEntriesEvent(
											eid.getBinderId(),
											eid ) );
								}
							}//end viewReady()
						} ) );
						
						if ( vfei.isContentView() )
						     m_viewMode = ViewMode.GWT_CONTENT_VIEW;
						else m_viewMode = ViewMode.POPUP_VIEW;
						
						break;
					}
					
					else {
						// If we get here, we just run the JSP based folder
						// entry viewer.
						jsViewFolderEntry( url, "no" );
						if (vi.isInvokeShare()) {
							EntityId eid = vi.getFolderEntryInfo().getEntityId();
							GwtTeaming.fireEventAsync(
								new ShareSelectedEntriesEvent(
									eid.getBinderId(),
									eid ) );
						}
						
						m_viewMode = ViewMode.JSP_ENTRY_VIEW;
						break;
					}
					
				case ADD_BINDER:
				case ADD_FOLDER_ENTRY:
				case ADD_PROFILE_ENTRY:
				case ADVANCED_SEARCH:
				case BUILD_FILTER:
				case MODIFY_BINDER:
				case SHOW_TEAM_MEMBERS:
				case VIEW_PROFILE_ENTRY:
					// These aren't handled!  Let things take the
					// default flow.
					break;
					
				default:
					// Something we don't know how to handle!
					GwtClientHelper.debugAlert( "ContentControl.setViewNow( Unhandled ViewType:  " + vt.name() + " )" );
					break;
				}			
			}
	
			// What view mode are we using?
			switch (m_viewMode) {
			case GWT_CONTENT_VIEW:
				// A GWT view!  Hide any entry <DIV>'s that are visible...
				GwtClientHelper.jsHideEntryPopupDiv();
				GwtClientHelper.jsHideNewPageEntryViewDIV();
				
				// ...and clear out the content of the IFRAME.
				setUrl( "", instigator );
				clear();
				
				break;
			
			case JSP_CONTENT_VIEW:
				// A JSP view!  Load the URL into the content frame...
				setUrl( url, instigator );
				
				// ...tell the main content layout panel to not show a
				// ...GWT widget it may have...
				m_mainPage.getMainContentLayoutPanel().showWidget( null );
				
				// ...make sure the ContentControl is showing...
				ShowContentControlEvent.fireOne();

				// ...and if requested...
				if ( vi.isInvokeShare() )
				{
					// ...invoke the share dialog on the view.
					GwtTeaming.fireEventAsync(
						new InvokeShareBinderEvent(
							vi.getBinderInfo().getBinderId() ) );
				}
				
				break;

			case JSP_ENTRY_VIEW:
			case POPUP_VIEW:
				// A JSP entry view or a popup view!  No further
				// processing is necessary.
				break;
			}
		}
		
		finally
		{
			// Finally, push the URL we just processed on the content
			// history stack.
			pushContentHistoryUrl( url );
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
			setViewFromUrl( osbInfo.getBinderUrl(), osbInfo.getInstigator() );
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
		if ( ! ( m_viewMode.equals( ViewMode.JSP_CONTENT_VIEW ) ) )
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
			try                    {contributorIds.add( Long.parseLong( cId.trim() ) );}
			catch ( Exception ex ) {}
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
	 * Handles CopySelectedEntriesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntriesEvent.Handler.onCopySelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntries( CopySelectedEntriesEvent event )
	{
		// Do have information about a binder currently in the view?  
		BinderInfo bi = getCurrentBinderInfo();
		if ( null == bi )
		{
			// No!  Ignore the event.
			return;
		}
		
		// Is the event targeted to the current view?
		Long eventFolderId = event.getFolderId();
		if ( eventFolderId.equals( bi.getBinderIdAsLong() ) )
		{
			// Yes!  Does it contain any selected entities?
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if ( GwtClientHelper.hasItems( selectedEntityIds ) )
			{
				// Yes!  Invoke the copy on them.
				BinderViewsHelper.copyEntries(selectedEntityIds );
			}
		}
	}
	
	
	/**
	 * Handles DeleteSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntriesEvent.Handler.onDeleteSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntries( DeleteSelectedEntriesEvent event )
	{
		// Do have information about a binder currently in the view?  
		BinderInfo bi = getCurrentBinderInfo();
		if ( null == bi )
		{
			// No!  Ignore the event.
			return;
		}
		
		// Is the event targeted to the current view?
		final Long eventFolderId = event.getFolderId();
		final Long biId          = bi.getBinderIdAsLong();
		if ( eventFolderId.equals( biId ) )
		{
			// Yes!  Does the event contain any entities?
			final List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if ( GwtClientHelper.hasItems( selectedEntityIds ) )
			{
				// Yes!  Are we deleting the binder in the view?
				if ( EntityId.isBinderInEntityIds( biId, selectedEntityIds ) )
				{
					// Yes!  After deleting it, we'll need to load its
					// parent.  Can we get a URL to it? 
					GetParentBinderPermalinkCmd cmd = new GetParentBinderPermalinkCmd( biId );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
					{
						@Override
						public void onFailure( Throwable t )
						{
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_GetParentBinderPermalink(),
								biId );
						}//end onFailure()
						
						@Override
						public void onSuccess( VibeRpcResponse response )
						{
							// Yes!  Perform the delete.
							StringRpcResponseData	responseData          = ((StringRpcResponseData) response.getResponseData());
							String					parentBinderPermalink = responseData.getStringValue();
							onDeleteSelectedEntriesAsync( selectedEntityIds, parentBinderPermalink );
						}// end onSuccess()
					});
				}
				else
				{
					// No, we aren't deleting the folder itself!
					// Perform the delete.
					onDeleteSelectedEntriesAsync( selectedEntityIds, null );
				}
			}
		}
	}// end onDeleteSelectedEntries()
	

	/*
	 * Asynchronously deletes the selected entities.
	 */
	private void onDeleteSelectedEntriesAsync( final List<EntityId> selectedEntityIds, final String targetBinderPermalink )
	{
		ScheduledCommand doDelete = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				onDeleteSelectedEntriesNow( selectedEntityIds, targetBinderPermalink );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( doDelete );
	}// end onDeleteSelectedEntriesAsync()
	
	/*
	 * Synchronously deletes the selected entities.
	 */
	private void onDeleteSelectedEntriesNow( final List<EntityId> selectedEntityIds, final String targetBinderPermalink )
	{
		// Delete the selected entities and reload the view to
		// redisplay things with the entries deleted.
		final boolean deletingBinders = EntityId.areBindersInEntityIds( selectedEntityIds );
		BinderViewsHelper.deleteFolderEntries(
			selectedEntityIds,
			new DeletePurgeEntriesCallback()
		{
			@Override
			public void operationCanceled()
			{
				if ( deletingBinders )
				{
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
				}
				postDeletePurgeReloadAsync( targetBinderPermalink );
			}// end operationCanceled())

			@Override
			public void operationComplete()
			{
				if ( deletingBinders )
				{
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
				}
				postDeletePurgeReloadAsync( targetBinderPermalink );
			}// end operationComplete()
			
			@Override
			public void operationFailed()
			{
				// Nothing to do.  The delete call will have told the
				// user about the failure.
			}// end operationFailed()
		} );
	}// end onDeleteSelectedEntriesNow()
	
	
	/**
	 * Handles the GetCurrentViewInfoEvents received by this class
	 * 
	 * Implements the GetCurrentViewInfoEvent.Handler.onGetCurrentViewInfo() method.
	 */
	@Override
	public void onGetCurrentViewInfo( GetCurrentViewInfoEvent event )
	{
		event.getViewInfoCallback().viewInfo( m_currentView );
	}

	
	/**
	 * Handles the GotoUrlEvents received by this class
	 * 
	 * Implements the GotoUrlEvent.Handler.onGotoUrl() method.
	 */
	@Override
	public void onGotoUrl( GotoUrlEvent event )
	{
		ContextChangingEvent.fireOne();						
		setViewAsync( null, event.getUrl(), Instigator.GOTO_CONTENT_URL );
	}

	
	/**
	 * Handles MoveSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntriesEvent.Handler.onMoveSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntries( MoveSelectedEntriesEvent event )
	{
		// Do have information about a binder currently in the view?  
		BinderInfo bi = getCurrentBinderInfo();
		if ( null == bi )
		{
			// No!  Ignore the event.
			return;
		}
		
		// Is the event targeted to the current view?
		Long eventFolderId = event.getFolderId();
		if ( eventFolderId.equals( bi.getBinderIdAsLong() ) )
		{
			// Yes!  If the event contains any entities...
			List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if ( GwtClientHelper.hasItems(selectedEntityIds ) )
			{
				// ...invoke the move on them.
				BinderViewsHelper.moveEntries(selectedEntityIds );
			}
		}
	}
	
	
	/**
	 * Handles PurgeSelectedEntriesEvent's received by this class.
	 * 
	 * Implements the PurgeSelectedEntriesEvent.Handler.onPurgeSelectedEntries() method.
	 * 
	 * @param event
	 */
	@Override
	public void onPurgeSelectedEntries( PurgeSelectedEntriesEvent event )
	{
		// Do have information about a binder currently in the view?  
		BinderInfo bi = getCurrentBinderInfo();
		if ( null == bi )
		{
			// No!  Ignore the event.
			return;
		}
		
		// Is the event targeted to the current view?
		final Long eventFolderId = event.getFolderId();
		final Long biId          = bi.getBinderIdAsLong();
		if ( eventFolderId.equals( biId ) )
		{
			// Yes!  Are there any entities in the event?
			final List<EntityId> selectedEntityIds = event.getSelectedEntities();
			if ( GwtClientHelper.hasItems(selectedEntityIds ) )
			{
				// Are we purging the binder in the view?
				if ( EntityId.isBinderInEntityIds( biId, selectedEntityIds ) )
				{
					// Yes!  After purging it, we'll need to load its
					// parent.  Can we get a URL to it? 
					GetParentBinderPermalinkCmd cmd = new GetParentBinderPermalinkCmd( biId );
					GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
					{
						@Override
						public void onFailure( Throwable t )
						{
							GwtClientHelper.handleGwtRPCFailure(
								t,
								GwtTeaming.getMessages().rpcFailure_GetParentBinderPermalink(),
								biId );
						}//end onFailure()
						
						@Override
						public void onSuccess( VibeRpcResponse response )
						{
							// Yes!  Perform the purge.
							StringRpcResponseData	responseData          = ((StringRpcResponseData) response.getResponseData());
							String					parentBinderPermalink = responseData.getStringValue();
							onPurgeSelectedEntriesAsync( selectedEntityIds, parentBinderPermalink );
						}// end onSuccess()
					});
				}
				else
				{
					// No, we aren't purging the binder in the view!
					// Perform the purge.
					onPurgeSelectedEntriesAsync( selectedEntityIds, null );
				}
			}
		}
	}// end onPurgeSelectedEntries()

	
	/*
	 * Asynchronously purges the selected entities.
	 */
	private void onPurgeSelectedEntriesAsync( final List<EntityId> selectedEntityIds, final String targetBinderPermalink )
	{
		ScheduledCommand doPurge = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				onPurgeSelectedEntriesNow( selectedEntityIds, targetBinderPermalink );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( doPurge );
	}// end onPurgeSelectedEntriesAsync()
	
	
	/*
	 * Synchronously purges the selected entities.
	 */
	private void onPurgeSelectedEntriesNow( final List<EntityId> selectedEntityIds, final String targetBinderPermalink )
	{
		// Purge the selected entities and reload the view to redisplay
		// things with the entries purged.
		final boolean purgingBinders = EntityId.areBindersInEntityIds( selectedEntityIds );
		BinderViewsHelper.purgeFolderEntries(
				selectedEntityIds,
				new DeletePurgeEntriesCallback()
		{
			@Override
			public void operationCanceled()
			{
				if ( purgingBinders )
				{
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
				}
				postDeletePurgeReloadAsync( targetBinderPermalink );
			}// end operationCanceled())

			@Override
			public void operationComplete()
			{
				if ( purgingBinders )
				{
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
				}
				postDeletePurgeReloadAsync( targetBinderPermalink );
			}// end operationComplete()
			
			@Override
			public void operationFailed()
			{
				// Nothing to do.  The purge call will have told the
				// user about the failure.
			}// end operationFailed()
		});
	}// end onPurgeSelectedEntriesNow()
	
	
	/**
	 * Handles ShowBlogFolderEvent's received by this class.
	 * 
	 * Implements the ShowBlogFolderEvent.Handler.onShowBlogFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowBlogFolder( final ShowBlogFolderEvent event )
	{
		ViewClient vClient;
		
		// Display a Blog folder for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase blogFolderView )
			{
				blogFolderView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( blogFolderView );
			}
		};
		
		// Create a BlogFolderView widget for the selected binder.
		BlogFolderView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
	/**
	 * Handles ShowCalendarFolderEvent's received by this class.
	 * 
	 * Implements the ShowCalendarFolderEvent.Handler.onShowCalendarFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowCalendarFolder( final ShowCalendarFolderEvent event )
	{
		// Create a CalendarFolderView widget for the selected binder.
		CalendarFolderView.createAsync(
				event.getFolderInfo(),
				event.getViewReady(),
				new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( ViewBase tfView )
			{
				tfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( tfView );
			}// end onSuccess()
		});
	}// end onShowCalendarFolder()
	
	/**
	 * Handles ShowCollectionEvent's received by this class.
	 * 
	 * Implements the ShowCollectionEvent.Handler.onShowCollection() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowCollectionView( final ShowCollectionViewEvent event )
	{
		// Create a CollectionView widget for the selected BinderInfo.
		CollectionView.createAsync(
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
			public void onSuccess( ViewBase cView )
			{
				cView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( cView );
			}// end onSuccess()
		});
	}// end onShowCollection()
	
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
	 * Handles ShowDiscussionWSEvent's received by this class.
	 * 
	 * Implements the ShowDiscussionWSEvent.Handler.onShowDiscussionWS() method.
	 */
	@Override
	public void onShowDiscussionWS( ShowDiscussionWSEvent event )
	{
		// Display a Discussion Workspace for the given binder id.
		ViewClient vClient;
		
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase discussionWS )
			{
				discussionWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( discussionWS );
			}
		};
		
		// Create a DiscussionWSView widget for the selected binder.
		DiscussionWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
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
			public void onSuccess( ViewBase ffView )
			{
				ffView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( ffView );
			}// end onSuccess()
		});
	}// end onShowFileFolder()
	
	/**
	 * Handles ShowFolderEntryEvent's received by this class.
	 * 
	 * Implements the ShowFolderEntryEvent.Handler.onShowFolderEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowFolderEntry( ShowFolderEntryEvent event )
	{
		// Are we running the folder entry viewer as content view or a
		// dialog?
		final ViewFolderEntryInfo	vfei      = event.getEntryViewInfo();
		final ViewReady				viewReady = event.getViewReady();
		if ( vfei.isContentView() )
		{
			// A content view!  Create a FolderEntryView widget for the
			// selected entity.
			FolderEntryView.createAsync(
					vfei,
					viewReady,
					new ViewClient()
			{
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}// end onUnavailable()

				@Override
				public void onSuccess( ViewBase feView )
				{
					feView.setViewSize();
					m_mainPage.getMainContentLayoutPanel().showWidget( feView );
				}// end onSuccess()
			});
		}
		
		else
		{
			// A dialog!  Have we created a folder entry dialog yet?
			if ( null == m_folderEntryDlg )
			{
				// No!  Create one now...
				FolderEntryDlg.createAsync(new FolderEntryDlgClient()
				{			
					@Override
					public void onUnavailable()
					{
						// Nothing to do.  Error handled in
						// asynchronous provider.
					}// end onUnavailable()
					
					@Override
					public void onSuccess( final FolderEntryDlg feDlg )
					{
						// ...and show it.
						m_folderEntryDlg = feDlg;
						showFolderEntryDlgAsync( vfei, viewReady );
					}// end onSuccess()
				} );
			}
			
			else
			{
				// Yes, we've already created a folder entry dialog!
				// Use it to show the requested entity.
				showFolderEntryDlgAsync( vfei, viewReady );
			}
		}
	}// end onShowFileFolder()
	
	/**
	 * Handles ShowGenericWSEvent's received by this class.
	 * 
	 * Implements the ShowGenericWSEvent.Handler.onShowTeamWS() method.
	 */
	@Override
	public void onShowGenericWS( ShowGenericWSEvent event )
	{
		ViewClient vClient;
		
		// Display a Generic Workspace for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase genericWS )
			{
				genericWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( genericWS );
			}
		};
		
		// Create a GenericWSView widget for the selected binder.
		GenericWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
	/**
	 * Handles ShowGlobalWSEvent's received by this class.
	 * 
	 * Implements the ShowGlobalWSEvent.Handler.onShowGlobalWS() method.
	 */
	@Override
	public void onShowGlobalWS( ShowGlobalWSEvent event )
	{
		ViewClient vClient;
		
		// Display a Generic Workspace for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase genericWS )
			{
				genericWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( genericWS );
			}
		};
		
		// Create a GenericWSView widget for the selected binder.
		GenericWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
	/**
	 * Handles ShowGuestbookFolderEvent's received by this class.
	 * 
	 * Implements the ShowGuestbookFolderEvent.Handler.onShowGuestbookFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowGuestbookFolder( final ShowGuestbookFolderEvent event )
	{
		// Create a GuestbookFolderView widget for the selected binder.
		GuestbookFolderView.createAsync(
				event.getFolderInfo(),
				event.getViewReady(),
				new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( ViewBase gbfView )
			{
				gbfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( gbfView );
			}// end onSuccess()
		});
	}// end onShowGuestbookFolder()
	
	/**
	 * Handles ShowHomeWSEvent's received by this class.
	 * 
	 * Implements the ShowHomeWSEvent.Handler.onShowHomeWS() method.
	 */
	@Override
	public void onShowHomeWS( ShowHomeWSEvent event )
	{
		ViewClient vClient;
		
		// Display a Generic Workspace for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase homeWS )
			{
				homeWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( homeWS );
			}
		};
		
		// Create a HomeWSView widget for the selected binder.
		HomeWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
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
		LandingPageView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}// end onShowLandingPage()
	
	/**
	 * Handles ShowMicroBlogFolderEvent's received by this class.
	 * 
	 * Implements the ShowMicroBlogFolderEvent.Handler.onShowMicroBlogFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowMicroBlogFolder( final ShowMicroBlogFolderEvent event )
	{
		// Create a MicroBlogFolderView widget for the selected binder.
		MicroBlogFolderView.createAsync(
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
			public void onSuccess( ViewBase mbfView )
			{
				mbfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( mbfView );
			}// end onSuccess()
		});
	}// end onShowMicroBlogFolder()
	
	/**
	 * Handles ShowMilestoneFolderEvent's received by this class.
	 * 
	 * Implements the ShowMilestoneFolderEvent.Handler.onShowMilestoneFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowMilestoneFolder( final ShowMilestoneFolderEvent event )
	{
		// Create a MilestoneFolderView widget for the selected binder.
		MilestoneFolderView.createAsync(
				event.getFolderInfo(),
				event.getViewReady(),
				new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( ViewBase msfView )
			{
				msfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( msfView );
			}// end onSuccess()
		});
	}// end onShowMilestoneFolder()
	
	/**
	 * Handles ShowMirroredFileFolderEvent's received by this class.
	 * 
	 * Implements the ShowMirroredFileFolderEvent.Handler.onShowMirroredFileFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowMirroredFileFolder( final ShowMirroredFileFolderEvent event )
	{
		// Create a MirroredFileFolderView widget for the selected
		// binder.
		MirroredFileFolderView.createAsync(
				event.getFolderInfo(),
				event.getViewReady(),
				new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( ViewBase mffView )
			{
				mffView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( mffView );
			}// end onSuccess()
		});
	}// end onShowMirroredFileFolder()
	
	/**
	 * Handles ShowNetFoldersWSEvent's received by this class.
	 * 
	 * Implements the ShowNetFoldersWSEvent.Handler.onShowTeamWS() method.
	 */
	@Override
	public void onShowNetFoldersWS( ShowNetFoldersWSEvent event )
	{
		ViewClient vClient;
		
		// Display a NetFolders Workspace for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase genericWS )
			{
				genericWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( genericWS );
			}
		};
		
		// Create a NetFoldersWSView widget for the selected binder.
		NetFoldersWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
	/**
	 * Handles ShowPersonalWorkspacesEvent's received by this class.
	 * 
	 * Implements the ShowPersonalWorkspacesEvent.Handler.onShowPersonalWorkspaces() method.
	 */
	@Override
	public void onShowPersonalWorkspaces( ShowPersonalWorkspacesEvent event )
	{
		// Create a PersonalWorkspacesView widget for the selected
		// binder.
		PersonalWorkspacesView.createAsync(
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
			public void onSuccess( ViewBase pwsView )
			{
				pwsView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( pwsView );
			}// end onSuccess()
		} );
	}// end onShowPersonalWorkspaces()
	
	/**
	 * Handles ShowProjectManagementWSEvent's received by this class.
	 * 
	 * Implements the ShowProjectManagementWSEvent.Handler.onShowTeamWS() method.
	 */
	@Override
	public void onShowProjectManagementWS( ShowProjectManagementWSEvent event )
	{
		ViewClient vClient;
		
		// Display a Project Management Workspace for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase projectManagementWS )
			{
				projectManagementWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( projectManagementWS );
			}
		};
		
		// Create a ProjectManagementWSView widget for the selected binder.
		ProjectManagementWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
	/**
	 * Handles ShowSurveyFolderEvent's received by this class.
	 * 
	 * Implements the ShowSurveyFolderEvent.Handler.onShowSurveyFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowSurveyFolder( final ShowSurveyFolderEvent event )
	{
		// Create a SurveyFolderView widget for the selected binder.
		SurveyFolderView.createAsync(
				event.getFolderInfo(),
				event.getViewReady(),
				new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}// end onUnavailable()

			@Override
			public void onSuccess( ViewBase sfView )
			{
				sfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( sfView );
			}// end onSuccess()
		});
	}// end onShowSurveyFolder()
	
	/**
	 * Handles ShowTaskFolderEvent's received by this class.
	 * 
	 * Implements the ShowTaskFolderEvent.Handler.onShowTaskFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowTaskFolder( final ShowTaskFolderEvent event )
	{
		// Create a TaskFolderView widget for the selected binder.
		TaskFolderView.createAsync(
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
			public void onSuccess( ViewBase tfView )
			{
				tfView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( tfView );
			}// end onSuccess()
		});
	}// end onShowTaskFolder()
	
	/**
	 * Handles ShowTeamRootWSEvent's received by this class.
	 * 
	 * Implements the ShowTeamRootWSEvent.Handler.onShowTeamRootWS() method.
	 */
	@Override
	public void onShowTeamRootWS( ShowTeamRootWSEvent event )
	{
		ViewClient vClient;
		
		// Display a Generic Workspace for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase genericWS )
			{
				genericWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( genericWS );
			}
		};
		
		// Create a GenericWSView widget for the selected binder.
		GenericWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
	/**
	 * Handles ShowTeamWSEvent's received by this class.
	 * 
	 * Implements the ShowTeamWSEvent.Handler.onShowTeamWS() method.
	 */
	@Override
	public void onShowTeamWS( ShowTeamWSEvent event )
	{
		ViewClient vClient;
		
		// Display a Team Workspace for the given binder id.
		vClient = new ViewClient()
		{
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in asynchronous provider.
			}
			
			@Override
			public void onSuccess( ViewBase teamWS )
			{
				teamWS.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( teamWS );
			}
		};
		
		// Create a TeamWSView widget for the selected binder.
		TeamWSView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
	}
	
	/**
	 * Handles ShowTrashEvent's received by this class.
	 * 
	 * Implements the ShowTrashEvent.Handler.onShowTrash() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowTrash( final ShowTrashEvent event )
	{
		// Create a TrashView widget for the selected binder.
		TrashView.createAsync(
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
			public void onSuccess( ViewBase tView )
			{
				tView.setViewSize();
				m_mainPage.getMainContentLayoutPanel().showWidget( tView );
			}// end onSuccess()
		});
	}// end onShowTrash()
	

	/*
	 * Asynchronously performs the reload necessary after an item has
	 * been deleted or purged.
	 */
	private void postDeletePurgeReloadAsync( final String targetBinderPermalink ) {
		ScheduledCommand doReload = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				postDeletePurgeReloadNow( targetBinderPermalink );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( doReload );
	}// end postDeletePurgeReloadAsync();
	
	
	/*
	 * Synchronously performs the reload necessary after an item has
	 * been deleted or purged.
	 */
	private void postDeletePurgeReloadNow( final String targetBinderPermalink ) {
		// Do we have a specific place to reload to?
		if ( GwtClientHelper.hasString( targetBinderPermalink ) )
		{
			// Yes!  Load it.
			OnSelectBinderInfo osbInfo = new OnSelectBinderInfo(
				targetBinderPermalink,
				Instigator.GOTO_CONTENT_URL );
			
			if ( GwtClientHelper.validateOSBI( osbInfo ) )
			{
				GwtTeaming.fireEvent( new ChangeContextEvent( osbInfo ));
			}
		}
		else
		{
			// No, we don't have a specific place, simply reload what's
			// already in the view.
			FullUIReloadEvent.fireOne();
		}
	}// end postDeletePurgeReloadNow();
	
	
	/*
	 * Asynchronously shows the folder entry dialog.
	 */
	private void showFolderEntryDlgAsync( final ViewFolderEntryInfo vfei, final ViewReady viewReady )
	{
		ScheduledCommand doShow = new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				showFolderEntryDlgNow( vfei, viewReady );
			}// end execute()
		};
		Scheduler.get().scheduleDeferred( doShow );
	}// end showFolderEntryDlgAsync()
	
	/*
	 * Synchronously shows the folder entry dialog.
	 */
	private void showFolderEntryDlgNow( ViewFolderEntryInfo vfei, ViewReady viewReady )
	{
		// Initialize and show the dialog.
		FolderEntryDlg.initAndShow( m_folderEntryDlg, vfei, viewReady );
	}// end showFolderEntryDlgNow()

	/**
	 * Callback interface to interact with the content control
	 * asynchronously after it loads. 
	 */
	public interface ContentControlClient
	{
		void onSuccess( ContentControl contentCtrl );
		void onUnavailable();
	}

	/**
	 * Handles ViewForumEntryEvent's received by this class.
	 * 
	 * Implements the ViewForumEntryEvent.Handler.onViewForumEntry() method.
	 * 
	 * @param event
	 */
	@Override
	public void onViewForumEntry( ViewForumEntryEvent event )
	{
		setViewFromUrl( event.getViewForumEntryUrl(), Instigator.VIEW_FOLDER_ENTRY );
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
				GwtClientHelper.deferredAlert( GwtTeaming.getMessages().codeSplitFailure_ContentControl() );
				contentCtrlClient.onUnavailable();
			}// end onFailure()
		} );
	}// end createAsync()
}// end ContentControl
