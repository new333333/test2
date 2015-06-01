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
package org.kablink.teaming.gwt.client.event;

import org.kablink.teaming.gwt.client.util.EntityId;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.user.client.ui.UIObject;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.SimpleEventBus;

/**
 * The SetSelectedPrincipalsLimitedUserVisibilityEvent is used to
 * set/clear limited user visibility on the currently selected users or
 * groups.
 * 
 * See the definition of the SelectedEntitiesEventBase class for how and
 * when an EntityId (or List<EntityId>) should be passed into the
 * construction of this class.
 * 
 * @author drfoster@novell.com
 */
public class SetSelectedPrincipalsLimitedUserVisibilityEvent extends SelectedEntitiesEventBase<SetSelectedPrincipalsLimitedUserVisibilityEvent.Handler> {
    public static Type<Handler> TYPE = new Type<Handler>();

    private boolean		m_selectPrincipal;	// true -> When the event is handled, allow the user to select the specified principal.  false -> The selected principals must be provided.
    private Boolean		m_limited;			// true -> The          Can Only See Members of Groups I am In rights are to be set.  false -> The rights are to be cleared.  null -> No action taken.
    private Boolean		m_override;			// true -> The Override Can Only See Members of Groups I am In rights are to be set.  false -> The rights are to be cleared.  null -> No action taken.
    private UIObject	m_showRelativeTo;	//
    
	/**
	 * Handler interface for this event.
	 */
	public interface Handler extends EventHandler {
		void onSetSelectedPrincipalsLimitedUserVisibility(SetSelectedPrincipalsLimitedUserVisibilityEvent event);
	}
	
	/*
	 * Class constructor.
	 */
	private SetSelectedPrincipalsLimitedUserVisibilityEvent() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param selectedEntityId
	 * @param limited
	 * @param override
	 * @param selectPrincipal
	 */
	public SetSelectedPrincipalsLimitedUserVisibilityEvent(EntityId selectedEntityId, Boolean limited, Boolean override, boolean selectPrincipal) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setSelectedEntityId(selectedEntityId);
		setLimited(         limited         );
		setOverride(        override        );
		setSelectPrincipal( selectPrincipal );
	}

	/**
	 * Get'er method.
	 * 
	 * @return
	 */
	public boolean  isSelectPrincipal() {return m_selectPrincipal;}
	public Boolean  getLimited()        {return m_limited;        }
	public Boolean  getOverride()       {return m_override;       }
	public UIObject getShowRelativeTo() {return m_showRelativeTo; }
	
	/**
	 * Set'er method.
	 * 
	 * @param
	 */
	public void setSelectPrincipal(boolean  selectPrincipal) {m_selectPrincipal = selectPrincipal;}
	public void setLimited(        Boolean  limited)         {m_limited         = limited;        }
	public void setOverride(       Boolean  override)        {m_override        = override;       }
	public void setShowRelativeTo( UIObject showRelativeTo)  {m_showRelativeTo  = showRelativeTo; } 
	
	/**
	 * Dispatches this event when one is triggered.
	 * 
	 * Implements the VibeEventBase.doDispatch() method.
	 * 
	 * @param handler
	 */
    @Override
    protected void doDispatch(Handler handler) {
        handler.onSetSelectedPrincipalsLimitedUserVisibility(this);
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
		return TeamingEvents.SET_SELECTED_PRINCIPALS_LIMIT_USER_VISIBILITY;
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
