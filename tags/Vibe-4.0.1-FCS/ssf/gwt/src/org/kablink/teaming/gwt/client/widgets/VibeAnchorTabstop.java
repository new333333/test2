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

import org.kablink.teaming.gwt.client.util.GwtClientHelper;
import org.kablink.teaming.gwt.client.util.VibeKBHook;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Anchor;


/**
 * Wraps a GWT Anchor widget ensuring it has a tab stop set.
 * 
 * @author drfoster@novell.com
 */
public class VibeAnchorTabstop extends Anchor {
	/**
	 * Constructor method.
	 * 
	 * @param tabIndex
	 * @param kbHook
	 */
	public VibeAnchorTabstop(int tabIndex, final VibeKBHook kbHook) {
		// Initialize the superclass...
		super();
		
		// ...set the tab index...
		setTabIndex(tabIndex);
		
		// ...add a key up handler to trigger an appropriate event
		// ...when the user presses the enter key.
		final VibeAnchorTabstop thisA = this;
		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (isEnabled() && isVisible()) {
					if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
						if (null == kbHook)
						     fireClick();
						else kbHook.onEnter(thisA);
					}
				}
			}
		});

		// ...and if we were given a keyboard hook...
		if (null != kbHook) {
			// ...add a key down handler to trigger an appropriate
			// ...event when the user presses the tab key.
			addKeyDownHandler(new KeyDownHandler() {
				@Override
				public void onKeyDown(KeyDownEvent event) {
					if (isEnabled() && isVisible()) {
						if (KeyCodes.KEY_TAB == event.getNativeEvent().getKeyCode()) {
							kbHook.onTab(thisA);
						}
					}
				}
			});
		}
	}
	
	/**
	 * Constructor method.
	 */
	public VibeAnchorTabstop() {
		// Always use the initial form of the constructor.
		this(0, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param tabIndex
	 */
	public VibeAnchorTabstop(int tabIndex) {
		// Always use the initial form of the constructor.
		this(tabIndex, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param kbHook
	 */
	public VibeAnchorTabstop(VibeKBHook kbHook) {
		// Always use the initial form of the constructor.
		this(0, kbHook);
	}

	/**
	 * Fires a click event to the anchor.
	 */
	public void fireClick() {
		GwtClientHelper.simulateElementClick(getElement());
	}
}
