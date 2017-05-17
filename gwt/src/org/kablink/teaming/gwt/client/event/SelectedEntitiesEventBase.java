/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.util.EntityId;

import com.google.gwt.event.shared.EventHandler;

/**
 * The SelectedEntitiesEventBase is used as a base class for those
 * events that operation against a collection of entities.
 * 
 * Typically, the extender of this class will NOT provide it with any
 * EntityId's.  They'll be obtained by the context the event was fired
 * from (e.g., in a data table, they'll be the entities that are
 * currently selected.)
 * 
 * The purpose of passing in selected entities is to 'piggy back' on
 * the handling of the operations of the extender's on one or more
 * entities INSTEAD of using those selected.  It is used this way to
 * implement the action menu associated with items in a data table.
 * For those, the event is created with the EntityId of the item whose
 * action menu was selected and then the event is fired, which then
 * uses the event implementor's logic to implement the action.
 * 
 * @author drfoster@novell.com
 */
public abstract class SelectedEntitiesEventBase<H extends EventHandler> extends VibeEventBase<H> {
    public List<EntityId>	m_selectedEntities;	//
    
	/**
	 * Class constructor.
	 */
	public SelectedEntitiesEventBase() {
		// Initialize the super this.
		super();
	}
	
	/**
	 * Class constructor.
	 * 
	 * @param selectedEntityId
	 */
	public SelectedEntitiesEventBase(EntityId selectedEntityId) {
		// Initialize this object...
		this();

		// ...and store the parameter.
		setSelectedEntityId(selectedEntityId);
	}

	/**
	 * Class constructor.
	 * 
	 * @param selectedEntityIds
	 */
	public SelectedEntitiesEventBase(List<EntityId> selectedEntities) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setSelectedEntities(selectedEntities);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	final public List<EntityId> getSelectedEntities() {return m_selectedEntities;}
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	final public void setSelectedEntities(List<EntityId> selectedEntities) {m_selectedEntities = selectedEntities;}
	final public void setSelectedEntityId(     EntityId  selectedEntityId) {
		if (null != selectedEntityId) {
			if (null == m_selectedEntities) {
				m_selectedEntities = new ArrayList<EntityId>();
			}
			m_selectedEntities.add(selectedEntityId);
		}
	}
}
