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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Provides a panel than can be instantiated by code in the outer frame
 * (i.e., GwtMainPage), placed in an inner frame (i.e., the content frame)
 * and have the defined handlers work.
 * 
 * @author drfoster@novell.com
 */
public class PassThroughEventsPanel extends AbsolutePanel implements HasClickHandlers, HasMouseOutHandlers, HasMouseOverHandlers {
	public PassThroughEventsPanel(Element elem) {
		super(elem.<com.google.gwt.user.client.Element> cast());
		onAttach();
	}
	
	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addDomHandler(handler, MouseOverEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addDomHandler(handler, MouseOutEvent.getType());
	}

	/**
	 * Adds the EventHandler's from a List<EventHandler> to a Widget
	 * via a PassThroughEventsPanel.
	 * 
	 * @param w
	 * @param handlers
	 */
	public static void addHandler(Widget w, List<EventHandler> handlers) {
		PassThroughEventsPanel p = new PassThroughEventsPanel(w.getElement());
		for (EventHandler eh:  handlers) {
			if (eh instanceof ClickHandler)     p.addClickHandler(    (ClickHandler)     eh); 
			if (eh instanceof MouseOverHandler) p.addMouseOverHandler((MouseOverHandler) eh); 
			if (eh instanceof MouseOutHandler)  p.addMouseOutHandler( (MouseOutHandler)  eh); 
		}
	}

	/**
	 * Adds a ClickHandler to a Widget via a PassThroughEventsPanel.
	 * 
	 * @param w
	 * @param ch
	 */
	public static void addHandler(Widget w, ClickHandler ch) {
		List<EventHandler> ehList = new ArrayList<EventHandler>();
		ehList.add(ch);
		addHandler(w, ehList);
	}
}
