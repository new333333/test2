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

package org.kablink.teaming.gwt.client.whatsnew;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.ViewAllEntriesEvent;
import org.kablink.teaming.gwt.client.event.ViewUnreadEntriesEvent;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;


/**
 * This popup menu is used to display the "show all" and "show unread" actions.
 * @author jwootton
 *
 */
public class ShowSettingPopupMenu extends PopupMenu
{
	private VibeMenuItem m_showAllMenuItem;
	private VibeMenuItem m_showUnreadMenuItem;
	
	/**
	 * 
	 */
	public ShowSettingPopupMenu( boolean autoHide, boolean modal )
	{
		super( autoHide, modal, true );

		// Add the "show all" menu item.
		m_showAllMenuItem = addMenuItem( new ViewAllEntriesEvent(), null, GwtTeaming.getMessages().showAll() );
		
		// Add the "show unread" menu item.
		m_showUnreadMenuItem = addMenuItem( new ViewUnreadEntriesEvent(), null, GwtTeaming.getMessages().showUnread() );
	}
	
	
	/**
	 * Check the appropriate menu item based on the given show setting.
	 */
	public void updateMenu( ActivityStreamDataType showSetting )
	{
		if ( showSetting == ActivityStreamDataType.ALL )
		{
			setMenuItemCheckedState( m_showAllMenuItem, true );
			setMenuItemCheckedState( m_showUnreadMenuItem, false );
		}
		else if ( showSetting == ActivityStreamDataType.UNREAD )
		{
			setMenuItemCheckedState( m_showAllMenuItem, false );
			setMenuItemCheckedState( m_showUnreadMenuItem, true );
		}
	}
}
