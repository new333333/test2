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

import java.math.BigDecimal;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtMainPage;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.util.CommentAddedCallback;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * ?
 * 
 * @author jwootton@novell.com
 */
public class ActivityStreamComment extends ActivityStreamUIEntry
	implements ActivityStreamCommentsContainer
{
	private CommentAddedCallback m_commentAddedCallback;

	private static final BigDecimal DELTA = BigDecimal.valueOf( 1, 1 );

	/**
	 * 
	 */
	public ActivityStreamComment(
				ActivityStreamCtrl activityStreamCtrl,
				CommentAddedCallback commentAddedCallback,
				DescViewFormat descViewFormat,
				boolean showTitle )
	{
		super( activityStreamCtrl, descViewFormat, showTitle );
		
		m_commentAddedCallback = commentAddedCallback;
	}
	
	/**
	 * 
	 */
	public ActivityStreamComment(
			ActivityStreamCtrl activityStreamCtrl,
			ActivityStreamTopEntry topEntry,
			DescViewFormat descViewFormat )
	{
		this( activityStreamCtrl, null, descViewFormat, !GwtTeaming.m_requestInfo.isLicenseFilr() );
	}

	/**
	 * Nothing to do.
	 */
	@Override
	public void addAdditionalHeaderUI( FlowPanel headerPanel )
	{
		// Nothing to do.
	}

	
	/**
	 * 
	 */
	private void addChildComment( ActivityStreamEntry activityStreamEntry, boolean scrollIntoView )
	{
		FlowPanel commentsPanel;
		ActivityStreamComment commentUI;

		// Get an ActivityStreamComment object.
		commentUI = new ActivityStreamComment(
											getActivityStreamCtrl(),
											m_commentAddedCallback,
											DescViewFormat.FULL,
											getShowTitle() );
		commentUI.setData( activityStreamEntry );
		
		// Add this ui widget to panel that holds all comments
		commentsPanel = getCommentsPanel();
		if ( commentsPanel != null )
			commentsPanel.add( commentUI );
		
		if ( scrollIntoView )
			showNewComment( commentUI );
	}
	
	
	/**
	 * Create the panel that all comments will live in.
	 */
	@Override
	public FlowPanel createCommentsPanel()
	{
		FlowPanel commentsPanel;
		
		commentsPanel = new FlowPanel();
		commentsPanel.addStyleName( "activityStreamComment_CommentsPanel" );
		
		return commentsPanel;
	}
	
	/**
	 * 
	 */
	@Override
	public String getAvatarImageStyleName( ActivityStreamEntry asEntry )
	{
		return "activityStreamCommentAvatarImg";
	}

	
	/**
	 * Return the name of the style used with the content panel.
	 */
	@Override
	public String getContentPanelStyleName()
	{
		return "activityStreamCommentContentPanel";
	}
	
	/**
	 * 
	 */
	@Override
	public String getEntryHeaderStyleName()
	{
		return "activityStreamCommentHeader";
	}
	
	/**
	 * Return the url to the author's avatar image
	 */
	@Override
	public String getEntryImgUrl( ActivityStreamEntry asEntry )
	{
		String url;
		
		// Get the url to the author's avatar
		url = asEntry.getAuthorAvatarUrl();
		
		// Does the author have an avatar?
		if ( url == null || url.length() == 0 )
		{
			// Default to the "no avatar" image.
			url = GwtMainPage.m_requestInfo.getImagesPath() + "pics/UserPhoto.png";
		}
		
		return url;
	}


	/**
	 * Return the name of the style used with a comment's full description
	 */
	@Override
	public String getFullDescStyleName()
	{
		return "activityStreamCommentFullDesc";
	}

	
	/**
	 * Return the name of the style used with the div that holds the entry.
	 */
	@Override
	public String getMainPanelStyleName()
	{
		return "activityStreamCommentMainPanel";
	}
	
	
	/**
	 * We don't display the number of comments so return null.
	 */
	@Override
	public FlowPanel getNumCommentsPanel()
	{
		return null;
	}
	
	
	/**
	 * Return the name of the style used with a comment's partial description
	 */
	@Override
	public String getPartialDescStyleName()
	{
		return "activityStreamCommentPartialDesc";
	}

	
	/**
	 * 
	 */
	@Override
	public String getTitlePanelStyleName()
	{
		return "activityStreamCommentTitlePanel";
	}

	
	/**
	 * 
	 */
	@Override
	public String getTitleStyleName()
	{
		return "activityStreamCommentTitle";
	}

	/**
	 * This method gets invoked when the user clicks on the avatar/file image.
	 */
	@Override
	public void handleClickOnAvatar( Element element )
	{
		// For a comment we treat clicking on the avatar the same as clicking on the author's name.
		handleClickOnAuthor( element );
	}
	

	/**
	 * Insert the given reply into the top entry's list of replies
	 */
	@Override
	public void insertReply( ActivityStreamEntry reply )
	{
		// Add this reply to the container that holds the comments.
		addChildComment( reply, true );
		
		if ( m_commentAddedCallback != null )
			m_commentAddedCallback.commentAdded( reply );
	}
	
	/**
	 * Set the data this we should display from the given ActivityStreamEntry
	 */
	@Override
	public void setData( ActivityStreamEntry entryItem )
	{
		List<ActivityStreamEntry> listOfChildComments;
		
		super.setData( entryItem );
		
		// Does this comment have any sub comments?
		listOfChildComments = entryItem.getComments();
		if ( listOfChildComments != null && listOfChildComments.size() > 0 )
		{
			// Yes
			for ( ActivityStreamEntry nextComment: listOfChildComments )
			{
				// Add this comment to the panel that holds all comments.
				addChildComment( nextComment, false );
			}
		}
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
}
