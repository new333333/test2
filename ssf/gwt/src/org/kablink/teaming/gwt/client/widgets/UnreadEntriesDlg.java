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

import org.kablink.teaming.gwt.client.EditCanceledHandler;
import org.kablink.teaming.gwt.client.EditSuccessfulHandler;
import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.EventHelper;
import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.event.ViewForumEntryEvent;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo.ActivityStream;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu;
import org.kablink.teaming.gwt.client.whatsnew.ActionsPopupMenu.ActionMenuItem;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlClient;
import org.kablink.teaming.gwt.client.whatsnew.ActivityStreamCtrl.ActivityStreamCtrlUsage;
import org.kablink.teaming.gwt.client.widgets.DlgBox;

import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Panel;

/**
 * ?
 *  
 * @author jwootton
 */
public class UnreadEntriesDlg extends DlgBox
	implements ViewForumEntryEvent.Handler
{
	private VibeFlowPanel m_mainPanel = null;
	
	// The following defines the TeamingEvents that are handled by
	// this class.  See EventHelper.registerEventHandlers() for how
	// this array is used.
	private TeamingEvents[] m_registeredEvents = new TeamingEvents[]
	{
		// View events.
		TeamingEvents.VIEW_FORUM_ENTRY
	};
	
	/**
	 * 
	 */
	public UnreadEntriesDlg(
		EditSuccessfulHandler editSuccessfulHandler,	// We will call this handler when the user presses the ok button
		EditCanceledHandler editCanceledHandler, 		// This gets called when the user presses the Cancel button
		boolean autoHide,
		boolean modal,
		int xPos,
		int yPos )
	{
		super( autoHide, modal, xPos, yPos, DlgButtonMode.Close );
		
		// Register the events to be handled by this class.
		EventHelper.registerEventHandlers( GwtTeaming.getEventBus(), m_registeredEvents, this );

		// Create the header, content and footer of this dialog box.
		createAllDlgContent( GwtTeaming.getMessages().unreadEntriesDlgHeader(), editSuccessfulHandler, editCanceledHandler, null ); 
	}
	

	/**
	 * Add an ActivityStreamCtrl object to this dialog.
	 */
	private void addActivityStream( final BinderInfo binderInfo )
	{
		ActionsPopupMenu actionsMenu;
		ActionMenuItem[] menuItems = {  ActionMenuItem.REPLY,
										ActionMenuItem.SHARE,
										ActionMenuItem.SUBSCRIBE,
										ActionMenuItem.TAG,
										ActionMenuItem.SEPARATOR,
										ActionMenuItem.MARK_READ,
										ActionMenuItem.MARK_UNREAD };

		
		actionsMenu = new ActionsPopupMenu( true, true, menuItems );

		// Create an ActivityStreamCtrl for the given binder and insert it into the page.
		ActivityStreamCtrl.createAsync( ActivityStreamCtrlUsage.UNREAD_ENTRIES, false, actionsMenu, new ActivityStreamCtrlClient()
		{			
			@Override
			public void onUnavailable()
			{
				// Nothing to do.  Error handled in
				// asynchronous provider.
			}
			
			@Override
			public void onSuccess( ActivityStreamCtrl asCtrl )
			{
				ActivityStreamInfo asi;

				asi = new ActivityStreamInfo();
				asi.setActivityStream( ActivityStream.SPECIFIC_BINDER );
				asi.setBinderId( binderInfo.getBinderId());
				asi.setTitle( binderInfo.getBinderTitle() );

				m_mainPanel.add( asCtrl );
				asCtrl.setCheckForChanges( false );
				asCtrl.setActivityStream( asi, ActivityStreamDataType.UNREAD );
				asCtrl.setSize( 600, 300 );
				asCtrl.show();
			}
		} );
	}
	
	/**
	 * Create all the controls that make up the dialog box.
	 */
	@Override
	public Panel createContent( Object props )
	{
		m_mainPanel = new VibeFlowPanel();
		m_mainPanel.setStyleName( "unreadEntriesDlgContent" );
		
		return m_mainPanel;
	}
	
	
	/**
	 * Nothing to return
	 */
	@Override
	public Object getDataFromDlg()
	{
		// Nothing to do.
		return new Object();
	}
	
	
	/**
	 * Return the widget that should get the focus when the dialog is shown. 
	 */
	@Override
	public FocusWidget getFocusWidget()
	{
		return null;
	}
	
	/**
	 * 
	 */
	public void init( BinderInfo binderInfo )
	{
		addActivityStream( binderInfo );
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
		// One of the entries we are displaying was clicked on.  Close this dialog.
		hide();
	}
}
