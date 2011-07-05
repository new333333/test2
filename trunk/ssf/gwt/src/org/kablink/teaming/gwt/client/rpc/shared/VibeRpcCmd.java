/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
 * This class represents a command sent to the server via GWT's rpc mechanism.
 * 
 * @author jwootton
 *
 */
public abstract class VibeRpcCmd implements IsSerializable
{
	/**
	 * This class defines all the possible commands
	 */
	public enum VibeRpcCmdType implements IsSerializable
	{
		EXECUTE_SEARCH,
		GET_ACTIVITY_STREAM_PARAMS,
		GET_ADMIN_ACTIONS,
		GET_BINDER_BRANDING,
		GET_BINDER_INFO,
		GET_BINDER_PERMALINK,
		GET_DEFAULT_ACTIVITY_STREAM,
		GET_DOCUMENT_BASE_URL,
		GET_ENTRY,
		GET_EXTENSION_FILES,
		GET_EXTENSION_INFO,
		GET_FILE_ATTACHMENTS,
		GET_FOLDER,
		GET_LOGGED_IN_USER_PERMALINK,
		GET_LOGIN_INFO,
		GET_MODIFY_BINDER_URL,
		GET_PERSONAL_PREFERENCES,
		GET_SITE_ADMIN_URL,
		GET_SITE_BRANDING,
		GET_UPGRADE_INFO,
		GET_USER_PERMALINK,
		GET_VIEW_FOLDER_ENTRY_URL,
		HAS_ACTIVITY_STREAM_CHANGED,
		PERSIST_ACTIVITY_STREAM_SELECTION,
		REMOVE_EXTENSION,
		SAVE_BRANDING,
		SAVE_PERSONAL_PREFERENCES,
		SAVE_WHATS_NEW_SETTINGS,
		VALIDATE_ENTRY_ACTIONS;
	}
	
	protected VibeRpcCmdType m_cmdType;
	
	/**
	 * 
	 */
	public VibeRpcCmd()
	{
	}
	
	/**
	 * 
	 */
	public VibeRpcCmdType getCmdType()
	{
		return m_cmdType;
	}
}
