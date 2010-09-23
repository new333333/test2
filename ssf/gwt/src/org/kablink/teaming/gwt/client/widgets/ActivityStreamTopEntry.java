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

package org.kablink.teaming.gwt.client.widgets;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionHandler;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.HttpRequestInfo;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

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
import com.google.gwt.user.client.ui.InlineLabel;


/**
 * 
 */
public class ActivityStreamTopEntry extends ActivityStreamUIEntry
{
	private ArrayList<ActivityStreamComment> m_comments;
	private InlineLabel m_numCommentsLabel;		// Shows the number of comments that exist for this entry.
	private Anchor m_parentBinderName;			// Name of the binder this entry comes from.
	private String m_parentBinderId;			// Id of the binder this entry comes from.
	private String m_parentBinderPermalink;
	private Image m_breadSpaceImg;
	
	/**
	 * 
	 */
	public ActivityStreamTopEntry( ActionHandler actionHandler )
	{
		super( actionHandler );
		
		m_parentBinderId = null;
		m_parentBinderPermalink = null;
		
		// Create a list to hold the comments for this entry.
		m_comments = new ArrayList<ActivityStreamComment>();
	}

	
	/**
	 * Add the name of the binder this entry comes from.
	 */
	public void addAdditionalHeaderUI( FlowPanel headerPanel )
	{
		ImageResource imageResource;

		// Create a span for the number of comments text to live in.
		m_numCommentsLabel = new InlineLabel();
		m_numCommentsLabel.addStyleName( "activityStreamNumCommentsLabel" );
		headerPanel.add( m_numCommentsLabel );
		
		imageResource = GwtTeaming.getImageBundle().breadSpace();
		m_breadSpaceImg = new Image( imageResource );
		m_breadSpaceImg.setVisible( false );
		headerPanel.add( m_breadSpaceImg );
		
		// Create a label that holds the name of the binder this entry comes from.
		m_parentBinderName = new Anchor();
		m_parentBinderName.addStyleName( "activityStreamTopEntryBinderName" );
		headerPanel.add( m_parentBinderName );
		
		// Add mouse over handler.
		{
			MouseOverHandler mouseOverHandler;
			
			mouseOverHandler = new MouseOverHandler()
			{
				/**
				 * 
				 */
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
	private void addComment( ActivityStreamEntry activityStreamEntry, int commentNum )
	{
		FlowPanel mainPanel;
		ActivityStreamComment commentUI;

		// We recycle ActivityStreamComment objects.
		// Do we have an old one we can use?
		commentUI = null;
		if ( commentNum < m_comments.size() )
			commentUI = m_comments.get( commentNum );
		if ( commentUI == null )
		{
			// No, create a new one.
			commentUI = new ActivityStreamComment( m_actionHandler );
			m_comments.add( commentUI );
		}
		
		commentUI.setData( activityStreamEntry );
		
		// Add this ui widget to our widget.
		mainPanel = getMainPanel();
		mainPanel.add( commentUI );
	}
	
	
	/**
	 * 
	 */
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
		
		m_numCommentsLabel.setText( "" );
		m_parentBinderName.setText( "" );
		m_parentBinderName.setTitle( "" );
		m_parentBinderId = null;
		m_parentBinderPermalink = null;
		
		// Hide the image between the title and the parent binder name.
		m_breadSpaceImg.setVisible( false );
	}
	
	
	/**
	 * 
	 */
	public String getAvatarImageStyleName()
	{
		return "activityStreamTopEntryAvatarImg";
	}

	
	/**
	 * Return the name of the style used with the content panel.
	 */
	public String getContentPanelStyleName()
	{
		return "activityStreamTopEntryContentPanel";
	}

	
	/**
	 * 
	 */
	public String getEntryHeaderStyleName()
	{
		return "activityStreamTopEntryHeader";
	}
	
	
	/**
	 * Return the name of the style used with the div that holds the entry.
	 */
	public String getMainPanelStyleName()
	{
		return "activityStreamTopEntryMainPanel";
	}
	
	
	/**
	 * 
	 */
	public String getTitlePanelStyleName()
	{
		return "activityStreamTopEntryTitlePanel";
	}

	
	/**
	 * 
	 */
	public String getTitleStyleName()
	{
		return "activityStreamTopEntryTitle";
	}
	
	
	/**
	 * Take the user to the parent binder.
	 */
	private void gotoParentBinder()
	{
		OnSelectBinderInfo binderInfo;
		
		if ( m_parentBinderId != null && m_parentBinderPermalink != null )
		{
			m_actionHandler.handleAction( TeamingAction.EXIT_ACTIVITY_STREAM_MODE, null );
			
			binderInfo = new OnSelectBinderInfo( m_parentBinderId, m_parentBinderPermalink, false, Instigator.OTHER );
			m_actionHandler.handleAction( TeamingAction.SELECTION_CHANGED, binderInfo );
		}
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
			HttpRequestInfo ri;
			AsyncCallback<String> callback;
			
			callback = new AsyncCallback<String>()
			{
				/**
				 * 
				 */
				public void onFailure(Throwable t)
				{
					GwtClientHelper.handleGwtRPCFailure(
						GwtTeaming.getMessages().rpcFailure_GetBinderPermalink(),
						m_parentBinderId );
				}
				
				/**
				 * 
				 */
				public void onSuccess( String binderPermalink )
				{
					// Take the user to the parent binder.
					m_parentBinderPermalink = binderPermalink;
					gotoParentBinder();
				}
			};
			
			// Issue an ajax request to get the permalink of the binder that is the source of the activity stream.
			ri = new HttpRequestInfo();
			GwtTeaming.getRpcService().getBinderPermalink( ri, m_parentBinderId, callback );
		}
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
	public void setData( ActivityStreamEntry entryItem )
	{
		List<ActivityStreamEntry> comments = null;
		String parentBinderName;
		int numComments;
		int i;
		
		super.setData( entryItem );
		
		numComments = entryItem.getCommentCount();
		if ( numComments > 0 )
		{
			String text;
			
			if ( numComments == 1 )
				text = GwtTeaming.getMessages().oneComment();
			else
				text = GwtTeaming.getMessages().multipleComments( numComments );
			m_numCommentsLabel.setText( text );
			m_numCommentsLabel.setVisible( true );
		}
		else
			m_numCommentsLabel.setVisible( false );
		
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
				addComment( nextComment, i );
			}
		}
	}
}
