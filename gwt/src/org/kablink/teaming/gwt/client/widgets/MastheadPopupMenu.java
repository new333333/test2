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

package org.kablink.teaming.gwt.client.widgets;


import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.GwtTeamingMessages;
import org.kablink.teaming.gwt.client.event.ViewResourceLibraryEvent;
import org.kablink.teaming.gwt.client.event.ViewTeamingFeedEvent;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;

import com.google.gwt.user.client.ui.Image;

/**
 * This popup menu is used to display the actions in the masthead, such as help, personal prefs, resource library.
 * @author jwootton
 *
 */
public class MastheadPopupMenu extends PopupMenu
{
	VibeMenuItem m_adminMenuItem;
	
	/**
	 * 
	 */
	public MastheadPopupMenu( String mastheadBinderId, boolean isUserLoggedIn, boolean autoHide, boolean modal )
	{
		super( autoHide, modal, false );
		
		// Add all the possible menu items.
		{
			GwtTeamingMessages messages;
			Image img;
			
			messages = GwtTeaming.getMessages();
			
			if ( isUserLoggedIn )
			{
				boolean addSeparator;
				
				addSeparator = false;
				
				// Create the "Open News Feed" menu item.
				if ( GwtTeaming.m_requestInfo.isLicenseFilr() == false )
				{
					img = new Image( GwtTeaming.getImageBundle().newsFeedMenuImg() );
					addMenuItem( new ViewTeamingFeedEvent(), img, messages.newsFeedMenuItem() );
					
					addSeparator = true;
				}
				
				// Add a separator
				if ( addSeparator )
					addSeparator();
			}
			
			// Create the "Vibe Resource Library" menu item.
			if ( GwtTeaming.m_requestInfo.isLicenseFilr() == false )
			{
				img = new Image( GwtTeaming.getImageBundle().resourceLibMenuImg() );
				addMenuItem( new ViewResourceLibraryEvent(), img, messages.resourceLibMenuItem() );
			}
		}
	}
}
