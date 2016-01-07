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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.binderviews.BlogFolderView;
import org.kablink.teaming.gwt.client.binderviews.CalendarFolderView;
import org.kablink.teaming.gwt.client.binderviews.CollectionView;
import org.kablink.teaming.gwt.client.binderviews.DiscussionFolderView;
import org.kablink.teaming.gwt.client.binderviews.DiscussionWSView;
import org.kablink.teaming.gwt.client.binderviews.FileFolderView;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryDlg;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryDlg.FolderEntryDlgClient;
import org.kablink.teaming.gwt.client.binderviews.FolderEntryView;
import org.kablink.teaming.gwt.client.binderviews.GenericWSView;
import org.kablink.teaming.gwt.client.binderviews.GlobalWorkspacesView;
import org.kablink.teaming.gwt.client.binderviews.GuestbookFolderView;
import org.kablink.teaming.gwt.client.binderviews.HomeWSView;
import org.kablink.teaming.gwt.client.binderviews.LandingPageView;
import org.kablink.teaming.gwt.client.binderviews.MicroBlogFolderView;
import org.kablink.teaming.gwt.client.binderviews.MilestoneFolderView;
import org.kablink.teaming.gwt.client.binderviews.MirroredFileFolderView;
import org.kablink.teaming.gwt.client.binderviews.NetFoldersWSView;
import org.kablink.teaming.gwt.client.binderviews.PersonalWorkspaceView;
import org.kablink.teaming.gwt.client.binderviews.PersonalWorkspacesView;
import org.kablink.teaming.gwt.client.binderviews.PhotoAlbumFolderView;
import org.kablink.teaming.gwt.client.binderviews.ProjectManagementWSView;
import org.kablink.teaming.gwt.client.binderviews.SurveyFolderView;
import org.kablink.teaming.gwt.client.binderviews.TaskFolderView;
import org.kablink.teaming.gwt.client.binderviews.TeamWSView;
import org.kablink.teaming.gwt.client.binderviews.TeamWorkspacesView;
import org.kablink.teaming.gwt.client.binderviews.TrashView;
import org.kablink.teaming.gwt.client.binderviews.ViewBase;
import org.kablink.teaming.gwt.client.binderviews.ViewBase.ViewClient;
import org.kablink.teaming.gwt.client.binderviews.WikiFolderView;
import org.kablink.teaming.gwt.client.binderviews.util.BinderViewsHelper;
import org.kablink.teaming.gwt.client.binderviews.util.DeleteEntitiesHelper.DeleteEntitiesCallback;
import org.kablink.teaming.gwt.client.binderviews.ViewReady;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent;
import org.kablink.teaming.gwt.client.event.ActivityStreamExitEvent.ExitMode;
import org.kablink.teaming.gwt.client.event.ContributorIdsReplyEvent;
import org.kablink.teaming.gwt.client.event.ContributorIdsRequestEvent;
import org.kablink.teaming.gwt.client.event.ChangeContextEvent;
import org.kablink.teaming.gwt.client.event.ContextChangedEvent;
import org.kablink.teaming.gwt.client.event.ContextChangingEvent;
import org.kablink.teaming.gwt.client.event.CopySelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.DeleteSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.FullUIReloadEvent;
import org.kablink.teaming.gwt.client.event.GetCurrentViewInfoEvent;
import org.kablink.teaming.gwt.client.event.GotoMyWorkspaceEvent;
import org.kablink.teaming.gwt.client.event.GotoUrlEvent;
import org.kablink.teaming.gwt.client.event.InvokeEmailNotificationEvent;
import org.kablink.teaming.gwt.client.event.InvokeShareBinderEvent;
import org.kablink.teaming.gwt.client.event.MoveSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.SetFilrActionFromCollectionTypeEvent;
import org.kablink.teaming.gwt.client.event.ShareSelectedEntitiesEvent;
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
import org.kablink.teaming.gwt.client.event.ShowPersonalWorkspaceEvent;
import org.kablink.teaming.gwt.client.event.ShowPersonalWorkspacesEvent;
import org.kablink.teaming.gwt.client.event.ShowPhotoAlbumFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowProjectManagementWSEvent;
import org.kablink.teaming.gwt.client.event.ShowSurveyFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowTaskFolderEvent;
import org.kablink.teaming.gwt.client.event.ShowTeamRootWSEvent;
import org.kablink.teaming.gwt.client.event.ShowTrashEvent;
import org.kablink.teaming.gwt.client.event.ShowWikiFolderEvent;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.ShowTeamWSEvent;
import org.kablink.teaming.gwt.client.event.SubscribeSelectedEntitiesEvent;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetParentBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetViewInfoCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.BinderType;
import org.kablink.teaming.gwt.client.util.CollectionType;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.FolderType;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HistoryHelper;
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
public class ContentControl extends Composite
	implements
		// Event handlers implemented by this class.
		ContributorIdsRequestEvent.Handler,
		ChangeContextEvent.Handler,
		ContextChangedEvent.Handler,
		CopySelectedEntitiesEvent.Handler,
		DeleteSelectedEntitiesEvent.Handler,
		GetCurrentViewInfoEvent.Handler,
		GotoUrlEvent.Handler,
		MoveSelectedEntitiesEvent.Handler,
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
		ShowPersonalWorkspaceEvent.Handler,
		ShowPersonalWorkspacesEvent.Handler,
		ShowPhotoAlbumFolderEvent.Handler,
		ShowProjectManagementWSEvent.Handler,
		ShowSurveyFolderEvent.Handler,
		ShowTaskFolderEvent.Handler,
		ShowTeamRootWSEvent.Handler,
		ShowTeamWSEvent.Handler,
		ShowTrashEvent.Handler,
		ShowWikiFolderEvent.Handler,
		ViewForumEntryEvent.Handler
{
	private boolean			m_isAdminContent;							//
	private boolean			m_isDebugUI;								//
	private FlowPanel		m_mainContentPanel;							//
	private FolderEntryDlg	m_folderEntryDlg;							//
	private GwtMainPage		m_mainPage;									//
	private Instigator		m_contentInstigator = Instigator.UNKNOWN;	//
	private NamedFrame		m_contentFrame;								//
	private ViewInfo		m_currentView;								//
	private ViewMode		m_viewMode = ViewMode.JSP_CONTENT_VIEW;		//
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
	{
		// Context events.
		TeamingEvents.CHANGE_CONTEXT,
		TeamingEvents.CONTEXT_CHANGED,
		TeamingEvents.GOTO_URL,
		
		// Contributor events.
		TeamingEvents.CONTRIBUTOR_IDS_REQUEST,
		
		// Selected entities events.
		TeamingEvents.COPY_SELECTED_ENTITIES,
		TeamingEvents.DELETE_SELECTED_ENTITIES,
		TeamingEvents.MOVE_SELECTED_ENTITIES,
		
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
		TeamingEvents.SHOW_PERSONAL_WORKSPACE,
		TeamingEvents.SHOW_PERSONAL_WORKSPACES,
		TeamingEvents.SHOW_PHOTO_ALBUM_FOLDER,
		TeamingEvents.SHOW_PROJECT_MANAGEMENT_WORKSPACE,
		TeamingEvents.SHOW_SURVEY_FOLDER,
		TeamingEvents.SHOW_TASK_FOLDER,
		TeamingEvents.SHOW_TEAM_ROOT_WORKSPACE,
		TeamingEvents.SHOW_TEAM_WORKSPACE,
		TeamingEvents.SHOW_TRASH,
		TeamingEvents.SHOW_WIKI_FOLDER,
		
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
		// Note that this is NOT the browser history, but a separate
		// history stack for managing views.
		initContentHistoryJS( CONTENT_HISTORY_MAXDEPTH );

		// Create the main FlowPanel to hold the content.
		m_mainContentPanel = new FlowPanel();
		m_mainContentPanel.addStyleName( "contentControl" );

		// Give the IFRAME a name so that view_workarea_navbar.jsp,
		// doesn't set the URL of the browser.
		m_contentFrame = new NamedFrame( name );
		m_contentFrame.setPixelSize( 700, 500 );
		m_contentFrame.getElement().setId( m_isAdminContent ?  "adminContentControl" : "contentControl" );
		m_contentFrame.setUrl( "" );
		m_mainContentPanel.add( m_contentFrame );
		
		// All composites must call initWidget() in their constructors.
		initWidget( m_mainContentPanel );
	}// end ContentControl()
	
	
	/**
	 * Clear the contents of the IFRAME.
	 */
	private void clear()
	{
		setContentFrameUrl( "/ssf/html/empty.html" );
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
	private void empty()
	{
		clear();
	}

	/*
	 * Returns the FrameElement encompassing this ContentControl.
	 */
	private FrameElement getContentFrame()
	{
		FrameElement reply;
		Element e = m_contentFrame.getElement();
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

	/**
	 * Returns the BinderInfo of what's currently loaded in the content
	 * area, if it can be determined.  If it can't, null is returned.
	 * 
	 * @return
	 */
	public BinderInfo getCurrentBinderInfo()
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
			
			$wnd.top.ss_popUrlFromContentHistory = function()
			{
				if ( $wnd.top.ss_contentHistory && ( 1 < $wnd.top.ss_contentHistory.length ) )
				{
					$wnd.top.ss_contentHistory.shift();
				}
			}//end ss_popUrlFromContentHistory()
		}
	}-*/;

	/*
	 * Returns true if the given entity is currently being viewed and
	 * false otherwise.
	 */
	private boolean isEntityInJSPEntryView( EntityId eid )
	{
		// Are we currently in a JSP entry viewer and we're we given an
		// EntityId of a folder entry? 
		if ( ( null != m_currentView ) && m_currentView.getViewType().equals( ViewType.FOLDER_ENTRY   ) &&
		     ( null != m_viewMode    ) && m_viewMode.equals(                  ViewMode.JSP_ENTRY_VIEW ) &&
		     ( null != eid )           && eid.isEntry() )
		{
			// Yes!  Return true if the given EntityId matches that
			// being viewed and false otherwise.
			return eid.equalsEntityId( m_currentView.getFolderEntryInfo().getEntityId() );
		}
	
		// If we get here, we're not viewing the given entity an a JSP
		// entry viewer.  Return false. 
		return false;
	}//end isEntityInJSPEntryView()
	
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
	private void reload()
	{
		// Clear the IFRAME content.
		clear();
		
		// Remember the current URL.
		String url = getContentHistoryUrl( 0 );

		// Reload the URL.
		ContextChangingEvent.fireOne();						
		setContentFrameUrl( "",  Instigator.FORCE_FULL_RELOAD              );
		setViewFromUrl(     url, Instigator.FORCE_FULL_RELOAD, false, null );
	}// end reload()

	/*
	 * Does whatever's necessary to sanitize a URL before it gets
	 * pushed on the history URL stack.
	 */
	private static String sanitizeHistoryUrl( String url )
	{
		url = sanitizeHistoryUrlImpl(url, "/invokeShare/1");
		url = sanitizeHistoryUrlImpl(url, "&invokeShare=1");
		
		url = sanitizeHistoryUrlImpl(url, "/invokeSubscribe/1");
		url = sanitizeHistoryUrlImpl(url, "&invokeSubscribe=1");
		
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
	 * 
	 * @param width
	 * @param height
	 */
	private void setDimensions( int width, int height )
	{
		if ( !m_isAdminContent )
		{
			// Adjust the width and height for proper spacing.
			width  += GwtConstants.CONTENT_WIDTH_ADJUST;
			height += GwtConstants.CONTENT_HEIGHT_ADJUST;
		}
		if ( 0 > width  ) width  = 0;
		if ( 0 > height ) height = 0;
		
		// Set the width and height of the frame.
		setSize( (width + "px"), (height + "px") );
		m_contentFrame.setPixelSize( width, height );

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
	 * This method will set the URL used by the content IFRAME.
	 *
	 * The logic here detaches the IFRAME from the DOM before setting
	 * the URL.   This is done to address a problem with IFRAME's and
	 * GWT history as per the following web site:
	 *  - - - - - - - - - - - - - - - - - - -
	 * http://owenrh.blogspot.com/2011/04/gwt-iframes-and-history.html
	 * - - - - - Copied From There - - - - -
	 * So I ran into this problem the other day, and thought I'd 
	 * document the solution I found for it.
	 *  
	 * We've got a scenario on a project where we embed an IFRAME into
	 * the page and load content into it. The problem is that the every
	 * time the src attribute on the IFRAME was set, the browser
	 * created a non-GWT history event.  This meant that when the user
	 * pressed back there would be spurious history events, stopping
	 * anything from happening, or they'd have to press back multiple
	 * times instead of once.
	 *  
	 * After some investigation I discovered the solution. Basically,
	 * creating a GWT Frame widget, setting the URL in the constructor,
	 * and adding it to the page each time the content changed didn't
	 * create these history events.  The history events seem to be
	 * related to call the Frame.setSrc( ) method, or setting the src
	 * attribute on the IFRAME element in the HTML.
	 *  - - - - - - - - - - - - - - - - - - -
	 *  
	 * @param newUrl
	 * @param instigator
	 */
	private void setContentFrameUrl( String newUrl, Instigator instigator )
	{
		// If we were given an instigator...
		if ( null != instigator )
		{
			// ..store it.
			m_contentInstigator = instigator;
		}

		// Remove the content frame from the DOM while we set the URL
		// into it.
		m_mainContentPanel.remove( m_contentFrame );
		m_contentFrame.setUrl(     newUrl         );
		m_mainContentPanel.add(    m_contentFrame );
	}// end setContentFrameUrl()
	

	/**
	 * 
	 * @param url
	 */
	private void setContentFrameUrl( String url )
	{
		// Always use the initial form of the method.
		setContentFrameUrl( url, null );
	}// end setContentFrameUrl()
	
	/*
	 * Asynchronously loads a view based on a ViewInfo.
	 */
	private void setViewAsync( final ViewInfo vi, final String url, final Instigator instigator, final boolean historyAction, final CollectionType historySelectedMastheadCollection )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				setViewNow(
					vi,
					url,
					instigator,
					historyAction,
					historySelectedMastheadCollection );
			}// end execute()
		} );
	}// end setViewAsync()

	/*
	 * Sets the view based on the URL.
	 */
	private void setViewFromUrl( final String url, final Instigator instigator, final boolean historyAction, final CollectionType historySelectedMastheadCollection )
	{
		// Are we running the admin console?
		if ( m_isAdminContent )
		{
			// Yes!  Simply activate the URL.
			setContentFrameUrl( url, Instigator.ADMINISTRATION_CONSOLE );
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
					String targetUrl;
					ViewInfo vi = ((ViewInfo) response.getResponseData());
					if ( null == vi )
					{
						targetUrl = url;
					}
					else
					{
						String overrideUrl = vi.getOverrideUrl();
						if ( GwtClientHelper.hasString( overrideUrl ) )
						     targetUrl = overrideUrl;
						else targetUrl = url;
					}
					setViewAsync( vi, targetUrl, instigator, historyAction, historySelectedMastheadCollection );
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
	private void setViewNow( final ViewInfo vi, final String url, final Instigator instigator, final boolean historyAction, final CollectionType historySelectedMastheadCollection )
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
							GwtClientHelper.deferCommand( new ScheduledCommand()
							{
								@Override
								public void execute()
								{
									// Notify those who care about the
									// change in context.
									OnSelectBinderInfo osbInfo = new OnSelectBinderInfo( bi, url, instigator );
									ContextChangedEvent ccEvent = new ContextChangedEvent( osbInfo );
									ccEvent.setHistoryAction( historyAction );
									ccEvent.setHistorySelectedMastheadCollection( historySelectedMastheadCollection );
									GwtTeaming.fireEvent( ccEvent );
									
									// If we're not navigating from the
									// history...
									if ( ! historyAction )
									{
										// ...push the URL into the
										// ...history cache.
										HistoryHelper.pushHistoryInfoAsync( url, instigator );
									}
									
									if ( ViewType.BINDER_WITH_ENTRY_VIEW.equals( vt ) )
									{
										GwtTeaming.fireEventAsync(
											new ViewForumEntryEvent(
												vi.getEntryViewUrl() ) );
									}
									
									else
									{
										if ( vi.isInvokeShare() )
										{
											if ( vi.isInvokeShareEnabled() )
											{
												GwtTeaming.fireEventAsync(
													new InvokeShareBinderEvent(
													bi.getBinderId() ) );
											}
											else
											{
												GwtClientHelper.deferredAlert(GwtTeaming.getMessages().contentControl_Warning_ShareNoRights());
											}
										}
										if ( vi.isInvokeSubscribe() )
										{
											InvokeEmailNotificationEvent.fireOneAsync();
										}
									}
								}// end execute()
							} );
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
							boolean showGwtPA = PhotoAlbumFolderView.SHOW_GWT_PHOTO_ALBUM;	//! DRF (20150318)
							if (showGwtPA) {
								GwtTeaming.fireEvent( new ShowPhotoAlbumFolderEvent( bi, viewReady ) );
								m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							}
							break;
	
							
						case WIKI:
							boolean showGwtWiki = WikiFolderView.SHOW_GWT_WIKI;	//! DRF (20150326)
							if (showGwtWiki) {
								GwtTeaming.fireEvent( new ShowWikiFolderEvent( bi, viewReady ) );
								m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							}
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
							boolean showGwtPWS = PersonalWorkspaceView.SHOW_GWT_PERSONAL_WORKSPACE;	//! DRF (20150318)
							if (showGwtPWS) {
								// Fire the event that will display the
								// Personal Workspace view.
								GwtTeaming.fireEvent( new ShowPersonalWorkspaceEvent( bi, viewReady ) );
								m_viewMode = ViewMode.GWT_CONTENT_VIEW;
							}
							break;
						
						default:
							// Something we don't know how to handle!  
							GwtClientHelper.debugAlert( "ContentControl.setViewNow( Unhandled WorkspaceType:  " + wt.name() + " )" );
							break;
						}
						break;
					
					case OTHER:
					default:
						// If we're navigating to a binder that's not
						// accessible...
						boolean binderInaccessible = (!(bi.isBinderAccessible()));
						if (ViewType.BINDER.equals(vt) && binderInaccessible) {
							// ...and we're not going to run the login
							// ...dialog...
							RequestInfo ri = GwtClientHelper.getRequestInfo();
							if (ri.isUserLoggedIn() || (!(ri.promptForLogin()))) {
								// ...tell the user about the
								// ...inaccessible binder...
								GwtClientHelper.alertViaDlg(GwtTeaming.getMessages().cantAccessFolder());
								
								// ...and if they have access to their
								// ...own personal workspace...
								if (ri.getMyWorkspaceAccessible()) {
									// ...navigate to it.
									GotoMyWorkspaceEvent.fireOneAsync();
								}
							}
							return;
						}

						// If we're viewing an entry and don't have
						// access to the binder, we don't want to
						// display an error.  Otherwise...
						boolean noError = ( ViewType.BINDER_WITH_ENTRY_VIEW.equals( vt ) && binderInaccessible );
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
								GwtClientHelper.deferCommand( new ScheduledCommand()
								{
									@Override
									public void execute()
									{
										// If we're navigating from the
										// history, make sure the
										// masthead matches what was
										// there.  Otherwise, push the
										// URL into the history cache.
										if ( historyAction )
										     GwtTeaming.fireEventAsync( new SetFilrActionFromCollectionTypeEvent( historySelectedMastheadCollection ) );
										else HistoryHelper.pushHistoryInfoAsync( url, instigator );
										
										EntityId eid = vfei.getEntityId();
										if ( vi.isInvokeShare() )
										{
											if ( vi.isInvokeShareEnabled() )
											{
												GwtTeaming.fireEventAsync(
													new ShareSelectedEntitiesEvent(
														eid.getBinderId(),
														eid ) );
											}
											else
											{
												GwtClientHelper.deferredAlert(GwtTeaming.getMessages().contentControl_Warning_ShareNoRights());
											}
										}
										if ( vi.isInvokeSubscribe() )
										{
											GwtTeaming.fireEventAsync(
												new SubscribeSelectedEntitiesEvent(
													eid.getBinderId(),
													eid ) );
										}
									}// end execute()
								} );
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
						
						// If we're navigating from the
						// history, make sure the
						// masthead matches what was
						// there.  Otherwise, push the
						// URL into the history cache.
						if ( historyAction )
						     GwtTeaming.fireEventAsync( new SetFilrActionFromCollectionTypeEvent( historySelectedMastheadCollection ) );
						else HistoryHelper.pushHistoryInfoAsync( url, instigator );
						
						EntityId eid = vi.getFolderEntryInfo().getEntityId();
						if ( vi.isInvokeShare() )
						{
							if ( vi.isInvokeShareEnabled() )
							{
								GwtTeaming.fireEventAsync(
									new ShareSelectedEntitiesEvent(
										eid.getBinderId(),
										eid ) );
							}
							else
							{
								GwtClientHelper.deferredAlert(GwtTeaming.getMessages().contentControl_Warning_ShareNoRights());
							}
						}
						if ( vi.isInvokeSubscribe() )
						{
							GwtTeaming.fireEventAsync(
								new SubscribeSelectedEntitiesEvent(
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
				case CREATE_FROM_WIKI_LINK:
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
				setContentFrameUrl( "", instigator );
				clear();
				
				break;
			
			case JSP_CONTENT_VIEW:
				// A JSP view!  Load the URL into the content frame...
				setContentFrameUrl( url, instigator );
				
				// ...tell the main content layout panel to not show a
				// ...GWT widget it may have...
				m_mainPage.getMainContentLayoutPanel().showWidget( null );
				
				// ...make sure the ContentControl is showing...
				ShowContentControlEvent.fireOne();

				// ...if we're navigating from the history, make sure
				// ...the masthead matches what was there.  Otherwise,
				// ...push the URL into the history cache.
				if ( historyAction )
				     GwtTeaming.fireEventAsync( new SetFilrActionFromCollectionTypeEvent( historySelectedMastheadCollection ) );
				else HistoryHelper.pushHistoryInfoAsync( url, instigator );
				
				// ...if requested invoke the share and/or subscribe
				// ...dialogs on the view.
				if ( vi.isInvokeShare() )
				{
					if ( vi.isInvokeShareEnabled() )
					{
						GwtTeaming.fireEventAsync(
							new InvokeShareBinderEvent(
								vi.getBinderInfo().getBinderId() ) );
					}
					else
					{
						GwtClientHelper.deferredAlert(GwtTeaming.getMessages().contentControl_Warning_ShareNoRights());
					}
				}
				if ( vi.isInvokeSubscribe() )
				{
					InvokeEmailNotificationEvent.fireOneAsync();
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
			setViewFromUrl(
				osbInfo.getBinderUrl(),
				osbInfo.getInstigator(),
				event.isHistoryAction(),
				event.getHistorySelectedMastheadCollection() );
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
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				GwtTeaming.fireEvent(
					new ContributorIdsReplyEvent(
						eventBinderId,
						contributorIds ) );
			}// end execute()
		} );
	}// end onContributorIdsRequest()
	
	
	/**
	 * Handles CopySelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the CopySelectedEntitiesEvent.Handler.onCopySelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onCopySelectedEntities( CopySelectedEntitiesEvent event )
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
	 * Handles ContextChangedEvent's received by this class.
	 * 
	 * Implements the ContextChangedEvent.Handler.onContextChanged() method.
	 * 
	 * @param event
	 */
	@Override
	public void onContextChanged( ContextChangedEvent event )
	{
		// We currently don't have anything to do after the context has
		// changed.
	}
	
	
	/**
	 * Handles DeleteSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the DeleteSelectedEntitiesEvent.Handler.onDeleteSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onDeleteSelectedEntities( DeleteSelectedEntitiesEvent event )
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
					cmd.setShowCollectionOnUserWS( GwtClientHelper.isLicenseFilr() );
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
							onDeleteSelectedEntitiesAsync( selectedEntityIds, parentBinderPermalink );
						}// end onSuccess()
					});
				}
				else
				{
					// No, we aren't deleting the binder in the view!
					// Are we deleting a folder entry that we're
					// currently viewing?
					EntityId eid = selectedEntityIds.get( 0 );
					if ( isEntityInJSPEntryView( eid ) )
					{
						// Yes!  Then we do the delete and refresh back
						// to deleted entry's parent binder.
						final Long binderId = eid.getBinderId();
						GetBinderPermalinkCmd cmd = new GetBinderPermalinkCmd( String.valueOf( binderId ) );
						GwtClientHelper.executeCommand( cmd, new AsyncCallback<VibeRpcResponse>()
						{
							@Override
							public void onFailure( Throwable t )
							{
								GwtClientHelper.handleGwtRPCFailure(
									t,
									GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
									binderId );
								onDeleteSelectedEntitiesAsync( selectedEntityIds, null );
							}//end onFailure()
							
							@Override
							public void onSuccess( VibeRpcResponse response )
							{
								StringRpcResponseData responseData = ((StringRpcResponseData) response.getResponseData());
								String binderPermalink = responseData.getStringValue();
								onDeleteSelectedEntitiesAsync( selectedEntityIds, binderPermalink );
							}// end onSuccess()
						});
					}
					else
					{
						// No, we aren't deleting a folder entry that
						// we're currently viewing either!  Simply
						// perform the delete.
						onDeleteSelectedEntitiesAsync( selectedEntityIds, null );
					}
				}
			}
		}
	}// end onDeleteSelectedEntities()
	

	/*
	 * Asynchronously deletes the selected entities.
	 */
	private void onDeleteSelectedEntitiesAsync( final List<EntityId> selectedEntityIds, final String targetBinderPermalink )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				onDeleteSelectedEntitiesNow( selectedEntityIds, targetBinderPermalink );
			}// end execute()
		} );
	}// end onDeleteSelectedEntitiesAsync()
	
	/*
	 * Synchronously deletes the selected entities.
	 */
	private void onDeleteSelectedEntitiesNow( final List<EntityId> selectedEntityIds, final String targetBinderPermalink )
	{
		// Delete the selected entities and reload the view to
		// redisplay things with the entries deleted.
		final boolean deletingBinders = EntityId.areBindersInEntityIds( selectedEntityIds );
		BinderViewsHelper.deleteSelections(
			selectedEntityIds,
			new DeleteEntitiesCallback()
		{
			@Override
			public void operationCanceled()
			{
				if ( deletingBinders )
				{
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
				}
				postDeleteReloadAsync( targetBinderPermalink );
			}// end operationCanceled())

			@Override
			public void operationComplete()
			{
				if ( deletingBinders )
				{
					GwtClientHelper.getRequestInfo().setRefreshSidebarTree();
				}
				postDeleteReloadAsync( targetBinderPermalink );
			}// end operationComplete()
			
			@Override
			public void operationFailed()
			{
				// Nothing to do.  The delete call will have told the
				// user about the failure.
			}// end operationFailed()
		} );
	}// end onDeleteSelectedEntitiesNow()
	
	
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
		setViewAsync( null, event.getUrl(), Instigator.GOTO_CONTENT_URL, false, null );
	}

	
	/**
	 * Handles MoveSelectedEntitiesEvent's received by this class.
	 * 
	 * Implements the MoveSelectedEntitiesEvent.Handler.onMoveSelectedEntities() method.
	 * 
	 * @param event
	 */
	@Override
	public void onMoveSelectedEntities( MoveSelectedEntitiesEvent event )
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
	 * Implements the ShowGenericWSEvent.Handler.onShowGenericWS() method.
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
		
		// Create a GlobalWorkspacesView widget for the selected
		// binder.
		GlobalWorkspacesView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
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
	 * Implements the ShowNetFoldersWSEvent.Handler.onShowNetFoldersWS() method.
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
	 * Handles ShowPersonalWorkspaceEvent's received by this class.
	 * 
	 * Implements the ShowPersonalWorkspaceEvent.Handler.onShowPersonalWorkspace() method.
	 */
	@Override
	public void onShowPersonalWorkspace( ShowPersonalWorkspaceEvent event )
	{
		// Create a PersonalWorkspaceView widget for the selected
		// binder.
		PersonalWorkspaceView.createAsync(
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
	}// end onShowPersonalWorkspace()
	
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
	 * Handles ShowPhotoAlbumFolderEvent's received by this class.
	 * 
	 * Implements the ShowPhotoAlbumFolderEvent.Handler.onShowPhotoAlbumFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowPhotoAlbumFolder( final ShowPhotoAlbumFolderEvent event )
	{
		// Create a PhotoAlbumFolderView widget for the selected
		// binder.
		PhotoAlbumFolderView.createAsync(
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
	}// end onShowPhotoAlbumFolder()
	
	/**
	 * Handles ShowProjectManagementWSEvent's received by this class.
	 * 
	 * Implements the ShowProjectManagementWSEvent.Handler.onShowProjectManagementWS() method.
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
		
		// Create the view widget for the selected binder.
		TeamWorkspacesView.createAsync( event.getBinderInfo(), event.getViewReady(), vClient );
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
		TeamWSView.createAsync(event.getBinderInfo(), event.getViewReady(), vClient);
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
	

	/**
	 * Handles ShowWikiFolderEvent's received by this class.
	 * 
	 * Implements the ShowWikiFolderEvent.Handler.onShowWikiFolder() method.
	 * 
	 * @param event
	 */
	@Override
	public void onShowWikiFolder( final ShowWikiFolderEvent event )
	{
		// Create a WikiFolderView widget for the selected binder.
		WikiFolderView.createAsync(
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
	}// end onShowWikiFolder()
	
	/*
	 * Asynchronously performs the reload necessary after an item has
	 * been deleted.
	 */
	private void postDeleteReloadAsync( final String targetBinderPermalink ) {
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				postDeleteReloadNow( targetBinderPermalink );
			}// end execute()
		} );
	}// end postDeleteReloadAsync();
	
	
	/*
	 * Synchronously performs the reload necessary after an item has
	 * been deleted.
	 */
	private void postDeleteReloadNow( final String targetBinderPermalink ) {
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
	}// end postDeleteReloadNow();
	
	
	/*
	 * Asynchronously shows the folder entry dialog.
	 */
	private void showFolderEntryDlgAsync( final ViewFolderEntryInfo vfei, final ViewReady viewReady )
	{
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute()
			{
				showFolderEntryDlgNow( vfei, viewReady );
			}// end execute()
		} );
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
	public void onViewForumEntry( final ViewForumEntryEvent event )
	{
		// If we're supposed to close activity stream mode on a view
		// details...
		if (m_mainPage.getMainPageInfo().isCloseActivityStreamOnViewDetails()) {
			// ...fire the appropriate event.
			ActivityStreamExitEvent.fireOne( ExitMode.EXIT_FOR_CONTEXT_SWITCH );
		}
		
		GwtClientHelper.deferCommand( new ScheduledCommand()
		{
			@Override
			public void execute() 
			{
				setViewFromUrl(
					event.getViewForumEntryUrl(),
					Instigator.VIEW_FOLDER_ENTRY,
					false,	// false -> Not a history action.
					null );	// null  -> No history Filr masthead action.
			}
		} );
	}
	
	/**
	 * Put clear() behind a split point
	 */
	public static void clear( final ContentControl contentControl )
	{
		GWT.runAsync( ContentControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				GwtClientHelper.deferredAlert( GwtTeaming.getMessages().codeSplitFailure_ContentControl() );
			}

			@Override
			public void onSuccess()
			{
				if ( contentControl != null )
					contentControl.clear();
			}
		} );
	}
	
	/**
	 * Loads the ContentControl split point and returns an instance of
	 * it via the callback.
	 * 
	 * @param mainPage
	 * @param name
	 * @param contentCtrlClient
	 */
	public static void createControl( final GwtMainPage mainPage, final String name, final ContentControlClient contentCtrlClient )
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

	/**
	 * Put empty() behind a split point
	 */
	public static void empty( final ContentControl contentControl )
	{
		GWT.runAsync( ContentControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				GwtClientHelper.deferredAlert( GwtTeaming.getMessages().codeSplitFailure_ContentControl() );
			}

			@Override
			public void onSuccess()
			{
				if ( contentControl != null )
					contentControl.empty();
			}
		} );
	}
	
	/**
	 * Put reload() behind a split point
	 */
	public static void reload( final ContentControl contentControl )
	{
		GWT.runAsync( ContentControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				GwtClientHelper.deferredAlert( GwtTeaming.getMessages().codeSplitFailure_ContentControl() );
			}

			@Override
			public void onSuccess()
			{
				if ( contentControl != null )
					contentControl.reload();
			}
		} );
	}
	
	/**
	 * Put the setContentFrameUrl() behind a split point
	 */
	public static void setContentFrameUrl(
		final ContentControl contentControl,
		final String newUrl,
		final Instigator instigator )
	{
		GWT.runAsync( ContentControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				GwtClientHelper.deferredAlert( GwtTeaming.getMessages().codeSplitFailure_ContentControl() );
			}

			@Override
			public void onSuccess()
			{
				if ( contentControl != null )
					contentControl.setContentFrameUrl( newUrl, instigator );
			}
		} );
	}// end setContentFrameUrl()

	/**
	 * Put setDimensions() behind a split point
	 */
	public static void setDimensions(
		final ContentControl contentControl,
		final int width,
		final int height )
	{
		GWT.runAsync( ContentControl.class, new RunAsyncCallback()
		{			
			@Override
			public void onFailure( Throwable reason )
			{
				GwtClientHelper.deferredAlert( GwtTeaming.getMessages().codeSplitFailure_ContentControl() );
			}

			@Override
			public void onSuccess()
			{
				if ( contentControl != null )
					contentControl.setDimensions( width, height );
			}
		} );
	}
}// end ContentControl
