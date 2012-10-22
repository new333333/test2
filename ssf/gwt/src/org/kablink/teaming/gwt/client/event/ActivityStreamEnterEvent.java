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

import org.kablink.teaming.gwt.client.util.ActivityStreamDataType;
import org.kablink.teaming.gwt.client.util.ActivityStreamInfo;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * The ActivityStreamEnterEvent is used to enter activity stream mode.
 * 
 * @author drfoster@novell.com
 */
public class ActivityStreamEnterEvent extends VibeEventBase<ActivityStreamEnterEvent.Handler> {
	public static Type<Handler> TYPE = new Type<Handler>();

	private ActivityStreamInfo m_asi;
	private ActivityStreamDataType m_showSetting = ActivityStreamDataType.OTHER; 

	/**
	 * Handler interface for this event.
	 */
	public interface Handler extends EventHandler {
		void onActivityStreamEnter(ActivityStreamEnterEvent event);
	}
	
	/**
	 * Constructor methods.
	 */
	public ActivityStreamEnterEvent() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor methods.
	 * 
	 * @param asi	May be null.
	 * @param ss
	 */
	public ActivityStreamEnterEvent(ActivityStreamInfo asi, ActivityStreamDataType ss) {
		// Initialize the object...
		this();

		// ...and store the parameters.
		setActivityStreamInfo(asi);
		setShowSetting(       ss );
	}
	
	/**
	 * Constructor methods.
	 * 
	 * @param asi	May be null.
	 */
	public ActivityStreamEnterEvent(ActivityStreamInfo asi) {
		// Always use one of the initial forms of the method.
		this(asi, ActivityStreamDataType.OTHER);
	}
	
	/**
	 * Constructor methods.
	 * 
	 * @param ss
	 */
	public ActivityStreamEnterEvent(ActivityStreamDataType ss) {
		// Always use one of the initial forms of the method.
		this(null, ss);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public ActivityStreamInfo     getActivityStreamInfo() {return m_asi;        }
	public ActivityStreamDataType getShowSetting()        {return m_showSetting;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setActivityStreamInfo(ActivityStreamInfo     asi) {m_asi         = asi;}
	public void setShowSetting(       ActivityStreamDataType ss)  {m_showSetting = ss; }
	

	/**
	 * Dispatches this event when one is triggered.
	 * 
	 * Implements the VibeEventBase.doDispatch() method.
	 * 
	 * @param handler
	 */
	@Override
	protected void doDispatch(Handler handler) {
		handler.onActivityStreamEnter(this);
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
		return TeamingEvents.ACTIVITY_STREAM_ENTER;
	}
		
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
