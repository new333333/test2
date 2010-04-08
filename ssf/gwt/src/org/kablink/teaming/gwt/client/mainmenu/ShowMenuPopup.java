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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.Iterator;
import java.util.List;

import org.kablink.teaming.gwt.client.GwtTeaming;
import org.kablink.teaming.gwt.client.util.ActionTrigger;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo;
import org.kablink.teaming.gwt.client.util.TeamingAction;
import org.kablink.teaming.gwt.client.util.OnSelectBinderInfo.Instigator;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Class used for the Show menu item popup.  
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ShowMenuPopup extends MenuBarPopup {
	private final String IDBASE = "show_";
	
	private List<TeamingMenuItem> m_menuItemList;	// The context based menu requirements.
	private String m_currentBinderId;				// ID of the currently selected binder.

	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 */
	public ShowMenuPopup(ActionTrigger actionTrigger) {
		// Initialize the super class.
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarShow());
	}
	
	/**
	 * Stores the ID of the currently selected binder.
	 * 
	 * Implements the MenuBarPopup.setCurrentBinder() abstract method.
	 * 
	 * @param binderId
	 */
	@Override
	public void setCurrentBinder(String binderId) {
		// Simply store the parameter.
		m_currentBinderId = binderId;
	}
	
	/**
	 * Store information about the context based menu requirements via
	 * a List<TeamingMenuItem>.
	 * 
	 * Implements the MenuBarPopup.setMenuItemList() abstract method.
	 * 
	 * @param menuItemList
	 */
	@Override
	public void setMenuItemList(List<TeamingMenuItem> menuItemList) {
		// Simply store the parameter.
		m_menuItemList = menuItemList;
	}
	
	/**
	 * Called to determine if given the List<TeamingMenuItem>, should
	 * the menu be shown.  Returns true if it should be shown and false
	 * otherwise.
	 * 
	 * Implements the MenuBarPopup.shouldShowMenu() abstract method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
//!		...this needs to be implemented...
		return true;
	}
	
	/**
	 * Completes construction of the menu and shows it.
	 * 
	 * Implements the MenuBarPopup.showPopup() abstract method.
	 * 
	 * @param left
	 * @param top
	 */
	@Override
	public void showPopup(int left, int top) {
		setPopupPosition(left, top);
		
		// Have we already constructed the menu's content?
		if (!(hasContent())) {
			// No!  Construct it now.
//!			...this needs to be implemented...
			addContentWidget(new Label("...this needs to be implemented..."));
		}
		
		show();
	}
}
