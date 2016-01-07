/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.gwt.client.rpc.shared;

import org.kablink.teaming.gwt.client.util.EntityId.EntityIdType;

/**
 * This class holds all of the information necessary to execute the
 * 'get entity ID' command.
 * 
 * @author drfoster
 */
public class GetEntityIdCmd extends VibeRpcCmd {
	private EntityIdType	m_eidType;			//
	private Long			m_binderId;			//
	private Long			m_entityId;			//
	private String			m_mobileDeviceId;	//
	
	/*
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	private GetEntityIdCmd() {
		// Initialize the super class.
		super();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 * @param binderId
	 * @param eidType
	 * @param mobileDeviceId
	 */
	public GetEntityIdCmd(Long binderId, Long entityId, EntityIdType eidType, String mobileDeviceId) {
		// Initialize this object...
		this();

		// ...and store the parameters.
		setEntityId(      entityId      );
		setBinderId(      binderId      );
		setEntityIdType(  eidType       );
		setMobileDeviceId(mobileDeviceId);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param entityId
	 * @param binderId
	 * @param eidType
	 */
	public GetEntityIdCmd(Long binderId, Long entityId, EntityIdType eidType) {
		// Initialize this object.
		this(binderId, entityId, eidType, null);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param eidType
	 * @param mobileDeviceId
	 */
	public GetEntityIdCmd(Long binderId, EntityIdType eidType, String mobileDeviceId) {
		// Initialize this object.
		this(binderId, null, eidType, mobileDeviceId);
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 * @param eidType
	 */
	public GetEntityIdCmd(Long binderId, EntityIdType eidType) {
		// Initialize this object.
		this(binderId, null, eidType, null);
	}
	
	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public EntityIdType getEntityIdType()   {return m_eidType;       }
	public Long         getEntityId()       {return m_entityId;      }
	public Long         getBinderId()       {return m_binderId;      }
	public String       getMobileDeviceId() {return m_mobileDeviceId;}

	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEntityIdType(  EntityIdType eidType )       {m_eidType        = eidType;       }
	public void setEntityId(      Long         entityId)       {m_entityId       = entityId;      }
	public void setBinderId(      Long         binderId)       {m_binderId       = binderId;      }
	public void setMobileDeviceId(String       mobileDeviceId) {m_mobileDeviceId = mobileDeviceId;}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.GET_ENTITY_ID.ordinal();
	}
}
