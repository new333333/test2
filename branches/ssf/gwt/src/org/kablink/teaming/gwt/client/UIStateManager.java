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

package org.kablink.teaming.gwt.client;

import java.util.Stack;

import org.kablink.teaming.gwt.client.event.MastheadHideEvent;
import org.kablink.teaming.gwt.client.event.MastheadShowEvent;
import org.kablink.teaming.gwt.client.event.MenuHideEvent;
import org.kablink.teaming.gwt.client.event.MenuShowEvent;
import org.kablink.teaming.gwt.client.event.SidebarHideEvent;
import org.kablink.teaming.gwt.client.event.SidebarShowEvent;

/**
 * This class is used to manage the state of the ui elements, such as the masthead and the sidebar.
 * @author jwootton
 *
 */
public class UIStateManager
{
	private Stack<UIState> m_uiStateStack = null;

	/**
	 * 
	 */
	public UIStateManager()
	{
		m_uiStateStack = new Stack<UIState>();
	}

	/**
	 * Restore the UI state to whatever is on the stack.
	 */
	public void restoreUIState()
	{
		// Do we have a saved ui state on the stack?
		if ( m_uiStateStack.empty() == false )
		{
			UIState uiState;
			
			// Yes, get the saved ui state.
			uiState = m_uiStateStack.pop();
			
			// Set the visibility of the masthead.
			if ( uiState.getMastheadVisibility() == true )
			     MastheadShowEvent.fireOne();
			else MastheadHideEvent.fireOne();
			
			// Set the visibility of the sidebar.
			if ( uiState.getSidebarVisibility() == true )
			     SidebarShowEvent.fireOne();
			else SidebarHideEvent.fireOne();
			
			// Set the visibility of the main menu
			if ( uiState.getMenuVisibility() == true )
				MenuShowEvent.fireOne();
			else
				MenuHideEvent.fireOne();
		}
	}
	
	/**
	 * Save the current UI state.
	 */
	public void saveUIState( UIState uiState )
	{
		if ( uiState != null )
		{
			m_uiStateStack.push( uiState );
		}
	}
	
	
	/**
	 * This class represents the state of the different ui elements such as the masthead and the sidebar.
	 */
	public class UIState
	{
		private boolean m_mastheadVisible;
		private boolean m_sidebarVisible;
		private boolean m_menuVisible;
		
		/**
		 * 
		 */
		public UIState()
		{
		}
		
		/**
		 * 
		 */
		public boolean getMastheadVisibility()
		{
			return m_mastheadVisible;
		}
		
		/**
		 * 
		 */
		public boolean getMenuVisibility()
		{
			return m_menuVisible;
		}

		/**
		 * 
		 */
		public boolean getSidebarVisibility()
		{
			return m_sidebarVisible;
		}
		/**
		 * 
		 */
		public void setMastheadVisibility( boolean visible )
		{
			m_mastheadVisible = visible;
		}
		
		/**
		 * 
		 */
		public void setMenuVisibility( boolean visible )
		{
			m_menuVisible = visible;
		}
		
		/**
		 * 
		 */
		public void setSidebarVisibility( boolean visible )
		{
			m_sidebarVisible = visible;
		}
	}
}
