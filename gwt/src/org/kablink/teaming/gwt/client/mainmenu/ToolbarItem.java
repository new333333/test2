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
package org.kablink.teaming.gwt.client.mainmenu;

import java.util.ArrayList;
import java.util.List;

import org.kablink.teaming.gwt.client.event.TeamingEvents;
import org.kablink.teaming.gwt.client.util.ClientEventParameter;
import org.kablink.teaming.gwt.client.util.EntityId;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class used to communicate toolbar item information between the
 * client and the server.
 * 
 * @author drfoster@novell.com
 */
public class ToolbarItem implements IsSerializable {
	private List<NameEntityIdPair>	m_entityIdsAL   = new ArrayList<NameEntityIdPair>();	// Name/entity ID       pairs for this toolbar item.
	private List<NameValuePair>		m_qualifiersAL  = new ArrayList<NameValuePair>();		// Qualifier name/value pairs for this toolbar item.
	private List<ToolbarItem>		m_nestedItemsAL = new ArrayList<ToolbarItem>();			// Toolbar items nested within this one.
	private String					m_name;													// The name of this toolbar item.
	private String					m_title;												// The display name for this toolbar item.
	private String					m_url;													// The URL to launch for this toolbar item.
	private TeamingEvents			m_teamingEvent  = TeamingEvents.UNDEFINED;				// If the toolbar item is to fire is an event.
	
	// The Client*Parameter's can only be specified and used
	// on the client side.
	private transient ClientEventParameter  m_clientEventParameter;					// Optional parameter for the TeamingEvents.
	
	// The name used for a separator toolbar item.
	public final static String SEPARATOR_NAME	= "999_separator";

	/**
	 * Inner class used to track name/entity ID pairs.
	 */
	public static class NameEntityIdPair implements IsSerializable {
		private EntityId	m_entityId;	// The EntityId for this name/entity ID pair.
		private String		m_name;		// The name  for this name/entity ID pair.

		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public NameEntityIdPair() {
			// Initialize the super class.
			super();
		}

		/**
		 * Constructor method.
		 * 
		 * @param name
		 * @param entityId
		 */
		public NameEntityIdPair(String name, EntityId entityId) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setName(    name    );
			setEntityId(entityId);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String   getName()     {return m_name;    }
		public EntityId getEntityId() {return m_entityId;}

		/**
		 * Set'er methods.
		 * 
		 * @param
		 */
		public void setName(    String   s)        {m_name     = s;       }
		public void setEntityId(EntityId entityId) {m_entityId = entityId;}
	}
	
	/**
	 * Inner class used to track name/value pairs.
	 */
	public static class NameValuePair implements IsSerializable {
		private String m_name;	// The name  for this name/value pair.
		private String m_value;	// The value for this name/value pair.

		/**
		 * Constructor method.
		 * 
		 * No parameters as per GWT serialization requirements.
		 */
		public NameValuePair() {
			// Initialize the super class.
			super();
		}

		/**
		 * Constructor method.
		 * 
		 * @param name
		 * @param value
		 */
		public NameValuePair(String name, String value) {
			// Initialize this object...
			this();
			
			// ...and store the parameters.
			setName( name );
			setValue(value);
		}

		/**
		 * Get'er methods.
		 * 
		 * @return
		 */
		public String getName()  {return m_name; }
		public String getValue() {return m_value;}

		/**
		 * Set'er methods.
		 * 
		 * @param s
		 */
		public void setName( String s) {m_name  = s;}
		public void setValue(String s) {m_value = s;}
	}
	
	/*
	 * Constructor method.
	 * 
	 * No parameters as per GWT serialization requirements.
	 */
	private ToolbarItem() {
		// Initialize the super class.
		super();
	}

	/**
	 * Constructor method.
	 * 
	 * @param name
	 */
	public ToolbarItem(String name) {
		// Initialize this object...
		this();
		
		// ...and store the parameter.
		setName(name);
	}

	/**
	 * Adds a name/entity ID pair to the entity ID list.
	 *  
	 * @param neidP
	 */
	public void addEntityId(NameEntityIdPair neidP) {
		m_entityIdsAL.add(neidP);
	}

	/**
	 * Adds a name/entity ID pair to the entity ID list.
	 *  
	 * @param name
	 * @param entityId
	 */
	public void addEntityId(String name, EntityId entityId) {
		addEntityId(new NameEntityIdPair(name, entityId));
	}

	/**
	 * Adds a nested toolbar item to this one.
	 *  
	 * @param tmi
	 */
	public void addNestedItem(ToolbarItem tmi) {
		if (null != tmi) {
			m_nestedItemsAL.add(tmi);
		}
	}
	
	/**
	 * Adds a the ToolbarItem's from a List<ToolbarItem> as nested
	 * items to a ToolbarItem.
	 * 
	 * @param tmiList
	 */
	public void addNestedItems(List<ToolbarItem> tmiList) {
		// If the list is not empty...
		if ((null != tmiList) && (!(tmiList.isEmpty()))) {
			// ...scan the items in the list...
			for (ToolbarItem tmi:  tmiList) {
				// ...adding each.
				m_nestedItemsAL.add(tmi);
			}
		}
	}

	/**
	 * Adds a name/value pair to the qualifier list.
	 *  
	 * @param nvp
	 */
	public void addQualifier(NameValuePair nvp) {
		m_qualifiersAL.add(nvp);
	}

	/**
	 * Adds a name/value pair to the qualifier list.
	 *  
	 * @param name
	 * @param value
	 */
	public void addQualifier(String name, String value) {
		addQualifier(new NameValuePair(name, value));
	}

	/**
	 * Constructs and returns a separator toolbar item.
	 * 
	 * @return
	 */
	public static ToolbarItem constructSeparatorTBI() {
		return new ToolbarItem(SEPARATOR_NAME);
	}
	
	/*
	 * Returns the name/entity ID pair for an EntityID based on its
	 * name.
	 */
	private static NameEntityIdPair getEntityId(String name, List<NameEntityIdPair> entityIdsAL) {
		name = name.toLowerCase();
		for (NameEntityIdPair nvp:  entityIdsAL) {
			String neidpName = nvp.getName().toLowerCase();
			if (neidpName.endsWith(name)) {
				return nvp;
			}
		}
		return null;
	}
	
	/**
	 * Returns the EntityId for an entity based on its name.
	 * 
	 * @param name
	 * 
	 * @return
	 */
	public EntityId getEntityIdValue(String name) {
		NameEntityIdPair neidp = getEntityId(name, m_entityIdsAL);
		return ((null == neidp) ? null : neidp.getEntityId());
	}
	
	/**
	 * Returns the EntityID for an entity based on its name.
	 * 
	 * @param name
	 * @param entityIdsAL
	 * 
	 * @return
	 */
	public static EntityId getEntityIdValueFromList(String name, List<NameEntityIdPair> entityIdsAL) {
		NameEntityIdPair neidp = getEntityId(name, entityIdsAL);
		return ((null == neidp) ? null : neidp.getEntityId());
	}
	
	/**
	 * Returns this toolbar item's EntityID's list.
	 * 
	 * @return
	 */
	public List<NameEntityIdPair> getEntityIdsList() {
		return m_entityIdsAL;
	}

	/**
	 * Returns the name of the toolbar item.
	 * 
	 * @return
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * Returns this toolbar item's nested items list.
	 * 
	 * @return
	 */
	public List<ToolbarItem> getNestedItemsList() {
		return m_nestedItemsAL;
	}

	/**
	 * Returns a nested toolbar item based on its name.
	 *
	 * @param tbiList
	 * @param name
	 * 
	 * @return
	 */
	public static ToolbarItem getNestedToolbarItem(List<ToolbarItem> tbiList, String name) {
		name = ((null == name) ? "" : name.toLowerCase());
		boolean noName = (0 == name.length());
		for (ToolbarItem tbi:  tbiList) {
			String tbName = tbi.getName();
			tbName = ((null == tbName) ? "" : tbName.toLowerCase());
			boolean noTBName = (0 == tbName.length());
			if ((noName && noTBName) || ((!noName) && tbName.endsWith(name))) {
				return tbi;
			}
		}
		return null;
	}
	
	public ToolbarItem getNestedToolbarItem(String name) {
		// Always use the initial form of the method.
		return getNestedToolbarItem(m_nestedItemsAL, name);
	}
	
	/*
	 * Returns the name/value pair for a qualifier based on its name.
	 */
	private static NameValuePair getQualifier(String name, List<NameValuePair> qualifiersAL) {
		name = name.toLowerCase();
		for (NameValuePair nvp:  qualifiersAL) {
			String nvpName = nvp.getName().toLowerCase();
			if (nvpName.endsWith(name)) {
				return nvp;
			}
		}
		return null;
	}
	
	/**
	 * Returns this toolbar item's qualifiers list.
	 * 
	 * @return
	 */
	public List<NameValuePair> getQualifiersList() {
		return m_qualifiersAL;
	}

	/**
	 * Returns the value for a qualifier based on its name.
	 * 
	 * @param name
	 * 
	 * @return
	 */
	public String getQualifierValue(String name) {
		NameValuePair nvp = getQualifier(name, m_qualifiersAL);
		return ((null == nvp) ? null : nvp.getValue());
	}
	
	/**
	 * Returns the value for a qualifier based on its name.
	 * 
	 * @param name
	 * @param qualifiersAL
	 * 
	 * @return
	 */
	public static String getQualifierValueFromList(String name, List<NameValuePair> qualifiersAL) {
		NameValuePair nvp = getQualifier(name, qualifiersAL);
		return ((null == nvp) ? null : nvp.getValue());
	}
	
	/**
	 * Returns the teaming event from a toolbar item.
	 * 
	 * @return
	 */
	public TeamingEvents getTeamingEvent() {
		return m_teamingEvent;
	}
	
	/**
	 * Returns the client event parameter from a toolbar item.
	 * 
	 * @return
	 */
	public ClientEventParameter getClientEventParameter() {
		return m_clientEventParameter;
	}
	
	/**
	 * Returns the title of the toolbar item.
	 * 
	 * @return
	 */
	public String getTitle() {
		return m_title;
	}
	
	/**
	 * Returns the URL of the toolbar item.
	 * 
	 * @return
	 */
	public String getUrl() {
		return m_url;
	}
	
	/**
	 * Returns true if this toolbar item has at least a specified
	 * number of nested toolbar items and false otherwise.
	 * 
	 * @param atLeast
	 * 
	 * @return
	 */
	public boolean hasNestedToolbarItems(int atLeast) {
		int count = ((null == m_nestedItemsAL) ? 0 : m_nestedItemsAL.size());
		return (count >= atLeast);
	}
	
	public boolean hasNestedToolbarItems() {
		return hasNestedToolbarItems(1);
	}

	/**
	 * Returns true if this is a separator item and false otherwise.
	 * 
	 * @return
	 */
	public boolean isSeparator() {
		String name = getName();
		return ((null != name) && name.equals(SEPARATOR_NAME));
	}
	
	/**
	 * Stores the name of the toolbar item.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		m_name = name;
	}
	
	/**
	 * Stores a teaming event in the toolbar item.
	 * 
	 * @param teamingEvent
	 */
	public void setTeamingEvent(TeamingEvents teamingEvent) {
		m_teamingEvent = teamingEvent;
	}
	
	/**
	 * Stores a client event parameter in the toolbar item.
	 * 
	 * @param clientEventParameter
	 */
	public void setClientEventParameter(ClientEventParameter clientEventParameter) {
		m_clientEventParameter = clientEventParameter;
	}
	
	/**
	 * Stores the title of the toolbar item.
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		m_title = title;
	}
	
	/**
	 * Stores the URL of the toolbar item.
	 * 
	 * @param url
	 */
	public void setUrl(String url) {
		m_url = url;
	}
}
