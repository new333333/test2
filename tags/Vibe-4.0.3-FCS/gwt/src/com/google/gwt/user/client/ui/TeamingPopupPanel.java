/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * Implements an extension of GWT's PopupPanel so that Vibe can
 * access features in PopupPanel that aren't otherwise exposed.
 *  
 * @author drfoster@novell.com
 * @author lokesh.reddy@microfocus.com
 */
public class TeamingPopupPanel extends PopupPanel {
	/**
	 * Creates an empty popup panel, specifying its auto-hide and modal
	 * properties.
	 * 
	 * @param autoHide
	 * @param modal
	 */
	public TeamingPopupPanel(boolean autoHide, boolean modal) {
		super(autoHide, modal);

		setStyleName( "teamingPopupPanel_NoClip", true );
	}

	public TeamingPopupPanel(boolean autoHide) {
		// Always use the initial form of the constructor.
		this(autoHide, false);
	}
	
	/*
	 * Overrides PopupPanel.onPreviewNativeEvent() to address an issue
	 * with PopupPanel's in FF closing with auto hide if the user
	 * scrolls them by clicking the browser's scroll bar.
	 * 
	 * Note:  This was implemented as a fix for Bugzilla bug#628120.
	 */
	@Override
	protected void onPreviewNativeEvent(NativePreviewEvent nativeEvent) {
		// Is this an auto hide PopupPanel?
		if (isAutoHideEnabled()) {
			// Yes!  Is this mouse event?
		    Event event = Event.as(nativeEvent.getNativeEvent());
		    int type = event.getTypeInt();
		    if (0 != (type & Event.MOUSEEVENTS)) {
				// Yes!  Is it targeted to the root <HTML>?
			    EventTarget target = event.getEventTarget();
			    if ( target != null )
			    {
				    Element element = Element.as(target);
					String tagName = element.getTagName();
					if (tagName.equalsIgnoreCase("html")) {
						// Yes!  Then we don't want the PopupPanel to have it.
						// On FF, if it gets it, it won't let the user scroll
						// by clicking on the scroll bar.
						nativeEvent.consume();
						nativeEvent.cancel();
						
						return;
					}
			    }
		    }
		}
		
		// If we get here, we need to pass the event on through the
		// PopupPanel's handler.
		super.onPreviewNativeEvent(nativeEvent);
	}

	/**
	 * Provides access to the PopupPanel's glass Element.
	 * 
	 * Required since PopupPanel.getClassElement() is protected and
	 * can't be called directly otherwise.
	 * 
	 * @return
	 */
	public Element getPopupGlassElement() {
		return super.getGlassElement();
	}
}
