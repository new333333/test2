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

package org.kablink.teaming.gwt.client.util;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This class defines all the possible types of actions that a user can
 * request from the GWT main page.
 * 
 * @author jwootton
 */
public enum TeamingAction implements IsSerializable
{
	ADVANCED_SEARCH( "Runs Advanced Search in the Content Panel" ),
	EDIT_BRANDING( "Edit Branding" ),
	EDIT_PERSONAL_PREFERENCES( "Edit Personal Preferences" ),
	EDIT_SITE_BRANDING ("Edit Site Branding" ),
	GOTO_CONTENT_URL( "Changes the Current Context to a non-Permalink URL" ),
	GOTO_PERMALINK_URL( "Changes the Current Context to a Permalink URL" ),
	HELP( "Help" ),
	INVOKE_SIMPLE_PROFILE( "Invoke the Simple Profile dialog" ),
	MARK_ENTRY_READ( "Mark the entry as read" ),
	MARK_ENTRY_UNREAD( "Mark the entry as unread" ),
	MY_WORKSPACE( "My Workspace" ),
	RELOAD_LEFT_NAVIGATION( "Tells the Left Navigation Panel To Reload Itself"),
	REPLY( "Invoke the 'reply to entry' ui" ),
	SAVED_SEARCH( "Executes a Saved Search Using a String as the Name" ),
	SHARE( "Invoke the 'share this entry' ui" ),
	SHOW_ALL_ENTRIES( "Show all entries" ),
	SHOW_RESOURCE_LIBRARY( "Show the Resource Library page" ),
	SHOW_UNREAD_ENTRIES( "Show unread entries" ),
	SIMPLE_SEARCH( "Performs a Simple Search on a String" ),
	SIZE_CHANGED( "The Size of Something Changed" ),
	SUBSCRIBE( "Invoke the 'subscribe to this entry' ui" ),
	TAG( "Invoke the 'tag this entry' ui" ),
	TAG_SEARCH( "Executes a Search Using a String as a tag Name" ),
	TEAMING_FEED( "Open the Teaming Feed window" ),
	TRACK_BINDER( "Tracks the Current Binder" ),
	UNTRACK_BINDER( "Untracks the Current Binder" ),
	UNTRACK_PERSON( "Untracks the Current Person" ),
	VIEW_FOLDER_ENTRY( "Open an entry for viewing" ),
	VIEW_TEAM_MEMBERS( "View Team Membership of Current Binder" ),
	SHOW_FORUM_ENTRY( "Open an entry for viewing" ),
	TASK_MOVE_UP( "Move the Selected Task Up in the Ordering." ),
	TASK_MOVE_DOWN( "Move the Selected Task Down in the Ordering." ),
	TASK_MOVE_LEFT( "Move the Selected Task Left (i.e., Decrease its Subtask Level.)" ),
	TASK_MOVE_RIGHT( "Move the Selected Task Right (i.e., Increase its Subtask Level.)" ),
	TASK_DELETE( "Delete the Selected Tasks." ),
	TASK_SET_PERCENT_DONE( "Sets a Task's Percentage Done." ),
	TASK_SET_PRIORITY( "Sets a Task's Priority." ),
	TASK_SET_STATUS( "Sets a Task's Status." ),
	TASK_VIEW( "Sets a Task's View." ),
	TASK_PURGE( "Delete and Immediately Purge the Selected Tasks." ),
	TASK_NEW_TASK( "Creates a New Task Relative to an Existing One." ),
	TASK_QUICK_FILTER( "Creates or Clears a Quick Filter on the Task Folder." ),

	// This is used as a default case to store a TeamingAction when
	// there isn't a real value to store.
	UNDEFINED( "Undefined Action - Should Never Be Triggered" );

	private final String m_unlocalizedDesc;
	
	/**
	 */
	private TeamingAction( String unlocalizedDesc )
	{
		m_unlocalizedDesc = unlocalizedDesc;
	}// end TeamingAction()
	
	
	/**
	 */
	public String getUnlocalizedDesc()
	{
		return m_unlocalizedDesc;
	}// end getUnlocalizedDesc()
}// end TeamingAction
