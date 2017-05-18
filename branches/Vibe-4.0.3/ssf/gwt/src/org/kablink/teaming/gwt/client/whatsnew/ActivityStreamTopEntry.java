/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.whatsnew;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.rpc.shared.ActivityStreamEntryListRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.GetBinderPermalinkCmd;
import org.kablink.teaming.gwt.client.rpc.shared.GetEntryCommentsCmd;
import org.kablink.teaming.gwt.client.rpc.shared.StringRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeRpcResponse;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * ?
 * 
 * @author jwootton@novell.com
 */
public class ActivityStreamTopEntry extends ActivityStreamUIEntry
	implements ActivityStreamCommentsContainer
{
	private ArrayList<ActivityStreamComment> m_comments;
	private FlowPanel m_numCommentsPanel;		// Shows the number of comments that exist for this entry.
	private Anchor m_parentBinderName;			// Name of the binder this entry comes from.
	private String m_parentBinderId;			// Id of the binder this entry comes from.
	private String m_parentBinderPermalink;
	private Image m_breadSpaceImg;
	private int m_numComments;
	private boolean m_showingAllComments;
	private boolean m_retrievedAllComments;
	
	/**
	 * 
	 */
	public ActivityStreamTopEntry( ActivityStreamCtrl activityStreamCtrl, DescViewFormat descViewFormat )
	{
		super( activityStreamCtrl, descViewFormat );
		
		m_parentBinderId = null;
		m_parentBinderPermalink = null;
		
		// Create a list to hold the comments for this entry.
		m_comments = new ArrayList<ActivityStreamComment>();

		m_showingAllComments = false;
		m_retrievedAllComments = false;
	}

	
	/**
	 * Add the name of the binder this entry comes from.
	 */
	@Override
	public void addAdditionalHeaderUI( FlowPanel headerPanel )
	{
		FlowPanel panel;
		ImageResource imageResource;

		panel = new FlowPanel();
		panel.addStyleName( "ActivityStreamTopEntryAdditionalUIPanel" );
		headerPanel.add( panel );
		
		imageResource = GwtTeaming.getImageBundle().breadSpace();
		m_breadSpaceImg = new Image( imageResource );
		m_breadSpaceImg.setVisible( false );
		panel.add( m_breadSpaceImg );
		
		// Create a label that holds the name of the binder this entry comes from.
		m_parentBinderName = new Anchor();
		m_parentBinderName.addStyleName( "activityStreamTopEntryBinderName" );
		panel.add( m_parentBinderName );
		
		// Add mouse over handler.
		{
			MouseOverHandler mouseOverHandler;
			
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOver( MouseOverEvent event )
				{
					m_parentBinderName.addStyleName( "activityStreamHover" );
				}
				
			};
			m_parentBinderName.addMouseOverHandler( mouseOverHandler );
		}
		
		// Add mouse out handler.
		{
			MouseOutHandler mouseOutHandler;
			
			mouseOutHandler = new MouseOutHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onMouseOut( MouseOutEvent event )
				{
					m_parentBinderName.removeStyleName( "activityStreamHover" );
				}
			};
			m_parentBinderName.addMouseOutHandler( mouseOutHandler );
		}
		
		// Add a click handler.
		{
			ClickHandler clickHandler;
			
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					handleClickOnParentBinder();
				}
				
			};
			m_parentBinderName.addClickHandler( clickHandler );
		}
	}

	
	/**
	 * 
	 */
	private void addComment( ActivityStreamEntry activityStreamEntry )
	{
		FlowPanel commentsPanel;
		ActivityStreamComment commentUI;

		// Get an ActivityStreamComment object.
		commentUI = getActivityStreamCommentObject();
		commentUI.setData( activityStreamEntry );
		
		// Add this ui widget to panel that holds all comments
		commentsPanel = getCommentsPanel();
		if ( commentsPanel != null )
			commentsPanel.add( commentUI );
	}
	
	
	/**
	 * Return true if the "reply to entry" widget is open and the user has entered text in it.
	 */
	public boolean checkForReplyInProgress()
	{
		boolean inProgress;
		FlowPanel commentsPanel;
		int i;
		int numComments;
		
		inProgress = false;
		
		if ( isReplyInProgress() )
			return true;
		
		// Check each comment to see if the "reply to entry" widget is open and the user has entered text in it,
		commentsPanel = getCommentsPanel();
		numComments = commentsPanel.getWidgetCount();
		for (i = 0; i < numComments && inProgress == false ; ++i)
		{
			Widget nextWidget;
			
			nextWidget = commentsPanel.getWidget( i );
			if ( nextWidget instanceof ActivityStreamComment )
			{
				ActivityStreamComment comment;
				
				comment = (ActivityStreamComment) nextWidget;
				inProgress = comment.isReplyInProgress();
			}
		}

		return inProgress;
	}
	
	
	/**
	 * 
	 */
	@Override
	public void clearEntrySpecificInfo()
	{
		super.clearEntrySpecificInfo();
		
		// Clear all data from the comments
		for (ActivityStreamComment nextComment : m_comments)
		{
			nextComment.clearEntrySpecificInfo();
			
			// Remove this comment from this entry.
			nextComment.removeFromParent();
		}
		
		m_showingAllComments = false;
		m_retrievedAllComments = false;
		m_numComments = 0;
		if ( m_numCommentsPanel != null )
			m_numCommentsPanel.getElement().setInnerText( "" );
		m_parentBinderName.setText( "" );
		m_parentBinderName.setTitle( "" );
		m_parentBinderId = null;
		m_parentBinderPermalink = null;
		
		// Hide the image between the title and the parent binder name.
		m_breadSpaceImg.setVisible( false );
	}
	
	
	/**
	 * Create the panel that all comments will live in.
	 */
	@Override
	public FlowPanel createCommentsPanel()
	{
		return new FlowPanel();
	}
	
	/**
	 * Find an unused ActivityStreamComment object and return it.  If we don't have an unused one
	 * create a new one.
	 */
	private ActivityStreamComment getActivityStreamCommentObject()
	{
		FlowPanel commentsPanel;
		ActivityStreamComment commentUI = null;
		int numberOfComments;
		
		// Get the panel that holds all the comments.
		commentsPanel = getCommentsPanel();
		
		// Do we have an ActivityStreamComment available?
		numberOfComments = commentsPanel.getWidgetCount();
		if ( numberOfComments < m_comments.size() )
		{
			// Yes, use an existing one.
			commentUI = m_comments.get( numberOfComments );
		}
		
		if ( commentUI == null )
		{
			// Create a new one.
			commentUI = new ActivityStreamComment( getActivityStreamCtrl(), this, getDescViewFormat() );
			m_comments.add( commentUI );
		}
		
		return commentUI;
	}
	
	/**
	 * 
	 */
	@Override
	public String getAvatarImageStyleName( ActivityStreamEntry asEntry )
	{
		// Is this entry a file entry?
		if ( asEntry.isEntryFile() )
			return "activityStreamTopEntryFileImg";
		
		return "activityStreamTopEntryAvatarImg";
	}

	
	/**
	 * Return the name of the style used with the content panel.
	 */
	@Override
	public String getContentPanelStyleName()
	{
		return "activityStreamTopEntryContentPanel";
	}
	
	/**
	 * 
	 */
	@Override
	public String getEntryHeaderStyleName()
	{
		return "activityStreamTopEntryHeader";
	}
	
	/**
	 * If the given entry is a file, return the url to the image that matches the file.  Otherwise, return
	 * the url to the author's avatar. 
	 */
	@Override
	public String getEntryImgUrl( ActivityStreamEntry asEntry )
	{
		String url;
		
		// Is this entry a file entry?
		if ( asEntry.isEntryFile() )
		{
			String fileImgUrl;
			
			// Yes
			// Does the file have an image we can display?
			fileImgUrl = asEntry.getEntryFileIcon();
			if ( fileImgUrl != null && fileImgUrl.length() > 0 )
			{
				// Yes
				url = GwtClientHelper.getRequestInfo().getImagesPath() + fileImgUrl;
			}
			else
			{
				// No, use default
				url = GwtTeaming.getFilrImageBundle().entry_medium().getSafeUri().asString();
			}
		}
		else
		{
			// No
			// Get the url to the author's avatar
			url = asEntry.getAuthorAvatarUrl();
			
			// Does the author have an avatar?
			if ( url == null || url.length() == 0 )
			{
				// Default to the "no avatar" image.
				url = GwtMainPage.m_requestInfo.getImagesPath() + "pics/UserPhoto.png";
			}
		}
		
		return url;
	}

	/**
	 * Return the name of the style used with a top entry's full description.
	 */
	@Override
	public String getFullDescStyleName()
	{
		return "activityStreamTopEntryFullDesc";
	}

	
	/**
	 * Return the name of the style used with the div that holds the entry.
	 */
	@Override
	public String getMainPanelStyleName()
	{
		return "activityStreamTopEntryMainPanel";
	}
	
	
	/**
	 * Return the panel that holds the number of comments
	 */
	@Override
	public FlowPanel getNumCommentsPanel()
	{
		// Create a span for the number of comments text to live in.
		m_numCommentsPanel = new FlowPanel();
		m_numCommentsPanel.setTitle( GwtTeaming.getMessages().showAllComments() );
		m_numCommentsPanel.addStyleName( "activityStreamNumCommentsPanel" );
		
		// Add a click handler.
		{
			ClickHandler clickHandler;
			
			clickHandler = new ClickHandler()
			{
				/**
				 * 
				 */
				@Override
				public void onClick( ClickEvent event )
				{
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute() 
						{
							// Are we currently showing all comments?
							if ( m_showingAllComments == false )
							{
								// No
								showAllComments();
								m_showingAllComments = true;

								// Change the title on the "num comments" label to "hide comments"
								m_numCommentsPanel.setTitle( GwtTeaming.getMessages().hideComments() );
							}
							else
							{
								// Yes
								hideComments();
								m_showingAllComments = false;
								
								// Change the title on the "num comments" label to "show comments"
								m_numCommentsPanel.setTitle( GwtTeaming.getMessages().showAllComments() );
							}
						}
					} );
				}
				
			};
			m_numCommentsPanel.addDomHandler( clickHandler, ClickEvent.getType() );
		}

		return m_numCommentsPanel;
	}
	
	/**
	 * Return the name of the style used with a top entry's partial description.
	 */
	@Override
	public String getPartialDescStyleName()
	{
		if ( "vibe".equalsIgnoreCase( GwtClientHelper.getProductName() ) )
			return "activityStreamTopEntryPartialDesc-vibe";
		
		return "activityStreamTopEntryPartialDesc";
	}

	
	/**
	 * 
	 */
	@Override
	public String getTitlePanelStyleName()
	{
		return "activityStreamTopEntryTitlePanel";
	}

	
	/**
	 * 
	 */
	@Override
	public String getTitleStyleName()
	{
		return "activityStreamTopEntryTitle";
	}
	
	
	/**
	 * Take the user to the parent binder.
	 */
	private void gotoParentBinder()
	{
		if ( m_parentBinderId != null && m_parentBinderPermalink != null )
		{
			EventHelper.fireChangeContextEventAsync(
				m_parentBinderId,
				m_parentBinderPermalink,
				Instigator.ACTIVITY_STREAM_BINDER_SELECT );
		}
	}
	
	
	/**
	 * This method gets invoked when the user clicks on the avatar/file image.
	 */
	@Override
	public void handleClickOnAvatar( Element element )
	{
		// Behave the same as if the user clicked on the title.
		handleClickOnTitle();
	}
	
	/**
	 * The user clicked on the binder name.  Take the user to that binder.
	 */
	public void handleClickOnParentBinder()
	{
		if ( m_parentBinderId == null )
		{
			Window.alert( "Parent binder id is null.  This should never happen" );
			return;
		}
		
		if ( m_parentBinderPermalink != null )
		{
			gotoParentBinder();
		}
		else
		{
			GetBinderPermalinkCmd cmd;
			AsyncCallback<VibeRpcResponse> callback;
			
			callback = new AsyncCallback<VibeRpcResponse>()
			{
				/**
				 * 
				 */
				@Override
				public void onFailure(Throwable t)
				{
					GwtClientHelper.handleGwtRPCFailure(
						t,
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						m_parentBinderId );
				}
				
				/**
				 * 
				 */
				@Override
				public void onSuccess(  VibeRpcResponse response )
				{
					StringRpcResponseData responseData;

					responseData = (StringRpcResponseData) response.getResponseData();
					m_parentBinderPermalink = responseData.getStringValue();
					
					GwtClientHelper.deferCommand( new ScheduledCommand()
					{
						@Override
						public void execute()
						{
							// Take the user to the parent binder.
							gotoParentBinder();
						}
					} );
				}
			};
			
			// Issue an ajax request to get the permalink of the binder that is the source of the activity stream.
			cmd = new GetBinderPermalinkCmd( m_parentBinderId );
			GwtClientHelper.executeCommand( cmd, callback );
		}
	}
	
	/**
	 * 
	 */
	private void hideComments()
	{
		FlowPanel commentsPanel;
		
		commentsPanel = getCommentsPanel();
		if ( commentsPanel != null )
		{
			commentsPanel.setVisible( false );
		}
	}

	
	/**
	 * Insert the given reply as the first reply to this entry.
	 */
	@Override
	public void insertReply( ActivityStreamEntry reply )
	{
		FlowPanel commentsPanel;
		ActivityStreamComment commentUI;

		// Get an ActivityStreamComment object.
		commentUI = getActivityStreamCommentObject();
		commentUI.setData( reply );
		
		// Add this ui widget to panel that holds all comments
		commentsPanel = getCommentsPanel();
		commentsPanel.add( commentUI );
		
		// Update the number of comments on this top entry.
		++m_numComments;
		updateCommentsLabel();
	}
	
	/**
	 * 
	 */
	public void setBinderId( String binderId )
	{
		m_parentBinderId = binderId;
	}
	
	
	/**
	 * 
	 */
	public void setBinderName( String binderName )
	{
		m_parentBinderName.setText( binderName );
	}
	
	
	/**
	 * Set the data this we should display from the given ActivityStreamEntry
	 */
	@Override
	public void setData( ActivityStreamEntry entryItem )
	{
		List<ActivityStreamEntry> comments = null;
		String parentBinderName;
		int i;
		
		super.setData( entryItem );
		
		m_numComments = entryItem.getEntryComments();
		updateCommentsLabel();
		
		parentBinderName = entryItem.getParentBinderName(); 
		setBinderName( parentBinderName );
		setBinderId( entryItem.getParentBinderId() );
		m_parentBinderPermalink = null;
		
		m_parentBinderName.setTitle( entryItem.getParentBinderHover() );
		
		// Do we have a parent binder name?  If the user does not have rights to the parent
		// binder we will not have a parent binder name.
		if ( parentBinderName != null && parentBinderName.length() > 0 )
		{
			// Yes, show the image between the title and the parent binder name.
			m_breadSpaceImg.setVisible( true );
		}
		else
			m_breadSpaceImg.setVisible( false );

		// Get the comments for the given item.
		comments = entryItem.getComments();
		
		if ( comments != null )
		{
			for (i = 0; i < comments.size(); ++i)
			{
				ActivityStreamEntry nextComment;
				
				nextComment = comments.get( i );
				
				// Add this comment to our entry.
				addComment( nextComment );
			}
		}
	}
	
	/**
	 * Show all the comments for this entry
	 */
	private void showAllComments()
	{
		if ( m_numCommentsPanel != null )
		{
			// Have we already retrieved all the comments for this entry?
			if ( m_retrievedAllComments == false )
			{
				GetEntryCommentsCmd cmd;
				AsyncCallback<VibeRpcResponse> callback;
				
				// No
				callback = new AsyncCallback<VibeRpcResponse>()
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
												getEntryTitle() );
					}
					
					/**
					 * 
					 */
					@Override
					public void onSuccess(  VibeRpcResponse response )
					{
						ActivityStreamEntryListRpcResponseData responseData;
						final List<ActivityStreamEntry> listOfComments;

						// Clear all data from the existing ui comment objects
						if ( m_comments != null )
						{
							for (ActivityStreamComment nextComment : m_comments)
							{
								nextComment.clearEntrySpecificInfo();
								
								// Remove this comment from this entry.
								nextComment.removeFromParent();
							}
						}
						
						// Get the list of comments from the response.
						responseData = (ActivityStreamEntryListRpcResponseData) response.getResponseData();
						listOfComments = responseData.getActivityStreamEntryList();
						
						GwtClientHelper.deferCommand( new ScheduledCommand()
						{
							@Override
							public void execute()
							{
								FlowPanel commentsPanel;
								
								m_retrievedAllComments = true;
								
								// Do we have a list of comments?
								if ( listOfComments != null )
								{
									// Yes
									for (ActivityStreamEntry nextComment: listOfComments)
									{
										addComment( nextComment );
									}
								}
								
								commentsPanel = getCommentsPanel();
								commentsPanel.setVisible( true );
								
								m_numComments = listOfComments.size();
								updateCommentsLabel();
							}
						} );
					}
				};
				
				// Issue a request to get all the comments for this entry.
				cmd = new GetEntryCommentsCmd( getEntryId() );
				GwtClientHelper.executeCommand( cmd, callback );
			}
			else
			{
				FlowPanel commentsPanel;
				
				// Yes, simply show the comments panel
				commentsPanel = getCommentsPanel();
				commentsPanel.setVisible( true );
			}
		}
	}
	
	/**
	 * Update the label that displays the number of comments there are on this entry.
	 */
	private void updateCommentsLabel()
	{
		if ( m_numCommentsPanel != null )
		{
			if ( m_numComments > 0 )
			{
				String text;

				text = String.valueOf( m_numComments );
				m_numCommentsPanel.getElement().setInnerText( text );
				m_numCommentsPanel.setVisible( true );
			}
			else
				m_numCommentsPanel.setVisible( false );
		}
	}
}
