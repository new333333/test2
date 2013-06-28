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

package org.kablink.teaming.gwt.client.widgets;


import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.event.SetShareRightsEvent;
import org.kablink.teaming.gwt.client.mainmenu.VibeMenuItem;
import org.kablink.teaming.gwt.client.menu.PopupMenu;
import org.kablink.teaming.gwt.client.util.GwtShareItem;
import org.kablink.teaming.gwt.client.util.ShareRights;


/**
 * This popup menu is used to display a pop-up menu that lists all of the rights the
 * user can grant.
 */
public class ShareRightsPopupMenu extends PopupMenu
{
	private VibeMenuItem m_viewMenuItem;
	private VibeMenuItem m_editorMenuItem;
	private VibeMenuItem m_contributorMenuItem;
	private SetShareRightsEvent m_setViewRightsEvent;
	private SetShareRightsEvent m_setEditorRightsEvent;
	private SetShareRightsEvent m_setContributorRightsEvent;

	/**
	 * 
	 */
	public ShareRightsPopupMenu( boolean autoHide, boolean modal )
	{
		super( autoHide, modal, true );

		ShareRights shareRights;
		
		shareRights = new ShareRights();

		// Add the "Viewer" menu item.
		shareRights.setAccessRights( ShareRights.AccessRights.VIEWER );
		m_setViewRightsEvent = new SetShareRightsEvent( shareRights );
		m_viewMenuItem = addMenuItem( m_setViewRightsEvent, null, GwtTeaming.getMessages().shareDlg_viewer() );
		
		// Add the "Editor" menu item.
		shareRights.setAccessRights( ShareRights.AccessRights.EDITOR );
		m_setEditorRightsEvent = new SetShareRightsEvent( shareRights );
		m_editorMenuItem = addMenuItem( m_setEditorRightsEvent, null, GwtTeaming.getMessages().shareDlg_editor() );

		// Add the "Contributor" menu item.
		shareRights.setAccessRights( ShareRights.AccessRights.CONTRIBUTOR );
		m_setContributorRightsEvent = new SetShareRightsEvent( shareRights );
		m_contributorMenuItem = addMenuItem( m_setContributorRightsEvent, null, GwtTeaming.getMessages().shareDlg_contributor() );
	}
	
	/**
	 * 
	 */
	public void hideContributorMenuItem()
	{
		m_contributorMenuItem.setVisible( false );
	}
	
	/**
	 * Set the ShareInfo this pop-up menu is dealing with.
	 */
	public void setShareInfo( GwtShareItem shareInfo )
	{
		if ( shareInfo != null )
		{
			updateMenu( shareInfo.getShareRights() );
			
			// Update the events that each menu item uses with the given ShareInfo
			m_setViewRightsEvent.setShareInfo( shareInfo );
			m_setEditorRightsEvent.setShareInfo( shareInfo );
			m_setContributorRightsEvent.setShareInfo( shareInfo );
		}
	}
	
	/**
	 * 
	 */
	public void showContributorMenuItem()
	{
		m_contributorMenuItem.setVisible( true );
	}
	
	/**
	 * Check the appropriate menu item based on the given show setting.
	 */
	public void updateMenu( ShareRights rights )
	{
		// Uncheck all of the menu items.
		setMenuItemCheckedState( m_viewMenuItem, false );
		setMenuItemCheckedState( m_editorMenuItem, false );
		setMenuItemCheckedState( m_contributorMenuItem, false );
		
		// Check the appropriate menu item.
		switch ( rights.getAccessRights() )
		{
		case CONTRIBUTOR:
			setMenuItemCheckedState( m_contributorMenuItem, true );
			break;
			
		case EDITOR:
			setMenuItemCheckedState( m_editorMenuItem, true );
			break;
			
		case VIEWER:
			setMenuItemCheckedState(m_viewMenuItem, true );
			break;
			
		case UNKNOWN:
		default:
			break;
		}
	}
}

