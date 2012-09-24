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

package org.kablink.teaming.gwt.client.whatsnew;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActivityStreamEntry;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.DescViewFormat;

import com.google.gwt.user.client.ui.FlowPanel;

/**
 * 
 */
public class ActivityStreamComment extends ActivityStreamUIEntry
{
	private ActivityStreamCommentsContainer m_commentsContainer;
	
	/**
	 * 
	 */
	public ActivityStreamComment(
				ActivityStreamCtrl activityStreamCtrl,
				ActivityStreamCommentsContainer commentsContainer,
				DescViewFormat descViewFormat,
				boolean showTitle )
	{
		super( activityStreamCtrl, descViewFormat, showTitle );
		
		m_commentsContainer = commentsContainer;
	}
	
	/**
	 * 
	 */
	public ActivityStreamComment(
			ActivityStreamCtrl activityStreamCtrl,
			ActivityStreamTopEntry topEntry,
			DescViewFormat descViewFormat )
	{
		this( activityStreamCtrl, topEntry, descViewFormat, !GwtTeaming.m_requestInfo.isLicenseFilr() );
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
	@Override
	public String getAvatarImageStyleName()
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
	 * Insert the given reply into the top entry's list of replies
	 */
	@Override
	public void insertReply( ActivityStreamEntry reply )
	{
		// Add this reply to the container that holds the comments.
		if ( m_commentsContainer != null )
			m_commentsContainer.insertReply( reply );
	}
}
