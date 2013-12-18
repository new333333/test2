/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.rpc.shared.ActivityStreamEntryListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCommentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.util.CommentsInfo;
import org.kablink.teaming.gwt.client.util.EntityId;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu.ActionMenuItem;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamComment;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCommentsContainer;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlUsage;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;

/**
 * This widget is used to display the comments for a given entity.
 * 
 * @author jwootton@novell.com
 */
public class CommentsWidget extends Composite
	implements ActivityStreamCommentsContainer, CommentAddedCallback
{
	private boolean m_acsPseudoAttached;	// Set true once m_activityStreamCtrl is used and pseudo attached to the DOM.
	private CommentAddedCallback m_commentAddedCallback;
	private CommentsInfo m_commentsInfo;
	private VibeFlowPanel m_mainPanel;
	private ActivityStreamCtrl m_activityStreamCtrl;
	private AsyncCallback<VibeRpcResponse> m_getCommentsCallback;
	private boolean m_showTitle;
	
	private static final BigDecimal DELTA = BigDecimal.valueOf( 1, 1 );
	

	/**
	 * 
	 */
	public CommentsWidget( boolean showTitle )
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.addStyleName( "commentsWidget_commentsPanel" );
		
		m_showTitle = showTitle;

		initWidget( m_mainPanel );
	}

	/**
	 * Add the given comment to our list of comments.
	 * 
	 * @param activityStreamEntry
	 * @param append
	 * @param scrollIntoView
	 */
	public void addComment( ActivityStreamEntry activityStreamEntry, boolean scrollIntoView )
	{
		ActivityStreamComment commentUI = null;
		
		commentUI = new ActivityStreamComment(
											m_activityStreamCtrl,
											this,
											DescViewFormat.FULL,
											m_showTitle );
		commentUI.addStyleName( "commentsWidget_commentStylesOverride" );
		commentUI.setData( activityStreamEntry );
		m_mainPanel.add( commentUI );

		// If the activity stream control hasn't been pseudo attached
		// to the DOM yet...
		if ( ! m_acsPseudoAttached )
		{
			// ...tell it to attach.
			m_activityStreamCtrl.onAttach();
			m_acsPseudoAttached = true;
		}
		
		if ( scrollIntoView )
		{
			showNewComment( commentUI );
		}
	}
	
	/**
	 * Add the list of comments to out list of comments.
	 */
	private void addComments( List<ActivityStreamEntry> listOfComments )
	{
		if ( listOfComments != null )
		{
			ArrayList<ActivityStreamEntry> topLevelComments;
			String baseDocNum;
			
			// Get the base doc number
			baseDocNum = ( GwtClientHelper.hasItems( listOfComments ) ? getBaseDocNum( listOfComments.get( 0 ) ) : null );
			
			if ( baseDocNum != null )
			{
				// Get a list of all the top-level comments.
				topLevelComments = getListOfChildComments( baseDocNum, listOfComments );
				
				// For each top-level comment get all of it's children.
				getChildComments( topLevelComments, listOfComments );

				for ( ActivityStreamEntry nextComment: topLevelComments )
				{
					// Add this comment to our ui
					addComment( nextComment, false );
				}
			}
		}
	}
	
	/**
	 * This method gets called when a reply is added to one of our sub comments
	 */
	@Override
	public void commentAdded( Object callbackData )
	{
		if ( m_commentAddedCallback != null )
			m_commentAddedCallback.commentAdded( m_commentsInfo );
	}

	/**
	 * Return the base doc number.  For example, if the given ActivityStreamEntry has a
	 * doc number of "4.1.x", we will return "4"
	 */
	private String getBaseDocNum( ActivityStreamEntry activityStreamEntry )
	{
		String baseDocNum = null;
		
		if ( activityStreamEntry != null && activityStreamEntry.getEntryDocNum() != null )
		{
			String entryDocNum;
			int index;
			
			entryDocNum = activityStreamEntry.getEntryDocNum();
			index = entryDocNum.indexOf( '.' );
			if ( index > 0 )
				baseDocNum = entryDocNum.substring( 0, index );
		}
		
		return baseDocNum;
	}
	
	/**
	 * Issue an rpc request to get the comments for the given entity
	 */
	private void getCommentsFromServer()
	{
		if ( m_commentsInfo != null )
		{
			GetEntryCommentsCmd cmd;
			
			if ( m_getCommentsCallback == null )
			{
				m_getCommentsCallback = new AsyncCallback<VibeRpcResponse>()
				{
					/**
					 * 
					 */
					@Override
					public void onFailure(Throwable t)
					{
						GwtClientHelper.handleGwtRPCFailure(
												t,
												GwtTeaming.getMessages().rpcFailure_GetEntryComments(),
												m_commentsInfo.getEntityTitle() );
					}
					
					/**
					 * 
					 */
					@Override
					public void onSuccess(  VibeRpcResponse response )
					{
						Scheduler.ScheduledCommand cmd;
						ActivityStreamEntryListRpcResponseData responseData;
						final List<ActivityStreamEntry> listOfComments;
	
						// Clear all data from the existing ui comment objects
						if ( m_mainPanel != null )
						{
							m_mainPanel.clear();
						}
						
						// Get the list of comments from the response.
						responseData = (ActivityStreamEntryListRpcResponseData) response.getResponseData();
						listOfComments = responseData.getActivityStreamEntryList();
						
						cmd = new Scheduler.ScheduledCommand()
						{
							@Override
							public void execute()
							{
								// Do we have a list of comments?
								if ( listOfComments != null )
								{
									// Add the comments to the widget
									addComments( listOfComments );
								}
							}
						};
						Scheduler.get().scheduleDeferred( cmd );
					}
				};
			}
			
			// Issue a request to get all the comments for this entry.
			cmd = new GetEntryCommentsCmd( getEntityId() );
			GwtClientHelper.executeCommand( cmd, m_getCommentsCallback );
		}
	}
	
	/**
	 * 
	 */
	private String getEntityId()
	{
		String entityIdS = "";
		
		if ( m_commentsInfo != null )
		{
			EntityId entityId;
			
			entityId = m_commentsInfo.getEntityId();
			if ( entityId != null )
			{
				if ( entityId.getEntityType().equalsIgnoreCase( EntityId.FOLDER_ENTRY ) )
					entityIdS = entityId.getEntityId().toString();
				else
					entityIdS = entityId.getBinderId().toString();
			}
		}
		
		return entityIdS;
	}
	
	/**
	 * Return a list of all the child sub comments for the given doc number.
	 * For example, if we are passed "4" as the doc number we would return "4.1", "4.2" but not "4.1.1"
	 * if we are passed "4.1" we would "4.1.1", "4.1.2" but not "4.1.1.1"
	 */
	private ArrayList<ActivityStreamEntry> getListOfChildComments(
		String baseDocNum,
		List<ActivityStreamEntry> listOfComments )
	{
		ArrayList<ActivityStreamEntry> listOfChildComments;
		
		listOfChildComments = new ArrayList<ActivityStreamEntry>();
		
		if ( listOfComments != null && baseDocNum != null )
		{
			int baseDocNumLen;
			int i;
			
			baseDocNum += ".";
			baseDocNumLen = baseDocNum.length();
			
			for ( i = 0; i < listOfComments.size(); ++i )
			{
				ActivityStreamEntry nextComment;
				String docNum;
				
				nextComment = listOfComments.get( i );
				
				// Does the doc number have a '.' in it after the base doc number?
				docNum = nextComment.getEntryDocNum();
				if ( docNum != null &&
					 docNum.startsWith( baseDocNum ) &&
					 docNum.indexOf( '.', baseDocNumLen ) < 0 )
				{
					// No, comment is a child comment
					listOfChildComments.add( nextComment );
					
					// Remove the comment from the list of comments so we don't include it when
					// we are searching for sub comments.
					listOfComments.remove( i );
					--i;
				}
			}
		}
		
		return listOfChildComments;
	}
	
	/**
	 * For each parent comment, find it's child comments from the listOfAllComments.
	 */
	private void getChildComments(
		ArrayList<ActivityStreamEntry> listOfParentComments,
		List<ActivityStreamEntry> listOfAllComments )
	{
		if ( listOfParentComments != null && listOfAllComments != null )
		{
			for ( ActivityStreamEntry nextComment: listOfParentComments )
			{
				String baseDocNum;
				
				// Get the list of child comments
				baseDocNum = nextComment.getEntryDocNum();
				if ( baseDocNum != null )
				{
					ArrayList<ActivityStreamEntry> childComments;
					
					childComments = getListOfChildComments( baseDocNum, listOfAllComments );
					nextComment.setComments( childComments );
					
					// For each child comment, get its child comments.
					if ( childComments != null )
					{
						getChildComments( childComments, listOfAllComments );
					}
				}
			}
		}
	}
	
	/**
	 * Initialize the widget
	 * 
	 * @param commentsInfo
	 * @param commentAddedCallback
	 */
	public void init( CommentsInfo commentsInfo, CommentAddedCallback commentAddedCallback )
	{
		m_commentsInfo = commentsInfo;
		m_commentAddedCallback = commentAddedCallback;
		
		// Have we created an ActivityStreamCtrl before?
		if ( m_activityStreamCtrl == null )
		{
			ActionsPopupMenu actionsMenu;
			ArrayList<ActionMenuItem> list;
			
			list = new ArrayList<ActionMenuItem>();
			list.add( ActionMenuItem.REPLY );
			list.add( ActionMenuItem.SUBSCRIBE );
			if ( GwtTeaming.m_requestInfo.isLicenseFilr() == false )
			{
				list.add( ActionMenuItem.TAG );
				list.add( ActionMenuItem.SEND_TO_FRIEND );
				list.add( ActionMenuItem.SEPARATOR );
				list.add( ActionMenuItem.MARK_READ );
				list.add( ActionMenuItem.MARK_UNREAD );
			}
			
			actionsMenu = new ActionsPopupMenu( true, true, list.toArray( new ActionMenuItem[list.size()] ) );
			
			// No, create one.  The only reason we need to create an ActivityStreamCtrl
			// is because the ActivityStreamComment object needs one.
			ActivityStreamCtrl.createAsync( ActivityStreamCtrlUsage.COMMENTS, false, actionsMenu, new ActivityStreamCtrlClient()
			{			
				@Override
				public void onUnavailable()
				{
					// Nothing to do.  Error handled in asynchronous provider.
				}
				
				@Override
				public void onSuccess( ActivityStreamCtrl asCtrl )
				{
					m_activityStreamCtrl = asCtrl;
					m_activityStreamCtrl.setCheckForChanges( false );
					m_activityStreamCtrl.setDefaultDescViewFormat( DescViewFormat.FULL );
					
					// Issue an rpc request to get the comments on the given entity
					getCommentsFromServer();
				}
			} );
		}
		else
		{
			// Issue an rpc request to get the comments on the given entity
			getCommentsFromServer();
		}
	}
	
	public void init( CommentsInfo commentsInfo )
	{
		// Always use the initial form of the method.
		init( commentsInfo, null );	// null -> Caller doesn't need notifications of added comments.
	}
	
	/**
	 * Insert the given comments as the first comment.
	 */
	@Override
	public void insertReply( ActivityStreamEntry reply )
	{
		addComment( reply, true );
		
		if ( m_commentAddedCallback != null )
			m_commentAddedCallback.commentAdded( m_commentsInfo );
	}
	
	/**
	 * 
	 */
	private void showNewComment( ActivityStreamComment asComment )
	{
		Timer showTimer;
		final Element element;
		
		element = asComment.getElement();
		
		element.scrollIntoView();
		element.getStyle().setOpacity( 0 );

		showTimer = new Timer()
		{
			@Override
			public void run()
			{
				String opacityStr;
				boolean increased = false;

				opacityStr = element.getStyle().getOpacity();
				if ( opacityStr != null && opacityStr.length() > 0 )
				{
					try
					{
						BigDecimal opacity;
				
						opacity = new BigDecimal( opacityStr );
						if ( opacity.compareTo( new BigDecimal( 1 ) ) < 0 )
						{
							element.getStyle().setOpacity( opacity.add( DELTA ).doubleValue() );
							increased = true;
						}
					}
					catch ( NumberFormatException nfe )
					{
					}
				}
				
				if ( increased == false )
				{
					element.getStyle().setOpacity( 1 );
					cancel();
				}
			}
		};
         
		showTimer.scheduleRepeating( 75 );
	}

	/**
	 * Called when the CommentsWidget is attached to the DOM.
	 * 
	 * Overrides the Widget.onAttach() method.
	 */
	@Override
	public void onAttach() {
		super.onAttach();
	}
	
	/**
	 * Called when the CommentsWidget is detached from the DOM.
	 * 
	 * Overrides the Widget.onDetach() method.
	 */
	@Override
	public void onDetach() {
		super.onDetach();

		// If the activity stream control has been pseudo attached to
		// the DOM...
		if ( m_acsPseudoAttached )
		{
			// ...tell it to detach.
			m_activityStreamCtrl.onDetach();
			m_acsPseudoAttached = false;
		}
	}
}

