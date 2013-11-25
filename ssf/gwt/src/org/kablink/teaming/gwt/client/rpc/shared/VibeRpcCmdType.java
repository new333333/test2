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

package org.kablink.teaming.gwt.client.rpc.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This enumeration defines all possible Vibe OnPrem GWT RPC commands.
 * 
 * @author jwootton@novell.com
 */
public enum VibeRpcCmdType implements IsSerializable
{
	ADD_FAVORITE,
	CAN_MODIFY_BINDER,
	COLLAPSE_SUBTASKS,
	CREATE_GROUP,
	DELETE_GROUPS,
	DELETE_TASKS,
	EXECUTE_SEARCH,
	EXPAND_HORIZONTAL_BUCKET,
	EXPAND_SUBTASKS,
	EXPAND_VERTICAL_BUCKET,
	GET_ACTIVITY_STREAM_DATA,
	GET_ACTIVITY_STREAM_PARAMS,
	GET_ADD_MEETING_URL,
	GET_ADMIN_ACTIONS,
	GET_ALL_GROUPS,
	GET_BINDER_BRANDING,
	GET_BINDER_INFO,
	GET_BINDER_PERMALINK,
	GET_BINDER_TAGS,
	GET_DEFAULT_ACTIVITY_STREAM,
	GET_DEFAULT_FOLDER_DEFINITION_ID,
	GET_DOCUMENT_BASE_URL,
	GET_DISK_USAGE_INFO,
	GET_DYNAMIC_MEMBERSHIP_CRITERIA,
	GET_ENTRY,
	GET_ENTRY_TAGS,
	GET_EXTENSION_FILES,
	GET_EXTENSION_INFO,
	GET_FAVORITES,
	GET_FILE_ATTACHMENTS,
	GET_FILE_SYNC_APP_CONFIGURATION,
	GET_FOLDER,
	GET_GROUP_ASSIGNEE_MEMBERSHIP,
	GET_GROUP_MEMBERSHIP,
	GET_GROUP_MEMBERSHIP_TYPE,
	GET_GROUPS,
	GET_HORIZONTAL_NODE,
	GET_HORIZONTAL_TREE,
	GET_IM_URL,
	GET_IS_DYNAMIC_GROUP_MEMBERSHIP_ALLOWED,
	GET_LANDING_PAGE_DATA,
	GET_LOGGED_IN_USER_PERMALINK,
	GET_LOGIN_INFO,
	GET_MICRO_BLOG_URL,
	GET_MODIFY_BINDER_URL,
	GET_MY_TEAMS,
	GET_NUMBER_OF_MEMBERS,
	GET_PERSONAL_PREFERENCES,
	GET_PRESENCE_INFO,
	GET_PROFILE_AVATARS,
	GET_PROFILE_INFO,
	GET_PROFILE_STATS,
	GET_QUICK_VIEW_INFO,
	GET_RECENT_PLACES,
	GET_ROOT_WORKSPACE_ID,
	GET_SAVED_SEARCHES,
	GET_SITE_ADMIN_URL,
	GET_SITE_BRANDING,
	GET_SUBSCRIPTION_DATA,
	GET_TAG_RIGHTS_FOR_BINDER,
	GET_TAG_RIGHTS_FOR_ENTRY,
	GET_TAG_SORT_ORDER,
	GET_TEAM_ASSIGNEE_MEMBERSHIP,
	GET_TEAM_MANAGEMENT_INFO,
	GET_TEAMS,
	GET_TASK_BUNDLE,
	GET_TASK_LINKAGE,
	GET_TASK_LIST,
	GET_TOOLBAR_ITEMS,
	GET_TOP_RANKED,
	GET_UPGRADE_INFO,
	GET_USER_PERMALINK,
	GET_USER_STATUS,
	GET_VERTICAL_ACTIVITY_STREAMS_TREE,
	GET_VERTICAL_NODE,
	GET_VERTICAL_TREE,
	GET_VIEW_FOLDER_ENTRY_URL,
	HAS_ACTIVITY_STREAM_CHANGED,
	IS_ALL_USERS_GROUP,
	IS_PERSON_TRACKED,
	IS_SEEN,
	MARKUP_STRING_REPLACEMENT,
	MODIFY_GROUP,
	PERSIST_ACTIVITY_STREAM_SELECTION,
	PERSIST_NODE_COLLAPSE,
	PERSIST_NODE_EXPAND,
	PURGE_TASKS,
	REMOVE_EXTENSION,
	REMOVE_FAVORITE,
	REMOVE_TASK_LINKAGE,
	REMOVE_SAVED_SEARCH,
	REPLY_TO_ENTRY,
	SAVE_BRANDING,
	SAVE_FILE_SYNC_APP_CONFIGURATION,
	SAVE_PERSONAL_PREFERENCES,
	SAVE_SUBSCRIPTION_DATA,
	SAVE_TASK_COMPLETED,
	SAVE_TASK_DUE_DATE,
	SAVE_TASK_LINKAGE,
	SAVE_TASK_PRIORITY,
	SAVE_TASK_SORT,
	SAVE_TASK_STATUS,
	SAVE_SEARCH,
	SAVE_TAG_SORT_ORDER,
	SAVE_USER_STATUS,
	SAVE_WHATS_NEW_SETTINGS,
	SET_SEEN,
	SET_UNSEEN,
	SHARE_ENTRY,
	TEST_GROUP_MEMBERSHIP_LDAP_QUERY,
	TRACK_BINDER,
	UPDATE_BINDER_TAGS,
	UPDATE_CALCULATED_DATES,
	UPDATE_ENTRY_TAGS,
	UPDATE_FAVORITES,
	UNTRACK_BINDER,
	UNTRACK_PERSON,
	VALIDATE_ENTRY_EVENTS,
	
	UNDEFINED;

	/**
	 * Converts the ordinal value of a VibeRpcCmdType to its enumeration
	 * equivalent.
	 * 
	 * @param cmdOrdinal
	 * 
	 * @return
	 */
	public static VibeRpcCmdType getEnum( int cmdOrdinal )
	{
		VibeRpcCmdType cmd;
		try
		{
			cmd = VibeRpcCmdType.values()[cmdOrdinal];
		}
		catch ( ArrayIndexOutOfBoundsException e )
		{
			cmd = VibeRpcCmdType.UNDEFINED;
		}
		return cmd;
	}// end getEnum()
}// end VibeRpcCmdType()
