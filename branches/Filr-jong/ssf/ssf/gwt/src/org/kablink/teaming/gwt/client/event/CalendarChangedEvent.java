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
package org.kablink.teaming.gwt.client.event;

import org.kablink.teaming.gwt.client.rpc.shared.CalendarDisplayDataRpcResponseData;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * The CalendarChangedEvent informs listeners that the calendar in
 * question has changed.
 * 
 * @author drfoster@novell.com
 */
public class CalendarChangedEvent extends VibeEventBase<CalendarChangedEvent.Handler> {
	public static Type<Handler> TYPE = new Type<Handler>();

	private CalendarDisplayDataRpcResponseData	m_displayData;	//
	private Long								m_folderId;		//

	/**
	 * Handler interface for this event.
	 */
	public interface Handler extends EventHandler {
		void onCalendarChanged(CalendarChangedEvent event);
	}
	
	/**
	 * Constructor method.
	 */
	public CalendarChangedEvent() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param folderId
	 */
	public CalendarChangedEvent(Long folderId, CalendarDisplayDataRpcResponseData displayData) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setFolderId(   folderId   );
		setDisplayData(displayData);
	}
	
	/**
	 * Dispatches this event when one is triggered.
	 *
	 * Implements the VibeEventBase.doDispatch() method.
	 * 
	 * @param handler
	 */
	@Override
	protected void doDispatch(Handler handler) {
		handler.onCalendarChanged(this);
	}
	
	/**
	 * Returns the GwtEvent.Type of this event.
	 * 
	 * Implements GwtEvent.getAssociatedType()
	 * 
	 * @return
	 */
	@Override
	public Type<Handler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * Returns the TeamingEvents enumeration value corresponding to
	 * this event.
	 * 
	 * Implements VibeBaseEvent.getEventEnum()
	 * 
	 * @return
	 */
	@Override
	public TeamingEvents getEventEnum() {
		return TeamingEvents.CALENDAR_CHANGED;
	}
		
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public CalendarDisplayDataRpcResponseData getDisplayData() {return m_displayData;}
	public Long                               getFolderId()    {return m_folderId;   }

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setDisplayData(CalendarDisplayDataRpcResponseData displayData) {m_displayData = displayData;}
	public void setFolderId(   Long                               folderId)    {m_folderId    = folderId;   }

	/**
	 * Registers this event on the given event bus and returns its
	 * HandlerRegistration.
	 * 
	 * @param eventBus
	 * @param handler
	 * 
	 * @return
	 */
	public static HandlerRegistration registerEvent(SimpleEventBus eventBus, Handler handler) {
		return eventBus.addHandler(TYPE, handler);
	}
}
