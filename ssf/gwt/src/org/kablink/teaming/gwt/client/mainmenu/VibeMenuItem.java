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
package org.kablink.teaming.gwt.client.mainmenu;

import org.kablink.teaming.gwt.client.util.GwtClientHelper;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;


/**
 * Class that wraps the GWT MenuItem implementation for use within Vibe.
 * 
 * @author drfoster@novell.com
 */
public class VibeMenuItem extends MenuItem {
	/**
	 * Constructor method.
	 *
	 * @param text
	 * @param asHtml
	 * @param cmd
	 * @param style
	 */
	public VibeMenuItem(String text, boolean asHtml, Command cmd, String style) {
		// Initialize the superclass...
		super(text, asHtml, cmd);
		
		// ...and if we were given a style for the MenuItem...
		if (GwtClientHelper.hasString(style)) {
			// ...add it to it.
			addStyleName(style);
		}
	}
	
	/**
	 * Constructor method.
	 *
	 * @param text
	 * @param asHtml
	 * @param subMenu
	 * @param style
	 */
	public VibeMenuItem(String text, boolean asHtml, MenuBar subMenu, String style) {
		// Initialize the superclass...
		super(text, asHtml, subMenu);
		
		// ...and if we were given a style for the MenuItem...
		if (GwtClientHelper.hasString(style)) {
			// ...add it to it.
			addStyleName(style);
		}
	}
	
	/**
	 * Constructor method.
	 *
	 * @param text
	 * @param cmd
	 * @param style
	 */
	public VibeMenuItem(String text, Command cmd, String style) {
		// Always use one of the initial forms of the constructor.
		this(text, false, cmd, style);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param text
	 * @param asHtml
	 * @param cmd
	 */
	public VibeMenuItem(String text, boolean asHtml, Command cmd) {
		// Always use one of the initial forms of the constructor.
		this(text, asHtml, cmd, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param text
	 * @param cmd
	 */
	public VibeMenuItem(String text, Command cmd) {
		// Always use one of the initial forms of the constructor.
		this(text, false, cmd, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param text
	 * @param subMenu
	 */
	public VibeMenuItem(String text, MenuBar subMenu) {
		// Always use one of the initial forms of the constructor.
		this(text, false, subMenu, null);
	}
}
