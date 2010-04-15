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
import org.kablink.teaming.gwt.client.util.BinderType;
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
 * Class used for the Action menu item popup.  
 * 
 * @author drfoster@novell.com
 */
@SuppressWarnings("unused")
public class ActionsMenuPopup extends MenuBarPopup {
	private final String IDBASE = "action_";	// Base ID for the items created in this menu.

	private BinderType m_currentBinderType;			// Type of the currently selected binder.
	private List<ToolbarItem> m_toolbarItemList;	// The context based toolbar requirements.
	private String m_currentBinderId;				// ID of the currently selected binder.
	private ToolbarItem m_sendEmailTBI;				// The send email toolbar item, if found.

	/**
	 * Class constructor.
	 * 
	 * @param actionTrigger
	 */
	public ActionsMenuPopup(ActionTrigger actionTrigger) {
		// Initialize the super class.
		super(actionTrigger, GwtTeaming.getMessages().mainMenuBarActions());
	}
	
	/**
	 * Stores the ID and type of the currently selected binder.
	 * 
	 * Implements the MenuBarPopup.setCurrentBinder() abstract method.
	 * 
	 * @param binderId
	 * @param binderType
	 */
	@Override
	public void setCurrentBinder(String binderId, BinderType binderType) {
		// Simply store the parameters.
		m_currentBinderId = binderId;
		m_currentBinderType = binderType;
	}
	
	/**
	 * Store information about the context based toolbar requirements
	 * via a List<ToolbarItem>.
	 * 
	 * Implements the MenuBarPopup.setToolbarItemList() abstract method.
	 * 
	 * @param toolbarItemList
	 */
	@Override
	public void setToolbarItemList(List<ToolbarItem> toolbarItemList) {
		// Simply store the parameter.
		m_toolbarItemList = toolbarItemList;
	}
	
	/**
	 * Called to determine if given the List<ToolbarItem>, should
	 * the menu be shown.  Returns true if it should be shown and false
	 * otherwise.
	 * 
	 * Implements the MenuBarPopup.shouldShowMenu() abstract method.
	 * 
	 * @return
	 */
	@Override
	public boolean shouldShowMenu() {
		// Scan the toolbar items...
		for (Iterator<ToolbarItem> tbiIT = m_toolbarItemList.iterator(); tbiIT.hasNext(); ) {
			// ...and keep track of the ones that appear on the actions
			// ...menu.
			ToolbarItem tbi = tbiIT.next();
			String tbName = tbi.getName();
			if (tbName.equalsIgnoreCase("ssGwtMiscToolbar")) {
				m_sendEmailTBI = tbi.getNestedToolbarItem("sendEmail");
			}
			
//!			...this needs to be implemented...
		}
		
		// Return true if we found any of the actions menu items and
		// false otherwise.
		return
			(null != m_sendEmailTBI);
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
		// Position the menu...
		setPopupPosition(left, top);
		
		// ...and if we haven't already constructed its contents...
		if (!(hasContent())) {
			// ...construct it now...
			addContextMenuItem(IDBASE, m_sendEmailTBI);
			
//!			...this needs to be implemented...
		}
					
		// ...and show it.
		show();
	}
}
