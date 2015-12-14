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
package org.kablink.teaming.gwt.client.rpc.shared;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.util.EntityId;

/**
 * This class holds all of the information necessary to execute the
 * 'save email notification information' command.
 * 
 * @author drfoster@novell.com
 */
public class SaveEmailNotificationInfoCmd extends VibeRpcCmd {
	private boolean			m_overridePresets;		// true -> These settings override the presets.  false -> They don't.
	private List<EntityId>	m_entityIds;			// List of EntityId's when running in entity subscription mode.
	private List<String>	m_digestAddressTypes;	// List of email address types used for digests,                                 if any.
	private List<String>	m_msgAddressTypes;		// List of email address types used for individual messages,                     if any.
	private List<String>	m_msgNoAttAddressTypes;	// List of email address types used for individual messages without attachments, if any.
	private List<String>	m_textAddressTypes;		// List of email address types used for text messaging,                          if any.
	private Long			m_binderId;				// ID of the binder email notifications are being set on when not running in entry subscription mode.
	
	/**
	 * Constructor method.
	 * 
	 * For GWT serialization, must have a zero parameter constructor.
	 */
	public SaveEmailNotificationInfoCmd() {
		// Initialize the super class...
		super();
		
		// ...and initialize everything else.
		m_digestAddressTypes   = new ArrayList<String>();
		m_msgAddressTypes      = new ArrayList<String>();
		m_msgNoAttAddressTypes = new ArrayList<String>();
		m_textAddressTypes     = new ArrayList<String>();
	}
	
	/**
	 * Constructor method.
	 * 
	 * @param binderId
	 */
	public SaveEmailNotificationInfoCmd(Long binderId) {
		// Initialize the super class and save the parameter.
		this();
		setBinderId(binderId);
	}

	/**
	 * Constructor method.
	 * 
	 * @param entityIds
	 */
	public SaveEmailNotificationInfoCmd(List<EntityId> entityIds) {
		// Initialize the super class and save the parameter.
		this();
		setEntityIds(entityIds);
	}

	/**
	 * Get'er methods.
	 * 
	 * @return
	 */
	public boolean        getOverridePresets()      {return m_overridePresets;     }
	public List<EntityId> getEntityIds()            {return m_entityIds;           }
	public List<String>   getDigestAddressTypes()   {return m_digestAddressTypes;  }
	public List<String>   getMsgAddressTypes()      {return m_msgAddressTypes;     }
	public List<String>   getMsgNoAttAddressTypes() {return m_msgNoAttAddressTypes;}
	public List<String>   getTextAddressTypes()     {return m_textAddressTypes;    }
	public Long           getBinderId()             {return m_binderId;            }
	
	/**
	 * Set'er methods.
	 * 
	 * @param
	 */
	public void setEntityIds(           List<EntityId> entityIds)            {m_entityIds            = entityIds;           }
	public void setOverridePresets(     boolean        overridePresets)      {m_overridePresets      = overridePresets;     }
	public void setDigestAddressTypes(  List<String>   digestAddressTypes)   {m_digestAddressTypes   = digestAddressTypes;  }
	public void setMsgAddressTypes(     List<String>   msgAddressTypes)      {m_msgAddressTypes      = msgAddressTypes;     }
	public void setMsgNoAttAddressTypes(List<String>   msgNoAttAddressTypes) {m_msgNoAttAddressTypes = msgNoAttAddressTypes;}
	public void setTextAddressTypes(    List<String>   textAddressTypes)     {m_textAddressTypes     = textAddressTypes;    }
	public void setBinderId(            Long           binderId)             {m_binderId             = binderId;            }
	
	/**
	 * Adds an email address to a list of email addresses.
	 * 
	 * @param
	 */
	public void addDigestAddressType(  String type) {m_digestAddressTypes.add(  type);}
	public void addMsgAddressType(     String type) {m_msgAddressTypes.add(     type);}
	public void addMsgNoAttAddressType(String type) {m_msgNoAttAddressTypes.add(type);}
	public void addTextAddressType(    String type) {m_textAddressTypes.add(    type);}
	
	/**
	 * Returns the command's enumeration value.
	 * 
	 * Implements VibeRpcCmd.getCmdType()
	 * 
	 * @return
	 */
	@Override
	public int getCmdType() {
		return VibeRpcCmdType.SAVE_EMAIL_NOTIFICATION_INFORMATION.ordinal();
	}
}
