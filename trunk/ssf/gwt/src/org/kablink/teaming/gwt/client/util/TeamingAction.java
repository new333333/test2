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
	ADMINISTRATION( "Invoke Administration Page" ),
	EDIT_BRANDING( "Edit Branding" ),
	BROWSE_HIERARCHY( "Browse Teaming's Hierarchy" ),
	HELP( "Help" ),
	HIDE_LEFT_NAVIGATION( "Hide the Left Navigation Panel" ),
	HIDE_MASTHEAD( "Hide the Header" ),
	HIERARCHY_BROWSER_CLOSED( "Teaming's Hierarchy Browser Has Been Closed" ),
	LOGOUT( "Logout" ),
	MY_WORKSPACE( "My Workspace" ),
	SELECTION_CHANGED( "The User Changed a Selection Somewhere" ),
	SHOW_LEFT_NAVIGATION( "Show the Left Navigation Panel" ),
	SHOW_MASTHEAD( "Show the Header" ),
	SIZE_CHANGED( "The Size of Something Changed" ),
	TOGGLE_GWT_UI( "Toggles the State of the GWT UI" ),
	TRACK_BINDER( "Tracks the Current Binder" ),
	UNTRACK_BINDER( "Untracks the Current Binder" ),
	UNTRACK_PERSON( "Untracks the Current Person" ),
	VIEW_TEAM_MEMBERS( "View Team Membership of Current Binder" ),
	GOTO_CONTENT_URL( "Changes the Current Context to a non-Permalink URL" ),
	GOTO_PERMALINK_URL( "Changes the Current Context to a Permalink URL" ),

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
