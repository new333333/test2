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

package org.kablink.teaming.gwt.client.event;

import com.google.gwt.user.client.rpc.IsSerializable;


/**
 * This enumeration defines all possible types of events generated by
 * Vibe OnPrem.
 * 
 * Note:  The events defined here must also be maintained in
 *    EventHelper.registerEventHandlers().
 * 
 * @author drfoster@novell.com
 */
public enum TeamingEvents implements IsSerializable {
	ACTIVITY_STREAM,					// Changes the selected an activity stream.
	ACTIVITY_STREAM_ENTER,				// Enters activity stream mode.
	ACTIVITY_STREAM_EXIT,				// Exits  activity stream mode.
	
	ADMINISTRATION,						// Enters administration mode.
	ADMINISTRATION_EXIT,				// Exits  administration mode.
	ADMINISTRATION_UPGRADE_CHECK,		// Tell the administration control to check for upgrade tasks that need to be performed.
	
	BROWSE_HIERARCHY,					// Browse Vibe OnPrem's hierarchy (i.e., the bread crumb tree.) 
	BROWSE_HIERARCHY_EXIT,				// Exits the bread crumb browser, if open.
	
	CHANGE_CONTEXT,						// Tells the UI that a context switch is currently taking place.	(I.e., Is happening.      )
	CONTEXT_CHANGED,					// Tells the UI that a context switch has just taken places.		(I.e., Has happened.      )
	CONTEXT_CHANGING,					// Tells the UI That a context switch is about to take place.		(I.e., Is going to happen.)
	
	CONTRIBUTOR_IDS_REPLY,				// Fired with the current set of contributor IDs.
	CONTRIBUTOR_IDS_REQUEST,			// Requests that the current contributor IDs be posted via a CONTRIBUTOR_IDS_REPLY event.

	EDIT_CURRENT_BINDER_BRANDING,		// Edits the branding on the current binder.
	EDIT_LANDING_PAGE_PROPERTIES,		// Edit the landing page properties
	EDIT_PERSONAL_PREFERENCES,			// Edits the user's personal preferences.
	EDIT_SITE_BRANDING,					// Edits the site branding.

	FILES_DROPPED,						// Fired when files have been successfully dropped on the drag and drop applet.
	FULL_UI_RELOAD,						// Forces the full Vibe OnPrem UI to be reloaded.
	
	GOTO_CONTENT_URL,					// Changes the current context to a non-permalink URL.
	GOTO_MY_WORKSPACE,					// Changes the current context to the user's workspace.
	GOTO_PERMALINK_URL,					// Changes the current context to a permalink URL.
	GOTO_URL,							// Changes the content to the given url.

	INVOKE_ABOUT,						// Invokes the About dialog.
	INVOKE_CLIPBOARD,					// Invokes the Clipboard dialog.
	INVOKE_COLUMN_RESIZER,				// Invokes the column resizing dialog.
	INVOKE_CONFIGURE_COLUMNS,			// Invokes the Configure Columns dialog.
	INVOKE_CONFIGURE_FILE_SYNC_APP_DLG,	// Invokes the Configure File Sync App dialog.
	INVOKE_DROPBOX,						// Invokes the files drop box (i.e., the file drag&drop applet.)
	INVOKE_EMAIL_NOTIFICATION,			// Invokes the Email Notification dialog.
	INVOKE_HELP,						// Invokes the Vibe OnPrem online help.
	INVOKE_IMPORT_ICAL_FILE,			// Invokes the dialog to import an iCal by uploading  a file.
	INVOKE_IMPORT_ICAL_URL,				// Invokes the dialog to import an iCal by specifying a URL.
	INVOKE_REPLY,						// Invokes the 'reply to entry' UI.
	INVOKE_SEND_EMAIL_TO_TEAM,			// Invokes the Send Email To Team dialog.
	INVOKE_SHARE,						// Invokes the 'share this entry' UI.
	INVOKE_SHARE_BINDER,				// Invokes the 'share this binder' ui.
	INVOKE_SIGN_GUESTBOOK,				// Invokes the 'sign the guest book' UI.
	INVOKE_SIMPLE_PROFILE,				// Invokes the simple profile dialog.
	INVOKE_SUBSCRIBE,					// Invokes the 'subscribe to this entry' UI.
	INVOKE_TAG,							// Invokes the 'tag this entry' UI.
	
	JSP_LAYOUT_CHANGED,					// The layout of JSP content has changed.
	
	LOGIN,								// Logs into   Vibe.
	LOGOUT,								// Logs out of Vibe.
	PRE_LOGOUT,							// Notifies everybody that we're about to log out of Vibe.
	
	PREVIEW_LANDING_PAGE,				// Invoke the ui to preview the landing page.

	MARK_ENTRY_READ,					// Mark the entry as read.
	MARK_ENTRY_UNREAD,					// Mark the entry as unread.
	
	MASTHEAD_HIDE,						// Hides the masthead.
	MASTHEAD_SHOW,						// Shows the masthead.
	
	MENU_HIDE,							// Hides the main menu
	MENU_SHOW,							// Shows the main menu
	
	SEARCH_ADVANCED,					// Runs the advanced Search in the content area.
	SEARCH_FIND_RESULTS,				// Fired when the FindCtrl is returning its results.
	SEARCH_RECENT_PLACE,				// Executes a recent place search.
	SEARCH_SAVED,						// Executes a saved search using a string as the name.
	SEARCH_SIMPLE,						// Performs a simple search on a string.
	SEARCH_TAG,							// Executes a search using a string as a tag Name.
	
	SHOW_CONTENT_CONTROL,				// Shows the ContentControl.
	SHOW_DISCUSSION_FOLDER,				// Shows a discussion folder.
	SHOW_DISCUSSION_WORKSPACE,			// Shows a discussion workspace.
	SHOW_FILE_FOLDER,					// Shows a file       folder.
	SHOW_GENERIC_WORKSPACE,				// Shows a generic workspace.
	SHOW_GLOBAL_WORKSPACE,				// Shows the global workspace
	SHOW_GUESTBOOK_FOLDER,				// Shows a guest book folder.
	SHOW_HOME_WORKSPACE,				// Shows the home (top) workspace
	SHOW_LANDING_PAGE,					// Shows a landing page.
	SHOW_MICRO_BLOG_FOLDER,				// Shows a micro-blog folder.
	SHOW_MILESTONE_FOLDER,				// Shows a milestone folder.
	SHOW_MIRRORED_FILE_FOLDER,			// Shows a mirrored file folder.
	SHOW_PERSONAL_WORKSPACES,			// Shows the Personal workspaces binder.
	SHOW_PROJECT_MANAGEMENT_WORKSPACE,	// Show a project management workspace.
	SHOW_SURVEY_FOLDER,					// Shows a survey folder.
	SHOW_TASK_FOLDER,					// Shows a task folder.
	SHOW_TEAM_ROOT_WORKSPACE,			// Shows the team root workspace.
	SHOW_TEAM_WORKSPACE,				// Shows a team workspace.
	SHOW_TRASH,							// Shows a trash view.
	
	SIDEBAR_HIDE,						// Hides the left navigation panel.
	SIDEBAR_RELOAD,						// Tells the left navigation panel to reload itself.
	SIDEBAR_SHOW,						// Shows the left navigation panel.
	
	SIZE_CHANGED,						// The size of something changed.
	
	TASK_DELETE,						// Delete the Selected Tasks.
	TASK_HIERARCHY_DISABLED,			// Shows the reasons why task hierarchy manipulation is disabled.
	TASK_LIST_READY,					// The task list in the specified folder has been read and is ready to use.
	TASK_MOVE_DOWN,						// Move the Selected Task Down in the Ordering.
	TASK_MOVE_LEFT,						// Move the Selected Task Left (i.e., Decrease its Subtask Level.)
	TASK_MOVE_RIGHT,					// Move the Selected Task Right (i.e., Increase its Subtask Level.)
	TASK_MOVE_UP,						// Move the Selected Task Up in the Ordering.
	TASK_NEW_TASK,						// Creates a New Task Relative to an Existing One.
	TASK_PICK_DATE,						// Run the date picker with the given ID.
	TASK_PURGE,							// Delete and Immediately Purge the Selected Tasks.
	TASK_QUICK_FILTER,					// Creates or Clears a Quick Filter on the Task Folder.
	TASK_SET_PERCENT_DONE,				// Sets a Task's Percentage Done.
	TASK_SET_PRIORITY,					// Sets a Task's Priority.
	TASK_SET_STATUS,					// Sets a Task's Status.
	TASK_VIEW,							// Sets a Task's View.

	TRASH_PURGE_ALL,					// Purges everything in the trash.
	TRASH_PURGE_SELECTED_ENTRIES,		// Purges the selected entries in the trash.
	TRASH_RESTORE_ALL,					// Restores everything in the trash.
	TRASH_RESTORE_SELECTED_ENTRIES,		// Restores the selected entries in the trash.
	
	TREE_NODE_COLLAPSED,				// Fired when the node in a tree is collapsed.
	TREE_NODE_EXPANDED,					// Fired when the node in a tree is expanded.
	
	TRACK_CURRENT_BINDER,				// Tracks the current binder.
	UNTRACK_CURRENT_BINDER,				// Removes tracking from the current binder.
	UNTRACK_CURRENT_PERSON,				// Removes tracking from the current person.
	
	VIEW_ALL_ENTRIES,					// Shows all entries.
	VIEW_CURRENT_BINDER_TEAM_MEMBERS,	// Views the Team Membership of the current binder.
	VIEW_FOLDER_ENTRY,					// Open an entry for viewing.
	VIEW_FORUM_ENTRY,					// Opens an entry for viewing.
	VIEW_RESOURCE_LIBRARY,				// Shows the resource library page.
	VIEW_TEAMING_FEED,					// Opens the Teaming Feed window.
	VIEW_UNREAD_ENTRIES,				// Show unread entries.
	VIEW_WHATS_UNSEEN_IN_BINDER,		// Shows the items that are unseen in the current binder.
	VIEW_WHATS_NEW_IN_BINDER,			// Shows the items that are new    in the current binder.
	
	CHANGE_ENTRY_TYPE_SELECTED_ENTRIES,	// Changes the Entry Type of the Selected Entries.
	COPY_SELECTED_ENTRIES,				// Copies the Selected Entries.
	DELETE_SELECTED_ENTRIES,			// Deletes the Selected Entries.
	DISABLE_SELECTED_USERS,				// Disables the Selected Users.
	ENABLE_SELECTED_USERS,				// Enables the Selected Users.
	LOCK_SELECTED_ENTRIES,				// Locks the Selected Entries.
	MARK_READ_SELECTED_ENTRIES,			// Marks the Selected Entries as Having Been Read.
	MOVE_SELECTED_ENTRIES,				// Moves the Selected Entries.
	PURGE_SELECTED_ENTRIES,				// Deletes and Immediately Purges the Selected Entries.
	SHARE_SELECTED_ENTRIES,				// Shares the Selected Entries.
	SUBSCRIBE_SELECTED_ENTRIES,			// Subscribes the Current User to the Selected Entries.
	UNLOCK_SELECTED_ENTRIES,			// Unlocks the Selected Entries.
		
	UNDEFINED;							// Undefined event - Should never be fired !!!

	/**
	 * Converts the ordinal value of a TeamingEvents to its enumeration
	 * equivalent.
	 * 
	 * @param eventOrdinal
	 * 
	 * @return
	 */
	public static TeamingEvents getEnum(int eventOrdinal) {
		TeamingEvents event;
		try {
			event = TeamingEvents.values()[eventOrdinal];
		}
		catch (ArrayIndexOutOfBoundsException e) {
			event = TeamingEvents.UNDEFINED;
		}
		return event;
	}
}
